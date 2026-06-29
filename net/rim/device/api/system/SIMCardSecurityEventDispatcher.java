package net.rim.device.api.system;

import net.rim.device.internal.system.EventDispatcher;
import net.rim.vm.Message;

final class SIMCardSecurityEventDispatcher extends EventDispatcher {
   @Override
   public final void dispatch(Message message, Object listener) {
      SIMCardSecurityListener securityListener = (SIMCardSecurityListener)listener;
      int event = message.getEvent();
      int subMessage = message.getSubMessage();
      int data0 = message.getData0();
      int data1 = message.getData1();
      switch (event) {
         case 1632:
            securityListener.wtlsKeyWriteComplete(subMessage);
            break;
         case 2306:
            if (subMessage == 1) {
               securityListener.requestSendPUK(data1);
               return;
            }

            securityListener.requestSendPIN(data0);
            return;
         case 2308:
         case 2309:
            switch (subMessage) {
               case -1:
                  return;
               case 0:
                  securityListener.responseValidatePIN(data0 >> 8, data1 >> 16, data1 & 0xFF);
                  return;
               case 1:
                  securityListener.responseChangePIN(data0 >> 8, data1 >> 16, data1 & 0xFF);
                  return;
               case 2:
                  securityListener.responseDisablePIN(data0 >> 8, data1 >> 16, data1 & 0xFF);
                  return;
               case 3:
               default:
                  securityListener.responseEnablePIN(data0 >> 8, data1 >> 16, data1 & 0xFF);
                  return;
               case 4:
                  securityListener.responseChangePIN(data0 >> 8, data1 >> 16, data1 >> 8 & 0xFF);
                  return;
            }
         case 2310:
            securityListener.pinValid();
            return;
         case 2311:
         case 2312:
            securityListener.responseDeactivateMEP(event == 2311);
            return;
      }
   }
}
