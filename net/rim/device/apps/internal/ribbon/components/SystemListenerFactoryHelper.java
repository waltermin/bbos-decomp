package net.rim.device.apps.internal.ribbon.components;

import net.rim.device.api.system.SystemListener;

public class SystemListenerFactoryHelper extends WeakReferenceCollectionUpdater implements SystemListener {
   @Override
   protected void updateComponent(Object component) {
      ((SystemListener)component).powerUp();
   }

   @Override
   public void batteryGood() {
   }

   @Override
   public void batteryLow() {
   }

   @Override
   public void batteryStatusChange(int status) {
   }

   @Override
   public void powerOff() {
   }

   @Override
   public void powerUp() {
      this.doUpdates();
   }
}
