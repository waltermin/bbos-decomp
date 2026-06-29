package net.rim.device.apps.api.addressbook;

import java.util.Enumeration;
import net.rim.device.api.collection.Collection;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.ui.theme.Tag;
import net.rim.device.api.util.CharacterUtilities;
import net.rim.device.api.util.Comparator;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.framework.model.KeyProvider;
import net.rim.device.apps.api.framework.model.Recognizer;
import net.rim.device.apps.api.framework.model.UIDGeneratorCallback;
import net.rim.device.apps.api.framework.registration.VerbRepository;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.utility.general.Copyable;
import net.rim.vm.Array;

public final class AddressBookServices {
   public static final long ADDRESS_BOOK_KEY = 5765246712487104764L;
   public static Tag TAG_LINE1 = Tag.create("addressbook-list-line1");
   public static Tag TAG_LINE2 = Tag.create("addressbook-list-line2");
   public static Tag ADDRESS_BOOK_LIST_STRIPES_TAG = Tag.create("addressbook");
   public static Tag ADDRESS_BOOK_LIST_PLAIN_TAG = Tag.create("addressbook-plain");
   public static final long LIST_FIELD_GUID = -3906294199383546540L;
   private static ApplicationRegistry _ar = ApplicationRegistry.getApplicationRegistry();

   private AddressBookServices() {
   }

   public static final AddressBook getAddressBook(boolean wait) {
      return wait ? (AddressBook)_ar.waitForStartup(5765246712487104764L) : (AddressBook)_ar.get(5765246712487104764L);
   }

   public static final AddressBook getAddressBook() {
      return getAddressBook(true);
   }

   public static final int getAddressCount() {
      return getAddressBook().getAddressCount();
   }

   public static final Enumeration getAddressCards() {
      return getAddressBook().getAddressCards();
   }

   public static final Collection getAddressBookCollection() {
      return getAddressBook().getAddressBookCollection();
   }

   public static final AddressBookOptions getAddressBookOptions() {
      return getAddressBook().getAddressBookOptions();
   }

   public static final boolean checkDuplicateName(Object name) {
      return getAddressBook().checkDuplicateName(name);
   }

   public static final Object[] lookup(Object name, int flags) {
      return getAddressBook().lookup(name, flags);
   }

   public static final void addAddressCard(Object addressCard) {
      getAddressBook().addAddressCard(addressCard);
   }

   public static final void updateAddressCard(Object oldCard, Object newCard) {
      getAddressBook().updateAddressCard(oldCard, newCard);
   }

   public static final void removeAddressCard(Object addressCard) {
      getAddressBook().removeAddressCard(addressCard);
   }

   public static final void removeAllAddressCards() {
      getAddressBook().removeAllAddressCards();
   }

   public static final Object reverseLookup(Object address) {
      return reverseLookup(address, true);
   }

   public static final Object reverseLookup(Object address, boolean wait) {
      AddressBook ab = getAddressBook(wait);
      return ab == null ? null : ab.reverseLookup(address);
   }

   public static final Object[] reverseLookup(Object address, Recognizer r) {
      return reverseLookup(address, r, true);
   }

   public static final Object[] reverseLookup(Object address, Recognizer r, boolean wait) {
      AddressBook ab = getAddressBook(wait);
      return ab == null ? null : ab.reverseLookup(address, r);
   }

   public static final boolean addExternalReverseLookupResolver(AddressReverseLookupResolver resolver, boolean higherPriorityThanAddressBook) {
      AddressBook addressBook = getAddressBook();
      if (addressBook != null) {
         addressBook.addExternalReverseLookupResolver(resolver, higherPriorityThanAddressBook);
         return true;
      } else {
         return false;
      }
   }

   public static final void removeExternalReverseLookupResolver(AddressReverseLookupResolver resolver, boolean higherPriorityThanAddressBook) {
      getAddressBook().removeExternalReverseLookupResolver(resolver, higherPriorityThanAddressBook);
   }

   public static final Object getAddressCard(long luid) {
      return getAddressCard(luid, true);
   }

   public static final Object getAddressCard(long luid, boolean wait) {
      AddressBook ab = getAddressBook(wait);
      return ab == null ? null : ab.getAddressCard(luid);
   }

   public static final Verb getAddressSelectionVerb(long type) {
      VerbRepository addressVerbs = VerbRepository.getVerbRepository(-1789952090272871921L);
      Verb[] verbs = addressVerbs.getVerbs(type);
      if (verbs != null && verbs.length > 0) {
         Verb verb = verbs[0];
         return !(verb instanceof Copyable) ? verb : (Verb)((Copyable)verb).copy();
      } else {
         return null;
      }
   }

   public static final Verb getAddToAddressBookVerb() {
      VerbRepository addressVerbs = VerbRepository.getVerbRepository(1666635727707141867L);
      Verb[] verbs = addressVerbs.getVerbs(3797587162219887872L);
      if (verbs != null && verbs.length > 0) {
         Verb verb = verbs[0];
         return !(verb instanceof Copyable) ? verb : (Verb)((Copyable)verb).copy();
      } else {
         return null;
      }
   }

   public static final void showAddressBook(Object context) {
      getAddressBook().showAddressBook(context);
   }

   public static final int getReverseLookupCode(byte[] bytes, int start, int length) {
      return StringUtilities.computeReverseLookupHashCodeBytes(bytes, start, length, false);
   }

   public static final int getReverseLookupCode(byte[] bytes, int start, int length, boolean allChars) {
      return StringUtilities.computeReverseLookupHashCodeBytes(bytes, start, length, allChars);
   }

   public static final int getReverseLookupCode(String string) {
      return StringUtilities.computeReverseLookupHashCodeString(string, false);
   }

   public static final int getReverseLookupCode(String string, boolean allChars) {
      return StringUtilities.computeReverseLookupHashCodeString(string, allChars);
   }

   public static final String getReverseLookupString(String string) {
      int length = string.length();
      StringBuffer buffer = new StringBuffer(length);

      for (int i = 0; i < length; i++) {
         buffer.append(CharacterUtilities.toLowerCase(string.charAt(i), 1701707776));
      }

      return buffer.toString();
   }

   public static final int getReverseLookupKeys(Object element, int[] keys) {
      return getReverseLookupKeys(element, keys, false);
   }

   public static final int getReverseLookupKeys(Object element, int[] keys, boolean allChars) {
      int keyCount = 0;
      if (!(element instanceof KeyProvider)) {
         if (element instanceof String) {
            if (keys.length < 2) {
               Array.resize(keys, 2);
            }

            keys[0] = getReverseLookupCode((String)element);
            keyCount = 1;
            if (allChars) {
               keys[1] = getReverseLookupCode((String)element, true);
               keyCount++;
            }
         }
      } else {
         KeyProvider keyProvider = (KeyProvider)element;
         keyCount = keyProvider.getKeys(null, keys, 0, -4145532165335996154L);
      }

      int src = 0;
      int dest = 0;

      while (src < keyCount) {
         if (keys[src] != 0) {
            keys[dest++] = keys[src];
         }

         src++;
      }

      Array.resize(keys, dest);
      return dest;
   }

   public static final void rebuildReverseLookupTable() {
      getAddressBook().rebuildReverseLookupTable();
   }

   public final Comparator getComparator(Object context, long sortOrder) {
      return getAddressBook().getComparator(context, sortOrder);
   }

   public static final void setLastSelectedAddress(Object parameter) {
      AddressBook ab = getAddressBook(false);
      if (ab != null) {
         ab.setLastSelectedAddress(parameter);
      }
   }

   public static final void setUIDGeneratorCallback(UIDGeneratorCallback hook) {
      AddressBook ab = getAddressBook(false);
      if (ab != null) {
         ab.setUIDGeneratorCallback(hook);
      }
   }
}
