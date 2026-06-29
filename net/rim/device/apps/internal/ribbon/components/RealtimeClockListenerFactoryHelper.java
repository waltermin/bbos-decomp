package net.rim.device.apps.internal.ribbon.components;

import net.rim.device.api.system.RealtimeClockListener;

public class RealtimeClockListenerFactoryHelper extends WeakReferenceCollectionUpdater implements RealtimeClockListener {
   @Override
   protected void updateComponent(Object component) {
      ((RealtimeClockListener)component).clockUpdated();
   }

   @Override
   public void clockUpdated() {
      this.doUpdates();
   }
}
