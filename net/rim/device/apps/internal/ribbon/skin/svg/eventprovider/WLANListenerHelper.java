package net.rim.device.apps.internal.ribbon.skin.svg.eventprovider;

import net.rim.device.api.system.WLANListenerInternal;
import net.rim.device.apps.internal.ribbon.components.WeakReferenceCollectionUpdater;

class WLANListenerHelper extends WeakReferenceCollectionUpdater implements WLANListenerInternal {
   private int _eventType;
   private boolean _started;
   private static final int TYPE_RADIO_STATUS = 0;
   private static final int TYPE_INTERFACE_SUCCESS = 1;
   private static final int TYPE_INTERFACE_FAIL = 2;

   @Override
   protected void updateComponent(Object component) {
      switch (this._eventType) {
         case 0:
         default:
            ((WLANListenerInternal)component).radioStatus(this._started);
            return;
         case 1:
            ((WLANListenerInternal)component).networkSuccess();
            return;
         case 2:
            ((WLANListenerInternal)component).networkFail(-1, -1, -1);
         case -1:
      }
   }

   @Override
   public void radioStatus(boolean started) {
      this._eventType = 0;
      this._started = started;
      this.doUpdates();
   }

   @Override
   public void networkSuccess() {
      this._eventType = 1;
      this.doUpdates();
   }

   @Override
   public void networkFail(int status, int error, int extendedInfo) {
      this._eventType = 2;
      this.doUpdates();
   }

   @Override
   public void networkFound(int handle) {
   }

   @Override
   public void networkApChange() {
   }
}
