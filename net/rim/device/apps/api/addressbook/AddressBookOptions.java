package net.rim.device.apps.api.addressbook;

public interface AddressBookOptions {
   long SORT_ORDER_CONTEXT_ID;
   long NEW_OPTIONS_SYNCED;
   byte COMPOSE_PREFERENCE_UNKNOWN;
   byte COMPOSE_PREFERENCE_USEONCE;
   byte COMPOSE_PREFERENCE_LOOKUP;
   byte LIST_SEPARATOR_NONE;
   byte LIST_SEPARATOR_STRIPES;
   byte LIST_SEPARATOR_LINES;

   long getSortOrder();

   void setSortOrder(long var1);

   boolean getConfirmDelete();

   boolean getDuplicateNamesAllowed();

   void setDuplicateNamesAllowed(boolean var1);

   void editOptions();

   String getUserDefinedFieldLabel(int var1);

   void setUserDefinedFieldLabel(int var1, String var2);

   boolean editUserDefinedFieldLabel(int var1);

   boolean isWirelessSyncAllowed();

   byte getComposePreference();

   void setComposePreference(byte var1);

   byte getListSeparatorAppearance();

   void setListSeparatorAppearance(byte var1);
}
