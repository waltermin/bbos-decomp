package net.rim.device.apps.api.addressbook;

public interface AddressBookDataContainer {
   char[] getPrefixes();

   int getPrefixStart();

   int getPrefixLength();

   void checkAndInsertVariant(String var1, int var2, int var3, int var4);

   void checkAndInsertVariant(char[] var1, int var2, int var3, int var4);

   int getVariantsCount();
}
