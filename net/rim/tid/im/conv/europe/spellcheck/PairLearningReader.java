package net.rim.tid.im.conv.europe.spellcheck;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.tid.data.LearningDataManager;
import net.rim.tid.im.conv.SLCurrentVariant;
import net.rim.tid.im.conv.europe.repository.LearningComplexPrefixTable;
import net.rim.tid.im.conv.europe.repository.LearningGlobalAlphabet;
import net.rim.tid.im.conv.europe.repository.LearningHeader;
import net.rim.tid.im.conv.europe.repository.LearningPrefixTable;
import net.rim.tid.im.conv.europe.repository.LearningReader;

public class PairLearningReader extends LearningReader {
   private SLCurrentVariant _tempVariant = (SLCurrentVariant)(new Object());
   private StringBuffer _tempStrBuf = (StringBuffer)(new Object());

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public PairLearningReader() {
      ResourceBundle bundle = ResourceBundle.getBundle(-7934727403592703506L, "net.rim.tid.im.options.SpellCheck.SpellCheck");
      boolean var4 = false /* VF: Semaphore variable */;

      label23:
      try {
         var4 = true;
         super._sizeLimit = Integer.parseInt(bundle.getString(12));
         if (super._sizeLimit < 1) {
            System.err.println("Warning: illegal pair learning DB size limit");
            super._sizeLimit = 3;
            var4 = false;
         } else {
            var4 = false;
         }
      } finally {
         if (var4) {
            System.err.println("Warning: illegal pair learning DB size limit");
            super._sizeLimit = 3;
            break label23;
         }
      }

      super._sizeLimit *= 1024;
      super._header = (LearningHeader)(new Object());
      super._alphabet = (LearningGlobalAlphabet)(new Object(false));
      super._simplePrefixTable = new PairLearningSimplePrefixTable(0, this, (byte)6);
   }

   @Override
   public String getFilename(String aLocaleStr) {
      return LearningDataManager.constructKey(null, aLocaleStr, (byte)6);
   }

   @Override
   public LearningComplexPrefixTable createLearningComplexPrefixTable(int aOrder, LearningReader aReader) {
      return new PairLearningComplexPrefixTable(aOrder, aReader);
   }

   public int addPair(String word, byte freq) {
      int len = word.indexOf("++");
      if (len >= 50) {
         System.out.println(((StringBuffer)(new Object("Word( "))).append(word).append(") is too long!").toString());
         return 1;
      } else {
         word.getChars(0, len, super._tempBuffer, 0);
         this._tempStrBuf.setLength(0);
         this._tempStrBuf.append(super._tempBuffer, 0, len);
         int replacementIndex = len + 2;
         len = word.length() - replacementIndex;
         if (len >= 50) {
            System.out.println(((StringBuffer)(new Object("Word( "))).append(word).append(") is too long!").toString());
            return 1;
         } else {
            word.getChars(replacementIndex, replacementIndex + len, super._tempBuffer, 0);
            this._tempVariant._variants = super._tempBuffer;
            this._tempVariant._length = len;
            return this.addPair(this._tempStrBuf, this._tempVariant, freq);
         }
      }
   }

   public int addPair(StringBuffer misspelledWord, SLCurrentVariant correctWord) {
      return this.addPair(misspelledWord, correctWord, (byte)1);
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public int addPair(StringBuffer aMisspelledWord, SLCurrentVariant aCorrectWord, byte freq) {
      try {
         if (!this.ensureSize(aMisspelledWord.length() + aCorrectWord._length + 10)) {
            System.err.println("addWord - no space");
            return 3;
         }

         int size = super._alphabet.size();
         int len = super._alphabet.encodeWord(aMisspelledWord, super._encoded, 0, true);
         if (len == -1) {
            System.err.println("addWord - no space");
            return 3;
         }

         LearningPrefixTable table = super._complexPrefixTables[0].findTableForEncodedWord(super._encoded, 0, aMisspelledWord.length());
         super._encoded[len] = (byte)(128 | freq);
         if (super._alphabet.encodeWord(aCorrectWord, super._encoded, len + 1, false) == -1) {
            System.err.println("addWord - no space");
            return 3;
         }

         int len1 = aMisspelledWord.length();
         int len2 = aCorrectWord._length;
         table.add(super._encoded, len1, len2, 0);
         int alphabet_change = super._alphabet.size() - size;
         if (alphabet_change != 0) {
            System.arraycopy(
               super._data, super._tablesStart, super._data, super._tablesStart + alphabet_change, super._header.getFileSize() - super._tablesStart
            );
            super._header.growFileSize(alphabet_change);
         }

         this.modifyLearningData(false);
         return 0;
      } catch (Throwable var11) {
         System.out.println(((StringBuffer)(new Object("Failed on word:"))).append(aMisspelledWord).toString());
         e.printStackTrace();
         return 1;
      }
   }

   public int trim() {
      int TRIM_INVOCATION_COUNT = 1;
      int ret = this.trim(TRIM_INVOCATION_COUNT);
      int TRIM_WORTHY_SIZE = 1000;
      super._header.setTrimmable(ret > TRIM_WORTHY_SIZE);
      return ret;
   }

   @Override
   public void trimToSizeLimit(int aSizeLimit) {
      int trimmed = 0;

      for (int count = 1; this.getFileSize() > aSizeLimit; count *= 2) {
         int current_trim = this.trim(count);
         if (current_trim == 0) {
            break;
         }

         trimmed += current_trim;
      }

      if (this.getFileSize() > aSizeLimit) {
         System.err.println("Could not trim to required size");
      }
   }

   @Override
   public int trim(int aMaxCount) {
      System.out.println("Trimming learning database...");
      int trimmed = super._complexPrefixTables[0].trim(aMaxCount);
      if (trimmed > 0) {
         this.modifyLearningData(false);
      }

      return trimmed;
   }
}
