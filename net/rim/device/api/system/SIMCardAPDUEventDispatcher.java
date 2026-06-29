package net.rim.device.api.system;

import net.rim.device.internal.system.EventDispatcher;
import net.rim.vm.Message;

final class SIMCardAPDUEventDispatcher extends EventDispatcher {
   @Override
   public final void dispatch(Message message, Object listener) {
      SIMCardAPDUListener apduListener = (SIMCardAPDUListener)listener;
      int event = message.getEvent();
      int subMessage = message.getSubMessage();
      int data0 = message.getData0();
      int data1 = message.getData1();
      int length = message.getDataLength();
      switch (event) {
         case 2347:
            break;
         case 2348:
         default:
            switch (subMessage) {
               case -1:
                  return;
               case 0:
               default:
                  apduListener.openSuccessful((byte)data0, (byte)data1);
                  return;
               case 1:
               case 2:
               case 3:
               case 4:
                  apduListener.openError((byte)subMessage, (byte)data1);
                  return;
            }
         case 2349:
            switch (subMessage) {
               case -1:
                  return;
               case 0:
               default:
                  apduListener.exchangeAPDUSuccessful((byte)data0, (byte)data1);
                  return;
               case 1:
               case 2:
               case 3:
               case 4:
               case 5:
               case 6:
               case 7:
                  apduListener.exchangeAPDUError((byte)data0, (byte)subMessage, (byte)data1);
                  return;
            }
         case 2350:
            switch (subMessage) {
               case -1:
                  return;
               case 0:
               default:
                  apduListener.closeSuccessful((byte)data0, (byte)data1);
                  return;
               case 1:
               case 2:
                  apduListener.closeError((byte)data0, (byte)subMessage, (byte)data1);
                  return;
            }
         case 2351:
            switch (subMessage) {
               case 0:
                  apduListener.pinOpeartionSuccessful((byte)length, data1, (byte)data0);
                  return;
               case 1:
               case 5:
                  apduListener.pinOperationUnSuccessful((byte)length, data1, (byte)subMessage, (byte)data0);
            }
      }
   }
}
