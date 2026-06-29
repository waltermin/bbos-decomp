package net.rim.device.apps.internal.blackberryemail.email.api;

import net.rim.device.api.notification.NotificationsManager;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.system.EventLogger;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.messaging.messagelist.AutoHolsterViewerListener;
import net.rim.device.apps.api.transmission.rim.CMIMEUtilities;
import net.rim.device.apps.internal.blackberryemail.email.EmailMessageModel;

class EmailMessageUtilities$1 implements Runnable {
   private final EmailMessageModel val$model;
   private final Object val$deferredContext;
   private final ContextObject val$immediateContext;

   EmailMessageUtilities$1(EmailMessageModel _1, Object _2, ContextObject _3) {
      this.val$model = _1;
      this.val$deferredContext = _2;
      this.val$immediateContext = _3;
   }

   @Override
   public void run() {
      EventLogger.logEvent(-1237457833540244999L, 1314148935, 5);
      long timeLimit = System.currentTimeMillis() + 60000;
      long id = this.val$model.getCMIMEReferenceIdentifier();
      int trigger = 0;
      long type = -1845850106795451018L;
      ServiceRecord sr = this.val$model.getServiceRecordForMessage();
      type = CMIMEUtilities.getProfileSourceIDForService(sr);
      if (EmailMessageUtilities.treatAsLevel1(this.val$model)) {
         type = -327746170160875990L;
      }

      if ((this.val$model.getFlags() & 524288) != 0) {
         type = 6432934947797527350L;
      }

      NotificationsManager.negotiateDeferredEvent(type, id, this.val$model, timeLimit, trigger, this.val$deferredContext);
      AutoHolsterViewerListener.getInstance().commenceOperation(timeLimit);
      NotificationsManager.triggerImmediateEvent(type, id, null, this.val$immediateContext);
   }
}
