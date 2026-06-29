package net.rim.tid.im.conv.europe.spellcheck;

import java.util.Vector;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.CharacterUtilities;
import net.rim.tid.im.conv.SLCurrentVariant;
import net.rim.vm.Array;

public class EnglishConversionRules implements LocaleConversionRules {
   private EnglishConversionRules$Conversion _conversion;
   private Vector _conversions = new Vector();
   private Word _convertedWord = new Word();
   private boolean _inRules;
   private SpellCheckVariantsCreator _creator;
   private char[] _tmpBuff = new char[50];
   private char[] _lcBuff = new char[50];
   private static final String VOWELS = "aeiouAEIOU";
   private static final String BEFORE_APOSTROPHE_S_EXCLUSION = "s'S";
   private static final String BEFORE_ING_EXCLUSION = "eiEI";
   private static final String BEFORE_PLURAL_S_EXCLUSION = "sS";
   private static final String BEFORE_PLURAL_YS_EXCLUSION = "aeiouAEIOU";
   private static final String BEFORE_PLURAL_ES_INCLUSION = "sxzSXZ";
   private static final String BEFORE_PLURAL_HES_INCLUSION = "csCS";
   private static final String DOUBLED_CONSONANT = "bdfglmnprstvzBDFGLMNPRSTVZ";
   private static final EnglishConversionRules$Conversion SIMPLE_ING_CONVERSION = new EnglishConversionRules$IngConversion(3);
   private static final EnglishConversionRules$Conversion CONTRACTION_ING_CONVERSION = new EnglishConversionRules$IngConversion(4);
   private static final EnglishConversionRules$Conversion DOUBLE_CONSONANT_ING_CONVERSION = new EnglishConversionRules$IngConversion(5);
   private static final EnglishConversionRules$Conversion SIMPLE_PLURAL_CONVERSION = new EnglishConversionRules$PluralConversion(6);
   private static final EnglishConversionRules$Conversion ES_PLURAL_CONVERSION = new EnglishConversionRules$PluralConversion(7);
   private static final EnglishConversionRules$Conversion IES_PLURAL_CONVERSION = new EnglishConversionRules$PluralConversion(8);
   private static final EnglishConversionRules$Conversion POSSESSIVE_CONVERSION = new EnglishConversionRules$PossessiveConversion();
   private static final EnglishConversionRules$Conversion SIMPLE_PLURAL_POSSESSIVE_CONVERSION = new EnglishConversionRules$PluralPossessiveConversion(
      SIMPLE_PLURAL_CONVERSION
   );
   private static final EnglishConversionRules$Conversion ES_PLURAL_POSSESSIVE_CONVERSION = new EnglishConversionRules$PluralPossessiveConversion(
      ES_PLURAL_CONVERSION
   );
   private static final EnglishConversionRules$Conversion IES_PLURAL_POSSESSIVE_CONVERSION = new EnglishConversionRules$PluralPossessiveConversion(
      IES_PLURAL_CONVERSION
   );
   private static final EnglishConversionRules$Conversion INVALID_PLURAL_POSSESSIVE_CONVERSION = new EnglishConversionRules$PluralPossessiveConversion(
      SIMPLE_PLURAL_CONVERSION, true
   );
   private static final EnglishConversionRules$Conversion INVALID_ES_PLURAL_POSSESSIVE_CONVERSION = new EnglishConversionRules$PluralPossessiveConversion(
      ES_PLURAL_CONVERSION, true
   );
   private static final EnglishConversionRules$Conversion INVALID_IES_PLURAL_POSSESSIVE_CONVERSION = new EnglishConversionRules$PluralPossessiveConversion(
      IES_PLURAL_CONVERSION, true
   );
   private static final EnglishConversionRules$Conversion NUMBER_ADJ_CONVERSION = new EnglishConversionRules$NumberAdjectiveConversion(10);

   public EnglishConversionRules(SpellCheckVariantsCreator aCreator) {
      this._creator = aCreator;
   }

   @Override
   public boolean exists(Word word) {
      if (this._inRules) {
         return false;
      }

      this._inRules = true;
      int len = word.length();
      this._conversions.removeAllElements();
      Word lower = word.getLower();
      this.checkForConversion(lower.text, lower.start, lower.length(), this._conversions);
      int conversionsLen = this._conversions.size();
      if (conversionsLen > 0) {
         if (this._tmpBuff.length < len + 10) {
            Array.extend(this._tmpBuff, 10 + len - this._tmpBuff.length);
         }

         char[] original;
         int originalOffset;
         if (this._creator.isCaseSensitive()) {
            original = word.text;
            originalOffset = word.start;
         } else {
            original = lower.text;
            originalOffset = lower.start;
         }

         System.arraycopy(original, originalOffset, this._tmpBuff, 0, len);

         for (int i = 0; i < conversionsLen; i++) {
            this._conversion = (EnglishConversionRules$Conversion)this._conversions.elementAt(i);
            int newWordLen = this._conversion.applyConversion(this._tmpBuff, 0, len);
            this._convertedWord.init(this._tmpBuff, 0, newWordLen, this._creator.getLocale());
            boolean rc = this._creator.exists(this._convertedWord);
            int reversedConversionLen = this._conversion.reverseConversion(this._tmpBuff, 0, newWordLen);
            if (rc) {
               if (reversedConversionLen != len) {
                  System.arraycopy(original, originalOffset, this._tmpBuff, 0, len);
               } else if (Arrays.equals(original, originalOffset, this._tmpBuff, 0, len)) {
                  this._inRules = false;
                  return true;
               }
            }
         }
      }

      this._inRules = false;
      return false;
   }

   @Override
   public int wordExists(char[] word, int offset, int len, char[] lcWord) {
      this._conversions.removeAllElements();
      this.checkForConversion(lcWord, 0, len, this._conversions);
      int conversionsLen = this._conversions.size();
      if (conversionsLen > 0) {
         if (this._tmpBuff.length < len + 10) {
            Array.extend(this._tmpBuff, 10 + len - this._tmpBuff.length);
            Array.extend(this._lcBuff, 10 + len - this._lcBuff.length);
         }

         char[] original;
         int originalOffset;
         if (this._creator.isCaseSensitive()) {
            original = word;
            originalOffset = offset;
         } else {
            original = lcWord;
            originalOffset = 0;
         }

         System.arraycopy(original, originalOffset, this._tmpBuff, 0, len);
         System.arraycopy(lcWord, 0, this._lcBuff, 0, len);

         for (int i = 0; i < conversionsLen; i++) {
            this._conversion = (EnglishConversionRules$Conversion)this._conversions.elementAt(i);
            int newWordLen = this._conversion.applyConversion(this._tmpBuff, 0, len);
            this._conversion.applyConversion(this._lcBuff, 0, len);
            int rc = this._creator.wordExists(this._tmpBuff, 0, newWordLen, this._lcBuff, false);
            int reversedConversionLen = this._conversion.reverseConversion(this._tmpBuff, 0, newWordLen);
            if (rc > 0) {
               if (reversedConversionLen != len) {
                  return 0;
               }

               if (Arrays.equals(original, originalOffset, this._tmpBuff, 0, len)) {
                  return rc;
               }
            }
         }
      }

      return 0;
   }

   @Override
   public void getVariants(char[] word, int len, SpellCheckResultContainer res) {
      res.setLocaleRules(this);
      this._conversions.removeAllElements();
      this.checkForConversion(word, 0, len, this._conversions);
      int clen = this._conversions.size();
      if (clen > 0) {
         for (int i = 0; i < clen; i++) {
            this._conversion = (EnglishConversionRules$Conversion)this._conversions.elementAt(i);
            int newWordLen = this._conversion.applyConversion(word, 0, len);
            this._creator.regularSearch(word, newWordLen, res);
         }
      }

      this._conversion = null;
      res.setLocaleRules(null);
   }

   @Override
   public void getVariants(Word word, SpellCheckResultContainer res) {
      int len = word.length();
      Word lower = word.getLower();
      res.setLocaleRules(this);
      this._conversions.removeAllElements();
      this.checkForConversion(lower.text, lower.start, len, this._conversions);
      int clen = this._conversions.size();
      if (clen > 0) {
         if (this._tmpBuff.length < len + 10) {
            Array.extend(this._tmpBuff, 10 + len - this._tmpBuff.length);
         }

         for (int i = 0; i < clen; i++) {
            this._conversion = (EnglishConversionRules$Conversion)this._conversions.elementAt(i);
            System.arraycopy(lower.text, lower.start, this._tmpBuff, 0, len);
            int newWordLen = this._conversion.applyConversion(this._tmpBuff, 0, len);
            this._convertedWord.init(this._tmpBuff, 0, newWordLen, this._creator.getLocale());
            this._creator.regularSearch(this._convertedWord, res);
         }
      }

      this._conversion = null;
      res.setLocaleRules(null);
   }

   @Override
   public boolean modifyInserted(SLCurrentVariant word) {
      char[] wordBuffer = word._variants;
      int offset = word._offset;
      int len = word._length;
      if (!this._conversion.isValidForReverseConversion(wordBuffer, offset, len)) {
         return false;
      }

      word._length = this._conversion.reverseConversion(wordBuffer, offset, len);
      return true;
   }

   private void checkForConversion(char[] word, int offset, int len, Vector conversions) {
      if (len >= 3) {
         int last = offset + len - 1;
         label101:
         switch (word[last]) {
            case '\'':
               switch (word[last - 1]) {
                  case 'S':
                  case 's':
                     int lastConvSize = conversions.size();
                     this.checkForConversion(word, offset, len - 1, conversions);

                     for (int i = lastConvSize; i < conversions.size(); i++) {
                        EnglishConversionRules$Conversion conv = (EnglishConversionRules$Conversion)conversions.elementAt(i);
                        switch (conv.getConversionType()) {
                           case 5:
                              break;
                           case 6:
                           default:
                              conversions.setElementAt(SIMPLE_PLURAL_POSSESSIVE_CONVERSION, i);
                              break;
                           case 7:
                              conversions.setElementAt(ES_PLURAL_POSSESSIVE_CONVERSION, i);
                              break;
                           case 8:
                              conversions.setElementAt(IES_PLURAL_POSSESSIVE_CONVERSION, i);
                        }
                     }
                     break label101;
                  default:
                     return;
               }
            case 'D':
            case 'd':
               switch (word[last - 1]) {
                  case 'N':
                  case 'R':
                  case 'n':
                  case 'r':
                     if (CharacterUtilities.isDigit(word[last - 2])) {
                        conversions.addElement(NUMBER_ADJ_CONVERSION);
                        return;
                     }

                     return;
                  default:
                     return;
               }
            case 'G':
            case 'g':
               this.checkIngEnding(word, offset, len, conversions);
               return;
            case 'H':
            case 'h':
               if ((word[last - 1] == 't' || word[last - 1] == 'T') && CharacterUtilities.isDigit(word[last - 2])) {
                  conversions.addElement(NUMBER_ADJ_CONVERSION);
                  return;
               }
               break;
            case 'S':
            case 's':
               switch (word[last - 1]) {
                  case '\'':
                     switch (word[last - 2]) {
                        case '\'':
                           return;
                        case 'S':
                        case 's':
                           int lastConvSize = conversions.size();
                           this.checkForConversion(word, offset, len - 2, conversions);

                           for (int i = lastConvSize; i < conversions.size(); i++) {
                              EnglishConversionRules$Conversion conv = (EnglishConversionRules$Conversion)conversions.elementAt(i);
                              switch (conv.getConversionType()) {
                                 case 5:
                                    break;
                                 case 6:
                                 default:
                                    conversions.setElementAt(INVALID_PLURAL_POSSESSIVE_CONVERSION, i);
                                    break;
                                 case 7:
                                    conversions.setElementAt(INVALID_ES_PLURAL_POSSESSIVE_CONVERSION, i);
                                    break;
                                 case 8:
                                    conversions.setElementAt(INVALID_IES_PLURAL_POSSESSIVE_CONVERSION, i);
                              }
                           }

                           return;
                        default:
                           conversions.addElement(POSSESSIVE_CONVERSION);
                           return;
                     }
                  case 'E':
                  case 'e':
                     conversions.addElement(SIMPLE_PLURAL_CONVERSION);
                     if ("sxzSXZ".indexOf(word[last - 2]) != -1) {
                        conversions.addElement(ES_PLURAL_CONVERSION);
                        return;
                     }

                     if (len >= 4 && (word[last - 2] == 'h' || word[last - 2] == 'H') && "csCS".indexOf(word[last - 3]) != -1) {
                        conversions.addElement(ES_PLURAL_CONVERSION);
                        return;
                     }

                     if (len >= 4 && (word[last - 2] == 'i' || word[last - 2] == 'I')) {
                        conversions.addElement(IES_PLURAL_CONVERSION);
                        return;
                     }

                     return;
                  case 'S':
                  case 's':
                     return;
                  default:
                     conversions.addElement(SIMPLE_PLURAL_CONVERSION);
                     return;
               }
            case 'T':
            case 't':
               if ((word[last - 1] == 's' || word[last - 1] == 'S') && CharacterUtilities.isDigit(word[last - 2])) {
                  conversions.addElement(NUMBER_ADJ_CONVERSION);
                  return;
               }
         }
      }
   }

   private void checkIngEnding(char[] word, int offset, int len, Vector conversions) {
      if (len >= 6) {
         int index = offset + len - 2;
         char ch = word[index];
         if (ch == 'n' || ch == 'N') {
            ch = word[--index];
            if (ch == 'i' || ch == 'I') {
               if ("eiEI".indexOf(word[index - 1]) != -1) {
                  conversions.addElement(SIMPLE_ING_CONVERSION);
               } else if (word[index - 1] == word[index - 2] && "bdfglmnprstvzBDFGLMNPRSTVZ".indexOf(word[index - 1]) != -1) {
                  conversions.addElement(DOUBLE_CONSONANT_ING_CONVERSION);
               } else {
                  conversions.addElement(SIMPLE_ING_CONVERSION);
                  conversions.addElement(CONTRACTION_ING_CONVERSION);
               }
            }
         }
      }
   }

   static void ensureSize(char[] buff, int len) {
      if (buff.length < len) {
         Array.extend(buff, len - buff.length);
      }
   }
}
