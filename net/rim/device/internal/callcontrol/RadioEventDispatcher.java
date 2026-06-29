package net.rim.device.internal.callcontrol;

import net.rim.device.api.system.PhoneCallListener;
import net.rim.device.api.system.PhoneControlListener;
import net.rim.device.api.system.PhoneTimerListener;
import net.rim.device.internal.system.EventDispatcher;
import net.rim.device.internal.system.Events;
import net.rim.vm.Message;

class RadioEventDispatcher extends EventDispatcher {
   @Override
   public void dispatch(Message message, Object listener) {
      int event = message.getEvent();
      int subMessage = message.getSubMessage();
      int data0 = message.getData0();
      int data1 = message.getData1();
      Object object0 = message.getObject0();
      if (!Events.dispatchPhoneCallEvent(event, subMessage, data0, (PhoneCallListener)listener)) {
         if (!Events.dispatchPhoneTimerEvent(event, subMessage, data0, (PhoneTimerListener)listener)) {
            if (!Events.dispatchPhoneControlEvent(event, subMessage, data0, data1, object0, (PhoneControlListener)listener)) {
               ;
            }
         }
      }
   }
}
