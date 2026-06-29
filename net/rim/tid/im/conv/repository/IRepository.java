package net.rim.tid.im.conv.repository;

import net.rim.device.api.i18n.Locale;
import net.rim.device.api.util.BitSet;
import net.rim.device.internal.ui.StringBufferGap;
import net.rim.tid.im.conv.SLCurrentVariant;
import net.rim.tid.itie.LinguisticData;
import net.rim.tid.util.SLTextDataContainer;

public class IRepository implements IDataSearchRepository {
   public static final int SL_WORDLIST_MAGIC_NUMBER;

   public void setLocale(Locale _1) {
      throw null;
   }

   public int getVariants(SLTextDataContainer aRegexpPrefixes, ResultContainer aRes) {
      return 0;
   }

   public int getVariants(ReIterator aIterator, ResultContainer aRes) {
      return 0;
   }

   public int wordExists(StringBufferGap word, int start, int end, boolean caseSensitive) {
      return 0;
   }

   public void removeAlphabetChangeListener(AlphabetChangeListener l) {
   }

   public int getLongestWordLength() {
      return 0;
   }

   public int addWord(SLCurrentVariant wordToAdd, boolean aConvertToLowerCase) {
      return 4;
   }

   public int addWord(SLCurrentVariant wordToAdd, byte type, boolean aConvertToLowerCase) {
      return this.addWord(wordToAdd, aConvertToLowerCase);
   }

   public int addWord(SLCurrentVariant wordToAdd) {
      return this.addWord(wordToAdd, true);
   }

   public int removeWord(SLCurrentVariant wordToAdd) {
      return 4;
   }

   public boolean updateFrequencies(String wordToUpdate, char frq, String wordToReset) {
      return false;
   }

   public boolean updateFrequencies(SLCurrentVariant wordToUpdate, char frq, SLCurrentVariant wordToReset) {
      return false;
   }

   public boolean loadWordlist(byte[][][] aWordlistData, Locale l, int learningSize) {
      return false;
   }

   public boolean reloadData() {
      return false;
   }

   public int clearLearningCache(int type) {
      return 4;
   }

   public int loadLinguisticData(LinguisticData aData) {
      return 1;
   }

   public boolean unloadWordList(int id) {
      return false;
   }

   public int unloadLinguisticData(int id) {
      return 4;
   }

   public boolean clearAll() {
      return false;
   }

   public boolean isEmpty() {
      return true;
   }

   public void protectContent(int flag) {
   }

   public boolean isInAlphabet(char aCh) {
      return false;
   }

   public void addAlphabetChangeListener(AlphabetChangeListener l) {
   }

   @Override
   public BitSet searchPrefixes(String[] words) {
      return null;
   }
}
