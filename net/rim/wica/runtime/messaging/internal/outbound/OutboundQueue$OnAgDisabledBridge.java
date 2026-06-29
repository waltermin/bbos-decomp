package net.rim.wica.runtime.messaging.internal.outbound;

final class OutboundQueue$OnAgDisabledBridge extends OutboundQueue$State {
   @Override
   final boolean onAgEnabled(OutboundQueue q) {
      q._state = OutboundQueue._enabled;
      return false;
   }

   @Override
   final void onRun(OutboundQueue q) {
      q._state = OutboundQueue._agDisabled;
   }

   @Override
   final boolean isDisabledBridge() {
      return true;
   }
}
