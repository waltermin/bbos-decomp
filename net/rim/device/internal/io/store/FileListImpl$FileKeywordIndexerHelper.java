package net.rim.device.internal.io.store;

import net.rim.device.api.collection.util.KeywordIndexerHelper;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.api.util.WeakReferenceUtilities;
import net.rim.vm.WeakReference;

class FileListImpl$FileKeywordIndexerHelper implements KeywordIndexerHelper {
   private WeakReference _wordsWR = new WeakReference(null);

   @Override
   public int getKeywords(Object element, String[] keywords) {
      FSDescriptor desc = (FSDescriptor)element;
      int count = 0;
      String name = desc.getName();
      return count + StringUtilities.stringToKeywords(name, keywords, count);
   }

   @Override
   public boolean checkForMatch(Object element, String[] words) {
      String[] keys1Words = WeakReferenceUtilities.getStringArray(this._wordsWR, 10);
      int wordCount = this.getKeywords(element, keys1Words);

      label24:
      for (int i = 0; i < words.length; i++) {
         for (int k = 0; k < wordCount; k++) {
            if (StringUtilities.startsWithIgnoreCaseAndAccents(keys1Words[k], words[i])) {
               continue label24;
            }
         }

         return false;
      }

      return true;
   }
}
