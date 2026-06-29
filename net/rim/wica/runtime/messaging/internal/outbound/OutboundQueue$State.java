package net.rim.wica.runtime.messaging.internal.outbound;

class OutboundQueue$State {
   boolean onAgEnabled(OutboundQueue q) {
      return false;
   }

   boolean onDeviceEnabled(OutboundQueue q) {
      return false;
   }

   boolean onAgDisabled(OutboundQueue q) {
      return false;
   }

   boolean onDeviceDisabled(OutboundQueue q) {
      return false;
   }

   void onRun(OutboundQueue q) {
   }

   boolean isEnabled() {
      return false;
   }

   boolean isEnabledBridge() {
      return false;
   }

   boolean isDisabled() {
      return false;
   }

   boolean isDisabledBridge() {
      return false;
   }

   void onUnregister(OutboundQueue q) {
      q._state = OutboundQueue._unregistered;
   }
}
