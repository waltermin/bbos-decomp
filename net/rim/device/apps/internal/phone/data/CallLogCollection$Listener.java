package net.rim.device.apps.internal.phone.data;

interface CallLogCollection$Listener {
   int ELEMENT_ADDED = 0;
   int ELEMENT_REMOVED = 1;
   int ELEMENT_REMOVED_TO_ALLOW_ADD = 2;
   int ELEMENT_UPDATED = 3;
   int DATABASE_DELETED = 4;
   int BULK_UPDATE_COMPLETE = 5;
   int ADDRESS_BOOK_UPDATED = 6;

   void onEvent(int var1, CallLogItem var2, int var3);
}
