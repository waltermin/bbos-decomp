package net.rim.device.apps.internal.phone.api;

public interface AddressBookDependentObject {
   int ADDRESS_BOOK_RESET = 0;
   int ADDRESS_BOOK_ADD = 1;
   int ADDRESS_BOOK_REMOVE = 2;
   int ADDRESS_BOOK_ELEMENT_UPDATE = 3;
   int INITIALIZATION = 4;
   int ADDRESS_BOOK_GENERIC_UPDATE = 5;

   boolean isOutOfSyncWithAddressBook();

   boolean addressBookUpdated(int var1, Object var2);
}
