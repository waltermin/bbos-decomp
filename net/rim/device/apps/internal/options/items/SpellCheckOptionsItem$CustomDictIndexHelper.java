package net.rim.device.apps.internal.options.items;

import net.rim.device.api.collection.util.KeywordIndexerHelper;
import net.rim.device.api.util.StringUtilities;
import net.rim.vm.Array;

final class SpellCheckOptionsItem$CustomDictIndexHelper implements KeywordIndexerHelper {
   public SpellCheckOptionsItem$CustomDictIndexHelper() {
   }

   @Override
   public final int getKeywords(Object element, String[] keywords) {
      if (element instanceof String) {
         keywords[0] = (String)element;
         Array.resize(keywords, 1);
         return 1;
      } else {
         return 0;
      }
   }

   @Override
   public final boolean checkForMatch(Object element, String[] words) {
      if (words.length != 1) {
         return false;
      } else {
         return !(element instanceof String) ? false : StringUtilities.startsWithIgnoreCaseAndAccents((String)element, words[0]);
      }
   }
}
