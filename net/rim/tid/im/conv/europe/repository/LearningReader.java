package net.rim.tid.im.conv.europe.repository;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Vector;
import net.rim.device.api.i18n.Locale;
import net.rim.device.api.io.NoCopyByteArrayOutputStream;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.system.PersistentContentListener;
import net.rim.device.api.util.Arrays;
import net.rim.device.internal.ui.StringBufferGap;
import net.rim.tid.data.LearningData;
import net.rim.tid.data.LearningDataManager;
import net.rim.tid.im.conv.SLCurrentVariant;
import net.rim.tid.im.conv.europe.CaseCorrector;
import net.rim.tid.im.conv.repository.AlphabetChangeListener;
import net.rim.tid.im.conv.repository.ReIterator;
import net.rim.tid.im.conv.repository.ResultContainer;
import net.rim.tid.util.SLTextDataContainer;

public class LearningReader implements ReaderConstants, PersistentContentListener {
   protected Locale _currentLocale;
   protected String _currentLearnName;
   protected LearningHeader _header;
   protected LearningGlobalAlphabet _alphabet;
   protected byte[] _data;
   protected byte[] _encoded = new byte[50];
   protected int _tablesStart;
   protected int _sizeLimit;
   protected boolean _sizeLimitSet;
   protected LearningComplexPrefixTable[] _complexPrefixTables;
   protected LearningSimplePrefixTable _simplePrefixTable;
   protected ReIterator _reIterator;
   protected byte _type;
   protected char[] _tempBuffer = new char[50];
   protected char[] _wordBuffer = new char[50];
   protected SLCurrentVariant iTempVariant = (SLCurrentVariant)(new Object());
   private char[] _caseCorrectionBuffer = new char[50];
   protected int _splitSize = 50;
   protected char _defaultFrequency = '耀';
   protected int _maxSplitNestingLevel = 2;
   protected static final int MAX_BUFFER_SIZE;
   protected static boolean _debugOutputEnabled;

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public boolean setLocale(Locale aLocale) {
      synchronized (LearningDataManager.getLock()) {
         if (aLocale != null && (this._currentLocale == null || !this._currentLocale.equals(aLocale))) {
            String lang = aLocale.getLanguage();
            String locale_str = lang;
            this._currentLearnName = this.getFilename(locale_str);
            this._header.setLocaleStr(locale_str);
            this._currentLocale = aLocale;

            try {
               this.loadLearningWordlist();
            } catch (Throwable var16) {
               e.printStackTrace();
               System.err.println("WARNING: Learning data corrupted. Resetting the buffer content.");
               boolean var11 = false /* VF: Semaphore variable */;

               label70:
               try {
                  var11 = true;
                  this.writeLearningData(this.createLearningWordlist());
                  this.updateLearningData();
                  var11 = false;
                  break label70;
               } finally {
                  if (var11) {
                     return false;
                  }
               }
            }

            this._alphabet.setLocale(this._currentLocale);
            return true;
         } else {
            return true;
         }
      }
   }

   public byte minTimeStamp(byte ts1, byte ts2) {
      return (byte)Math.max(ts1 & 255, ts2 & 255);
   }

   protected void reloadIfChanged() {
      if (this._header.isChanged()) {
         if (_debugOutputEnabled) {
            System.out.println("LEARNING DATA RELOADED");
         }

         try {
            this.reloadData();
         } finally {
            throw new Object("");
         }
      }
   }

   public String getFilename(String _1) {
      throw null;
   }

   protected void loadLearningWordlist() {
      synchronized (LearningDataManager.getLock()) {
         LearningData data = LearningDataManager.getLearningData(this._currentLearnName);
         if (data == null) {
            this.writeLearningData(this.createLearningWordlist());
            this.updateLearningData();
         } else {
            this.loadLearningWordlist(data.getData());
         }

         if (this._sizeLimitSet) {
            if (this._data.length != this._sizeLimit) {
               this.setSizeLimit(this._sizeLimit);
            }
         } else {
            this._sizeLimit = this._data.length;
         }

         this.resetPrivateMembers();
      }
   }

   protected void writeLearningData(byte[] aData) {
      synchronized (LearningDataManager.getLock()) {
         if (aData != null) {
            LearningData data = LearningDataManager.getLearningData(this._currentLearnName);
            if (data == null) {
               data = (LearningData)(new Object());
            }

            data.setData(aData);
            LearningDataManager.setLearningData(this._currentLearnName, data, false);
         }
      }
   }

   protected void updateLearningData() {
      synchronized (LearningDataManager.getLock()) {
         LearningData data = LearningDataManager.getLearningData(this._currentLearnName);
         if (data != null) {
            this.loadLearningWordlist(data.getData());
         }
      }
   }

   protected void modifyLearningData(boolean aDataChanged) {
      synchronized (LearningDataManager.getLock()) {
         this._header.update();
         this._alphabet.update();
         if (aDataChanged) {
            this.writeLearningData(this._data);
         }

         this.updateLearningData();
      }
   }

   protected byte[] createLearningWordlist() {
      this._data = null;
      return this.clearLearningWordlist();
   }

   protected byte[] clearLearningWordlist() {
      byte[] block = this._data != null && this._data.length == this._sizeLimit ? this._data : new byte[this._sizeLimit];
      NoCopyByteArrayOutputStream os = (NoCopyByteArrayOutputStream)(new Object(block, 0));
      DataOutputStream dos = (DataOutputStream)(new Object(os));
      this._alphabet.init();
      this._header.reset(this._alphabet);
      this._header.write(dos, this._alphabet);
      this._alphabet.write(dos);
      block[this._header.getHeaderSize() + this._alphabet.size()] = 0;
      this.createStructures();
      return block;
   }

   protected void loadLearningWordlist(byte[] aData) {
      if (this._header.getHeaderSize() == -1) {
         ByteArrayInputStream bais = (ByteArrayInputStream)(new Object(aData));
         DataInputStream dis = (DataInputStream)(new Object(bais));
         this._header.read(dis);
         this._alphabet.read(dis);
      }

      this._header.init(aData);
      this._alphabet.init(aData, this._header.getHeaderSize());
      this._data = aData;
      this.createStructures();
   }

   public LearningComplexPrefixTable createLearningComplexPrefixTable(int _1, LearningReader _2) {
      throw null;
   }

   public void getVariants(StringBufferGap aWord, int aStart, int aEnd, ResultContainer aRes) {
      if (this._sizeLimit > 0) {
         synchronized (LearningDataManager.getLock()) {
            this.reloadIfChanged();
            int len = aEnd - aStart;
            aWord.getChars(aStart, aEnd, this._tempBuffer, 0);

            for (int i = 0; i < len; i++) {
               this._tempBuffer[i] = CaseCorrector.toLowerCase(this._tempBuffer[i], this._currentLocale);
            }

            this._reIterator.init(this._tempBuffer, 0, len, false);
            this._complexPrefixTables[0].getPredictions(this._reIterator, aRes, this._wordBuffer);
         }
      }
   }

   public void getVariants(char[] aWord, int aLength, ResultContainer aRes) {
      this.getVariants(aWord, 0, aLength, aRes);
   }

   public void getVariants(char[] aWord, int aOffset, int aLength, ResultContainer aRes) {
      if (this._sizeLimit > 0) {
         synchronized (LearningDataManager.getLock()) {
            this.reloadIfChanged();
            if (aLength < this._tempBuffer.length) {
               for (int i = 0; i < aLength; i++) {
                  this._tempBuffer[i] = CaseCorrector.toLowerCase(aWord[i + aOffset], this._currentLocale);
               }

               int subst_no = this._reIterator.getWildcardSubstNo();
               aRes.resetWildcardSubst(subst_no);
               this._reIterator.init(this._tempBuffer, 0, aLength, false);
               this._complexPrefixTables[0].getPredictions(this._reIterator, aRes, this._wordBuffer);
               aRes.insertRestOfRegularFastVariants(subst_no);
            }
         }
      }
   }

   public boolean matches(RegularExpression expr, RegularExpressionState state, ResultContainer result, boolean isCaseSensitive) {
      if (this._sizeLimit == 0) {
         return false;
      }

      synchronized (LearningDataManager.getLock()) {
         this.reloadIfChanged();
         ComplexTableRegularExpressionState cstate = new ComplexTableRegularExpressionState(true, !isCaseSensitive, expr, state, this._currentLocale);
         if (result == null) {
            return this._complexPrefixTables[0].matches(expr, state, cstate);
         }

         int resultSize = result.getVariantsCount();
         char[] buff = new char[50];
         this._complexPrefixTables[0].getMatches(expr, state, cstate, buff, result);
         return resultSize < result.getVariantsCount();
      }
   }

   public void getVariants(String aWord, ResultContainer aRes) {
      if (this._sizeLimit > 0) {
         synchronized (LearningDataManager.getLock()) {
            this.reloadIfChanged();
            int len = aWord.length();
            aWord.getChars(0, len, this._tempBuffer, 0);

            for (int i = 0; i < len; i++) {
               this._tempBuffer[i] = CaseCorrector.toLowerCase(this._tempBuffer[i], this._currentLocale);
            }

            this._reIterator.init(this._tempBuffer, 0, len, false);
            this._complexPrefixTables[0].getPredictions(this._reIterator, aRes, this._wordBuffer);
         }
      }
   }

   public void getVariants(SLTextDataContainer aWords, ResultContainer aRes) {
      if (this._sizeLimit > 0) {
         aWords.resetIteration();

         while (aWords.hasMoreElements()) {
            aWords.next(this.iTempVariant);
            this.getVariants(this.iTempVariant._variants, this.iTempVariant._offset, this.iTempVariant._length, aRes);
         }
      }
   }

   public void getVariants(ReIterator aIterator, ResultContainer aRes) {
      if (this._sizeLimit > 0) {
         synchronized (LearningDataManager.getLock()) {
            this.reloadIfChanged();
            this._complexPrefixTables[0].getPredictions(aIterator, aRes, this._wordBuffer);
         }
      }
   }

   public int getFileSize() {
      return this._sizeLimit == 0 ? 0 : this._header.getFileSize();
   }

   public void setSizeLimit(int aSizeLimit) {
      synchronized (LearningDataManager.getLock()) {
         if (aSizeLimit != this._sizeLimit || this._data != null && this._data.length != aSizeLimit) {
            if (aSizeLimit <= this._header.getHeaderSize() + 20) {
               System.err.println("Warning: learning database trimmed to zero.");
               aSizeLimit = 0;
               this._complexPrefixTables = null;
               this._header.reset(null);
               this._data = null;
            } else if (this._sizeLimit == 0) {
               this._sizeLimit = aSizeLimit;
               this.writeLearningData(this.clearLearningWordlist());
               this.updateLearningData();
            } else if (this._data != null) {
               this.trimToSizeLimit(aSizeLimit);
               int newSize = Math.max(aSizeLimit, this.getFileSize());
               byte[] new_data = new byte[newSize];
               System.arraycopy(this._data, 0, new_data, 0, this.getFileSize());
               this._data = new_data;
               this.modifyLearningData(true);
            }

            this._sizeLimit = aSizeLimit;
            this._sizeLimitSet = true;
         }
      }
   }

   public int getSizeLimit() {
      return this._sizeLimit;
   }

   public void trimToSizeLimit(int _1) {
      throw null;
   }

   public boolean ensureSize(int aPotentialAdd) {
      if (this._sizeLimit == 0) {
         return false;
      }

      int fullSize = this.getFileSize();
      if (fullSize + aPotentialAdd <= this._sizeLimit) {
         return true;
      }

      if (!this._header.isTrimmable()) {
         return false;
      }

      try {
         return this.trim(aPotentialAdd) >= aPotentialAdd;
      } finally {
         System.out.println("Failed on trimming");
         return false;
      }
   }

   public int trim(int _1) {
      throw null;
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public void clearAll() {
      synchronized (LearningDataManager.getLock()) {
         if (this._sizeLimit > 0) {
            this.resetPrivateMembers();

            try {
               this.writeLearningData(this.clearLearningWordlist());
               this.updateLearningData();
            } catch (Throwable var6) {
               e.printStackTrace();
               return;
            }
         }
      }
   }

   public LearningGlobalAlphabet getAlphabet() {
      return this._sizeLimit == 0 ? null : this._alphabet;
   }

   public Locale getLocale() {
      return this._currentLocale;
   }

   public LearningSimplePrefixTable getSimplePrefixTable(int aLevel) {
      this._simplePrefixTable.setLevel(aLevel);
      return this._simplePrefixTable;
   }

   public LearningComplexPrefixTable getComplexPrefixTable(int aLevel) {
      return this._complexPrefixTables[aLevel];
   }

   public void growFileSize(int aVal) {
      this._header.growFileSize(aVal);
   }

   public void updateNestingLevel(int aLevel) {
      this._header.updateNestingLevel(aLevel);
   }

   public boolean isInAlphabet(char aCh) {
      this.reloadIfChanged();
      return this._sizeLimit == 0 ? false : this._alphabet.indexOf(aCh) != -1;
   }

   public void addAlphabetChangeListener(AlphabetChangeListener l) {
      this._alphabet.addAlphabetChangeListener(l);
   }

   public void removeAlphabetChangeListener(AlphabetChangeListener l) {
      this._alphabet.removeAlphabetChangeListener(l);
   }

   public byte[] getData() {
      return this._data;
   }

   public void setData(byte[] aData) {
      synchronized (LearningDataManager.getLock()) {
         this._complexPrefixTables = null;
         this._header.reset(null);
         if (aData.length > this._sizeLimit) {
            this._sizeLimit = aData.length;
         }

         this.writeLearningData(aData);
         this.updateLearningData();
         this.resetPrivateMembers();
      }
   }

   public void reloadData() {
      synchronized (LearningDataManager.getLock()) {
         this._complexPrefixTables = null;
         this._header.reset(null);
         this.updateLearningData();
         this.resetPrivateMembers();
         if (this._data.length > this._sizeLimit) {
            this._sizeLimit = this._data.length;
         }
      }
   }

   protected void resetPrivateMembers() {
   }

   public int getEntryNo() {
      this.reloadIfChanged();
      return this._sizeLimit == 0 ? 0 : this._complexPrefixTables[0].getEntryNo();
   }

   public void getEntries(Vector aEntries) {
      this.reloadIfChanged();
      if (this._sizeLimit > 0) {
         try {
            this._complexPrefixTables[0].getEntries(aEntries, this._wordBuffer);
         } finally {
            this.clearAll();
            aEntries.setSize(0);
            return;
         }
      }
   }

   public byte getMaxNestingLevel() {
      return this._header.getMaxNestingLevel();
   }

   void correctCase(SLCurrentVariant aWord) {
      char ch = aWord._variants[aWord._offset + aWord._length - 1];
      if (ch < 5) {
         aWord._length--;
         System.arraycopy(aWord._variants, aWord._offset, this._caseCorrectionBuffer, 0, aWord._length);
         aWord._variants = this._caseCorrectionBuffer;
         aWord._offset = 0;
         if (ch == 2) {
            CaseCorrector.toUpperCase(this._caseCorrectionBuffer, aWord._length, this._currentLocale);
         } else {
            if (ch == 1 || ch == 4) {
               this._caseCorrectionBuffer[0] = CaseCorrector.toUpperCase(this._caseCorrectionBuffer[0], this._currentLocale);
            }

            if (ch == 3 || ch == 4) {
               this._caseCorrectionBuffer[1] = CaseCorrector.toUpperCase(this._caseCorrectionBuffer[1], this._currentLocale);
            }
         }

         aWord._hadCaseControl = true;
      }
   }

   int getSplitSize() {
      return this._splitSize;
   }

   int getMaxSplitNestingLevel() {
      return this._maxSplitNestingLevel;
   }

   void setMaxSplitNestingLevel(int level) {
      this._maxSplitNestingLevel = level;
   }

   public void setSplitSize(int aSize) {
      this._splitSize = aSize;
   }

   public void splitOccurred(int aNewTableCount) {
   }

   public void setDefaultLearningFrequency(char aFreq) {
      this._defaultFrequency = aFreq;
   }

   public char getDefaultFrequency() {
      return this._defaultFrequency;
   }

   public boolean isFrequencyIncluded() {
      return this._type == 3;
   }

   public String getAllDataAsString() {
      if (this._data == null) {
         return "";
      }

      this.reloadIfChanged();
      StringBuffer sb = (StringBuffer)(new Object(this._data.length * 2));

      for (int i = 0; i < this._data.length; i++) {
         String hex = Integer.toHexString(this._data[i] & 255);
         if (hex.length() == 1) {
            sb.append('0');
         }

         sb.append(hex);
      }

      return sb.toString();
   }

   @Override
   public void persistentContentModeChanged(int generation) {
   }

   @Override
   public void persistentContentStateChanged(int state) {
      if (state == 2) {
         if (this._tempBuffer != null) {
            Arrays.fill(this._tempBuffer, ' ');
         }

         if (this._wordBuffer != null) {
            Arrays.fill(this._wordBuffer, ' ');
         }

         if (this._caseCorrectionBuffer != null) {
            Arrays.fill(this._caseCorrectionBuffer, ' ');
         }
      }
   }

   private void createStructures() {
      int complexNo = this._header.getMaxNestingLevel() - 1;
      if (this._complexPrefixTables == null || complexNo != this._complexPrefixTables.length) {
         this._complexPrefixTables = new LearningComplexPrefixTable[complexNo];

         for (int i = 0; i < complexNo; i++) {
            this._complexPrefixTables[i] = this.createLearningComplexPrefixTable(i, this);
         }
      }

      this._tablesStart = this._header.getHeaderSize() + this._alphabet.size();
      if (this._data != null) {
         this._complexPrefixTables[0].init(this._data, this._tablesStart, ' ', null, 0);
      }

      if (this._reIterator == null) {
         this._reIterator = (ReIterator)(new Object(this._header.getMaxNestingLevel() + 1));
      } else {
         this._reIterator.setMaxStackSize(this._header.getMaxNestingLevel() + 1);
      }
   }

   public LearningReader() {
      PersistentContent.addWeakListener(this);
   }
}
