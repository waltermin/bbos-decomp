package net.rim.wica.runtime.messaging.internal.outbound;

final class OutboundQueue$OnEnabledBridge extends OutboundQueue$State {
   @Override
   final boolean onAgDisabled(OutboundQueue q) {
      q._state = OutboundQueue._agDisabled;
      return false;
   }

   @Override
   final boolean onDeviceDisabled(OutboundQueue q) {
      q._state = OutboundQueue._deviceDisabled;
      return false;
   }

   @Override
   final void onRun(OutboundQueue q) {
      q._state = OutboundQueue._enabled;
   }

   @Override
   final boolean isEnabledBridge() {
      return true;
   }
}
