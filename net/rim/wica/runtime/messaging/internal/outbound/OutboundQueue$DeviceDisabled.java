package net.rim.wica.runtime.messaging.internal.outbound;

final class OutboundQueue$DeviceDisabled extends OutboundQueue$State {
   @Override
   final boolean onAgEnabled(OutboundQueue q) {
      q._state = OutboundQueue._onEnabledBridge;
      return true;
   }

   @Override
   final boolean onDeviceEnabled(OutboundQueue q) {
      q._state = OutboundQueue._onEnabledBridge;
      return true;
   }

   @Override
   final boolean onAgDisabled(OutboundQueue q) {
      q._state = OutboundQueue._agDisabled;
      return false;
   }

   @Override
   final boolean isDisabled() {
      return true;
   }
}
