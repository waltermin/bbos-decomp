package net.rim.device.cldc.impl.network;

import net.rim.device.api.network.NetworkInterfaceFactory;

public final class Registration {
   public static final void NetworkMain(String[] args) {
      NetworkInterfaceFactory.getInstance();
      DefaultNetworkInterfaceManager.init();
   }
}
