package net.rim.device.api.system;

import net.rim.device.internal.system.EventDispatcher;
import net.rim.vm.Message;

final class SIMCardPBEventDispatcher extends EventDispatcher {
   @Override
   public final void dispatch(Message message, Object listener) {
      SIMCardPhoneBookListener pbListener = (SIMCardPhoneBookListener)listener;
      int event = message.getEvent();
      int subMessage = message.getSubMessage();
      int data0 = message.getData0();
      int data1 = message.getData1();
      switch (subMessage) {
         case 0:
         default:
            pbListener.responsePhoneBookRead(event == 2344);
            return;
         case 1:
            pbListener.responsePhoneBookWrite(event == 2344, data0, data1);
            return;
         case 2:
            pbListener.responsePhoneBookDelete(event == 2344, data0);
         case -1:
      }
   }
}
