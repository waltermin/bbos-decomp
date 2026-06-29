package net.rim.device.apps.internal.sms;

import net.rim.device.api.notification.NotificationsManager;
import net.rim.device.apps.api.addressbook.AddressBookServices;
import net.rim.device.apps.api.addressbook.AddressCardModel;
import net.rim.device.apps.api.framework.model.PersistableRIMModel;
import net.rim.device.apps.api.ribbon.indicators.NewMessageEventManager;

class SMSService$1 implements Runnable {
   private final SMSModel val$model;
   private final long val$id;
   private final SMSService this$0;

   SMSService$1(SMSService _1, SMSModel _2, long _3) {
      this.this$0 = _1;
      this.val$model = _2;
      this.val$id = _3;
   }

   @Override
   public void run() {
      long type = 7986617465467730856L;
      int trigger = 0;
      if (this.val$model != null) {
         PersistableRIMModel peerAddress = this.val$model._payload.getFirstAddress();
         Object actualModel = AddressBookServices.reverseLookup(peerAddress);
         if (actualModel instanceof AddressCardModel) {
            int addresscardUID = ((AddressCardModel)actualModel).getUID();
            this.this$0._immediateContext.put(-7004855975111283545L, new Integer(addresscardUID));
            NewMessageEventManager.addFlag(1, addresscardUID);
         }
      }

      NotificationsManager.triggerImmediateEvent(type, this.val$id, null, this.this$0._immediateContext);
      if (this.val$model != null) {
         long timeLimit = System.currentTimeMillis() + 40000;
         NotificationsManager.negotiateDeferredEvent(type, this.val$id, this.val$model, timeLimit, trigger, this.this$0._deferredContext);
      }
   }
}
