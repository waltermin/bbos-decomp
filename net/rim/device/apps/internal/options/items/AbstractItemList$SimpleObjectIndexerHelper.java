package net.rim.device.apps.internal.options.items;

import net.rim.device.api.collection.util.KeywordIndexerHelper;
import net.rim.device.api.util.StringUtilities;
import net.rim.vm.Array;

class AbstractItemList$SimpleObjectIndexerHelper implements KeywordIndexerHelper {
   private final AbstractItemList this$0;

   AbstractItemList$SimpleObjectIndexerHelper(AbstractItemList _1) {
      this.this$0 = _1;
   }

   protected String getString(Object _1) {
      throw null;
   }

   @Override
   public int getKeywords(Object element, String[] keywords) {
      int count = StringUtilities.stringToKeywords(this.getString(element), keywords, 0);
      Array.resize(keywords, count);
      return count;
   }

   @Override
   public boolean checkForMatch(Object element, String[] words) {
      String[] keywords = new Object[1];
      this.getKeywords(element, keywords);

      for (int i = 0; i < words.length; i++) {
         for (int j = 0; j < keywords.length; j++) {
            if (StringUtilities.startsWithIgnoreCaseAndAccents(keywords[j], words[i])) {
               return true;
            }
         }
      }

      return false;
   }
}
