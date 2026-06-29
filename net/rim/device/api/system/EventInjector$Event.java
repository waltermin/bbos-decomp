package net.rim.device.api.system;

import net.rim.vm.Message;

public class EventInjector$Event {
   protected Message _msg;
   private static ApplicationManagerImpl _ami = (ApplicationManagerImpl)ApplicationManager.getApplicationManager();

   EventInjector$Event(int device, int event, int subMessage, int data0, int data1, Object o1, Object o2) {
      this._msg = new Message(device, event, subMessage, data0, data1);
      this._msg.setObject0(o1);
      this._msg.setObject1(o2);
   }

   public void post() {
      EventInjector.assertPermission();
      _ami.postMessageToForegroundProcess(this._msg);
   }

   public void setEvent(int event) {
      this._msg.setEvent(event);
   }

   public int getEvent() {
      return this._msg.getEvent();
   }

   public void setStatus(int status) {
      this._msg.setData0(status);
   }

   public int getStatus() {
      return this._msg.getData0();
   }

   int getDevice() {
      return this._msg.getDevice();
   }
}
