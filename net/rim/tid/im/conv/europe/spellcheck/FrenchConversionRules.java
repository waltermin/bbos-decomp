package net.rim.tid.im.conv.europe.spellcheck;

import net.rim.tid.im.conv.SLCurrentVariant;
import net.rim.vm.Array;

public class FrenchConversionRules implements LocaleConversionRules {
   private FrenchConversionRules$Conversion _currentConversion;
   private SpellCheckVariantsCreator _creator;
   private int[] _bounds = new int[2];
   private char[] _tmpBuff = new char[50];
   private Word _convertedWord = new Word();
   private boolean _inRules;
   private static final char APOSTROPHE = '\'';
   private static final String CONTRACTION_FOLLOWS = "aeiouh";
   private static final String CONTRACTION_CONSONANTS = "cdjlmnst";
   private static final char SPECIAL_CONTRACTION1 = 'q';
   private static final char SPECIAL_CONTRACTION2 = 'u';
   private static final FrenchConversionRules$SimpleContractionConversion SIMP_CONTRACTION = new FrenchConversionRules$SimpleContractionConversion();
   private static final FrenchConversionRules$QUConversion QU_CONTRACTION = new FrenchConversionRules$QUConversion();

   public FrenchConversionRules(SpellCheckVariantsCreator creator) {
      this._creator = creator;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public boolean exists(Word word) {
      if (this._inRules) {
         return false;
      }

      this._inRules = true;
      boolean var7 = false /* VF: Semaphore variable */;

      boolean var9;
      label40: {
         try {
            var7 = true;
            Word lower = word.getLower();
            FrenchConversionRules$Conversion conversion = this.checkForContractionConversion(lower.text, lower.start, lower.length());
            if (conversion != null) {
               conversion.setConversionBounds(lower.text, lower.start, lower.length(), this._bounds);
               this._convertedWord.init(lower.text, this._bounds[0], this._bounds[0] + this._bounds[1], this._creator.getLocale());
               var9 = this._creator.exists(this._convertedWord);
               var7 = false;
               break label40;
            }

            var9 = false;
            var7 = false;
         } finally {
            if (var7) {
               this._inRules = false;
            }
         }

         this._inRules = false;
         return var9;
      }

      this._inRules = false;
      return var9;
   }

   @Override
   public int wordExists(char[] word, int offset, int len, char[] lcWord) {
      FrenchConversionRules$Conversion conversion = this.checkForContractionConversion(lcWord, 0, len);
      if (conversion != null) {
         conversion.setConversionBounds(word, 0, len, this._bounds);
         return this._creator.wordExists(word, this._bounds[0], this._bounds[1], false);
      } else {
         return 0;
      }
   }

   @Override
   public void getVariants(char[] word, int len, SpellCheckResultContainer res) {
      res.setLocaleRules(this);
      this._currentConversion = this.checkForContractionConversion(word, 0, len);
      if (this._currentConversion != null) {
         len = this._currentConversion.applyConversion(word, len);
         this._creator.regularSearch(word, len, res);
      }

      this._currentConversion = null;
      res.setLocaleRules(null);
   }

   @Override
   public void getVariants(Word word, SpellCheckResultContainer res) {
      int len = word.length();
      Word lower = word.getLower();
      res.setLocaleRules(this);
      this._currentConversion = this.checkForContractionConversion(lower.text, lower.start, len);
      if (this._currentConversion != null) {
         if (this._tmpBuff.length < len + 10) {
            Array.extend(this._tmpBuff, 10 + len - this._tmpBuff.length);
         }

         System.arraycopy(lower.text, lower.start, this._tmpBuff, 0, len);
         len = this._currentConversion.applyConversion(this._tmpBuff, len);
         this._convertedWord.init(this._tmpBuff, 0, len, this._creator.getLocale());
         this._creator.regularSearch(this._convertedWord, res);
      }

      this._currentConversion = null;
      res.setLocaleRules(null);
   }

   @Override
   public boolean modifyInserted(SLCurrentVariant word) {
      char[] wordBuffer = word._variants;
      int offset = word._offset;
      int len = word._length;
      if (!this._currentConversion.isValidForReverseConversion(wordBuffer, offset, len)) {
         return false;
      }

      word._length = this._currentConversion.reverseConversion(wordBuffer, offset, len);
      return true;
   }

   private FrenchConversionRules$Conversion checkForContractionConversion(char[] word, int offset, int len) {
      if (len < 3) {
         return null;
      }

      if (word[offset + 1] == '\'') {
         if ("cdjlmnst".indexOf(word[offset]) != -1 && "aeiouh".indexOf(word[offset + 2]) != -1) {
            SIMP_CONTRACTION.setContractionCharacter(word[offset]);
            return SIMP_CONTRACTION;
         }
      } else if (len >= 4 && word[offset + 2] == '\'' && 'q' == word[offset] && 'u' == word[offset + 1] && "aeiouh".indexOf(word[offset + 2]) != -1) {
         return QU_CONTRACTION;
      }

      return null;
   }
}
