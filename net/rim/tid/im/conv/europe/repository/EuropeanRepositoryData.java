package net.rim.tid.im.conv.europe.repository;

import java.util.Vector;
import net.rim.device.api.i18n.Locale;
import net.rim.tid.awt.im.repository.CustomWordsRepository;
import net.rim.tid.data.LearningDataManager;
import net.rim.tid.im.conv.SLCurrentVariant;
import net.rim.tid.im.conv.europe.repository.mailExtractor.MailExtractorRepository;
import net.rim.tid.im.conv.repository.AlphabetChangeListener;
import net.rim.tid.im.conv.repository.ResultContainer;
import net.rim.tid.itie.LinguisticData;

public class EuropeanRepositoryData implements CustomWordsRepository {
   Vector _readers = (Vector)(new Object());
   WordLearningReader _learning;
   int _longestWordLength = 20;
   int _maxNestingLevel = 2;
   private SLCurrentVariant _addVariant = (SLCurrentVariant)(new Object());
   private ResultContainer _resContainer = (ResultContainer)(new Object());
   private MailExtractorRepository _extractorRepository = new MailExtractorRepository();
   private static String LEARNING_WORDLIST_NAME = "Learning Wordlist";

   protected void initLearning() {
      this.initLearning((byte)1);
   }

   protected void initLearning(byte type) {
      if (!LearningDataManager.isDataLocked()) {
         this._learning = new WordLearningReader();
         this._learning.init(type);
         this._learning.enableOTAListener(true);
         this._learning.setDefaultLearningFrequency('d');
      }
   }

   public MailExtractorRepository getMailExtractorRepository() {
      return this._extractorRepository;
   }

   synchronized void setWordLearningSizeLimit(int aSizeLimit) {
      if (this._learning != null) {
         this._learning.setSizeLimit(aSizeLimit);
      }
   }

   public void removeAlphabetChangeListener(AlphabetChangeListener l) {
      if (this._learning != null) {
         this._learning.removeAlphabetChangeListener(l);
      }
   }

   public void addAlphabetChangeListener(AlphabetChangeListener l) {
      if (this._learning != null) {
         this._learning.addAlphabetChangeListener(l);
      }
   }

   synchronized boolean isInBasicAlphabet(char aCh) {
      for (int i = 0; i < this._readers.size(); i++) {
         Reader reader = (Reader)this._readers.elementAt(i);
         int type = reader.getType() >> 4;
         if (type == 1 && reader.isInAlphabet(aCh)) {
            return true;
         }
      }

      return false;
   }

   synchronized boolean isInExtendedAlphabet(char aCh) {
      if (this._learning.isInAlphabet(aCh)) {
         return true;
      }

      for (int i = 0; i < this._readers.size(); i++) {
         Reader reader = (Reader)this._readers.elementAt(i);
         if (reader.isInAlphabet(aCh)) {
            return true;
         }
      }

      return false;
   }

   public int addWord(SLCurrentVariant wordToAdd, boolean aConvertToLowerCase, byte priority) {
      int ret = this._learning.addWord(wordToAdd, '\u0000', aConvertToLowerCase, priority);
      if (ret == 0) {
         this._longestWordLength = Math.max(this._longestWordLength, wordToAdd._length);
      }

      return ret;
   }

   public WordLearningReader getLearningReader() {
      return this._learning;
   }

   synchronized void setLocale(Locale locale) {
      if (this._learning != null) {
         this._learning.setLocale(locale);
         this._maxNestingLevel = Math.max(this._maxNestingLevel, this._learning.getMaxNestingLevel());
         this._learning.setMaxSplitNestingLevel(this._maxNestingLevel);
      }

      this._extractorRepository.setLocale(locale);
   }

   synchronized int loadLinguisticData(LinguisticData aData) {
      int ret = 1;
      if (this.findReader(aData.getID()) == -1) {
         Reader reader = new Reader();
         ret = reader.loadLinguisticData(aData);
         if (ret != 2) {
            this.insertReader0(this._readers, reader);
            this._longestWordLength = Math.max(this._longestWordLength, reader.getLongestWordLength());
            this._maxNestingLevel = Math.max(this._maxNestingLevel, reader.getMaxNestingLevel());
            if (this._learning != null) {
               this._learning.setMaxSplitNestingLevel(this._maxNestingLevel);
            }
         }
      }

      return ret;
   }

   synchronized int getMinWordLength() {
      if (this._readers.size() == 0) {
         return 2;
      }

      int length = Integer.MAX_VALUE;

      for (int i = this._readers.size() - 1; i >= 0; i--) {
         length = Math.min(length, ((Reader)this._readers.elementAt(i)).getMinWordLength());
      }

      return length;
   }

   synchronized String[] getAvailableWordlists() {
      int len = this._readers.size() == 0 ? 1 : this._readers.size() + 1;
      String[] names = new Object[len];

      for (int i = 0; i < len - 1; i++) {
         names[i] = ((Reader)this._readers.elementAt(i)).getName();
      }

      names[len - 1] = LEARNING_WORDLIST_NAME;
      return names;
   }

   synchronized int unloadLinguisticData(int id) {
      int pos = this.findReader(id);
      if (pos != -1) {
         this._readers.removeElementAt(pos);
         this._extractorRepository.removeReader(pos);
         return 1;
      } else {
         return 4;
      }
   }

   synchronized boolean clearAll() {
      this._readers.removeAllElements();
      this._longestWordLength = 0;
      this._extractorRepository.clearAll();
      return true;
   }

   synchronized boolean isInAlphabet(char aCh) {
      if (this._learning.isInAlphabet(aCh)) {
         return true;
      }

      for (int i = 0; i < this._readers.size(); i++) {
         if (((Reader)this._readers.elementAt(i)).isInAlphabet(aCh)) {
            return true;
         }
      }

      return false;
   }

   @Override
   public int addWord(char[] word, int length, byte priority, int type) {
      return 4;
   }

   @Override
   public int addWord(char[] word, int length, byte priority) {
      this._addVariant.set(word, 0, length);
      return this.addWord(this._addVariant, false, priority);
   }

   @Override
   public int addWord(char[] word, int length) {
      return this.addWord(word, length, (byte)2);
   }

   @Override
   public void removeWords(Object obj) {
   }

   @Override
   public void addWords(Object obj) {
   }

   @Override
   public int containsWord(char[] word, int length) {
      if (this._learning.contains(new Object(word, 0, length))) {
         return 1;
      }

      this._resContainer.reset(1, length, length, true, true);

      for (int i = 0; i < this._readers.size(); i++) {
         ((Reader)this._readers.elementAt(i)).getPredictions(word, length, this._resContainer);
         if (this._resContainer.getVariantsCount() > 0) {
            return 1;
         }
      }

      return 0;
   }

   @Override
   public int removeWord(char[] word, int length) {
      return 4;
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

   public EuropeanRepositoryData() {
      this.initLearning();
      this._resContainer.setCurrentSource((byte)0, false);
   }

   private int findReader(int id) {
      for (int i = this._readers.size() - 1; i >= 0; i--) {
         Reader r = (Reader)this._readers.elementAt(i);
         if (r.getID() == id) {
            return i;
         }
      }

      return -1;
   }

   private boolean insertReader0(Vector aReaders, Reader reader) {
      for (int i = 0; i < aReaders.size(); i++) {
         Reader r = (Reader)aReaders.elementAt(i);
         if (r.getPriority() < reader.getPriority()) {
            aReaders.insertElementAt(reader, i);
            this._extractorRepository.insertReader(new Reader(reader), i);
            return true;
         }
      }

      aReaders.addElement(reader);
      this._extractorRepository.addReader(new Reader(reader));
      return true;
   }
}
