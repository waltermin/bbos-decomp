package net.rim.device.apps.internal.sms;

import net.rim.device.api.notification.NotificationsManager;

class SMSService$2 implements Runnable {
   private final SMSModel val$model;
   private final SMSService this$0;

   SMSService$2(SMSService _1, SMSModel _2) {
      this.this$0 = _1;
      this.val$model = _2;
   }

   @Override
   public void run() {
      long id = this.val$model.getUID();
      long type = 7986617465467730856L;
      NotificationsManager.cancelImmediateEvent(type, id, null, this.this$0._immediateContext);
   }
}
