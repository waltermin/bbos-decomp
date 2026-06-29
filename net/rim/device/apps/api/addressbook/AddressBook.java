package net.rim.device.apps.api.addressbook;

import java.util.Enumeration;
import net.rim.device.api.collection.Collection;
import net.rim.device.api.collection.CollectionEventSource;
import net.rim.device.api.collection.util.KeywordFilterList;
import net.rim.device.api.util.Comparator;
import net.rim.device.apps.api.framework.model.Recognizer;
import net.rim.device.apps.api.framework.model.UIDGeneratorCallback;

public interface AddressBook extends CollectionEventSource {
   int EXACT_MATCH;
   int EXACT_MATCH_LAST_NAME;
   int PREFIX_MATCH;
   int SHORT_DISPLAY_NAME;
   long FIRST_NAME_ORDER;
   long LAST_NAME_ORDER;
   long COMPANY_NAME_ORDER;
   long FIRST_NAME_KEYS_ONLY;
   long LAST_NAME_KEYS_ONLY;
   long REVERSE_LOOKUP_KEYS;
   long ADDRESSCARD_KEYWORDS;
   long ADDRESSCARD_KEYWORDS_ONLY;

   Object reverseLookup(Object var1);

   Object[] reverseLookup(Object var1, Recognizer var2);

   Object[] reverseLookup(Object var1, Recognizer var2, boolean var3);

   int getAddressCount();

   Enumeration getAddressCards();

   AddressBookOptions getAddressBookOptions();

   Collection getAddressBookCollection();

   boolean checkDuplicateName(Object var1);

   Object[] lookup(Object var1, int var2);

   void addAddressCard(Object var1);

   void updateAddressCard(Object var1, Object var2);

   void forceUpdateAddressCard(Object var1, Object var2);

   Object mergeUpdateAddressCard(Object var1, Object var2);

   void removeAddressCard(Object var1);

   void removeAllAddressCards();

   Object getAddressCard(long var1);

   void showAddressBook(Object var1);

   void rebuildReverseLookupTable();

   boolean isBusy();

   boolean isFull();

   void suspendOTASync();

   void resumeOTASync();

   boolean isSyncInProgress();

   Comparator getComparator(Object var1, long var2);

   void addExternalReverseLookupResolver(AddressReverseLookupResolver var1, boolean var2);

   void removeExternalReverseLookupResolver(AddressReverseLookupResolver var1, boolean var2);

   KeywordFilterList getView(long var1);

   void setLastSelectedAddress(Object var1);

   void setUIDGeneratorCallback(UIDGeneratorCallback var1);
}
