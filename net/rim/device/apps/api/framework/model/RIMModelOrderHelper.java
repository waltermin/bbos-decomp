package net.rim.device.apps.api.framework.model;

import net.rim.device.api.collection.util.KeywordIndexerHelper;
import net.rim.device.api.util.Comparator;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.api.util.WeakReferenceUtilities;
import net.rim.vm.WeakReference;

public class RIMModelOrderHelper implements KeywordIndexerHelper, Comparator {
   private ContextObject _context;
   private WeakReference _keys1WR = new WeakReference(null);
   private WeakReference _wordsWR = new WeakReference(null);
   private Comparator _comparator;

   public RIMModelOrderHelper(Comparator comparator, ContextObject context) {
      this._comparator = comparator;
      this._context = context;
   }

   @Override
   public int getKeywords(Object element, String[] keywords) {
      int keyCount = 0;
      if (element instanceof KeyProvider) {
         KeyProvider keyProvider = (KeyProvider)element;
         keyCount = keyProvider.getKeys(this._context, keywords, 0, 0);
      }

      return keyCount;
   }

   @Override
   public synchronized boolean checkForMatch(Object element, String[] words) {
      if (!(element instanceof KeyProvider)) {
         return false;
      }

      KeyProvider keyProvider = (KeyProvider)element;
      Object[] keys1 = WeakReferenceUtilities.getObjectArray(this._keys1WR, 10);
      String[] keys1Words = WeakReferenceUtilities.getStringArray(this._wordsWR, 10);
      int keyCount = keyProvider.getKeys(this._context, keys1, 0, 0);

      label37:
      for (int i = 0; i < words.length; i++) {
         for (int j = 0; j < keyCount; j++) {
            int wordCount = StringUtilities.stringToKeywords((String)keys1[j], keys1Words, 0);

            for (int k = 0; k < wordCount; k++) {
               if (StringUtilities.startsWithIgnoreCaseAndAccents(keys1Words[k], words[i])) {
                  continue label37;
               }
            }
         }

         return false;
      }

      return true;
   }

   @Override
   public synchronized int compare(Object o1, Object o2) {
      return this._comparator.compare(o1, o2);
   }
}
