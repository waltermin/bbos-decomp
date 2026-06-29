package net.rim.tid.im.conv.europe.repository.mailExtractor;

import java.util.Vector;
import net.rim.device.api.i18n.Locale;
import net.rim.tid.awt.im.repository.CustomWordsRepository;
import net.rim.tid.data.LearningDataManager;
import net.rim.tid.im.conv.SLCurrentVariant;
import net.rim.tid.im.conv.europe.repository.Reader;
import net.rim.tid.im.conv.europe.repository.WordLearningReader;
import net.rim.tid.im.conv.repository.ExtendedCurrentVariant;
import net.rim.tid.im.conv.repository.IRepository;
import net.rim.tid.im.conv.repository.ReIterator;
import net.rim.tid.im.conv.repository.ResultContainer;

public final class MailExtractorRepository extends IRepository implements CustomWordsRepository {
   private Vector _readers = (Vector)(new Object());
   private ResultContainer _resContainer = (ResultContainer)(new Object());
   private SLCurrentVariant _addVariant = (SLCurrentVariant)(new Object());
   WordLearningReader _extractedWordsReader;
   WordLearningReader _learningWordsReader;
   WordLearningReader _emailAndUrlWordsReader;
   WordLearningReader _rejectedWordsReader;

   public final WordLearningReader getExtractedWordsReader() {
      return this._extractedWordsReader;
   }

   public final WordLearningReader getEmailAndUrlAddressesWordsReader() {
      return this._emailAndUrlWordsReader;
   }

   public final WordLearningReader getRejectedWordsReader() {
      return this._rejectedWordsReader;
   }

   final void initLearning() {
      if (!LearningDataManager.isDataLocked()) {
         this._extractedWordsReader = new ExtractedWordsLearningReader();
         this._extractedWordsReader.init((byte)1);
         this._extractedWordsReader.setDefaultLearningFrequency('\u0001');
         this._learningWordsReader = new MailExtractorLearningReader();
         this._learningWordsReader.init((byte)1);
         this._emailAndUrlWordsReader = new EmailAddrAndURLAddrLearningReader();
         this._emailAndUrlWordsReader.init((byte)1);
         this._rejectedWordsReader = new RejectedWordsLearningReader();
         this._rejectedWordsReader.init((byte)1);
      }
   }

   public final synchronized void addReader(Reader reader) {
      this._readers.addElement(reader);
   }

   public final synchronized void insertReader(Reader reader, int index) {
      if (this._readers.size() == 0) {
         this._readers.addElement(reader);
      } else {
         this._readers.insertElementAt(reader, index);
      }
   }

   public final synchronized void removeReader(int index) {
      this._readers.removeElementAt(index);
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public final void deleteWord(ExtendedCurrentVariant word) {
      WordLearningReader reader = this._extractedWordsReader;
      if (reader != null) {
         try {
            reader.removeWord(word);
         } catch (Throwable var5) {
            e.printStackTrace();
            reader.clearAll();
            System.out.println("WARNING: mail extractor reader failed");
            return;
         }
      }
   }

   @Override
   public final synchronized boolean containsCharInAlphabet(char ch) {
      if (this._learningWordsReader != null && this._learningWordsReader.isInAlphabet(ch)) {
         return true;
      }

      if (this._extractedWordsReader != null && this._extractedWordsReader.isInAlphabet(ch)) {
         return true;
      }

      if (this._emailAndUrlWordsReader != null && this._emailAndUrlWordsReader.isInAlphabet(ch)) {
         return true;
      }

      if (this._rejectedWordsReader != null && this._rejectedWordsReader.isInAlphabet(ch)) {
         return true;
      }

      for (int i = 0; i < this._readers.size(); i++) {
         if (((Reader)this._readers.elementAt(i)).isInAlphabet(ch)) {
            return true;
         }
      }

      return false;
   }

   @Override
   public final synchronized int containsWord(char[] word, int length) {
      String check = (String)(new Object(word, 0, length));
      if (this._extractedWordsReader != null && this._extractedWordsReader.contains(check)) {
         return 2;
      }

      if (this._learningWordsReader != null && this._learningWordsReader.contains(check)) {
         return 2;
      }

      if (this._emailAndUrlWordsReader != null && this._emailAndUrlWordsReader.contains(check)) {
         return 2;
      }

      if (this._rejectedWordsReader != null && this._rejectedWordsReader.contains(check)) {
         return 2;
      }

      this._resContainer.reset(5, length, Integer.MAX_VALUE, true, true);

      for (int i = 0; i < this._readers.size() && this._resContainer.getVariantsCount() == 0; i++) {
         ((Reader)this._readers.elementAt(i)).getPredictions(word, length, this._resContainer);
      }

      int ret = 0;
      if (this._resContainer.getVariantsCount() > 0) {
         ret = 3;
         byte[] lengths = this._resContainer.getLengths();

         for (int i = 0; i < this._resContainer.getVariantsCount(); i++) {
            if (lengths[i] == length) {
               ret = 2;
               break;
            }
         }
      }

      return ret;
   }

   @Override
   public final void init(int anInitialSize) {
   }

   @Override
   public final void addWords(StringBuffer words) {
   }

   @Override
   public final void removeWords(StringBuffer words) {
   }

   @Override
   public final void clear() {
   }

   @Override
   public final void commit() {
   }

   @Override
   public final int getType() {
      return -1;
   }

   @Override
   public final int addWord(char[] word, int length) {
      return this.addWord(word, length, (byte)2);
   }

   @Override
   public final int removeWord(char[] word, int length) {
      return 4;
   }

   @Override
   public final void addWords(Object obj) {
   }

   @Override
   public final void removeWords(Object obj) {
   }

   @Override
   public final int addWord(char[] word, int length, byte priority) {
      return this.addWord(word, length, priority, 5);
   }

   @Override
   public final int addWord(char[] word, int length, byte priority, int type) {
      switch (type) {
         case 3:
            return 4;
         case 4:
            if (this._emailAndUrlWordsReader != null) {
               this._addVariant.set(word, 0, length);
               return this._emailAndUrlWordsReader.addWord(this._addVariant, (char)0, false, priority);
            }

            return 4;
         case 5:
         default:
            if (this._extractedWordsReader != null) {
               this._addVariant.set(word, 0, length);
               return this._extractedWordsReader.addWord(this._addVariant, (char)0, false, priority);
            } else {
               return 4;
            }
      }
   }

   @Override
   public final void setLocale(Locale locale) {
      if (this._extractedWordsReader != null) {
         this._extractedWordsReader.setLocale(locale);
      }

      if (this._learningWordsReader != null) {
         this._learningWordsReader.setLocale(locale);
      }

      if (this._emailAndUrlWordsReader != null) {
         this._emailAndUrlWordsReader.setLocale(locale);
      }

      if (this._rejectedWordsReader != null) {
         this._rejectedWordsReader.setLocale(locale);
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final int getVariants(ReIterator aIterator, ResultContainer aRes) {
      WordLearningReader reader = this._extractedWordsReader;
      int variantsBefore = aRes.getVariantsCount();
      if (reader != null) {
         try {
            reader.getVariants(aIterator, aRes);
         } catch (Throwable var7) {
            e.printStackTrace();
            reader.clearAll();
            System.out.println("WARNING: mail extractor reader failed");
            return aRes.getVariantsCount() - variantsBefore;
         }
      }

      return aRes.getVariantsCount() - variantsBefore;
   }

   public MailExtractorRepository() {
      this.initLearning();
      this._resContainer.setCurrentSource((byte)0, false);
   }

   @Override
   public final synchronized boolean clearAll() {
      this._readers.removeAllElements();
      return true;
   }

   @Override
   public final synchronized int clearLearningCache(int type) {
      if (this._extractedWordsReader != null) {
         this._extractedWordsReader.clearAll();
      }

      if (this._emailAndUrlWordsReader != null) {
         this._emailAndUrlWordsReader.clearAll();
      }

      if (this._rejectedWordsReader != null) {
         this._rejectedWordsReader.clearAll();
      }

      return 0;
   }
}
