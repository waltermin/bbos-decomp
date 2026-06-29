package net.rim.device.cldc.impl.tunnel;

import java.util.Vector;
import net.rim.device.api.io.DatagramAddressBase;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.system.RadioStatusListener;
import net.rim.device.api.system.WLAN;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.cldc.io.daemon.ProtocolDaemon;
import net.rim.device.cldc.io.tunnel.Tunnel;
import net.rim.device.cldc.io.tunnel.TunnelConfig;
import net.rim.device.cldc.io.tunnel.TunnelFactory;
import net.rim.device.cldc.io.tunnel.TunnelListener;
import net.rim.device.internal.system.DataServices;
import net.rim.device.internal.system.RadioInternal;
import net.rim.vm.WeakReference;

public final class TunnelManagerImpl extends Thread implements GlobalEventListener, RadioStatusListener, TunnelEvent {
   private TunnelConfig _config;
   private long _age;
   private int _status;
   private int _tunnelId = -1;
   private Vector _tunnels = (Vector)(new Object(2));
   private int _waitTmo;
   private int _waitState;
   private int _nextAction;
   private int _nextData;
   private int _activationAttempts;
   private boolean _dataService;
   private DataServices _dataServices;
   private boolean _dataServicesEnabled;
   private boolean _cellular;
   private RadioStatusListener _listener;
   private static final int WAIT_NONE = 0;
   private static final int WAIT_INACTIVE = 1;
   private static final int WAIT_ACTIVE = 2;
   private static final int WAIT_ACTIVATING = 3;
   private static final int WAIT_BACKOFF = 4;
   private static final int WAIT_DORMANT = 5;
   private static final int WAIT_OFF = 6;
   private static final int WAIT_LINGER = 7;
   private static final int ACTION_NONE = 0;
   private static final int ACTION_UPDATE = 1;
   private static final int ACTION_KICK = 2;
   private static final int ACTION_RESET = 3;
   private static final int ACTION_ACTIVATE = 4;
   private static final int ACTION_BACKOFF = 5;
   private static final int ACTION_ACTIVE = 6;
   private static final int ACTION_INACTIVE = 7;
   private static final int ACTION_CRITICAL = 8;
   private static final int ACTION_STANDBY = 9;
   private static final int ACTION_DORMANT = 10;
   private static final int ACTION_OFF = 11;

   TunnelManagerImpl(TunnelConfig config) {
      this._config = config;
      this._age = System.currentTimeMillis();
      this._dataServices = DataServices.getInstance();
      this._cellular = !config.getName().equalsIgnoreCase(WLAN.WLAN_PSEUDO_APN);
      this._dataServicesEnabled = this.getDataServicesEnabled();
      this._waitState = this._dataServicesEnabled ? 1 : 6;
   }

   private final boolean getDataServicesEnabled() {
      return this._cellular ? this._dataServices.isDataServicesEnabled() : this._dataServices.getMode() != 2;
   }

   private final void pruneDeadHandles(Tunnel tunnel) {
      for (int i = this._tunnels.size() - 1; i >= 0; i--) {
         WeakReference w = (WeakReference)this._tunnels.elementAt(i);
         Tunnel t;
         if (w == null || (t = (Tunnel)w.get()) == null || t == tunnel) {
            this._tunnels.removeElementAt(i);
         }
      }
   }

   final void addTunnel(Tunnel tunnel) {
      this.logEvent(4292459735430940092L, 1332766062, tunnel.getConfig().getName(), 0);
      synchronized (this._tunnels) {
         this.pruneDeadHandles(null);
         this._tunnels.addElement(new Object(tunnel));
      }
   }

   public final boolean isClosed() {
      synchronized (this._tunnels) {
         return this._waitState == 0;
      }
   }

   public final TunnelConfig getConfig() {
      return this._config;
   }

   public final long getAge() {
      return this._age;
   }

   public final int getStatus() {
      synchronized (this._tunnels) {
         return this._status;
      }
   }

   public final int getIdentifier() {
      synchronized (this._tunnels) {
         return this._tunnelId;
      }
   }

   public final void close(Tunnel tunnel) {
      this.logEvent(4292459735430940092L, 1131179891, tunnel.getConfig().getName(), 0);
      synchronized (this._tunnels) {
         this.pruneDeadHandles(tunnel);
         if (this._tunnels.size() == 0 && this._waitState != 7) {
            this._nextAction = 1;
            this._tunnels.notify();
         }
      }
   }

   public final void kick(boolean delay) {
      this.logEvent(4292459735430940092L, 1265197931, this._config.getName(), 0);
      synchronized (this._tunnels) {
         this.pruneDeadHandles(null);
         switch (this._waitState) {
            default:
               this._nextAction = 2;
               this._nextData = delay ? this._config.getShortDelayTimeout() : 0;
               this._tunnels.notify();
            case 6:
         }
      }
   }

   public final void reset() {
      this.logEvent(4292459735430940092L, 1383294324, this._config.getName(), 0);
      synchronized (this._tunnels) {
         this.pruneDeadHandles(null);
         switch (this._waitState) {
            case 2:
               this._nextAction = 3;
               this._tunnels.notify();
            case 6:
               break;
            default:
               this._nextAction = 2;
               this._nextData = 0;
               this._tunnels.notify();
         }
      }
   }

   public final void standby() {
      this.logEvent(4292459735430940092L, 1400136802, this._config.getName(), 0);
      synchronized (this._tunnels) {
         this.pruneDeadHandles(null);
         switch (this._waitState) {
            case 4:
               this._nextAction = 9;
               this._tunnels.notify();
            case 5:
            case 6:
               break;
            case 7:
            default:
               this._tunnels.notify();
         }
      }
   }

   public final Object setup(int callType, Object context) {
      switch (callType) {
         case -380645053:
            return this;
         case -380645052:
            return this._tunnels;
         case -380645051:
            return new Object(this._waitTmo);
         case -380645050:
            return new Object(this._waitState);
         case -380645049:
            return new Object(this._nextAction);
         case -380645048:
            return new Object(this._nextData);
         case -380645047:
            return new Object(this._activationAttempts);
         case -380645046:
            return new Object(this._dataService);
         case 202662285:
            return this.getTunnels((Vector)context);
         case 1483043725:
            this.clean();
         default:
            return null;
      }
   }

   private final Vector getTunnels(Vector v) {
      synchronized (this._tunnels) {
         if (v != null) {
            for (int i = this._tunnels.size() - 1; i >= 0; i--) {
               v.addElement(this._tunnels.elementAt(i));
            }
         }

         return this._tunnels;
      }
   }

   private final void clean() {
      synchronized (this._tunnels) {
         this.pruneDeadHandles(null);
      }
   }

   @Override
   public final void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == -3556743465989743742L) {
         synchronized (this._tunnels) {
            boolean dataServicesEnabled = this.getDataServicesEnabled();
            if (this._dataServicesEnabled != dataServicesEnabled) {
               this._dataServicesEnabled = dataServicesEnabled;
               if (dataServicesEnabled) {
                  this._nextAction = 2;
                  this._nextData = 0;
               } else {
                  this._nextAction = 11;
               }

               this._tunnels.notify();
            }
         }
      }
   }

   @Override
   public final void networkStarted(int networkId, int service) {
      this.networkServiceChange(true, networkId, service);
   }

   @Override
   public final void radioTurnedOff() {
      this.networkServiceChange(true, 0, 0);
   }

   @Override
   public final void pdpStateChange(int apn, int state, int cause) {
      synchronized (this._tunnels) {
         if (apn == this._tunnelId) {
            this.logEvent(4292459735430940092L, 1348759600 | state, this._config.getName(), 0);
            if (state == 0) {
               switch (this._waitState) {
                  case 5:
                  default:
                     this._nextAction = 6;
                     this._tunnels.notify();
                  case 6:
                  case 7:
               }
            } else if (state == 1) {
               switch (this._waitState) {
                  case 1:
                     this._nextAction = 7;
                     break;
                  case 5:
                     this._nextAction = 10;
                  case 6:
                  case 7:
                     break;
                  default:
                     this._nextAction = 2;
                     this._nextData = this._config.getDelayTimeout();
               }

               this._tunnels.notify();
            } else if (state == 2) {
               switch (cause) {
                  case 25:
                  case 26:
                  case 34:
                  case 35:
                  case 36:
                  case 38:
                  case 39:
                  case 102:
                  case 111:
                     switch (this._waitState) {
                        case 1:
                           return;
                        case 2:
                        default:
                           this._nextAction = 2;
                           this._nextData = this._config.getDelayTimeout();
                           this._tunnels.notify();
                           return;
                        case 3:
                           this._nextAction = 5;
                           this._nextData = cause;
                           this._tunnels.notify();
                           return;
                     }
                  default:
                     switch (this._waitState) {
                        case 4:
                        default:
                           this._nextAction = 8;
                           this._nextData = cause;
                           this._tunnels.notify();
                        case 5:
                        case 6:
                     }
               }
            }
         }
      }
   }

   @Override
   public final void networkServiceChange(int networkId, int service) {
      this.networkServiceChange(true, networkId, service);
   }

   private final void networkServiceChange(boolean cellular, int networkId, int service) {
      if (cellular == this._cellular) {
         synchronized (this._tunnels) {
            boolean dataService = (service & 4) != 0;
            if (dataService != this._dataService) {
               this._dataService = dataService;
               switch (this._waitState) {
                  case 4:
                  default:
                     if (dataService) {
                        this._nextAction = 2;
                        this._nextData = this._config.getShortDelayTimeout();
                     } else {
                        this._nextAction = 7;
                     }

                     this._tunnels.notify();
                  case 5:
                  case 6:
                  case 7:
               }
            }
         }
      }
   }

   @Override
   public final void signalLevel(int level) {
   }

   @Override
   public final void baseStationChange() {
   }

   @Override
   public final void networkStateChange(int state) {
   }

   @Override
   public final void networkScanComplete(boolean success) {
   }

   @Override
   public final void run() {
      EventLogger.logEvent(4292459735430940092L, 1414034292, 5);
      ProtocolDaemon daemon = ProtocolDaemon.getInstance();
      synchronized (this._tunnels) {
         this._dataService = (RadioInfo.getNetworkService() & 4) != 0;
         daemon.addGlobalEventListener(this);
         if (!StringUtilities.strEqualIgnoreCase(this._config.getName(), WLAN.WLAN_PSEUDO_APN)) {
            this._listener = this;
            daemon.addRadioListener(this);
         } else {
            this._listener = new TunnelManagerImpl$TunnelWlanListener(this);
            daemon.addRadioListener(4, this._listener);
         }

         label161:
         try {
            try {
               while (true) {
                  EventLogger.logEvent(4292459735430940092L, 1414029616 | this._nextAction, 5);

                  try {
                     switch (this._nextAction) {
                        case 0:
                           break;
                        case 1:
                        default:
                           this.actionUpdate();
                           break;
                        case 2:
                           this.actionKick(this._nextData);
                           break;
                        case 3:
                           this.actionReset();
                           break;
                        case 4:
                           this.actionActivate(false);
                           break;
                        case 5:
                           this.actionBackoff(this._nextData);
                           break;
                        case 6:
                           this.actionActive();
                           break;
                        case 7:
                           this.actionInactive();
                           break;
                        case 8:
                           this.actionCritical(this._nextData);
                           break;
                        case 9:
                           this.actionStandby();
                           break;
                        case 10:
                           this.actionDormant();
                           break;
                        case 11:
                           this.actionOff();
                     }
                  } catch (DeactivateTunnelException dte) {
                     if (this._waitState == 7) {
                        throw dte;
                     }

                     int lingerTimeout = this._config.getLingerTimeout();
                     if (lingerTimeout <= 0) {
                        throw dte;
                     }

                     this._waitState = 7;
                     this._waitTmo = lingerTimeout * 1000;
                     this._nextAction = 1;
                  }

                  EventLogger.logEvent(4292459735430940092L, 1414035248 | this._waitState, 5);
                  this._tunnels.wait(this._waitTmo);
               }
            } catch (DeactivateTunnelException var18) {
            }
         } finally {
            break label161;
         }

         daemon.removeRadioListener(this._listener);
         daemon.removeGlobalEventListener(this);
         if (this._tunnelId >= 0) {
            EventLogger.logEvent(4292459735430940092L, 1414423653, 0);

            label144:
            try {
               RadioInternal.deregisterAccessPointNumber(this._tunnelId);
            } finally {
               break label144;
            }

            this._tunnelId = -1;
         }

         this._waitState = 0;
         this._status = 0;
      }

      ((TunnelFactoryImpl)TunnelFactory.getTunnelFactory()).kickManagers(true);
      EventLogger.logEvent(4292459735430940092L, 1414034288, 5);
   }

   private final void actionUpdate() {
      this.updateStatus(0, 0);
   }

   private final void actionKick(int delay) {
      this.updateStatus(1, 0);
      this._activationAttempts = 0;
      if (delay > 0) {
         this._waitTmo = delay;
         this._waitState = 4;
         this._nextAction = 4;
      } else {
         this.actionActivate(true);
      }
   }

   private final void actionReset() {
      this.updateStatus(6, 0);
      if (this._tunnelId >= 0) {
         EventLogger.logEvent(4292459735430940092L, 1414423653, 3);

         label24:
         try {
            RadioInternal.deregisterAccessPointNumber(this._tunnelId);
         } finally {
            break label24;
         }

         this._tunnelId = -1;
      }

      this._activationAttempts = 0;
      this._waitTmo = this._config.getDeactivateTimeout();
      this._waitState = 4;
      this._nextAction = 4;
   }

   // $VF: Could not inline inconsistent finally blocks
   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final void actionActivate(boolean kick) {
      this.updateStatus(2, 0);
      if (!this._dataService && !kick) {
         this.actionInactive();
      } else {
         String name = this._config.getName();
         boolean var10 = false /* VF: Semaphore variable */;

         int tunnelId;
         boolean active;
         label83:
         try {
            var10 = true;
            tunnelId = RadioInfo.getAccessPointNumber(name);
            active = RadioInfo.isPDPContextActive(tunnelId);
            var10 = false;
         } finally {
            if (var10) {
               tunnelId = -1;
               active = false;
               break label83;
            }
         }

         if (active && tunnelId >= 0) {
            this._tunnelId = tunnelId;
            this.actionActive();
         } else {
            this._activationAttempts++;
            EventLogger.logEvent(4292459735430940092L, 1414422883, 0);

            label77:
            try {
               this._tunnelId = RadioInternal.registerAccessPointNumber(
                  name, 0, name.length(), this._config.getQos(), this._config.getUsername(), this._config.getPassword()
               );
            } catch (Throwable var11) {
               this.logEvent(4292459735430940092L, 1398042211, e.getMessage(), 3);
               this._tunnelId = -1;
               break label77;
            }

            if (this._tunnelId < 0) {
               int error = this._tunnelId;
               this._tunnelId = -1;
               this.actionBackoff(error);
            } else if (RadioInfo.isPDPContextActive(this._tunnelId)) {
               this.actionActive();
            } else {
               this._waitTmo = this._config.getActivateTimeout();
               this._waitState = 3;
               this._nextAction = 5;
               this._nextData = 0;
            }
         }
      }
   }

   private final void actionBackoff(int code) {
      this.logEvent(4292459735430940092L, 1398042211, code, 3);
      if (this._activationAttempts >= this._config.getMaxAttempts()) {
         this.actionCritical(1);
      } else {
         this.updateStatus(1, 0);
         this._waitTmo = this._config.getBackoff(this._activationAttempts > 0 ? this._activationAttempts - 1 : 0);
         this._waitState = 4;
         this._nextAction = 4;
      }
   }

   private final void actionStandby() {
      this.updateStatus(1, 0);
      if (this._tunnelId < 0) {
         this.actionDormant();
      } else {
         EventLogger.logEvent(4292459735430940092L, 1414423653, 0);

         label24:
         try {
            RadioInternal.deregisterAccessPointNumber(this._tunnelId);
         } finally {
            break label24;
         }

         this.updateStatus(7, 0);
         this._waitTmo = this._config.getDeactivateTimeout();
         this._waitState = 5;
         this._nextAction = 10;
      }
   }

   private final void actionDormant() {
      this.updateStatus(7, 0);
      this._tunnelId = -1;
      ProtocolDaemon.getInstance().submitRunnable(new TunnelManagerImpl$1(this));
      this._waitTmo = 0;
      this._waitState = 5;
      this._nextAction = 1;
   }

   private final void actionOff() {
      if (this._tunnelId >= 0) {
         this.updateStatus(1, 0);
         EventLogger.logEvent(4292459735430940092L, 1414423653, 0);

         label24:
         try {
            RadioInternal.deregisterAccessPointNumber(this._tunnelId);
         } finally {
            break label24;
         }

         this._tunnelId = -1;
         this.updateStatus(7, 0);
      }

      this._waitTmo = 0;
      this._waitState = 6;
      this._nextAction = 1;
   }

   private final void actionActive() {
      EventLogger.logEvent(4292459735430940092L, 1398038883, 0);
      this.updateStatus(3, 0);
      this._waitTmo = this._config.getUpdateTimeout();
      this._waitState = 2;
      this._nextAction = 1;
   }

   private final void actionInactive() {
      EventLogger.logEvent(4292459735430940092L, 1398040942, 0);
      this.updateStatus(5, 0);
      this._waitTmo = this._config.getUpdateTimeout();
      this._waitState = 1;
      this._nextAction = 1;
   }

   private final void actionCritical(int code) {
      this.logEvent(4292459735430940092L, 1398039410, code, 2);
      this.updateStatus(4, code);
      this._waitTmo = this._config.getUpdateTimeout();
      this._waitState = 1;
      this._nextAction = 1;
   }

   private final void updateStatus(int status, int code) {
      EventLogger.logEvent(4292459735430940092L, 1398043952 | status, 5);

      for (int i = this._tunnels.size() - 1; i >= 0; i--) {
         WeakReference w = (WeakReference)this._tunnels.elementAt(i);
         Tunnel tunnel;
         if (w == null || (tunnel = (Tunnel)w.get()) == null) {
            this._tunnels.removeElementAt(i);
         } else if (status != 0 && status != tunnel.getStatus()) {
            TunnelListener listener = tunnel.getConfig().getListener();
            if (listener != null) {
               listener.statusChanged(status, code);
            }
         }
      }

      if (status != 0) {
         this._status = status;
      }

      if (this._tunnels.size() == 0) {
         throw new DeactivateTunnelException();
      }
   }

   private final void logEvent(long guid, int value, String str, int level) {
      int length = str != null ? str.length() : 0;
      byte[] buf = new byte[5 + length];
      DatagramAddressBase.writeInt(buf, 0, value);
      buf[4] = 45;

      for (int i = 0; i < length; i++) {
         buf[5 + i] = (byte)str.charAt(i);
      }

      EventLogger.logEvent(guid, buf, level);
   }

   private final void logEvent(long guid, int value, int code, int level) {
      byte[] buf = new byte[13];
      DatagramAddressBase.writeInt(buf, 0, value);
      buf[4] = 45;
      DatagramAddressBase.appendHex(buf, 5, code, 8);
      EventLogger.logEvent(guid, buf, level);
   }
}
