package net.rim.device.apps.internal.addressbook.addresscard;

import net.rim.device.apps.api.framework.verb.Verb;

final class StoreAction extends Verb {
   private Object _storedObject;

   StoreAction() {
      super(0);
   }

   @Override
   public final String toString() {
      return "";
   }

   @Override
   public final Object invoke(Object newEntry) {
      if (newEntry != null) {
         AddressCardUtilities.addToAddressBook(newEntry);
         this._storedObject = newEntry;
      }

      return newEntry;
   }

   final Object getStoredObject() {
      return this._storedObject;
   }
}
