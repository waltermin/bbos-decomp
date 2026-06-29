package net.rim.tid.im.conv.europe.repository;

import net.rim.device.api.i18n.Locale;
import net.rim.tid.im.conv.SLCurrentVariant;
import net.rim.tid.im.conv.europe.spellcheck.PairLearningReader;
import net.rim.tid.im.conv.europe.spellcheck.SpellCheckResultContainer;
import net.rim.tid.im.conv.repository.ReIterator;
import net.rim.tid.im.conv.repository.ResultContainer;
import net.rim.tid.itie.LinguisticData;

public class SpellCheckMetaphoneRepository extends EuropeanRepository {
   private PairLearningReader _pairLearning;
   protected boolean _useTransitivePairLearningSearch = true;

   public void enablePairLearning(boolean aEnable) {
      if (aEnable != (this._pairLearning != null)) {
         if (aEnable) {
            this._pairLearning = new PairLearningReader();
            if (super._locale != null) {
               this._pairLearning.setLocale(super._locale);
               return;
            }
         } else {
            this._pairLearning = null;
         }
      }
   }

   @Override
   public void setLocale(Locale locale) {
      super.setLocale(locale);
      if (this._pairLearning != null) {
         this._pairLearning.setLocale(super._locale);
      }
   }

   @Override
   public int loadLinguisticData(LinguisticData aData) {
      int ret = super.loadLinguisticData(aData);
      super._data._learning.setUseCache(false);
      if (this._pairLearning != null) {
         this._pairLearning.setLocale(super._locale);
      }

      return ret;
   }

   public void getVariants(ReIterator aIterator, SpellCheckResultContainer aRes) {
      aRes.setCurrentSource((byte)0);
      int readerCount = super._data._readers.size();

      for (int i = 0; i < readerCount; i++) {
         if (i > 0) {
            aIterator.reset();
         }

         this.getReaderAt(i).getPredictions(aIterator, aRes);
      }

      aRes.setCurrentSource((byte)3);
      aIterator.reset(false);
      super._data._learning.getVariants(aIterator, aRes);
      if (this._useTransitivePairLearningSearch && this._pairLearning != null) {
         aRes.setCurrentSource((byte)0);
         aRes.setSearchLearningDB(true);
         aIterator.reset(false);
         this._pairLearning.getVariants(aIterator, aRes);
         aRes.setSearchLearningDB(false);
      }
   }

   public boolean matches(RegularExpression expr, RegularExpressionState state, ResultContainer result, boolean isCaseSensitive) {
      int resultSize = result == null ? 0 : result.getVariantsCount();
      int readerCount = super._data._readers.size();
      Object mark = state.newMark();

      for (int i = 0; i < readerCount; i++) {
         state.rollback(mark);
         if (this.getReaderAt(i).matches(expr, state, result, isCaseSensitive) && (result == null || result.isFull())) {
            return true;
         }
      }

      state.rollback(mark);
      if (super._data._learning.matches(expr, state, result, isCaseSensitive)) {
         return true;
      }

      if (this._useTransitivePairLearningSearch && result instanceof SpellCheckResultContainer && this._pairLearning != null) {
         SpellCheckResultContainer scResult = (SpellCheckResultContainer)result;
         scResult.setCurrentSource((byte)0);
         scResult.setSearchLearningDB(true);
         state.rollback(mark);
         this._pairLearning.matches(expr, state, scResult, isCaseSensitive);
         scResult.setSearchLearningDB(false);
      }

      return result == null ? false : resultSize < result.getVariantsCount();
   }

   public void getVariantsForWordExist(ReIterator aIterator, ResultContainer aRes) {
      int readerCount = super._data._readers.size();

      for (int i = 0; i < readerCount; i++) {
         if (i > 0) {
            aIterator.reset(true);
         }

         this.getReaderAt(i).getPredictions(aIterator, aRes);
      }

      aIterator.reset(false);
      super._data._learning.getVariants(aIterator, aRes);
   }

   public void getVariantsForWordExist(char[] aWord, int aLength, ResultContainer aRes) {
      super._data._learning.getVariants(aWord, aLength, aRes);
      int readerCount = super._data._readers.size();

      for (int i = 0; i < readerCount; i++) {
         this.getReaderAt(i).getPredictions(aWord, aLength, aRes);
      }
   }

   public PairLearningReader getPairLearning() {
      return this._pairLearning;
   }

   public LearningGlobalAlphabet getLearningAlphabet() {
      return super._data._learning.getAlphabet();
   }

   public int addPair(StringBuffer aMisspelledWord, SLCurrentVariant aCorrectWord) {
      return this._pairLearning != null ? this._pairLearning.addPair(aMisspelledWord, aCorrectWord) : 1;
   }

   public void setUseTransitivePairLearningSearch(boolean aVal) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public boolean getUseTransitivePairLearningSearch() {
      return this._useTransitivePairLearningSearch;
   }

   public void clearPairLearning() {
      if (this._pairLearning != null) {
         this._pairLearning.clearAll();
      }
   }

   public void setPairLearningSizeLimit(int aSizeLimit) {
      if (this._pairLearning != null) {
         this._pairLearning.setSizeLimit(aSizeLimit);
      }
   }

   public int getPairLearningSizeLimit() {
      return this._pairLearning != null ? this._pairLearning.getSizeLimit() : 0;
   }

   public byte[] getPairLearningData() {
      return this._pairLearning == null ? null : this._pairLearning.getData();
   }

   public void setPairLearningData(byte[] aData) {
      if (this._pairLearning != null) {
         this._pairLearning.setData(aData);
      }
   }

   @Override
   public void reloadLearningData() {
      super.reloadLearningData();
      if (this._pairLearning != null) {
         this._pairLearning.reloadData();
      }
   }

   public int word1CharExists(char[] aOrigWord, int aOffset) {
      return super._data._readers.size() == 0 ? 0 : this.getReaderAt(0).get1CharWordFreq(aOrigWord[aOffset]);
   }

   @Override
   public int removeWord(char[] word, int length) {
      return 4;
   }

   @Override
   public int addWord(char[] word, int length) {
      return this.addWord(word, length, (byte)2);
   }

   @Override
   public int addWord(char[] word, int length, byte priority) {
      return 4;
   }

   @Override
   public int addWord(char[] word, int length, byte priority, int type) {
      return 4;
   }

   @Override
   public int containsWord(char[] word, int length) {
      return 0;
   }

   @Override
   public void init(int anInitialSize) {
   }

   @Override
   public void addWords(StringBuffer words) {
   }

   @Override
   public void removeWords(StringBuffer words) {
   }

   @Override
   public void clear() {
   }

   @Override
   public void commit() {
   }

   @Override
   public int getType() {
      return -1;
   }

   @Override
   public boolean containsCharInAlphabet(char ch) {
      return false;
   }

   @Override
   protected EuropeanRepositoryData createDefaultCustomWordsRepository() {
      return new SpellCheckRepositoryData();
   }
}
