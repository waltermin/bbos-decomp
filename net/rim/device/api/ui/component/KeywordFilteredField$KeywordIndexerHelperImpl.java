package net.rim.device.api.ui.component;

import net.rim.device.api.collection.util.KeywordIndexerHelper;
import net.rim.device.api.util.StringUtilities;
import net.rim.vm.Array;

class KeywordFilteredField$KeywordIndexerHelperImpl implements KeywordIndexerHelper {
   private final KeywordFilteredField this$0;

   public KeywordFilteredField$KeywordIndexerHelperImpl(KeywordFilteredField _1) {
      this.this$0 = _1;
   }

   @Override
   public boolean checkForMatch(Object element, String[] words) {
      String[] keywords = this.this$0._keywordProvider.getKeywords(element);

      for (int i = 0; i < words.length; i++) {
         for (int j = 0; j < keywords.length; j++) {
            String[] keys = StringUtilities.stringToWords(keywords[j]);

            for (int k = 0; k < keys.length; k++) {
               if (StringUtilities.startsWithIgnoreCaseAndAccents(keys[k], words[i])) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   @Override
   public int getKeywords(Object element, String[] keywords) {
      String[] keys = this.this$0._keywordProvider.getKeywords(element);
      System.arraycopy(keys, 0, keywords, 0, keys.length);
      Array.resize(keywords, keys.length);
      return keywords.length;
   }
}
