package net.rim.device.cldc.impl.network;

import net.rim.device.api.network.NetworkInterfaceFactory;
import net.rim.device.api.network.NetworkInterfaceManager;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.system.WLAN;
import net.rim.device.api.system.WLANListenerInternal;
import net.rim.device.cldc.io.tunnel.Tunnel;
import net.rim.device.cldc.io.tunnel.TunnelConfig;
import net.rim.device.internal.io.tunnel.TunnelWorker;
import net.rim.device.internal.proxy.Proxy;

final class WlanNetworkInterfaceManager extends NetworkInterfaceManager implements WLANListenerInternal {
   private boolean _radioStatus;
   private boolean _networkStatus;
   private boolean _interfaceStatus;
   private byte[] _ipAddress;
   private static final long GUID = 833896789245248843L;
   protected static final String NAME = "wlan";
   private static final int STALE_RADIO_STATE = 1400132164;
   private static final int STALE_NETWORK_STATE = 1400131156;
   private static final int STALE_INTERFACE_STATE = 1400129862;
   private static final int NO_IP_ADDRESS = 1315916112;
   private static final int STALE_INTERFACE_INFO = 1400129865;
   private static final int EXTRA_INTERFACE_INFO = 1165510985;

   public static final void init() {
      NetworkInterfaceFactory.getInstance().registerManager(new WlanNetworkInterfaceManager());
   }

   private WlanNetworkInterfaceManager() {
      super("wlan", 833896789245248843L, null);
      boolean inBootUp = this.systemBootingUp();
      this._radioStatus = inBootUp ? false : WLAN.isRadioOn();
      this._networkStatus = inBootUp ? false : WLAN.isAssociated() != null;
      this._ipAddress = this.verifyIpAddress(inBootUp ? null : this.getIpAddress());
      this._interfaceStatus = this._ipAddress != null;
      if (!inBootUp) {
         this.updateStatus();
      }

      Proxy.getInstance().addRadioListener(this);
   }

   public final void networkSuccessInternal() {
      if (!this._networkStatus) {
         this._networkStatus = true;
      } else {
         EventLogger.logEvent(833896789245248843L, 1400131156, 3);
      }
   }

   public final void networkFailInternal(int status) {
      if (this._networkStatus) {
         this._networkStatus = false;
      } else {
         EventLogger.logEvent(833896789245248843L, 1400131156, 3);
      }
   }

   @Override
   public final void radioStatus(boolean started) {
      if (this._radioStatus != started) {
         this._radioStatus = started;
         this.updateStatus();
      } else {
         EventLogger.logEvent(833896789245248843L, 1400132164, 3);
      }
   }

   @Override
   public final void networkApChange() {
   }

   @Override
   public final void networkFound(int handle) {
   }

   @Override
   public final void networkSuccess() {
      this.networkSuccessInternal();
      this._ipAddress = this.verifyIpAddress(this.getIpAddress());
      if (this._ipAddress == null) {
         EventLogger.logEvent(833896789245248843L, 1315916112, 3);
      }

      if (!this._interfaceStatus) {
         this._interfaceStatus = true;
         this.updateStatus();
      } else {
         EventLogger.logEvent(833896789245248843L, 1400129862, 3);
      }
   }

   @Override
   public final void networkFail(int status, int error, int extendedInfo) {
      this.networkFailInternal(status);
      this._ipAddress = null;
      if (this._interfaceStatus) {
         this._interfaceStatus = false;
         this.updateStatus();
      } else {
         EventLogger.logEvent(833896789245248843L, 1400129862, 3);
      }
   }

   private final void updateStatus() {
      boolean status = this._radioStatus && this._networkStatus && this._interfaceStatus;
      if (super._status != status) {
         super._status = status;
         this.invokeListeners(status, null, true);
      }
   }

   private final boolean systemBootingUp() {
      ApplicationManager mgr = ApplicationManager.getApplicationManager();
      return mgr != null && mgr.inStartup();
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final byte[] getIpAddress() {
      byte[] addr = null;
      Tunnel t = null;
      boolean var8 = false /* VF: Semaphore variable */;
      boolean var11 = false /* VF: Semaphore variable */;

      label91: {
         try {
            label85:
            try {
               var11 = true;
               var8 = true;
               int e = RadioInfo.getAccessPointNumber(WLAN.WLAN_PSEUDO_APN);
               if (!RadioInfo.isPDPContextActive(e)) {
                  TunnelWorker tw = (TunnelWorker)(new Object());
                  t = tw.open((TunnelConfig)(new Object(WLAN.WLAN_PSEUDO_APN, "net.rim.wlaniface", null, null, null, tw)));
               }

               addr = RadioInfo.getIPAddress(e);
               var8 = false;
               var11 = false;
               break label91;
            } finally {
               if (var11) {
                  addr = null;
                  var8 = false;
                  break label85;
               }
            }
         } finally {
            if (var8) {
               if (t != null) {
                  t.close();
                  Tunnel var14 = null;
               }

               return addr;
            }
         }

         if (t != null) {
            t.close();
            Tunnel var15 = null;
         }

         return addr;
      }

      if (t != null) {
         t.close();
         Tunnel var16 = null;
      }

      return addr;
   }

   private final byte[] verifyIpAddress(byte[] ipAddress) {
      return ipAddress != null && ipAddress[0] != 0 && ipAddress[1] != 0 && ipAddress[2] != 0 && ipAddress[3] != 0 ? ipAddress : null;
   }
}
