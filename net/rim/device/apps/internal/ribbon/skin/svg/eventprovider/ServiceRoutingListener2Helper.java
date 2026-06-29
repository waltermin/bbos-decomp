package net.rim.device.apps.internal.ribbon.skin.svg.eventprovider;

import net.rim.device.api.servicebook.ServiceRoutingListener2;
import net.rim.device.apps.internal.ribbon.components.WeakReferenceCollectionUpdater;

class ServiceRoutingListener2Helper extends WeakReferenceCollectionUpdater implements ServiceRoutingListener2 {
   int _routeHandle;
   boolean _routeState;

   @Override
   protected void updateComponent(Object component) {
      ((ServiceRoutingListener2)component).serviceRouteStateChanged(this._routeHandle, this._routeState);
   }

   @Override
   public void serviceRouteStateChanged(int routeHandle, boolean routeState) {
      this._routeHandle = routeHandle;
      this._routeState = routeState;
      this.doUpdates();
   }

   @Override
   public void serviceRoutingStateChanged(String service, boolean serviceState) {
   }

   @Override
   public void serviceRoutingCapabilitiesChanged(String service) {
   }
}
