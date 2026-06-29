package net.rim.device.apps.internal.mms.service;

import net.rim.device.api.notification.NotificationsManager;
import net.rim.device.apps.api.addressbook.AddressBookServices;
import net.rim.device.apps.api.addressbook.AddressCardModel;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.ribbon.indicators.NewMessageEventManager;
import net.rim.device.apps.internal.mms.api.MMSMessageModel;
import net.rim.device.apps.internal.mms.api.MMSPayloadModel;

class MMSNotificationManager$1 implements Runnable {
   private final MMSMessageModel val$model;

   MMSNotificationManager$1(MMSMessageModel _1) {
      this.val$model = _1;
   }

   @Override
   public void run() {
      ContextObject notificationContext = new ContextObject();
      notificationContext.putIntegerData(0);
      MMSPayloadModel payload = this.val$model.getPayload();
      Object sender = payload.getSender();
      if (sender != null) {
         Object senderModel = AddressBookServices.getAddressBook().reverseLookup(sender);
         if (senderModel instanceof AddressCardModel) {
            int addressCardUID = ((AddressCardModel)senderModel).getUID();
            NewMessageEventManager.addFlag(1, addressCardUID);
         }
      }

      NewMessageEventManager.addFlag(1, this.val$model.getUID());
      NotificationsManager.triggerImmediateEvent(8609386677418041260L, this.val$model.getUID(), null, notificationContext);
      long timeLimit = System.currentTimeMillis() + 40000;
      int trigger = 0;
      ContextObject deferredContext = new ContextObject();
      deferredContext.setFlag(65);
      NotificationsManager.negotiateDeferredEvent(8609386677418041260L, this.val$model.getUID(), this.val$model, timeLimit, trigger, deferredContext);
   }
}
