package net.rim.device.apps.internal.phone.api;

public interface AddressBookDependentObject {
   int ADDRESS_BOOK_RESET;
   int ADDRESS_BOOK_ADD;
   int ADDRESS_BOOK_REMOVE;
   int ADDRESS_BOOK_ELEMENT_UPDATE;
   int INITIALIZATION;
   int ADDRESS_BOOK_GENERIC_UPDATE;

   boolean isOutOfSyncWithAddressBook();

   boolean addressBookUpdated(int var1, Object var2);
}
