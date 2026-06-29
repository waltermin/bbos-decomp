package net.rim.wica.runtime.messaging.internal.outbound;

final class OutboundQueue$Enabled extends OutboundQueue$State {
   @Override
   final boolean onAgDisabled(OutboundQueue q) {
      q._state = OutboundQueue._onAgDisabledBridge;
      return true;
   }

   @Override
   final boolean onDeviceDisabled(OutboundQueue q) {
      q._state = OutboundQueue._onDeviceDisabledBridge;
      return true;
   }

   @Override
   final boolean isEnabled() {
      return true;
   }
}
