package net.rim.tid.im.conv.europe.repository;

import net.rim.device.api.i18n.Locale;
import net.rim.tid.awt.im.repository.CustomWordsRepository;
import net.rim.tid.im.conv.SLCurrentVariant;
import net.rim.tid.im.conv.repository.AlphabetChangeListener;
import net.rim.tid.im.conv.repository.IAlphabetRepository;
import net.rim.tid.im.conv.repository.IRepository;
import net.rim.tid.itie.LinguisticData;

public class EuropeanRepository extends IRepository implements IAlphabetRepository, CustomWordsRepository {
   protected Locale _locale;
   protected EuropeanRepositoryData _data;
   private boolean _isOwningRepositoryData = true;

   public EuropeanRepositoryData getRepositoryData() {
      return this._data;
   }

   public void setRepositoryData(EuropeanRepositoryData aData) {
      this._data = aData;
      this._isOwningRepositoryData = false;
   }

   protected EuropeanRepositoryData createDefaultCustomWordsRepository() {
      return new EuropeanRepositoryData();
   }

   public int getMinWordLength() {
      return this._data.getMinWordLength();
   }

   public WordLearningReader getWordLearning() {
      synchronized (this._data) {
         return this._data._learning;
      }
   }

   public Reader getReaderAt(int aPos) {
      return (Reader)this._data._readers.elementAt(aPos);
   }

   public int getReadersCount() {
      return this._data._readers.size();
   }

   public void setDefaultLearningFrequency(char aFreq) {
      synchronized (this._data) {
         this._data._learning.setDefaultLearningFrequency(aFreq);
      }
   }

   public int getMaxNestingLevel() {
      synchronized (this._data) {
         return this._data._maxNestingLevel;
      }
   }

   public void updateWordTimeStamp(SLCurrentVariant wordToUpdate) {
      synchronized (this._data) {
         this._data._learning.updateTimeStamp(wordToUpdate);
      }
   }

   public String[] getAvailableWordlists() {
      return this._data.getAvailableWordlists();
   }

   public synchronized void clearCache() {
   }

   public void reloadLearningData() {
      synchronized (this._data) {
         this._data._learning.reloadData();
      }
   }

   public void setWordLearningData(byte[] aData) {
      synchronized (this._data) {
         this._data._learning.setData(aData);
      }
   }

   public void clearWordLearning() {
      synchronized (this._data) {
         this._data._learning.clearAll();
      }
   }

   public void setWordLearningSizeLimit(int aSizeLimit) {
      if (this._isOwningRepositoryData) {
         synchronized (this._data) {
            this._data._learning.setSizeLimit(aSizeLimit);
         }
      }
   }

   public int getWordLearningSize() {
      synchronized (this._data) {
         return this._data._learning.getFileSize();
      }
   }

   public byte[] getWordLearningData() {
      synchronized (this._data) {
         return this._data._learning.getData();
      }
   }

   @Override
   public boolean isInBasicAlphabet(char aCh) {
      return this._data.isInBasicAlphabet(aCh);
   }

   @Override
   public boolean isInExtendedAlphabet(char aCh) {
      return this._data.isInExtendedAlphabet(aCh);
   }

   @Override
   public void removeWords(Object obj) {
   }

   @Override
   public void addWords(Object obj) {
   }

   @Override
   public int getType() {
      throw null;
   }

   @Override
   public void commit() {
      throw null;
   }

   @Override
   public void clear() {
      throw null;
   }

   @Override
   public int containsWord(char[] _1, int _2) {
      throw null;
   }

   @Override
   public boolean containsCharInAlphabet(char _1) {
      throw null;
   }

   @Override
   public int removeWord(char[] _1, int _2) {
      throw null;
   }

   @Override
   public int addWord(char[] _1, int _2, byte _3, int _4) {
      throw null;
   }

   @Override
   public int addWord(char[] _1, int _2, byte _3) {
      throw null;
   }

   @Override
   public int addWord(char[] _1, int _2) {
      throw null;
   }

   @Override
   public void removeWords(StringBuffer _1) {
      throw null;
   }

   @Override
   public void addWords(StringBuffer _1) {
      throw null;
   }

   @Override
   public void init(int _1) {
      throw null;
   }

   @Override
   public void setLocale(Locale locale) {
      if (this._isOwningRepositoryData && this._data == null) {
         this._data = this.createDefaultCustomWordsRepository();
      }

      synchronized (this) {
         this.clearAll();
         if (this._isOwningRepositoryData) {
            this._data.setLocale(locale);
         }

         this._locale = locale;
      }
   }

   @Override
   public boolean isInAlphabet(char aCh) {
      return this._data.isInAlphabet(aCh);
   }

   @Override
   public boolean clearAll() {
      return this._isOwningRepositoryData ? this._data.clearAll() : true;
   }

   @Override
   public int unloadLinguisticData(int id) {
      return this._data.unloadLinguisticData(id);
   }

   @Override
   public void addAlphabetChangeListener(AlphabetChangeListener l) {
      this._data.addAlphabetChangeListener(l);
   }

   @Override
   public void removeAlphabetChangeListener(AlphabetChangeListener l) {
      this._data.removeAlphabetChangeListener(l);
   }

   @Override
   public int addWord(SLCurrentVariant wordToAdd, boolean aConvertToLowerCase) {
      synchronized (this._data) {
         return this._data.addWord(wordToAdd, aConvertToLowerCase, (byte)2);
      }
   }

   @Override
   public int removeWord(SLCurrentVariant wordToRemove) {
      synchronized (this._data) {
         return this._data._learning.removeWord(wordToRemove);
      }
   }

   @Override
   public int loadLinguisticData(LinguisticData aData) {
      return this._isOwningRepositoryData ? this._data.loadLinguisticData(aData) : 1;
   }

   @Override
   public boolean isEmpty() {
      synchronized (this._data) {
         return this._data._readers.size() == 0;
      }
   }

   @Override
   public int getLongestWordLength() {
      synchronized (this._data) {
         return this._data._longestWordLength;
      }
   }

   @Override
   public boolean updateFrequencies(SLCurrentVariant wordToUpdate, char frq, SLCurrentVariant wordToReset) {
      return false;
   }
}
