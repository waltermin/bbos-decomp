package net.rim.device.apps.internal.manageconnections;

import net.rim.device.api.servicebook.ServiceRoutingListener2;

final class MyServiceRoutingListener2 implements ServiceRoutingListener2 {
   ConnectionsPopupScreen _screen;

   MyServiceRoutingListener2(ConnectionsPopupScreen screen) {
      this._screen = screen;
   }

   @Override
   public final void serviceRouteStateChanged(int routeHandle, boolean routeState) {
      this._screen.updateLater();
   }

   @Override
   public final void serviceRoutingStateChanged(String service, boolean serviceState) {
   }

   @Override
   public final void serviceRoutingCapabilitiesChanged(String service) {
   }
}
