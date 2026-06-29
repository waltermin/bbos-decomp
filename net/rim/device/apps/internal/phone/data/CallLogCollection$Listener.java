package net.rim.device.apps.internal.phone.data;

interface CallLogCollection$Listener {
   int ELEMENT_ADDED;
   int ELEMENT_REMOVED;
   int ELEMENT_REMOVED_TO_ALLOW_ADD;
   int ELEMENT_UPDATED;
   int DATABASE_DELETED;
   int BULK_UPDATE_COMPLETE;
   int ADDRESS_BOOK_UPDATED;

   void onEvent(int var1, CallLogItem var2, int var3);
}
