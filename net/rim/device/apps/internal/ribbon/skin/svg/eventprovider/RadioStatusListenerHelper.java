package net.rim.device.apps.internal.ribbon.skin.svg.eventprovider;

import net.rim.device.api.system.RadioStatusListener;
import net.rim.device.apps.internal.ribbon.components.WeakReferenceCollectionUpdater;

class RadioStatusListenerHelper extends WeakReferenceCollectionUpdater implements RadioStatusListener {
   private int _eventType;
   private int _networkId;
   private int _service;
   private static final int TYPE_NETWORK_STARTED;
   private static final int TYPE_NETWORK_SERVICE_CHANGED;

   public void mobilityManagementEvent(int eventCode, int cause) {
   }

   @Override
   public void networkServiceChange(int networkId, int service) {
      this._eventType = 1;
      this._networkId = networkId;
      this._service = service;
      this.doUpdates();
   }

   @Override
   public void signalLevel(int level) {
   }

   @Override
   public void baseStationChange() {
   }

   @Override
   public void radioTurnedOff() {
   }

   @Override
   public void pdpStateChange(int apn, int state, int cause) {
   }

   @Override
   public void networkScanComplete(boolean success) {
   }

   @Override
   public void networkStarted(int networkId, int service) {
      this._eventType = 0;
      this._networkId = networkId;
      this._service = service;
      this.doUpdates();
   }

   @Override
   public void networkStateChange(int state) {
   }

   @Override
   protected void updateComponent(Object component) {
      switch (this._eventType) {
         case 0:
         default:
            ((RadioStatusListener)component).networkStarted(this._networkId, this._service);
            return;
         case 1:
            ((RadioStatusListener)component).networkServiceChange(this._networkId, this._service);
         case -1:
      }
   }
}
