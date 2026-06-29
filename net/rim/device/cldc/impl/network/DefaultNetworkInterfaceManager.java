package net.rim.device.cldc.impl.network;

import net.rim.device.api.network.NetworkInterfaceFactory;
import net.rim.device.api.network.NetworkInterfaceListener;
import net.rim.device.api.network.NetworkInterfaceManager;
import net.rim.device.api.system.WLAN;

final class DefaultNetworkInterfaceManager extends NetworkInterfaceManager implements NetworkInterfaceListener {
   private NetworkInterfaceManager _subManager;
   private static final long GUID;
   protected static final String NAME;

   protected static final void init() {
      NetworkInterfaceFactory.getInstance().registerManager(new DefaultNetworkInterfaceManager());
   }

   private DefaultNetworkInterfaceManager() {
      super("default", -8914838971140472771L, null);
      if (WLAN.isSupported()) {
         DefaultWlanNetworkInterfaceManager.init();
         this._subManager = NetworkInterfaceFactory.getInstance().getManager("default-wlan");
         this._subManager.addListener(this);
      }
   }

   @Override
   public final void networkInterfaceStatusChanged(boolean status, Object context, NetworkInterfaceManager manager) {
      this.invokeListeners(status, context, false);
   }

   @Override
   public final void networkInterfaceEvent(int event, Object context, NetworkInterfaceManager manager) {
      this.invokeListeners(event, context, false);
   }
}
