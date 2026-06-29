package net.rim.device.cldc.impl.network;

import net.rim.device.api.network.NetworkInterfaceFactory;
import net.rim.device.api.network.NetworkInterfaceListener;
import net.rim.device.api.network.NetworkInterfaceManager;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.WLAN;
import net.rim.device.internal.crypto.vpn.VPN;

final class DefaultWlanNetworkInterfaceManager extends NetworkInterfaceManager implements NetworkInterfaceListener {
   private NetworkInterfaceManager _wlanManager;
   private NetworkInterfaceManager _vpnManager;
   private boolean _wlanStatus;
   private boolean _vpnStatus;
   private static final long GUID = 7400008644286649816L;
   protected static final String NAME = "default-wlan";
   private static final int STALE_VPN_ENABLED_STATE = 1400133189;
   private static final int STALE_WLAN_STATE = 1400133452;
   private static final int STALE_VPN_STATE = 1400133200;

   protected static final void init() {
      NetworkInterfaceFactory.getInstance().registerManager(new DefaultWlanNetworkInterfaceManager());
   }

   private DefaultWlanNetworkInterfaceManager() {
      super("default-wlan", 7400008644286649816L, null);
      WlanNetworkInterfaceManager.init();
      this._wlanManager = NetworkInterfaceFactory.getInstance().getManager("wlan");
      VpnNetworkInterfaceManager.init();
      this._vpnManager = NetworkInterfaceFactory.getInstance().getManager("vpn");
      this._wlanStatus = this._wlanManager.getStatus();
      this._vpnStatus = this._vpnManager.getStatus();
      this._wlanManager.addListener(this);
      this._vpnManager.addListener(this);
   }

   @Override
   public final void networkInterfaceStatusChanged(boolean status, Object context, NetworkInterfaceManager manager) {
      if (manager == this._wlanManager) {
         if (this._wlanStatus != status) {
            this._wlanStatus = status;
            this.updateStatus(false);
         } else {
            EventLogger.logEvent(7400008644286649816L, 1400133452, 3);
         }
      } else if (this._vpnStatus != status) {
         this._vpnStatus = status;
         this.updateStatus(false);
      } else {
         EventLogger.logEvent(7400008644286649816L, 1400133200, 3);
      }
   }

   @Override
   public final void networkInterfaceEvent(int event, Object context, NetworkInterfaceManager manager) {
      this.invokeListeners(event, context, false);
   }

   private final void updateStatus(boolean redirect) {
      boolean status = this._wlanStatus && (!VPN.isIPSecRequiredForNetwork(WLAN.isAssociated(), 6) || this._vpnStatus);
      if (super._status != status) {
         super._status = status;
         this.invokeListeners(status, null, redirect);
      }
   }
}
