package net.rim.wica.runtime.messaging.internal.outbound;

final class OutboundQueue$OnDeviceDisabledBridge extends OutboundQueue$State {
   @Override
   final boolean onAgEnabled(OutboundQueue q) {
      q._state = OutboundQueue._enabled;
      return false;
   }

   @Override
   final boolean onDeviceEnabled(OutboundQueue q) {
      q._state = OutboundQueue._enabled;
      return false;
   }

   @Override
   final boolean onAgDisabled(OutboundQueue q) {
      q._state = OutboundQueue._onAgDisabledBridge;
      return false;
   }

   @Override
   final void onRun(OutboundQueue q) {
      q._state = OutboundQueue._deviceDisabled;
   }

   @Override
   final boolean isDisabledBridge() {
      return true;
   }
}
