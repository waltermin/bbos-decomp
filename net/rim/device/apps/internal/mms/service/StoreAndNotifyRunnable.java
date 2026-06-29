package net.rim.device.apps.internal.mms.service;

import net.rim.device.apps.api.ribbon.indicators.UnreadCountManager;
import net.rim.device.apps.internal.mms.MMSStorage;
import net.rim.device.apps.internal.mms.api.MMSMessageModel;

final class StoreAndNotifyRunnable implements Runnable {
   private MMSMessageModel _message;

   StoreAndNotifyRunnable(MMSMessageModel message) {
      this._message = message;
   }

   @Override
   public final void run() {
      run(this._message);
   }

   static final void run(MMSMessageModel message) {
      String transactionID = message.getPayload().getAttribute("x-mms-transaction-id");
      if (MMSStorage.findMessageByTransactionID(transactionID) != null) {
         System.out.println("MMS Ignored - duplicate");
      } else {
         MMSStorage.fileMessage(message, 8244211460627721111L);
         if (message.isUnopened()) {
            boolean isNew = message.isNew();
            UnreadCountManager.incrementUnreadCount(5, isNew, true);
            UnreadCountManager.incrementUnreadCount(11, isNew, true);
         }

         MMSNotificationManager.triggerNotifications(message);
      }
   }
}
