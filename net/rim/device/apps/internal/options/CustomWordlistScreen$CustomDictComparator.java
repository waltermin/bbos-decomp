package net.rim.device.apps.internal.options;

import net.rim.device.api.util.Comparator;
import net.rim.tid.im.customWordRepository.ja.JapaneseCustomWord;

final class CustomWordlistScreen$CustomDictComparator implements Comparator {
   @Override
   public final int compare(Object o1, Object o2) {
      int comp = 0;
      if (o1 instanceof String && o2 instanceof String) {
         return ((String)o1).compareTo((String)o2);
      }

      if (o1 instanceof JapaneseCustomWord && o2 instanceof JapaneseCustomWord) {
         JapaneseCustomWord word1 = (JapaneseCustomWord)o1;
         JapaneseCustomWord word2 = (JapaneseCustomWord)o2;
         comp = word1.getCandidate().compareTo(word2.getCandidate());
         if (comp == 0) {
            comp = word1.getReading().compareTo(word2.getReading());
            if (comp == 0) {
               int pos1 = word1.getPartOfSpeech();
               int pos2 = word2.getPartOfSpeech();
               if (pos1 == pos2) {
                  return 0;
               }

               if (pos1 > pos2) {
                  return 1;
               }

               return -1;
            }
         }
      }

      return comp;
   }
}
