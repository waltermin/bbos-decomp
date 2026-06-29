package net.rim.tid.im.conv.europe.spellcheck;

import net.rim.tid.im.conv.europe.repository.LearningGlobalAlphabet;
import net.rim.tid.im.conv.europe.repository.LearningReader;
import net.rim.tid.im.conv.repository.ExtendedCurrentVariant;
import net.rim.tid.im.conv.repository.ResultContainer;

class PairLearningPrefixTableHelper {
   void readAndInsertPairs(LearningReader aReader, byte[] aWordDefTable, char[] aWordBuffer, int aPrefixLen, int aOffset, int aNextOffset, ResultContainer aRes) {
      aRes.setMinWordLength(0);
      aRes.setMaxWordLength(2147483646);
      LearningGlobalAlphabet alphabet = aReader.getAlphabet();

      while (aOffset < aNextOffset) {
         int invocation_count = aWordDefTable[aOffset++] & 127;
         int word_len = 0;

         for (byte coded = aWordDefTable[aOffset]; aOffset < aNextOffset && (coded & 128) == 0; coded = aWordDefTable[++aOffset]) {
            aWordBuffer[word_len++] = alphabet.charAt(coded);
         }

         ExtendedCurrentVariant insertedWord = aRes.getTempInsertedWordContainer();
         insertedWord.setData(aWordBuffer, 0, word_len, (char)invocation_count);
         aRes.insert(insertedWord, aPrefixLen, null, 1);
      }
   }
}
