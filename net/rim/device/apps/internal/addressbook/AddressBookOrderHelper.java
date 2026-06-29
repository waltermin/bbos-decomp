package net.rim.device.apps.internal.addressbook;

import net.rim.device.api.collection.util.KeywordIndexerHelper;
import net.rim.device.api.collection.util.KeywordPrefixManager;
import net.rim.device.api.i18n.Locale;
import net.rim.device.api.util.Comparator;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.api.util.WeakReferenceUtilities;
import net.rim.device.apps.api.framework.model.KeyProvider;
import net.rim.device.apps.internal.addressbook.addresscard.AddressCardComparator;
import net.rim.device.internal.i18n.CollatorImpl;
import net.rim.vm.WeakReference;

final class AddressBookOrderHelper implements KeywordIndexerHelper, Comparator {
   private Object _context;
   private long _keyToRequest;
   private WeakReference _keys1WR = (WeakReference)(new Object(null));
   private WeakReference _keys2WR = (WeakReference)(new Object(null));
   private WeakReference _wordsWR = (WeakReference)(new Object(null));
   private boolean _fastCardCompare;
   private static CollatorImpl _collator = (CollatorImpl)(new Object());

   final long getSortOrder() {
      return this._keyToRequest;
   }

   final void setSortOrder(long sortOrder) {
      this._keyToRequest = sortOrder;
      switch (Locale.getDefaultForSystem().getCode()) {
         case 1784741888:
         case 2053636096:
         case 2053653326:
         case 2053654603:
            this._fastCardCompare = false;
            return;
         default:
            this._fastCardCompare = sortOrder == 1232448844688687736L || sortOrder == -227891759293611117L || sortOrder == -4388042602796535003L;
      }
   }

   final synchronized int compare(Object o1, Object o2, long keyToRequest) {
      if (this._fastCardCompare && o1 instanceof Object && o2 instanceof Object) {
         return AddressCardComparator.compare(o1, o2, keyToRequest);
      }

      int keyCount1 = 0;
      Object[] keys1 = WeakReferenceUtilities.getObjectArray(this._keys1WR, 10);
      Object[] keys2 = WeakReferenceUtilities.getObjectArray(this._keys2WR, 10);
      String[] wordsBuffer = WeakReferenceUtilities.getStringArray(this._wordsWR, 10);
      if (o1 instanceof Object) {
         KeyProvider keyProvider1 = (KeyProvider)o1;
         keyCount1 = keyProvider1.getKeys(this._context, keys1, 0, keyToRequest);
      } else if (o1 instanceof Object) {
         keyCount1 = StringUtilities.stringToKeywords((String)o1, wordsBuffer, 0);
         System.arraycopy(wordsBuffer, 0, keys1, 0, keyCount1);
      }

      KeyProvider keyProvider2 = (KeyProvider)o2;
      int keyCount2 = keyProvider2.getKeys(this._context, keys2, 0, keyToRequest);
      int minKeyCount = keyCount1 < keyCount2 ? keyCount1 : keyCount2;

      for (int i = 0; i < minKeyCount; i++) {
         int result = _collator.compare((String)keys1[i], (String)keys2[i]);
         if (result != 0) {
            return result;
         }
      }

      if (keyCount1 < keyCount2) {
         return -1;
      } else {
         return keyCount1 > keyCount2 ? 1 : 0;
      }
   }

   @Override
   public final synchronized boolean checkForMatch(Object element, String[] words) {
      if (!(element instanceof Object)) {
         return false;
      }

      KeyProvider keyProvider = (KeyProvider)element;
      Object[] keys1 = WeakReferenceUtilities.getObjectArray(this._keys1WR, 10);
      String[] wordsBuffer = WeakReferenceUtilities.getStringArray(this._wordsWR, 10);
      int keyCount = keyProvider.getKeys(this._context, keys1, 0, this._keyToRequest);

      label37:
      for (int i = 0; i < words.length; i++) {
         for (int j = 0; j < keyCount; j++) {
            int wordCount = StringUtilities.stringToKeywords((String)keys1[j], wordsBuffer, 0);

            for (int k = 0; k < wordCount; k++) {
               if (KeywordPrefixManager.startsWithUsingMapping(wordsBuffer[k], words[i])) {
                  continue label37;
               }
            }
         }

         return false;
      }

      return true;
   }

   @Override
   public final int compare(Object o1, Object o2) {
      return this.compare(o1, o2, this._keyToRequest);
   }

   @Override
   public final int getKeywords(Object element, String[] keywords) {
      int keyCount = 0;
      if (element instanceof Object) {
         KeyProvider keyProvider = (KeyProvider)element;
         keyCount = keyProvider.getKeys(this._context, keywords, 0, this._keyToRequest);
      }

      return keyCount;
   }

   AddressBookOrderHelper(Object context, long keyToRequest) {
      this._context = context;
      this.setSortOrder(keyToRequest);
   }
}
