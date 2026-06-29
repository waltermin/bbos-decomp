package net.rim.device.internal.media;

import net.rim.device.internal.system.EventDispatcher;
import net.rim.vm.Message;

final class MediaRemoteControlDispatcher extends EventDispatcher {
   @Override
   public final void dispatch(Message message, Object listener) {
      MediaRemoteControlListener mListener = (MediaRemoteControlListener)listener;
      mListener.mediaPanelEvent(message.getEvent(), message.getSubMessage());
   }
}
