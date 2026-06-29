package net.rim.device.internal.system;

import net.rim.vm.Message;

public class EventDispatcher {
   public boolean notify(Message message) {
      return true;
   }

   public int getNotifyProcessId(Message message) {
      return -1;
   }

   public void dispatch(Message _1, Object _2) {
      throw null;
   }
}
