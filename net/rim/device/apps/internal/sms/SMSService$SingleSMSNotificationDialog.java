package net.rim.device.apps.internal.sms;

import net.rim.device.apps.api.framework.model.ContextObject;

class SMSService$SingleSMSNotificationDialog extends SMSNotificationDialog {
   private final SMSService this$0;

   public SMSService$SingleSMSNotificationDialog(
      SMSService _1, SMSModel model, long folderId, String text, ContextObject notificationContext, long notificationID
   ) {
      super(model, folderId, text, notificationContext, notificationID);
      this.this$0 = _1;
   }

   @Override
   public void close() {
      super.close();
      this.this$0._voicemailMessageDialog = null;
   }
}
