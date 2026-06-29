package net.rim.device.api.system;

import net.rim.device.internal.system.EventDispatcher;
import net.rim.vm.Message;

final class AudioRouterEventDispatcher extends EventDispatcher {
   @Override
   public final void dispatch(Message message, Object listener) {
      int event = message.getEvent();
      AudioRouterListener arListener = (AudioRouterListener)listener;
      switch (event) {
         case 1:
         default:
            arListener.audioVolumeChanged(message.getSubMessage() != 0);
            return;
         case 2:
            arListener.audioSinkChanged();
            return;
         case 3:
            arListener.audioSourceChanged();
         case 0:
      }
   }
}
