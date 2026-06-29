package net.rim.device.cldc.io.datarecovery;

import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.GAN;
import net.rim.device.api.system.GANSystem;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.system.WLAN;
import net.rim.device.api.system.WLANListenerInternal;
import net.rim.device.api.system.WLANSystem;
import net.rim.device.api.util.Arrays;
import net.rim.device.internal.crypto.vpn.VPN;
import net.rim.device.internal.proxy.Proxy;
import net.rim.device.internal.system.DataServices;
import net.rim.device.internal.system.InternalServices;

public final class DataRecoveryWLAN extends DataRecovery implements GlobalEventListener, WLANListenerInternal {
   private int _lastNetwork = -1;
   private int[] _errorLevels = new int[5];
   private long _timeOnTheNetwork;
   private static final int THRESHOLD_RECOVERY_WIFI = 4;
   private static final int START_RECOVERY_BACKOFF_WIFI = 600000;
   private static final int MAX_RECOVERY_BACKOFF_WIFI = 3600000;
   private static final int MIN_RECOVERY_WIFI = 300000;

   public DataRecoveryWLAN(int linkType, long guid) {
      super(linkType, guid);
      Proxy.getInstance().addRadioListener(this);
   }

   @Override
   public final void radioStatus(boolean started) {
      if (!started) {
         this.networkFail(1, 0, 0);
      }
   }

   @Override
   public final void networkSuccess() {
      synchronized (this) {
         this._timeOnTheNetwork = InternalServices.getUptime();
         super._primed = false;
         if (this._lastNetwork == -1 || !this.doesNetworkExist(this._lastNetwork)) {
            this._lastNetwork = this.getCurrentNetworkId();
            if (super._currentRecoveryBackoff >= 600000) {
               super._currentRecoveryBackoff = 0;
            }
         }
      }
   }

   @Override
   public final void networkFail(int status, int error, int extendedInfo) {
      synchronized (this) {
         this.resetErrorLevel();
      }
   }

   @Override
   public final void networkFound(int handle) {
   }

   @Override
   public final void networkApChange() {
   }

   @Override
   public final void fileReport(int report, int linkType, int connectionType) {
      if (!this.ignoreReport(linkType, connectionType)) {
         int event;
         Object[] listeners;
         synchronized (this) {
            switch (report) {
               case -1:
                  return;
               case 0:
               default:
                  if (this.getErrorLevel() >= 4) {
                     super._primed = false;
                  }

                  this.resetErrorLevel();
                  if (connectionType == 4) {
                     return;
                  }

                  if (super._currentRecoveryBackoff > 600000) {
                     super._currentRecoveryBackoff >>= 1;
                  } else {
                     super._currentRecoveryBackoff = 0;
                  }

                  if (super._primed) {
                     return;
                  }

                  super._primed = true;
                  event = 1;
                  break;
               case 1:
                  this.incrementErrorLevel(connectionType);
                  if (this.getTotalErrorLevel(-1) == 1) {
                     super._lastRecoveryTime = System.currentTimeMillis();
                  }

                  if (this.getErrorLevel() < 4 || this.serviceAvailable(-1)) {
                     return;
                  }

                  long currentTime = InternalServices.getUptime();
                  if (currentTime > this._timeOnTheNetwork && currentTime - this._timeOnTheNetwork < 300000) {
                     EventLogger.logEvent(1916912427746348095L, 1466001780, 0);
                     return;
                  }

                  long now = System.currentTimeMillis();
                  if (super._currentRecoveryBackoff < 600000) {
                     super._lastRecoveryTime = now;
                     super._currentRecoveryBackoff = 600000;
                     event = 2;
                  } else {
                     if (now - super._lastRecoveryTime < super._currentRecoveryBackoff) {
                        EventLogger.logEvent(1916912427746348095L, 1113678699, 0);
                        return;
                     }

                     int networkId = this.getCurrentNetworkId();
                     if (networkId != -1 && this._lastNetwork == networkId) {
                        EventLogger.logEvent(1916912427746348095L, 1113678699, 3);
                        super._currentRecoveryBackoff <<= 1;
                        if (super._currentRecoveryBackoff > 3600000) {
                           super._currentRecoveryBackoff = 3600000;
                        }
                     }

                     super._lastRecoveryTime = now;
                     event = 2;
                  }

                  this.resetErrorLevel();
                  EventLogger.logEvent(1916912427746348095L, 1382248310, 3);
            }

            listeners = super._listeners;
         }

         if (listeners != null) {
            this.informListeners(listeners, event, linkType);
         }
      }
   }

   @Override
   public final synchronized boolean isConnectionAvailable() {
      return WLAN.isWLANAllowed() && this.getCurrentNetworkId() != -1 && this.getErrorLevel() < 4;
   }

   private final int getErrorLevel() {
      return super._errorLevel;
   }

   private final void resetErrorLevel() {
      this.resetErrorLevel(-1);
      super._errorLevel = 0;
   }

   private final void incrementErrorLevel() {
      super._errorLevel++;
   }

   private final void resetErrorLevel(int connectionType) {
      if (connectionType < 0) {
         Arrays.fill(this._errorLevels, 0);
      } else {
         if (connectionType < this._errorLevels.length) {
            this._errorLevels[connectionType] = 0;
         }
      }
   }

   private final int getTotalErrorLevel(int connectionType) {
      if (connectionType == -1) {
         return this._errorLevels[1] + this._errorLevels[2] + this._errorLevels[3] + this._errorLevels[4];
      } else {
         return connectionType >= 0 && connectionType < this._errorLevels.length ? this._errorLevels[connectionType] : 0;
      }
   }

   private final void incrementErrorLevel(int connectionType) {
      if (connectionType >= 0 && connectionType < this._errorLevels.length) {
         this._errorLevels[connectionType] = Math.min(this._errorLevels[connectionType] + 1, 4);
         if ((this._errorLevels[1] >= 4 || this.serviceDisabled(1))
            && (this._errorLevels[2] >= 4 || this.serviceDisabled(2))
            && (this._errorLevels[3] >= 4 || this.serviceDisabled(3))
            && (this._errorLevels[4] >= 4 || this.serviceDisabled(4))) {
            super._errorLevel = 4;
         }
      }
   }

   private final boolean ignoreReport(int linkType, int connectionType) {
      if (linkType != super._linkType) {
         return false;
      } else {
         return this.getCurrentNetworkId() == -1 ? false : this.serviceDisabled(connectionType);
      }
   }

   private final int getCurrentNetworkId() {
      WLANSystem sys = WLAN.getWLANSystem();
      return sys != null ? sys.isAssociated() : -1;
   }

   private final boolean doesNetworkExist(int networkId) {
      WLANSystem sys = WLAN.getWLANSystem();
      return sys != null && sys.getProfileSSID(networkId) != null ? sys.getWLANNetworkInfo(sys.getActiveProfileSet(), networkId) != null : false;
   }

   private final boolean serviceDisabled(int connectionType) {
      switch (connectionType) {
         case 0:
            return true;
         case 1:
         default:
            boolean disabled = !DataServices.getInstance().isDataServicesEnabled(2, 1);
            if (!disabled) {
               WLANSystem sys = WLAN.getWLANSystem();
               if (sys != null) {
                  disabled = !sys.isBlackberryInfrastructureConnectionProvisioned();
               }
            }

            return disabled;
         case 2:
            boolean var5 = !DataServices.getInstance().isDataServicesEnabled(2, 2) || VPN.isIPSecRequiredForNetwork(null, 6) && !VPN.isConnected();
            if (!var5) {
               WLANSystem sys = WLAN.getWLANSystem();
               if (sys != null) {
                  var5 = !sys.isEnterpriseConnectionProvisioned();
               }
            }

            return var5;
         case 3:
            boolean var4 = !GAN.isGANAllowed();
            if (!var4) {
               GANSystem sysGAN = GAN.getGANSystem();
               if (sysGAN != null) {
                  var4 = !sysGAN.isGANActive();
               }
            }

            return var4;
         case 4:
            boolean disabled = !VPN.isVPNAllowed() || !VPN.isIPSecRequiredForNetwork(null, 6) || VPN.isConnected() && !VPN.livenessCheckEnabled();
            if (!disabled) {
               if (DataServices.getInstance().isDataServicesEnabled(2, 2)) {
                  WLANSystem sys = WLAN.getWLANSystem();
                  if (sys != null) {
                     return !sys.isEnterpriseConnectionProvisioned();
                  }
               } else {
                  disabled = false;
               }
            }

            return disabled;
      }
   }

   private final boolean serviceAvailable(int connectionType) {
      if (connectionType == -1) {
         if ((RadioInfo.getNetworkService() & 16384) != 0) {
            return true;
         }

         WLANSystem sys = WLAN.getWLANSystem();
         return sys != null ? sys.isBlackberryInfrastructureConnectionAvailable() || sys.isEnterpriseConnectionAvailable() : false;
      } else {
         switch (connectionType) {
            case 0:
               return false;
            case 1:
            default:
               WLANSystem sys = WLAN.getWLANSystem();
               if (sys != null && sys.isBlackberryInfrastructureConnectionAvailable()) {
                  return true;
               }

               return false;
            case 2:
               WLANSystem sys = WLAN.getWLANSystem();
               if (sys != null && sys.isEnterpriseConnectionAvailable()) {
                  return true;
               }

               return false;
            case 3:
               if ((RadioInfo.getNetworkService() & 16384) != 0) {
                  return true;
               }

               return false;
            case 4:
               return VPN.isConnected();
         }
      }
   }
}
