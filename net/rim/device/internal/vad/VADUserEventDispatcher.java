package net.rim.device.internal.vad;

import net.rim.device.internal.system.EventDispatcher;
import net.rim.vm.Message;

final class VADUserEventDispatcher extends EventDispatcher {
   @Override
   public final void dispatch(Message message, Object listener) {
      VADUserEventListener vadListener = (VADUserEventListener)listener;
      vadListener.vadEvent(message.getEvent(), message.getSubMessage());
   }
}
