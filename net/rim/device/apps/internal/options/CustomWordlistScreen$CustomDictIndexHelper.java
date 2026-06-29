package net.rim.device.apps.internal.options;

import net.rim.device.api.collection.util.KeywordIndexerHelper;
import net.rim.tid.im.customWordRepository.ja.JapaneseCustomWord;
import net.rim.tid.util.Utils;
import net.rim.vm.Array;

final class CustomWordlistScreen$CustomDictIndexHelper implements KeywordIndexerHelper {
   public CustomWordlistScreen$CustomDictIndexHelper() {
   }

   @Override
   public final int getKeywords(Object element, String[] keywords) {
      if (element instanceof Object) {
         keywords[0] = (String)element;
         Array.resize(keywords, 1);
         return 1;
      } else if (element instanceof Object) {
         Array.resize(keywords, 2);
         keywords[0] = ((JapaneseCustomWord)element).getReading();
         keywords[1] = ((JapaneseCustomWord)element).getCandidate();
         return 2;
      } else {
         return 0;
      }
   }

   @Override
   public final boolean checkForMatch(Object element, String[] words) {
      if (words.length != 1) {
         return false;
      } else if (!(element instanceof Object)) {
         return !(element instanceof Object)
            ? false
            : Utils.startsWithIgnoreCase(((JapaneseCustomWord)element).getReading(), words[0])
               | Utils.startsWithIgnoreCase(((JapaneseCustomWord)element).getCandidate(), words[0]);
      } else {
         return Utils.startsWithIgnoreCase((String)element, words[0]);
      }
   }
}
