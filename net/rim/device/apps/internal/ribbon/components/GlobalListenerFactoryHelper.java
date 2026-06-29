package net.rim.device.apps.internal.ribbon.components;

import net.rim.device.api.system.GlobalEventListener;

public class GlobalListenerFactoryHelper extends WeakReferenceCollectionUpdater implements GlobalEventListener {
   private long _guid;

   @Override
   protected void updateComponent(Object component) {
      ((GlobalEventListener)component).eventOccurred(this._guid, -1, -1, null, null);
   }

   @Override
   public void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      this._guid = guid;
      this.doUpdates();
   }
}
