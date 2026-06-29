package net.rim.device.apps.api.addressbook;

import java.util.Enumeration;
import net.rim.device.api.collection.Collection;
import net.rim.device.api.collection.CollectionEventSource;
import net.rim.device.api.collection.util.KeywordFilterList;
import net.rim.device.api.util.Comparator;
import net.rim.device.apps.api.framework.model.Recognizer;
import net.rim.device.apps.api.framework.model.UIDGeneratorCallback;

public interface AddressBook extends CollectionEventSource {
   int EXACT_MATCH = 1;
   int EXACT_MATCH_LAST_NAME = 3;
   int PREFIX_MATCH = 5;
   int SHORT_DISPLAY_NAME = 6;
   long FIRST_NAME_ORDER = 1232448844688687736L;
   long LAST_NAME_ORDER = -227891759293611117L;
   long COMPANY_NAME_ORDER = -4388042602796535003L;
   long FIRST_NAME_KEYS_ONLY = 4922084531409364683L;
   long LAST_NAME_KEYS_ONLY = 8199160529614935340L;
   long REVERSE_LOOKUP_KEYS = -4145532165335996154L;
   long ADDRESSCARD_KEYWORDS = -6544199576583918793L;
   long ADDRESSCARD_KEYWORDS_ONLY = -6544199576583918792L;

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
