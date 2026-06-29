package net.rim.tid.im.conv.europe.spellcheck;

import java.util.Stack;
import net.rim.device.api.i18n.Locale;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.system.RIMGlobalMessagePoster;
import net.rim.device.api.util.CharacterUtilities;
import net.rim.device.internal.ui.StringBufferGap;
import net.rim.tid.awt.im.InputContext;
import net.rim.tid.awt.im.repository.CustomWordsRepository;
import net.rim.tid.im.conv.SLCurrentVariant;
import net.rim.tid.im.conv.europe.AccentGrouping;
import net.rim.tid.im.conv.europe.CaseCorrector;
import net.rim.tid.im.conv.europe.repository.AddressBookDataRepository;
import net.rim.tid.im.conv.europe.repository.EuropeanRepositoryData;
import net.rim.tid.im.conv.europe.repository.SpellCheckMetaphoneRepository;
import net.rim.tid.im.conv.repository.ExtendedCurrentVariant;
import net.rim.tid.im.conv.repository.IRepository;
import net.rim.tid.im.conv.repository.ReIterator;
import net.rim.tid.im.conv.repository.ResultContainer;
import net.rim.tid.itie.LinguisticData;
import net.rim.vm.Array;

public class SpellCheckVariantsCreator {
   private SpellCheckMetaphoneRepository _repository = new SpellCheckMetaphoneRepository();
   private boolean _useAddrBookRep;
   private IRepository _addrBookRep;
   private IMetaphone _metaphone;
   private PhoneticReplacements _phoneticReplacements;
   private boolean _fullMetaphone;
   private boolean _isCaseSensitive = true;
   private int _weightJump;
   private boolean _useLocaleSpecificRules;
   private LocaleConversionRules _localeRules;
   protected boolean _isAccentSensitive = true;
   protected AccentGrouping _accentGrouping = new AccentGrouping();
   private ReIterator _reIterator;
   private MetaphoneRegExprIterator _metaphoneReIterator;
   private Locale _locale;
   private char[] _tempBuffer = new char[50];
   private char[] _tempBuffer1 = new char[50];
   private char[] _tempBuffer2 = new char[50];
   private char[] _accentInsensitiveBuffer = new char[50];
   private char[] _lcBuffer = new char[50];
   private char[] _tempMetaphoneBuffer;
   private ResultContainer _wordExistRes = new ResultContainer();
   private SLCurrentVariant _variant1 = new SLCurrentVariant();
   private SLCurrentVariant _metaphoneKey1 = new SLCurrentVariant();
   private byte[] _tempAccentCount = new byte[2];
   private char[] _tempAccent = new char[10];
   private Stack _caseSensitiveWordStack = new Stack();
   private Stack _caseInsensitiveWordStack = new Stack();
   private Stack _ambiguousWordStack = new Stack();
   private Stack _editDistanceMatchStack = new Stack();
   private Stack _editDistanceMatchStateStack = new Stack();
   private Stack _multiClickMatchStack = new Stack();
   private Stack _resultContainerStack = new Stack();
   private Stack _wordStateStack = new Stack();
   private Stack _wordStack = new Stack();
   private Stack _charBufferStack = new Stack();
   private static final long ADDRESS_BOOK_KEY = 5765246712487104764L;
   private static final int CASE_SENSITIVE_MATCH = 1;
   private static final int CASE_INSENSITIVE_MATCH = 2;
   private static final int AMBIGUOUS_MATCH = 3;
   private static final int EDIT_DIST_1_MATCH = 1;
   private static final int MULTI_CLICK_MATCH = 2;
   private static boolean NEW_LOOKUP = true;

   public SpellCheckVariantsCreator() {
      this._metaphoneKey1._variants = new char[50];
      ResourceBundle bundle = ResourceBundle.getBundle(-7934727403592703506L, "net.rim.tid.im.options.SpellCheck.SpellCheck");
      this.setFullMetaphone(bundle.getString(1).equals("true"));
      this.setCaseSensitive(bundle.getString(0).equals("true"));
      this.setUseLocaleSpecificRules(bundle.getString(16).equals("true"));
      this._repository.enablePairLearning(bundle.getString(17).equals("true"));
      int jump = 5;

      label20:
      try {
         jump = Integer.parseInt(bundle.getString(6));
      } finally {
         break label20;
      }

      this.setWeightJump(jump);
   }

   public void setLocale(Locale aLocale) {
      if (!aLocale.equals(this._locale)) {
         this._repository.setLocale(aLocale);
         this._locale = aLocale;
         if (this._locale.getLanguage().equals("en")) {
            this._metaphone = new EnglishMetaphone(this);
            this._phoneticReplacements = new EnglishPhoneticReplacements();
            if (this._useLocaleSpecificRules) {
               this._localeRules = new EnglishConversionRules(this);
            }
         } else if (this._locale.getLanguage().equals("fr")) {
            if (this._useLocaleSpecificRules) {
               this._localeRules = new FrenchConversionRules(this);
            }

            this._metaphone = null;
            this._phoneticReplacements = null;
         } else if (this._locale.getLanguage().equals("it")) {
            if (this._useLocaleSpecificRules) {
               this._localeRules = new ItalianConversionRules(this);
            }

            this._metaphone = null;
            this._phoneticReplacements = null;
         } else {
            this._metaphone = null;
            this._phoneticReplacements = null;
            this._localeRules = null;
         }

         if (this._accentGrouping != null) {
            this._accentGrouping.setRepository(this._repository, this._locale);
         }

         if (this._addrBookRep != null) {
            this._addrBookRep.setLocale(this._locale);
         }
      }
   }

   public Locale getLocale() {
      return this._locale;
   }

   public int loadLinguisticData(LinguisticData data) {
      if (2 != this._repository.loadLinguisticData(data)) {
         int max = this._repository.getMaxNestingLevel() + 1;
         if (this._reIterator == null) {
            this._reIterator = new ReIterator(max);
         } else {
            this._reIterator.setMaxStackSize(max);
         }

         if (this._metaphoneReIterator != null) {
            this._metaphoneReIterator.setMaxStackSize(max);
         }

         return 1;
      } else {
         return 2;
      }
   }

   public void useAddressBookRepository(boolean use) {
      this._useAddrBookRep = use;
      if (this._useAddrBookRep && this._addrBookRep == null) {
         InputContext context = InputContext.getInstance();
         this._addrBookRep = (IRepository)context.getRepository(1);
         if (this._addrBookRep == null) {
            AddressBookDataRepository rep = new AddressBookDataRepository();
            rep.init(context.getRepository(2), context.getRepository(4));
            this._addrBookRep = rep;
         }
      }

      if (this._useAddrBookRep) {
         RIMGlobalMessagePoster.postGlobalEvent(3010955501926355683L);
      }
   }

   public void clearCustomDictionary() {
      this._repository.clearWordLearning();
   }

   public void clearLearningPairs() {
      this._repository.clearPairLearning();
   }

   public int getMinWordLength() {
      return this._repository.getMinWordLength();
   }

   public void setUseLocaleSpecificRules(boolean aVal) {
      if (this._useLocaleSpecificRules != aVal) {
         this._useLocaleSpecificRules = aVal;
         if (this._useLocaleSpecificRules && this._locale != null) {
            if (this._locale.getLanguage().equals("en")) {
               this._localeRules = new EnglishConversionRules(this);
               return;
            }

            if (this._locale.getLanguage().equals("fr")) {
               this._localeRules = new FrenchConversionRules(this);
               return;
            }

            if (this._locale.getLanguage().equals("it")) {
               this._localeRules = new ItalianConversionRules(this);
               return;
            }

            this._localeRules = null;
            return;
         }

         this._localeRules = null;
      }
   }

   public boolean getUseLocaleSpecificRules() {
      return this._useLocaleSpecificRules;
   }

   public void setWeightJump(int aJump) {
      this._weightJump = aJump;
   }

   public int getWeightJump() {
      return this._weightJump;
   }

   public void setFullMetaphone(boolean aVal) {
      this._fullMetaphone = aVal;
      if (aVal) {
         if (this._tempMetaphoneBuffer == null) {
            this._tempMetaphoneBuffer = new char[50];
            return;
         }
      } else {
         this._tempMetaphoneBuffer = null;
      }
   }

   public boolean getFullMetaphone() {
      return this._fullMetaphone;
   }

   public void setCaseSensitive(boolean aVal) {
      this._isCaseSensitive = aVal;
   }

   public boolean isCaseSensitive() {
      return this._isCaseSensitive;
   }

   public void setIgnoreWrongAccents(boolean aVal) {
      this._isAccentSensitive = !aVal;
   }

   public boolean getIgnoreWrongAccents() {
      return !this._isAccentSensitive;
   }

   public void setUseTransitivePairLearningSearch(boolean aVal) {
      this._repository.setUseTransitivePairLearningSearch(aVal);
   }

   public boolean getUseTransitivePairLearningSearch() {
      return this._repository.getUseTransitivePairLearningSearch();
   }

   public void clearPairLearning() {
      this._repository.clearPairLearning();
   }

   public void clearWordLearning() {
      this._repository.clearWordLearning();
   }

   public void setPairLearningSizeLimit(int aSizeLimit) {
      this._repository.setPairLearningSizeLimit(aSizeLimit);
   }

   public void setWordLearningSizeLimit(int aSizeLimit) {
      this._repository.setWordLearningSizeLimit(aSizeLimit);
   }

   public int getPairLearningSizeLimit() {
      return this._repository.getPairLearningSizeLimit();
   }

   public byte[] getPairLearningData() {
      return this._repository.getPairLearningData();
   }

   public byte[] getWordLearningData() {
      return this._repository.getWordLearningData();
   }

   public void setPairLearningData(byte[] aData) {
      this._repository.setPairLearningData(aData);
   }

   public void setWordLearningData(byte[] aData) {
      this._repository.setWordLearningData(aData);
   }

   public void reloadLearningData() {
      this._repository.reloadLearningData();
   }

   public void enablePairLearning(boolean aEnable) {
      this._repository.enablePairLearning(aEnable);
   }

   public int addPair(StringBuffer aMisspelledWord, SLCurrentVariant aCorrectWord) {
      int longest = this._repository.getLongestWordLength();
      if (aMisspelledWord.length() <= longest && aCorrectWord._length <= longest) {
         if (CharacterUtilities.isUpperCase(aCorrectWord._variants[aCorrectWord._offset])) {
            int len = aCorrectWord._length;
            if (len > this._tempBuffer.length) {
               this._tempBuffer = new char[len];
            }

            System.arraycopy(aCorrectWord._variants, aCorrectWord._offset, this._tempBuffer, 0, len);
            if (this.wordExists(this._tempBuffer, 0, len, this._tempBuffer, true, true, true) == 0) {
               CaseCorrector.toLowerCase(this._tempBuffer, 0, len, this._locale);
            }

            aCorrectWord._variants = this._tempBuffer;
            aCorrectWord._offset = 0;
         }

         return this._repository.addPair(aMisspelledWord, aCorrectWord);
      } else {
         return 1;
      }
   }

   public int addWord(SLCurrentVariant wordToAdd, boolean aConvertToLowerCase) {
      return this._repository.addWord(wordToAdd, aConvertToLowerCase);
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public int getVariants(StringBufferGap aWord, int aStart, int aEnd, SpellCheckResultContainer aRes) {
      int len = aEnd - aStart;
      int longest = this._repository.getLongestWordLength();
      aRes.setWeightJump(this._weightJump);
      if (len > longest + 10) {
         return aRes.getVariantsCount();
      }

      boolean storedCaseSensitivity = this._reIterator.isCaseSensitive();
      this._reIterator.setCaseSensitive(true);
      if (len + 1 > this._lcBuffer.length) {
         this._lcBuffer = new char[len + 1];
         this._tempBuffer = new char[len + 1];
         this._tempBuffer1 = new char[len - 1];
      }

      aWord.getChars(aStart, aEnd, this._lcBuffer, 0);
      aWord.getChars(aStart, aEnd, this._tempBuffer, 0);
      boolean is_capitalized = false;
      boolean is_uc = true;

      for (int i = 0; i < len; i++) {
         char ch = this._lcBuffer[i];
         this._lcBuffer[i] = CaseCorrector.toLowerCase(this._lcBuffer[i], this._locale);
         boolean currIsUc = ch != this._lcBuffer[i];
         if (i == 0) {
            is_capitalized = currIsUc;
         } else {
            is_capitalized = is_capitalized && !currIsUc;
         }

         is_uc = is_uc && currIsUc;
      }

      char[] metaphone = null;
      int meta_len = 0;
      this._variant1._variants = this._lcBuffer;
      this._variant1._length = len;
      if (this._metaphone != null) {
         if (len << 1 > this._metaphoneKey1._variants.length) {
            this._metaphoneKey1._variants = new char[len << 1];
         }

         this._metaphone.getMetaphoneKey(this._variant1, this._metaphoneKey1);
         meta_len = this._metaphoneKey1._length;
         metaphone = this._metaphoneKey1._variants;
      }

      aRes.setOriginalWord(this._tempBuffer, len, metaphone, meta_len, this._metaphone);
      aRes.setConversionFunction((byte)1);
      PairLearningReader pairLearning = this._repository.getPairLearning();
      if (pairLearning != null) {
         aRes.setCurrentSource((byte)6);
         this._reIterator.init(this._lcBuffer, 0, len, false);
         aRes.setMinWordLength(1);
         aRes.setMaxWordLength(2147483646);
         pairLearning.getVariants(this._reIterator, aRes);
      }

      boolean skip_search = false;
      if (!skip_search) {
         if (!NEW_LOOKUP) {
            this.dividedWordSearch(aWord, aStart, len, this._lcBuffer, aRes);
            skip_search = this.regularSearch(this._lcBuffer, len, aRes);
            if (!skip_search && this._localeRules != null) {
               this._localeRules.getVariants(this._lcBuffer, len, aRes);
            }
         } else {
            aRes.setConversionFunction((byte)7);
            Word word = this.popWord();
            boolean var21 = false /* VF: Semaphore variable */;

            try {
               var21 = true;
               word.init(aWord, aStart, len, this._locale);
               aRes.setCapitalize(word.isCapitalized);
               aRes.setAllUpperCase(!word.containsLower);
               this.dividedWordSearch(word, aRes);
               skip_search = this.regularSearch(word, aRes);
               if (!skip_search) {
                  if (this._localeRules != null) {
                     this._localeRules.getVariants(word, aRes);
                     var21 = false;
                  } else {
                     var21 = false;
                  }
               } else {
                  var21 = false;
               }
            } finally {
               if (var21) {
                  this.pushWord(word);
               }
            }

            this.pushWord(word);
         }
      }

      if (!skip_search && aRes.getVariantsCount() < 1 && this._metaphone != null) {
         skip_search = this.metaphoneSearch(metaphone, meta_len, aRes);
      }

      if (!skip_search && aRes.getVariantsCount() < 1) {
         skip_search = this.greatestCommonPrefixSearch(this._lcBuffer, len, aRes);
      }

      if (!NEW_LOOKUP) {
         char[] words = aRes.getWords();
         byte[] lengths = aRes.getLengths();
         int offset = 0;

         label213:
         for (int i = 0; i < aRes.getVariantsCount(); i++) {
            if (CharacterUtilities.isUpperCase(words[offset])) {
               int offset1 = 0;

               for (int j = 0; j < aRes.getVariantsCount(); j++) {
                  if (offset != offset1 && lengths[i] == lengths[j] && this.wordsEqualIgnoreCase(words, offset, offset1, lengths[i])) {
                     aRes.removeWordAtDirectIndex(i);
                     i--;
                     continue label213;
                  }

                  offset1 += lengths[j];
               }
            }

            offset += lengths[i];
         }

         if (is_capitalized) {
            int var28 = 0;

            for (int i = 0; i < aRes.getVariantsCount(); i++) {
               words[var28] = CaseCorrector.toUpperCase(words[var28], this._locale);
               var28 += lengths[i];
            }
         } else if (is_uc) {
            CaseCorrector.toUpperCase(words, aRes.getTotalLength(), this._locale);
         }
      }

      this._reIterator.setCaseSensitive(storedCaseSensitivity);
      return aRes.getVariantsCount();
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public int getAmbiguousVariants(StringBufferGap str, int start, int end, SpellCheckResultContainer res) {
      int len = end - start;
      res.setConversionFunction((byte)7);
      Word word = this.popWord();
      SpellCheckWordMatch expr = this.popWordMatchExpr(3);
      SpellCheckWordMatchState state = this.popWordMatchState();
      boolean var12 = false /* VF: Semaphore variable */;

      int var9;
      try {
         var12 = true;
         word.init(str, start, len, this._locale);
         res.setCapitalize(word.isCapitalized);
         res.setAllUpperCase(!word.containsLower);
         expr.setWord(word);
         state.set(expr);
         this._repository.matches(expr, state, res, this._isCaseSensitive);
         var9 = res.getVariantsCount();
         var12 = false;
      } finally {
         if (var12) {
            this.pushWord(word);
            this.pushWordMatchExpr(3, expr);
            this.pushWordMatchState(state);
         }
      }

      this.pushWord(word);
      this.pushWordMatchExpr(3, expr);
      this.pushWordMatchState(state);
      return var9;
   }

   public boolean greatestCommonPrefixSearch(char[] word, int len, SpellCheckResultContainer result) {
      if (!result.setConversionFunction((byte)14)) {
         return true;
      }

      int savedMaxLen = result.getMaxWordLength();
      int savedMinLen = result.getMinWordLength();
      result.setMaxWordLength(len);

      for (int i = len - 2; i >= 1; i--) {
         char tmp = word[i];
         word[i] = 4;
         result.setMinWordLength(i + 1);
         this.getVariants(word, 0, i + 1, result);
         word[i] = tmp;
         if (!result.setConversionFunction((byte)14)) {
            result.setMinWordLength(savedMinLen);
            result.setMaxWordLength(savedMaxLen);
            return true;
         }

         if (i < len >> 1 && result.getVariantsCount() > 0) {
            break;
         }
      }

      result.setMinWordLength(savedMinLen);
      result.setMaxWordLength(savedMaxLen);
      return false;
   }

   public boolean regularSearch(char[] aWord, int aLen, SpellCheckResultContainer aRes) {
      int min = aRes.getMinWordLength();
      int max = aRes.getMaxWordLength();
      if (!aRes.setConversionFunction((byte)2)) {
         return true;
      }

      aRes.setMinWordLength(aLen);
      aRes.setMaxWordLength(aLen);
      this.invokeAccentInsensitiveRegExprGenerator(false, aWord, 0, aLen, aWord, false, false, false, false, aRes);
      if (!aRes.setConversionFunction((byte)3)) {
         return true;
      }

      if (this._phoneticReplacements != null) {
         this._phoneticReplacements.init(aWord, aLen);

         SLCurrentVariant repl;
         while ((repl = this._phoneticReplacements.nextReplacement()) != null) {
            aRes.setMinWordLength(repl._length);
            aRes.setMaxWordLength(repl._length);
            this.getVariants(repl._variants, 0, repl._length, aRes);
         }
      }

      if (!aRes.setConversionFunction((byte)7)) {
         return true;
      }

      aRes.setMinWordLength(aLen);
      aRes.setMaxWordLength(aLen);

      for (int i = 0; i < aLen; i++) {
         char temp = aWord[i];
         aWord[i] = 4;
         this.getVariants(aWord, 0, aLen, aRes);
         aWord[i] = temp;
      }

      if (!aRes.setConversionFunction((byte)4)) {
         return true;
      }

      aRes.setMinWordLength(aLen + 1);
      aRes.setMaxWordLength(aLen + 1);

      for (int i = aLen; i >= 0; i--) {
         aWord[i] = 4;
         this.getVariants(aWord, 0, aLen + 1, aRes);
         if (i > 0) {
            aWord[i] = aWord[i - 1];
         }
      }

      System.arraycopy(aWord, 1, aWord, 0, aLen);
      if (!aRes.setConversionFunction((byte)5)) {
         return true;
      }

      aRes.setMinWordLength(aLen);
      aRes.setMaxWordLength(aLen);

      for (int i = 0; i < aLen - 1; i++) {
         if (aWord[i] != aWord[i + 1]) {
            char temp = aWord[i];
            aWord[i] = aWord[i + 1];
            aWord[i + 1] = temp;
            this._reIterator.init(aWord, 0, aLen);
            this.getVariants(this._reIterator, aRes);
            aWord[i + 1] = aWord[i];
            aWord[i] = temp;
         }
      }

      if (!aRes.setConversionFunction((byte)6)) {
         return true;
      }

      aRes.setMinWordLength(aLen - 1);
      aRes.setMaxWordLength(aLen - 1);

      for (int i = 0; i < aLen; i++) {
         System.arraycopy(aWord, 0, this._tempBuffer1, 0, i);
         System.arraycopy(aWord, i + 1, this._tempBuffer1, i, aLen - i - 1);
         this._reIterator.init(this._tempBuffer1, 0, aLen - 1);
         this.getVariants(this._reIterator, aRes);
      }

      aRes.setMinWordLength(min);
      aRes.setMaxWordLength(max);
      return false;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public boolean regularSearch(Word word, SpellCheckResultContainer res) {
      EditDistance1WordMatch expr = this.popEditDistanceMatch();
      EditDistance1WordMatchState state = this.popEditDistanceMatchState();
      boolean var10 = false /* VF: Semaphore variable */;

      boolean var16;
      try {
         var10 = true;
         expr.setWord(word, this._locale, this._accentGrouping);
         state.set(expr);
         this._repository.matches(expr, state, res, this._isCaseSensitive);
         if (res.getVariantsCount() < 1) {
            MultiClickWordMatch mexpr = this.popMultiClickMatch();
            boolean var13 = false /* VF: Semaphore variable */;

            try {
               var13 = true;
               mexpr.setWord(word, this._locale, this._accentGrouping);
               state.set(mexpr);
               this._repository.matches(mexpr, state, res, this._isCaseSensitive);
               var13 = false;
            } finally {
               if (var13) {
                  this.pushMultiClickMatch(mexpr);
               }
            }

            this.pushMultiClickMatch(mexpr);
         }

         var16 = !res.setConversionFunction((byte)6);
         var10 = false;
      } finally {
         if (var10) {
            this.pushEditDistanceMatch(expr);
            this.pushEditDistanceMatchState(state);
         }
      }

      this.pushEditDistanceMatch(expr);
      this.pushEditDistanceMatchState(state);
      return var16;
   }

   public void getVariants(ReIterator aIterator, SpellCheckResultContainer aRes) {
      this._repository.getVariants(aIterator, aRes);
      if (this._useAddrBookRep && this._addrBookRep != null) {
         aIterator.reset(false);
         this._addrBookRep.getVariants(aIterator, aRes);
      }
   }

   private void getVariants(char[] aWord, int aOffset, int aLength, SpellCheckResultContainer aRes) {
      this._reIterator.init(aWord, aOffset, aLength, true);
      this.getVariants(this._reIterator, aRes);
   }

   public int wordExists(StringBufferGap aWord, int aStart, int aEnd) {
      return this.wordExists(aWord, aStart, aEnd, true);
   }

   public int wordExists(StringBufferGap aWord, int aStart, int aEnd, boolean aApplyLocaleRules) {
      int len = aEnd - aStart;
      if (len > this._tempBuffer1.length) {
         this._tempBuffer1 = new char[len];
      }

      aWord.getChars(aStart, aEnd, this._tempBuffer1, 0);
      return this.wordExists(this._tempBuffer1, 0, len, aApplyLocaleRules);
   }

   public int wordExists(StringBufferGap aWord, int aStart, int aEnd, char[] aLcWord, boolean aApplyLocaleRules) {
      int len = aEnd - aStart;
      if (len > this._tempBuffer1.length) {
         this._tempBuffer1 = new char[len];
      }

      aWord.getChars(aStart, aEnd, this._tempBuffer1, 0);
      return this.wordExists(this._tempBuffer1, 0, len, aLcWord, aApplyLocaleRules, this._isCaseSensitive, false);
   }

   public int wordExists(char[] aWord, int aOffset, int aLen, boolean aApplyLocaleRules) {
      if (aLen > this._lcBuffer.length) {
         this._lcBuffer = new char[aLen];
      }

      System.arraycopy(aWord, aOffset, this._lcBuffer, 0, aLen);

      for (int i = 0; i < aLen; i++) {
         this._lcBuffer[i] = CaseCorrector.toLowerCase(this._lcBuffer[i], this._locale);
      }

      return this.wordExists(aWord, aOffset, aLen, this._lcBuffer, aApplyLocaleRules, this._isCaseSensitive, false);
   }

   public int wordExists(char[] aWord, int aOffset, int aLen, char[] aLcWord, boolean aApplyLocaleRules) {
      return this.wordExists(aWord, aOffset, aLen, aLcWord, aApplyLocaleRules, this._isCaseSensitive, false);
   }

   public boolean exists(Word word) {
      if (!word.containsAlpha) {
         return true;
      } else {
         return this.existsInRepository(word) ? true : this.existsAsHyphenatedWord(word);
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public int existsAmbiguous(Word word) {
      if (!word.containsAlpha) {
         return 1;
      }

      SpellCheckWordMatch expr = this.popWordMatchExpr(3);
      SpellCheckWordMatchState state = this.popWordMatchState();
      ResultContainer res = this.popResultContainer();
      boolean var9 = false /* VF: Semaphore variable */;

      int var13;
      label68: {
         label67: {
            label66: {
               try {
                  var9 = true;
                  expr.setWord(word);
                  state.set(expr);
                  res.reset(2, 0, Integer.MAX_VALUE, true, true);
                  boolean exists = this._repository.matches(expr, state, res, this._isCaseSensitive);
                  if (exists) {
                     var13 = res.getVariantsCount();
                     var9 = false;
                     break label68;
                  }

                  if (this.existsAsHyphenatedWord(word)) {
                     var12 = 1;
                     var9 = false;
                     break label67;
                  }

                  if (this._localeRules != null && this._localeRules.exists(word)) {
                     var11 = 1;
                     var9 = false;
                     break label66;
                  }

                  var6 = 0;
                  var9 = false;
               } finally {
                  if (var9) {
                     this.pushWordMatchExpr(3, expr);
                     this.pushWordMatchState(state);
                     this.pushResultContainer(res);
                  }
               }

               this.pushWordMatchExpr(3, expr);
               this.pushWordMatchState(state);
               this.pushResultContainer(res);
               return var6;
            }

            this.pushWordMatchExpr(3, expr);
            this.pushWordMatchState(state);
            this.pushResultContainer(res);
            return var11;
         }

         this.pushWordMatchExpr(3, expr);
         this.pushWordMatchState(state);
         this.pushResultContainer(res);
         return var12;
      }

      this.pushWordMatchExpr(3, expr);
      this.pushWordMatchState(state);
      this.pushResultContainer(res);
      return var13;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public int getFrequency(Word word) {
      if (!word.containsAlpha) {
         return Integer.MAX_VALUE;
      }

      SpellCheckWordMatch expr;
      if (this._isCaseSensitive && word.containsLower) {
         expr = this.popWordMatchExpr(1);
      } else {
         expr = this.popWordMatchExpr(2);
      }

      expr.setWord(word);
      SpellCheckWordMatchState state = this.popWordMatchState();
      state.set(expr);
      ResultContainer res = this.popResultContainer();
      res.reset(1, 0, Integer.MAX_VALUE, true, true);
      boolean var8 = false /* VF: Semaphore variable */;

      int var10;
      label106: {
         try {
            var8 = true;
            this._repository.matches(expr, state, res, this._isCaseSensitive);
            switch (res.getVariantsCount()) {
               case 0:
                  var10 = 0;
                  var8 = false;
                  break label106;
            }

            var10 = res.getMinFrequency();
            var8 = false;
         } finally {
            if (var8) {
               if (this._isCaseSensitive && word.containsLower) {
                  this.pushWordMatchExpr(1, expr);
               } else {
                  this.pushWordMatchExpr(2, expr);
               }

               this.pushWordMatchState(state);
               this.pushResultContainer(res);
            }
         }

         if (this._isCaseSensitive && word.containsLower) {
            this.pushWordMatchExpr(1, expr);
         } else {
            this.pushWordMatchExpr(2, expr);
         }

         this.pushWordMatchState(state);
         this.pushResultContainer(res);
         return var10;
      }

      if (this._isCaseSensitive && word.containsLower) {
         this.pushWordMatchExpr(1, expr);
      } else {
         this.pushWordMatchExpr(2, expr);
      }

      this.pushWordMatchState(state);
      this.pushResultContainer(res);
      return var10;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private boolean existsInRepository(Word word) {
      ResultContainer res = null;
      SpellCheckWordMatch expr;
      if (this._isCaseSensitive && word.containsLower) {
         expr = this.popWordMatchExpr(1);
      } else {
         expr = this.popWordMatchExpr(2);
      }

      expr.setWord(word);
      SpellCheckWordMatchState state = this.popWordMatchState();
      state.set(expr);
      boolean var10 = false /* VF: Semaphore variable */;

      boolean var14;
      label199: {
         label200: {
            boolean var7;
            label201: {
               try {
                  var10 = true;
                  if (this._repository.matches(expr, state, null, this._isCaseSensitive)) {
                     var14 = true;
                     var10 = false;
                     break label199;
                  }

                  if (this._localeRules != null && this._localeRules.exists(word)) {
                     var14 = true;
                     var10 = false;
                     break label200;
                  }

                  if (this._useAddrBookRep && this._addrBookRep != null) {
                     Word lower = word.getLower();
                     int len = lower.length();
                     this._reIterator.init(lower.text, lower.start, len, true);
                     this._reIterator.setCaseSensitive(false);
                     this._reIterator.reset(false);
                     res = this.popResultContainer();
                     res.reset(1, len, len, true, true);
                     this._addrBookRep.getVariants(this._reIterator, res);
                     if (res.getVariantsCount() > 0) {
                        var7 = true;
                        var10 = false;
                        break label201;
                     }
                  }

                  var14 = false;
                  var10 = false;
               } finally {
                  if (var10) {
                     if (this._isCaseSensitive && word.containsLower) {
                        this.pushWordMatchExpr(1, expr);
                     } else {
                        this.pushWordMatchExpr(2, expr);
                     }

                     this.pushWordMatchState(state);
                     if (res != null) {
                        this.pushResultContainer(res);
                     }
                  }
               }

               if (this._isCaseSensitive && word.containsLower) {
                  this.pushWordMatchExpr(1, expr);
               } else {
                  this.pushWordMatchExpr(2, expr);
               }

               this.pushWordMatchState(state);
               if (res != null) {
                  this.pushResultContainer(res);
               }

               return var14;
            }

            if (this._isCaseSensitive && word.containsLower) {
               this.pushWordMatchExpr(1, expr);
            } else {
               this.pushWordMatchExpr(2, expr);
            }

            this.pushWordMatchState(state);
            if (res != null) {
               this.pushResultContainer(res);
            }

            return var7;
         }

         if (this._isCaseSensitive && word.containsLower) {
            this.pushWordMatchExpr(1, expr);
         } else {
            this.pushWordMatchExpr(2, expr);
         }

         this.pushWordMatchState(state);
         if (res != null) {
            this.pushResultContainer(res);
         }

         return var14;
      }

      if (this._isCaseSensitive && word.containsLower) {
         this.pushWordMatchExpr(1, expr);
      } else {
         this.pushWordMatchExpr(2, expr);
      }

      this.pushWordMatchState(state);
      if (res != null) {
         this.pushResultContainer(res);
      }

      return var14;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private boolean existsAsHyphenatedWord(Word word) {
      int wlen = word.length();
      int index = word.indexOf('-', 1, wlen - 1);
      if (index == -1) {
         return false;
      }

      Word subword = this.popWord();
      boolean var9 = false /* VF: Semaphore variable */;

      int i;
      label71: {
         boolean var6;
         label70: {
            try {
               var9 = true;
               word.subword(0, index, subword);
               if (!this.exists(subword)) {
                  i = 0;
                  var9 = false;
                  break label71;
               }

               i = index + 1;

               while (i < wlen) {
                  index = word.indexOf('-', i, wlen - 1);
                  if (index == -1) {
                     break;
                  }

                  word.subword(i, index, subword);
                  if (!this.exists(subword)) {
                     var6 = false;
                     var9 = false;
                     break label70;
                  }

                  i = index + 1;
               }

               word.subword(i, wlen, subword);
               var6 = this.exists(subword);
               var9 = false;
            } finally {
               if (var9) {
                  this.pushWord(subword);
               }
            }

            this.pushWord(subword);
            return var6;
         }

         this.pushWord(subword);
         return var6;
      }

      this.pushWord(subword);
      return (boolean)i;
   }

   public int wordExists(
      char[] aOrigWord, int aOffset, int aLen, char[] aLcWord, boolean aApplyLocaleRules, boolean aCaseFixMode, boolean aFirstLetterStrictCase
   ) {
      if (aLen == 1) {
         return !CharacterUtilities.isLetter(aOrigWord[aOffset]) ? 1 : this._repository.word1CharExists(aOrigWord, aOffset);
      }

      boolean isUc = true;
      boolean isLc = true;
      boolean containsAlpha = false;

      for (int i = aOffset; i < aLen + aOffset; i++) {
         if (CharacterUtilities.isLetter(aOrigWord[i])) {
            containsAlpha = true;
            if (!CharacterUtilities.isUpperCase(aOrigWord[i])) {
               isUc = false;
            } else {
               isLc = false;
            }
         }
      }

      if (!containsAlpha) {
         return 1;
      }

      boolean isMixedCase = !isUc && !isLc;
      ResultContainer res = this._wordExistRes;
      res.reset(3, aLen, aLen, true, true);
      this._reIterator.init(aLcWord, 0, aLen, true);
      boolean storedCaseSensitivity = this._reIterator.isCaseSensitive();
      this._reIterator.setCaseSensitive(this.isCaseSensitive() && !isMixedCase);
      this._repository.getVariantsForWordExist(this._reIterator, res);
      int freq = this.resultFound(aOrigWord, aOffset, aLen, res, aCaseFixMode, aFirstLetterStrictCase, isUc);
      if (freq > 0) {
         return freq;
      }

      if (this._useAddrBookRep && this._addrBookRep != null) {
         res.reset(3, aLen, aLen, true, true);
         this._reIterator.reset(false);
         this._addrBookRep.getVariants(this._reIterator, res);
         freq = this.resultFound(aOrigWord, aOffset, aLen, res, aCaseFixMode, aFirstLetterStrictCase, isUc);
         if (freq > 0) {
            return freq;
         }
      }

      if (aApplyLocaleRules && this._localeRules != null) {
         freq = this._localeRules.wordExists(aOrigWord, aOffset, aLen, aLcWord);
         if (freq > 0) {
            return freq;
         }
      }

      int wordStart = aOffset;
      int end = aLen + aOffset;
      int hyphenFreq = 0;
      if (this._tempBuffer2.length < aLen) {
         this._tempBuffer2 = new char[aLen];
      }

      for (int i = aOffset; i < end; i++) {
         if (aOrigWord[i] == '-') {
            int wordLen = i - wordStart;
            if (wordLen > 0) {
               if (wordLen == 1) {
                  hyphenFreq = 32768;
               } else {
                  System.arraycopy(aLcWord, 0, this._tempBuffer2, 0, aLen);
                  System.arraycopy(aLcWord, wordStart - aOffset, aLcWord, 0, wordLen);
                  hyphenFreq = this.wordExists(aOrigWord, wordStart, wordLen, aLcWord, aApplyLocaleRules, aCaseFixMode, aFirstLetterStrictCase);
                  System.arraycopy(this._tempBuffer2, 0, aLcWord, 0, aLen);
               }

               if (hyphenFreq == 0) {
                  break;
               }
            }

            wordStart = i + 1;
         }
      }

      if (hyphenFreq > 0) {
         int wordLen = end - wordStart;
         System.arraycopy(aLcWord, 0, this._tempBuffer2, 0, aLen);
         System.arraycopy(aLcWord, wordStart - aOffset, aLcWord, 0, wordLen);
         freq = this.wordExists(aOrigWord, wordStart, wordLen, aLcWord, aApplyLocaleRules, aCaseFixMode, aFirstLetterStrictCase);
         System.arraycopy(this._tempBuffer2, 0, aLcWord, 0, aLen);
         if (freq > 0) {
            return freq;
         }
      }

      if (!this._isAccentSensitive) {
         freq = this.invokeAccentInsensitiveRegExprGenerator(
            true, aOrigWord, aOffset, aLen, aLcWord, aApplyLocaleRules, aCaseFixMode, aFirstLetterStrictCase, isUc, null
         );
         if (freq > 0) {
            return freq;
         }
      }

      this._reIterator.setCaseSensitive(storedCaseSensitivity);
      return 0;
   }

   private int resultFound(
      char[] aBuffer, int aOffset, int aLen, ResultContainer aRes, boolean aCaseFixMode, boolean aFirstLetterStrictCase, boolean aIsUpperCase
   ) {
      int offset = 0;
      char[] data = aRes.getWords();
      int i = 0;

      label57:
      while (i < aRes.getVariantsCount()) {
         for (int j = 0; j < aLen; j++) {
            if (aBuffer[aOffset + j] != data[offset + j]) {
               boolean is_incorrect = true;
               boolean ignore_case = false;
               if (!aCaseFixMode || aIsUpperCase) {
                  ignore_case = true;
                  if (CaseCorrector.toUpperCase(data[offset + j], this._locale) == CaseCorrector.toUpperCase(aBuffer[aOffset + j], this._locale)) {
                     is_incorrect = false;
                  }
               } else if (j == 0 && !aFirstLetterStrictCase) {
                  if (CaseCorrector.toUpperCase(data[offset + j], this._locale) == aBuffer[aOffset + j]) {
                     is_incorrect = false;
                  }

                  ignore_case = true;
               }

               if (is_incorrect && this._accentGrouping != null && this._accentGrouping.areFromOneGroup(aBuffer[aOffset + j], data[offset + j], ignore_case)) {
                  is_incorrect = false;
               }

               if (is_incorrect) {
                  i++;
                  offset += aLen;
                  continue label57;
               }
            }
         }

         return aRes.getFrequencies()[i] + 1;
      }

      return 0;
   }

   private boolean wordsEqualIgnoreCase(char[] aWords, int aOffset, int aOffset1, int aLength) {
      for (int i = 0; i < aLength; i++) {
         if (CaseCorrector.toLowerCase(aWords[aOffset + i], this._locale) != CaseCorrector.toLowerCase(aWords[aOffset1 + i], this._locale)) {
            return false;
         }
      }

      return true;
   }

   private int invokeAccentInsensitiveRegExprGenerator(
      boolean aCheckWordExistence,
      char[] aOrigWord,
      int aOffset,
      int aLen,
      char[] aLcWord,
      boolean aApplyLocaleRules,
      boolean aCaseFixMode,
      boolean aFirstLetterStrictCase,
      boolean aIsUc,
      SpellCheckResultContainer aRes
   ) {
      int regExprLen = 0;
      int end = aLen + aOffset;
      boolean variantsCollected = false;
      this._tempAccentCount[0] = this._tempAccentCount[1] = 0;

      for (int i = aOffset; i < end; i++) {
         String accents = this._accentGrouping.getAccents(aOrigWord[i]);
         if (i - aOffset == 1) {
            this._tempAccentCount[1] = this._tempAccentCount[0];
         }

         if (accents == null) {
            if (i - aOffset < 2) {
               this._tempAccentCount[i - aOffset]++;
               this._tempAccent[this._tempAccentCount[i - aOffset]] = aLcWord[i];
            }

            this._accentInsensitiveBuffer[regExprLen++] = aLcWord[i];
         } else if (i - aOffset >= 2) {
            int len = accents.length();
            this._accentInsensitiveBuffer[regExprLen++] = 2;
            this._accentInsensitiveBuffer[regExprLen++] = aLcWord[i];
            accents.getChars(0, len, this._accentInsensitiveBuffer, regExprLen);
            regExprLen += len;
            this._accentInsensitiveBuffer[regExprLen++] = 3;
         } else {
            char store_ch = aOrigWord[i];
            char store_ch1 = aLcWord[i - aOffset];
            this._tempAccentCount[i - aOffset]++;
            this._tempAccent[this._tempAccentCount[i - aOffset]] = store_ch1;

            for (int j = 0; j < accents.length(); j++) {
               aOrigWord[i] = accents.charAt(j);
               this._tempAccentCount[i - aOffset]++;
               aLcWord[i - aOffset] = this._tempAccent[this._tempAccentCount[i - aOffset]] = CaseCorrector.toLowerCase(aOrigWord[i], this._locale);
               if (aCheckWordExistence) {
                  int freq = this.wordExists(aOrigWord, aOffset, aLen, aLcWord, aApplyLocaleRules, aCaseFixMode, aFirstLetterStrictCase);
                  if (freq > 0) {
                     return freq;
                  }
               } else {
                  this.getVariants(aLcWord, aOffset, aLen, aRes);
                  variantsCollected = true;
               }
            }

            aOrigWord[i] = store_ch;
            this._accentInsensitiveBuffer[regExprLen++] = store_ch;
            aLcWord[i - aOffset] = store_ch1;
         }
      }

      if (regExprLen != aLen || !aCheckWordExistence && !variantsCollected) {
         if (aCheckWordExistence) {
            ResultContainer res = this._wordExistRes;
            res.reset(3, aLen, aLen, true, true);
            this._repository.getVariantsForWordExist(this._accentInsensitiveBuffer, regExprLen, res);
            int freq = this.resultFound(aOrigWord, aOffset, aLen, res, aCaseFixMode, aFirstLetterStrictCase, aIsUc);
            if (freq > 0) {
               return freq;
            }

            if (this._useAddrBookRep && this._addrBookRep != null) {
               res.reset(3, aLen, aLen, true, true);
               this._reIterator.init(this._accentInsensitiveBuffer, 0, regExprLen, false);
               this._addrBookRep.getVariants(this._reIterator, res);
               freq = this.resultFound(aOrigWord, aOffset, aLen, res, aCaseFixMode, aFirstLetterStrictCase, aIsUc);
               if (freq > 0) {
                  return freq;
               }
            }
         } else {
            for (int i = 0; i < this._tempAccentCount[0]; i++) {
               this._accentInsensitiveBuffer[0] = this._tempAccent[i];

               for (int j = this._tempAccentCount[0]; j < this._tempAccentCount[1]; j++) {
                  this._accentInsensitiveBuffer[1] = this._tempAccent[j];
                  this.getVariants(this._accentInsensitiveBuffer, 0, regExprLen, aRes);
               }
            }
         }
      }

      return 0;
   }

   public MetaphoneRegExprIterator getMetaphoneRegExprIterator() {
      if (this._metaphoneReIterator == null) {
         this._metaphoneReIterator = new MetaphoneRegExprIterator(this._repository.getMaxNestingLevel() + 1);
         this._metaphoneReIterator.setCaseSensitive(true);
      }

      return this._metaphoneReIterator;
   }

   public SpellCheckMetaphoneRepository getMainRepository() {
      return this._repository;
   }

   public CustomWordsRepository getRepository(int type) {
      return this._addrBookRep != null && ((CustomWordsRepository)this._addrBookRep).getType() == type ? (CustomWordsRepository)this._addrBookRep : null;
   }

   public void setRepositoryData(EuropeanRepositoryData aData) {
      this._repository.setRepositoryData(aData);
   }

   private boolean dividedWordSearch(StringBufferGap word, int offset, int len, char[] lcBuffer, SpellCheckResultContainer res) {
      if (!res.setConversionFunction((byte)8)) {
         return true;
      }

      word.getChars(offset, offset + len, this._tempBuffer, 0);

      for (int i = 0; i < len - 1; i++) {
         int freq1 = this.wordExists(this._tempBuffer, 0, i + 1, lcBuffer, false);
         if (freq1 > 0) {
            int firstWordEnd = i + 1;

            while (CharacterUtilities.isPunctuation(this._tempBuffer[firstWordEnd])) {
               firstWordEnd++;
            }

            System.arraycopy(lcBuffer, firstWordEnd, this._tempBuffer1, 0, len - firstWordEnd);
            int freq2 = this.wordExists(this._tempBuffer, firstWordEnd, len - firstWordEnd, this._tempBuffer1, false);
            if (freq2 > 0) {
               word.getChars(offset, offset + firstWordEnd, this._tempBuffer1, 0);
               this._tempBuffer1[firstWordEnd] = ' ';
               word.getChars(offset + firstWordEnd, offset + len, this._tempBuffer1, firstWordEnd + 1);
               char freq = (char)Math.min(freq1, freq2);
               if (!res.isFull()) {
                  ExtendedCurrentVariant insertedWord = res.getTempInsertedWordContainer();
                  insertedWord.setData(this._tempBuffer1, 0, len + 1, freq);
                  insertedWord._distance = 1;
                  insertedWord._subDistance = 0;
                  res.insertWord(insertedWord);
               }
            }
         }
      }

      return false;
   }

   private boolean dividedWordSearch(Word word, SpellCheckResultContainer res) {
      int len = word.length();
      Word subword = null;
      Word subword2 = null;

      for (int i = 1; i < len; i++) {
         subword = word.subword(0, i, subword);
         if (this.exists(subword)) {
            subword2 = word.subword(i, len, subword2);
            if (this.exists(subword2)) {
               int freq1 = this.getFrequency(subword);
               int freq2 = this.getFrequency(subword2);
               char[] buff = this.popBuffer(len + 1);
               subword.getChars(0, i, buff, 0);
               buff[i] = ' ';
               subword2.getChars(0, subword2.length(), buff, i + 1);
               int freq = Math.min(freq1, freq2);
               ExtendedCurrentVariant insertedWord = res.getTempInsertedWordContainer();
               insertedWord.setData(buff, 0, len + 1, freq);
               insertedWord._distance = 1;
               insertedWord._subDistance = 0;
               res.insertWord(insertedWord);
               this.pushBuffer(buff);
               return false;
            }
         }
      }

      return false;
   }

   private boolean metaphoneSearch(char[] metaphone, int metaLen, SpellCheckResultContainer res) {
      if (!res.setConversionFunction((byte)9)) {
         return true;
      }

      res.setMinWordLength(0);
      res.setMaxWordLength(2147483646);
      this._metaphone.getVariants(metaphone, metaLen, res);
      if (!this._fullMetaphone) {
         return false;
      }

      String meta_vowels = this._metaphone.getMetaVowels();
      if (!res.setConversionFunction((byte)10)) {
         return true;
      }

      for (int i = metaLen; i >= 0 && (i != 0 || meta_vowels.indexOf(metaphone[0]) == -1); i--) {
         metaphone[i] = 1;
         this._metaphone.getVariants(metaphone, metaLen + 1, res);
         if (i > 0) {
            metaphone[i] = metaphone[i - 1];
         }
      }

      System.arraycopy(metaphone, 1, metaphone, 0, metaLen);
      if (!res.setConversionFunction((byte)11)) {
         return true;
      }

      for (int i = 0; i < metaLen - 1; i++) {
         if (metaphone[i] != metaphone[i + 1] && (i > 0 || meta_vowels.indexOf(metaphone[0]) == -1)) {
            char temp = metaphone[i];
            metaphone[i] = metaphone[i + 1];
            metaphone[i + 1] = temp;
            this._metaphone.getVariants(metaphone, metaLen, res);
            metaphone[i + 1] = metaphone[i];
            metaphone[i] = temp;
         }
      }

      if (!res.setConversionFunction((byte)12)) {
         return true;
      }

      for (int i = 0; i < metaLen; i++) {
         System.arraycopy(metaphone, 0, this._tempMetaphoneBuffer, 0, i);
         System.arraycopy(metaphone, i + 1, this._tempMetaphoneBuffer, i, metaLen - i - 1);
         this._metaphone.getVariants(this._tempMetaphoneBuffer, metaLen - 1, res);
      }

      if (!res.setConversionFunction((byte)13)) {
         return true;
      }

      for (int i = 0; i < metaLen; i++) {
         char temp = metaphone[i];
         metaphone[i] = 1;
         this._metaphone.getVariants(metaphone, metaLen, res);
         metaphone[i] = temp;
      }

      return false;
   }

   private SpellCheckWordMatch popWordMatchExpr(int matchType) {
      Stack stack;
      switch (matchType) {
         case 0:
            stack = this._ambiguousWordStack;
            break;
         case 1:
         default:
            stack = this._caseSensitiveWordStack;
            break;
         case 2:
            stack = this._caseInsensitiveWordStack;
      }

      if (!stack.empty()) {
         return (SpellCheckWordMatch)stack.pop();
      }

      switch (matchType) {
         case 0:
            return new SpellCheckWordMatch(false, true);
         case 1:
         default:
            return new SpellCheckWordMatch(true, false);
         case 2:
            return new SpellCheckWordMatch(false, false);
      }
   }

   private void pushWordMatchExpr(int matchType, SpellCheckWordMatch expr) {
      switch (matchType) {
         case 0:
            this._ambiguousWordStack.push(expr);
            return;
         case 1:
         default:
            this._caseSensitiveWordStack.push(expr);
            return;
         case 2:
            this._caseInsensitiveWordStack.push(expr);
      }
   }

   private SpellCheckWordMatchState popWordMatchState() {
      return !this._wordStateStack.empty() ? (SpellCheckWordMatchState)this._wordStateStack.pop() : new SpellCheckWordMatchState();
   }

   private void pushWordMatchState(SpellCheckWordMatchState state) {
      this._wordStateStack.push(state);
   }

   private ResultContainer popResultContainer() {
      return !this._resultContainerStack.empty() ? (ResultContainer)this._resultContainerStack.pop() : new ResultContainer();
   }

   private void pushResultContainer(ResultContainer container) {
      this._resultContainerStack.push(container);
   }

   private Word popWord() {
      return !this._wordStack.empty() ? (Word)this._wordStack.pop() : new Word();
   }

   private void pushWord(Word word) {
      this._wordStack.push(word);
   }

   private EditDistance1WordMatch popEditDistanceMatch() {
      return !this._editDistanceMatchStack.empty() ? (EditDistance1WordMatch)this._editDistanceMatchStack.pop() : new EditDistance1WordMatch(1);
   }

   private void pushEditDistanceMatch(EditDistance1WordMatch expr) {
      this._editDistanceMatchStack.push(expr);
   }

   private EditDistance1WordMatchState popEditDistanceMatchState() {
      return !this._editDistanceMatchStateStack.empty()
         ? (EditDistance1WordMatchState)this._editDistanceMatchStateStack.pop()
         : new EditDistance1WordMatchState();
   }

   private void pushEditDistanceMatchState(EditDistance1WordMatchState state) {
      this._editDistanceMatchStateStack.push(state);
   }

   private MultiClickWordMatch popMultiClickMatch() {
      return !this._multiClickMatchStack.empty() ? (MultiClickWordMatch)this._multiClickMatchStack.pop() : new MultiClickWordMatch(1);
   }

   private void pushMultiClickMatch(MultiClickWordMatch expr) {
      this._multiClickMatchStack.push(expr);
   }

   private char[] popBuffer(int length) {
      if (!this._charBufferStack.empty()) {
         char[] buff = (char[])this._charBufferStack.pop();
         if (buff.length < length) {
            Array.resize(buff, length);
         }

         return buff;
      } else {
         return new char[length];
      }
   }

   private void pushBuffer(char[] buff) {
      this._charBufferStack.push(buff);
   }
}
