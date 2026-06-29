package net.rim.device.api.system;

import net.rim.device.internal.system.EventDispatcher;
import net.rim.vm.Message;

final class SIMCardEFEventDispatcher extends EventDispatcher {
   @Override
   public final void dispatch(Message message, Object listener) {
      SIMCardEFListener efListener = (SIMCardEFListener)listener;
      int event = message.getEvent();
      int subMessage = message.getSubMessage();
      int data0 = message.getData0();
      int data1 = message.getData1();
      switch (event) {
         case 2316:
            efListener.responseEFRead(0, subMessage, data0 & 65535, data1 & 65535, data1 >> 16);
            return;
         case 2317:
         default:
            efListener.responseEFInfo(0, subMessage & 65535, subMessage >> 16, data0 & 65535, data0 >> 16, data1 & 65535, data1 >> 16);
            return;
         case 2318:
            efListener.responseEFInfo(data0, subMessage & 65535, subMessage >> 16, -1, -1, -1, -1);
            return;
         case 2319:
            efListener.responseEFRead(data0, subMessage, -1, -1, -1);
            return;
         case 2320:
            efListener.responseEFWrite(0, subMessage, data0, data1);
            return;
         case 2321:
            efListener.responseEFWrite(data0, subMessage, -1, -1);
         case 2315:
      }
   }
}
