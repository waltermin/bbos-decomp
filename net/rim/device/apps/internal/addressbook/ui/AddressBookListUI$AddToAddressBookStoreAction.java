package net.rim.device.apps.internal.addressbook.ui;

import net.rim.device.apps.api.addressbook.AddressBookServices;
import net.rim.device.apps.api.framework.verb.Verb;

final class AddressBookListUI$AddToAddressBookStoreAction extends Verb {
   private Object _storedObject;

   AddressBookListUI$AddToAddressBookStoreAction() {
      super(0);
   }

   @Override
   public final String toString() {
      return "";
   }

   @Override
   public final Object invoke(Object newEntry) {
      if (newEntry != null) {
         AddressBookServices.getAddressBook().addAddressCard(newEntry);
         this._storedObject = newEntry;
      }

      return newEntry;
   }

   final Object getStoredObject() {
      return this._storedObject;
   }
}
