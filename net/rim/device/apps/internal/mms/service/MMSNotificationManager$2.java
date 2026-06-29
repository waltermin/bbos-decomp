package net.rim.device.apps.internal.mms.service;

import net.rim.device.api.notification.NotificationsManager;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.internal.mms.api.MMSMessageModel;

class MMSNotificationManager$2 implements Runnable {
   private final MMSMessageModel val$model;

   MMSNotificationManager$2(MMSMessageModel _1) {
      this.val$model = _1;
   }

   @Override
   public void run() {
      ContextObject notificationContext = (ContextObject)(new Object());
      notificationContext.putIntegerData(0);
      NotificationsManager.cancelImmediateEvent(8609386677418041260L, this.val$model.getUID(), null, notificationContext);
   }
}
