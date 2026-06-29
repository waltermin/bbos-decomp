package net.rim.device.apps.api.search;

import net.rim.device.api.collection.ReadableList;
import net.rim.device.api.util.StringMatch;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.framework.model.MatchProvider;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.vm.Array;

public class Match {
   private static int[] _cachedWords = new int[16];

   public static int performMatch(RIMModel m, SearchCriterion sc) {
      if (m instanceof Object) {
         MatchProvider matchProvider = (MatchProvider)m;
         if (sc.getType() != 23) {
            return matchProvider.match(sc);
         }

         Object[] criteria = (Object[])sc.getValue();
         if (criteria == null) {
            return -1;
         }

         boolean somethingNotApplicable = false;
         int numCriteria = criteria.length;

         for (int i = 0; i < numCriteria; i++) {
            if (criteria[i] instanceof SearchCriterion) {
               switch (matchProvider.match((SearchCriterion)criteria[i])) {
                  case -1:
                     somethingNotApplicable = true;
                     break;
                  case 1:
                     return 1;
               }
            }
         }

         if (!somethingNotApplicable) {
            return 0;
         }
      }

      return -1;
   }

   public static int match(RIMModel containerModel, ReadableList elements, SearchCriterion[] crit, int[] hints) {
      int numHints = hints.length;
      int numCriterion = crit.length;
      if (numHints < numCriterion) {
         Array.resize(hints, numCriterion);

         for (int i = numHints; i < numCriterion; i++) {
            hints[i] = 0;
         }
      }

      int numSubmembers = -1;

      label81:
      for (int i = 0; i < numCriterion; i++) {
         SearchCriterion criterion = crit[i];
         boolean somethingMismatched = false;
         int skip = -1;
         int hint = hints[i];
         if (hint != 0) {
            if (numSubmembers < 0) {
               numSubmembers = elements.size();
               skip = numSubmembers + 1;
            }

            hint--;
            boolean disableHint = true;
            if (hint < numSubmembers) {
               Object o = elements.getAt(hint);
               if (!(o instanceof Object)) {
                  return -1;
               }

               RIMModel m = (RIMModel)o;
               switch (performMatch(m, criterion)) {
                  case 0:
                  default:
                     somethingMismatched = true;
                     disableHint = false;
                  case -1:
                     skip = hint;
                     break;
                  case 1:
                     continue;
               }
            }

            if (disableHint) {
               hints[i] = 0;
            }
         }

         switch (performMatch(containerModel, criterion)) {
            case -1:
               if (numSubmembers < 0) {
                  numSubmembers = elements.size();
                  skip = numSubmembers + 1;
               }

               int j = 0;

               for (; j < numSubmembers; j++) {
                  if (j != skip) {
                     Object o = elements.getAt(j);
                     if (!(o instanceof Object)) {
                        hints[i] = j + 1;
                        break;
                     }

                     RIMModel m = (RIMModel)o;
                     switch (performMatch(m, criterion)) {
                        case -1:
                           break;
                        case 0:
                           hints[i] = j + 1;
                           somethingMismatched = true;
                           break;
                        case 1:
                        default:
                           hints[i] = j + 1;
                           continue label81;
                     }
                  }
               }

               if (somethingMismatched) {
                  return 0;
               }

               return -1;
            case 0:
            default:
               return 0;
            case 1:
         }
      }

      return 1;
   }

   public static int nameStringMatch(String nameString, String[] testWords) {
      if (nameString == null) {
         return 0;
      }

      int nameStringLen = nameString.length();
      int wordsLen = nameStringLen / 2 + 16 & -16;
      int[] sWords = _cachedWords;
      if (wordsLen > sWords.length) {
         sWords = new int[wordsLen];
         _cachedWords = sWords;
      }

      int numSWords = StringUtilities.stringToWords(nameString, sWords, 0);

      label32:
      for (String testWord : testWords) {
         int testWordLen = testWord.length();

         for (int j = 0; j < numSWords; j++) {
            if (nameString.regionMatches(true, sWords[j], testWord, 0, testWordLen)) {
               continue label32;
            }
         }

         return 0;
      }

      return 1;
   }

   public static int stringMatchMatch(StringMatch stringMatch, String text) {
      return text != null && stringMatch.indexOf(text) >= 0 ? 1 : 0;
   }
}
