package net.rim.device.internal.system;

import net.rim.vm.Message;

final class SmartCardPortEventDispatcher extends EventDispatcher {
   @Override
   public final void dispatch(Message message, Object listener) {
      if (listener instanceof SmartCardPortListener) {
         int event = message.getEvent();
         int subMessage = message.getSubMessage();
         SmartCardPortListener scListener = (SmartCardPortListener)listener;
         switch (event) {
            case 4097:
               scListener.dataReceived();
               return;
            case 4098:
               scListener.dataSent();
               return;
            case 4099:
               if (subMessage == 4101) {
                  scListener.cardRemoved();
                  return;
               }

               if (subMessage == 4100) {
                  scListener.cardInserted();
                  return;
               }

               if (subMessage == 4102) {
                  scListener.openSuccessful();
                  return;
               }

               if (subMessage == 4107) {
                  scListener.setProtocolSuccessful();
                  return;
               }
               break;
            case 4103:
               if (subMessage == 4104) {
                  scListener.openError(message.getData0());
               }

               if (subMessage == 4108) {
                  scListener.setProtocolError();
               }
         }
      }
   }
}
