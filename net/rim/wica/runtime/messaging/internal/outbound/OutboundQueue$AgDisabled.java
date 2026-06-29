package net.rim.wica.runtime.messaging.internal.outbound;

final class OutboundQueue$AgDisabled extends OutboundQueue$State {
   @Override
   final boolean onAgEnabled(OutboundQueue q) {
      q._state = OutboundQueue._onEnabledBridge;
      return true;
   }

   @Override
   final boolean isDisabled() {
      return true;
   }
}
