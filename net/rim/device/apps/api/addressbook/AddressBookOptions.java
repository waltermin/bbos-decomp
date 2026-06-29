package net.rim.device.apps.api.addressbook;

public interface AddressBookOptions {
   long SORT_ORDER_CONTEXT_ID = 614335798810617774L;
   long NEW_OPTIONS_SYNCED = -3950819934062689467L;
   byte COMPOSE_PREFERENCE_UNKNOWN = 0;
   byte COMPOSE_PREFERENCE_USEONCE = 1;
   byte COMPOSE_PREFERENCE_LOOKUP = 2;
   byte LIST_SEPARATOR_NONE = 0;
   byte LIST_SEPARATOR_STRIPES = 1;
   byte LIST_SEPARATOR_LINES = 2;

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
