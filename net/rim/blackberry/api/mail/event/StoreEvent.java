package net.rim.blackberry.api.mail.event;

import net.rim.blackberry.api.mail.Store;

public class StoreEvent extends MailEvent {
   private int _type;
   public static final int BATCH_OPERATION = 1;

   public StoreEvent(Store store, int type) {
      super(store);
      this._type = type;
   }

   @Override
   public void dispatch(Object listener) {
      if (listener instanceof StoreListener) {
         switch (this._type) {
            case 1:
               ((StoreListener)listener).batchOperation(this);
         }
      }
   }

   public int getType() {
      return this._type;
   }

   @Override
   public String toString() {
      return "StoreEvent: " + this._type;
   }
}
