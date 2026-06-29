package net.rim.device.apps.internal.addressbook.lookup;

import net.rim.device.api.collection.util.KeywordIndexerHelper;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.framework.model.KeyProvider;

public class SearchViewIndexerHelper implements KeywordIndexerHelper {
   private Object _context;
   private long _keyToRequest;
   private Object[] _keys1 = new Object[10];
   private String[] _words = new String[10];

   SearchViewIndexerHelper(Object context, long keyToRequest) {
      this._context = context;
      this.setSortOrder(keyToRequest);
   }

   long getSortOrder() {
      return this._keyToRequest;
   }

   void setSortOrder(long sortOrder) {
      this._keyToRequest = sortOrder;
   }

   @Override
   public int getKeywords(Object element, String[] keywords) {
      return !(element instanceof KeyProvider) ? 0 : ((KeyProvider)element).getKeys(this._context, keywords, 0, this._keyToRequest);
   }

   @Override
   public synchronized boolean checkForMatch(Object element, String[] words) {
      if (!(element instanceof KeyProvider)) {
         return false;
      }

      int keyCount = ((KeyProvider)element).getKeys(this._context, this._keys1, 0, this._keyToRequest);

      label37:
      for (int i = 0; i < words.length; i++) {
         for (int j = 0; j < keyCount; j++) {
            int wordCount = StringUtilities.stringToWords((String)this._keys1[j], this._words, 0);

            for (int k = 0; k < wordCount; k++) {
               if (StringUtilities.startsWithIgnoreCaseAndAccents(this._words[k], words[i])) {
                  continue label37;
               }
            }
         }

         return false;
      }

      return true;
   }
}
