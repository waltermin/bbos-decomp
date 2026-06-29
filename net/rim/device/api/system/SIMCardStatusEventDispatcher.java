package net.rim.device.api.system;

import net.rim.device.internal.system.EventDispatcher;
import net.rim.vm.Message;

final class SIMCardStatusEventDispatcher extends EventDispatcher {
   @Override
   public final void dispatch(Message message, Object listener) {
      SIMCardStatusListener statusListener = (SIMCardStatusListener)listener;
      int event = message.getEvent();
      int subMessage = message.getSubMessage();
      int data0 = message.getData0();
      switch (event) {
         case 2304:
            if (subMessage == 0) {
               statusListener.cardInserted();
               return;
            }

            statusListener.cardReady();
            return;
         case 2305:
            statusListener.cardInvalid(subMessage, data0);
            return;
         case 2307:
            statusListener.cardFault(subMessage);
            return;
         case 2315:
            statusListener.smsEFFull();
            return;
         case 2323:
            statusListener.cardUpdated();
            return;
         case 2324:
            statusListener.responseDeleteSMS(subMessage, data0);
            return;
         case 2342:
            statusListener.responseMarkSMSAsRead(subMessage, data0);
      }
   }
}
