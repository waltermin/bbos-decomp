package net.rim.tid.im.conv.europe.repository;

import java.util.Enumeration;
import java.util.Vector;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.CharacterUtilities;
import net.rim.tid.OTAsync.CustomWordSyncObject;
import net.rim.tid.OTAsync.CustomWordsSyncCollection;
import net.rim.tid.OTAsync.CustomWordsSyncManager;
import net.rim.tid.data.LearningDataManager;
import net.rim.tid.im.conv.SLCurrentVariant;
import net.rim.tid.im.conv.europe.CaseCorrector;
import net.rim.tid.im.conv.repository.ExtendedCurrentVariant;
import net.rim.tid.im.conv.repository.ResultContainer;
import net.rim.vm.Array;

public class WordLearningReader extends LearningReader {
   protected byte _internalEvent;
   private SLCurrentVariant _tempVariant = (SLCurrentVariant)(new Object());
   protected ExtendedCurrentVariant _extTempVariant = (ExtendedCurrentVariant)(new Object());
   private ResultContainer _resultContainer = (ResultContainer)(new Object());
   protected CustomWordsSyncCollection _listener;
   private boolean _enableOTAlistener;
   private WordLearningReader$TimeStampTrimController _trimControllerForTrim;
   private WordLearningReader$TimeStampTrimController _trimControllerForSetSize;
   protected boolean _ignoreTimeStamps;
   private static final byte SPLIT_EVENT = 1;
   protected static final byte TRIM_EVENT = 2;
   private static final byte PROMOTE_TS_EVENT = 4;
   public static final int FREQ_TYPE_SIZE = 7168;
   public static final int LEARN_TYPE_SIZE = 5120;

   public void init(byte type) {
      super._type = type;
      super._sizeLimit = super._type == 3 ? 7168 : 5120;
      super._header = new WordLearningHeader();
      super._alphabet = new LearningGlobalAlphabet(true);
      super._simplePrefixTable = new WordLearningSimplePrefixTable(0, this, type);
      this._listener = CustomWordsSyncCollection.getInstance();
   }

   public void enableOTAListener(boolean enable) {
      this._enableOTAlistener = enable;
   }

   @Override
   public void clearAll() {
      synchronized (LearningDataManager.getLock()) {
         if (this._enableOTAlistener && super._sizeLimit > 0) {
            if (this._listener.isQuickDeleteSupported()) {
               this._listener.clear(super._currentLearnName);
            } else {
               CustomWordsSyncManager sm = (CustomWordsSyncManager)(new Object(2));

               try {
                  super._complexPrefixTables[0].getEntries(sm, super._wordBuffer, super._header.getLocaleStr());
               } finally {
                  ;
               }

               this._listener.remove(sm);
            }
         }

         super.clearAll();
      }
   }

   @Override
   public String getFilename(String aLocaleStr) {
      return LearningDataManager.constructKey(null, aLocaleStr, super._type);
   }

   @Override
   public LearningComplexPrefixTable createLearningComplexPrefixTable(int aOrder, LearningReader aReader) {
      return new WordLearningComplexPrefixTable(aOrder, aReader, super._type);
   }

   public int addWord(String aWord, char aFreq, boolean aConvertToLowerCase, boolean programmatically) {
      int len = aWord.length();
      if (len >= 50) {
         System.out.println(((StringBuffer)(new Object("Word( "))).append(aWord).append(") is too long!").toString());
         return 1;
      } else {
         aWord.getChars(0, len, super._tempBuffer, 0);
         this._tempVariant._variants = super._tempBuffer;
         this._tempVariant._length = len;
         return this.addWord(this._tempVariant, aFreq, aConvertToLowerCase, false, programmatically, (byte)2);
      }
   }

   public int addWord(String aWord, char aFreq, boolean aConvertToLowerCase) {
      return this.addWord(aWord, aFreq, aConvertToLowerCase, true);
   }

   public int addWord(SLCurrentVariant aWord, char aFreq, boolean aConvertToLowerCase) {
      return this.addWord(aWord, aFreq, aConvertToLowerCase, false, true, (byte)2);
   }

   public int addWord(SLCurrentVariant aWord, char aFreq, boolean aConvertToLowerCase, byte aPriority) {
      return this.addWord(aWord, aFreq, aConvertToLowerCase, false, true, aPriority);
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public int addWord(SLCurrentVariant aWord, char aFreq, boolean aConvertToLowerCase, boolean aUpdateTimeStampOnly, boolean programmatically, byte aPriority) {
      this._internalEvent = 0;
      if (aWord._length > getMaxAllowedWordLength()) {
         System.out
            .println(
               ((StringBuffer)(new Object("Word( ")))
                  .append((String)(new Object(aWord._variants, aWord._offset, aWord._length)))
                  .append(") is too long!")
                  .toString()
            );
         return 1;
      }

      try {
         if (super._sizeLimit == 0) {
            return 3;
         }

         synchronized (LearningDataManager.getLock()) {
            this.reloadIfChanged();
            int maxSizeGrow = aWord._length + super._alphabet.getAlphabetGrowth(aWord) + 8;
            if (!this.ensureSize(maxSizeGrow)) {
               System.err.println("addWord - no space");
               this.modifyLearningData(false);
               return 3;
            }

            int size = super._alphabet.size();
            int offset = 0;
            super._encoded[offset++] = super._header.getTimeStamp(aPriority);
            if (LearningReader._debugOutputEnabled && aPriority == 1) {
               System.out.println(((StringBuffer)(new Object("\tAssigned time stamp: "))).append(super._encoded[offset - 1] & 255).toString());
            }

            int encodedLen = -1;
            if (super._type == 3) {
               super._encoded[offset++] = (byte)((aFreq & '\uff00') >>> 8);
               super._encoded[offset++] = (byte)aFreq;
               encodedLen -= 2;
            }

            int len = super._alphabet.encodeWord(aWord, super._encoded, offset, aConvertToLowerCase, !aUpdateTimeStampOnly);
            if (len == -1) {
               this.modifyLearningData(false);
               return 7;
            }

            encodedLen += len;
            int alphabet_change = super._alphabet.size() - size;
            if (alphabet_change != 0) {
               System.arraycopy(
                  super._data, super._tablesStart, super._data, super._tablesStart + alphabet_change, super._header.getFileSize() - super._tablesStart
               );
               super._header.growFileSize(alphabet_change);
               super._tablesStart += alphabet_change;
               super._complexPrefixTables[0].init(super._data, super._tablesStart, ' ', null, 0);
            }

            LearningPrefixTable table = super._complexPrefixTables[0].findTableForEncodedWord(super._encoded, offset, encodedLen);
            if (aUpdateTimeStampOnly && table instanceof LearningComplexPrefixTable) {
               return 7;
            }

            int action = aUpdateTimeStampOnly ? 1 : 0;
            table.add(super._encoded, len, action, super._type == 3 ? 3 : 1, aPriority);
            this.modifyLearningData(false);
            if (this._enableOTAlistener && !aUpdateTimeStampOnly) {
               this._listener
                  .update(
                     (CustomWordSyncObject)(new Object(
                        aWord._variants,
                        aWord._offset,
                        aWord._length,
                        super._header.getLocaleStr(),
                        super._type,
                        aFreq,
                        super._type == 3 ? 3 : 1,
                        !programmatically
                     ))
                  );
            }

            return 0;
         }
      } catch (Throwable var20) {
         System.out
            .println(((StringBuffer)(new Object("Failed on word:"))).append((String)(new Object(aWord._variants, aWord._offset, aWord._length))).toString());
         e.printStackTrace();
         return 1;
      }
   }

   public static int getMaxAllowedWordLength() {
      return 46;
   }

   public void updateTimeStamp(SLCurrentVariant wordToUpdate) {
      this.addWord(wordToUpdate, '\u0000', false, true, true, (byte)2);
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public int removeWord(SLCurrentVariant aWord, boolean programmatically) {
      try {
         if (super._sizeLimit == 0) {
            return 1;
         }

         synchronized (LearningDataManager.getLock()) {
            this.reloadIfChanged();
            int len = super._alphabet.encodeWord(aWord, super._encoded, 0, false, false);
            if (len == -1) {
               return 1;
            }

            LearningPrefixTable table = super._complexPrefixTables[0].findTableForEncodedWord(super._encoded, 0, len);
            if (table.removeWord(super._encoded, len)) {
               this.modifyLearningData(false);
               if (this._enableOTAlistener) {
                  this._listener
                     .update(
                        (CustomWordSyncObject)(new Object(
                           aWord._variants, aWord._offset, aWord._length, super._header.getLocaleStr(), super._type, 0, 2, !programmatically
                        ))
                     );
               }

               return 0;
            } else {
               return 1;
            }
         }
      } catch (Throwable var10) {
         System.out
            .println(((StringBuffer)(new Object("Failed on word:"))).append((String)(new Object(aWord._variants, aWord._offset, aWord._length))).toString());
         e.printStackTrace();
         return 1;
      }
   }

   public int removeWord(SLCurrentVariant aWord) {
      return this.removeWord(aWord, true);
   }

   public int removeWord(String aWord, boolean programmatically) {
      int len = aWord.length();
      aWord.getChars(0, len, super._tempBuffer, 0);
      this._tempVariant._variants = super._tempBuffer;
      this._tempVariant._length = len;
      return this.removeWord(this._tempVariant, programmatically);
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public boolean ensureSize(int aPotentialAdd) {
      if (!super.ensureSize(aPotentialAdd)) {
         return false;
      }

      if (this._ignoreTimeStamps) {
         return true;
      }

      int curTimeStamp = super._header.getTimeStamp(false) & 255;
      int leastTimeStamp = super._header.getLeastTimeStamp();
      if (leastTimeStamp == 0 && curTimeStamp == 255 || curTimeStamp + 1 == leastTimeStamp) {
         this._internalEvent = (byte)(this._internalEvent | 4);
         boolean var6 = false /* VF: Semaphore variable */;

         try {
            var6 = true;
            this.trim(this.getTrimControllerForPromote(10));
            if (this._trimControllerForTrim != null) {
               this._trimControllerForTrim.setDirty();
               return true;
            }

            var6 = false;
         } finally {
            if (var6) {
               System.out.println("Failed on trimming");
               return false;
            }
         }
      }

      return true;
   }

   @Override
   public int trim(int desiredSize) {
      this._internalEvent = (byte)(this._internalEvent | 2);
      CustomWordsSyncManager sm = (CustomWordsSyncManager)(new Object(2));
      int trimmed = 0;

      for (int i = 0; i < 3 && trimmed < desiredSize; i++) {
         trimmed += this.trim(this.getTrimControllerForTrim(super._type == 3 ? 7 : 3, sm));
         if (this._enableOTAlistener && sm.size() > 0 && trimmed >= desiredSize) {
            this._listener.remove(sm);
         }
      }

      return trimmed;
   }

   @Override
   public void trimToSizeLimit(int aSizeLimit) {
      int oldSize = 0;

      int newSize;
      for (newSize = this.getFileSize(); newSize > aSizeLimit && newSize != oldSize; newSize = this.getFileSize()) {
         int currTimeStamp = ((WordLearningHeader)super._header).getTimeStamp(false) & 255;
         int leastTimeStamp = ((WordLearningHeader)super._header).getLeastTimeStamp();
         int fullInterval = currTimeStamp >= leastTimeStamp ? currTimeStamp - leastTimeStamp : currTimeStamp + 255 - leastTimeStamp;
         int fileSize = this.getFileSize();
         double AUX_FACTOR = (double)4591870180066957722L;
         double wordsSize = fileSize * (4607182418800017408L - AUX_FACTOR);
         int toTrimSize = fileSize - aSizeLimit;
         double removeFactor = toTrimSize / wordsSize;
         int removeInterval = (int)(fullInterval * removeFactor);
         if (removeInterval == 0) {
            removeInterval = 1;
         } else if (removeInterval < 5) {
            removeInterval = 5;
         } else if (removeInterval < 10) {
            removeInterval = 10;
         }

         CustomWordsSyncManager sm = (CustomWordsSyncManager)(new Object(2));
         this.trim(this.getTrimControllerForSetSize(removeInterval, toTrimSize, sm));
         if (this._enableOTAlistener && sm.size() > 0) {
            this._listener.remove(sm);
         }

         oldSize = newSize;
      }

      if (oldSize == newSize) {
         System.err.println("Could not trim to required size");
      }

      this._trimControllerForTrim = null;
   }

   protected int trim(TrimController aTrimController) {
      int trimmed = 0;
      if (LearningReader._debugOutputEnabled) {
         aTrimController.printTrimMessage();
         System.out.println(((StringBuffer)(new Object("\tBefore trim: "))).append(this.size()).append(" words").toString());
      }

      super._complexPrefixTables[0].trim(aTrimController, super._wordBuffer);
      trimmed = aTrimController.getTrimmedSize();
      if (LearningReader._debugOutputEnabled) {
         ((WordLearningHeader)super._header).setLeastTimeStamp(aTrimController.getNewLeastTimeStamp());
         this.modifyLearningData(false);
         System.out.println(((StringBuffer)(new Object("\tAfter trim: "))).append(this.size()).append(" words").toString());
      }

      if (!aTrimController.isTrimmingFinished() && aTrimController.allowSecondPass()) {
         if (LearningReader._debugOutputEnabled) {
            System.out.println(((StringBuffer)(new Object("\tBefore cycled trim: "))).append(this.size()).append(" words").toString());
         }

         super._complexPrefixTables[0].trim(aTrimController, super._wordBuffer);
         trimmed = aTrimController.getTrimmedSize();
         if (LearningReader._debugOutputEnabled) {
            ((WordLearningHeader)super._header).setLeastTimeStamp(aTrimController.getNewLeastTimeStamp());
            this.modifyLearningData(false);
            System.out.println(((StringBuffer)(new Object("\tAfter cycled trim: "))).append(this.size()).append(" words").toString());
         }
      }

      if (aTrimController.leastTimeStampChanged()) {
         ((WordLearningHeader)super._header).setLeastTimeStamp(aTrimController.getNewLeastTimeStamp());
         this.modifyLearningData(false);
      }

      return trimmed;
   }

   public void setUseCache(boolean aUseCache) {
   }

   public void setElements(Vector addedElements, Vector removedElements, boolean programmatically) {
      if (addedElements != null) {
         Enumeration en = addedElements.elements();

         while (en.hasMoreElements()) {
            this.addWord((String)en.nextElement(), '\u0000', false, programmatically);
         }
      }

      if (removedElements != null) {
         Enumeration en = removedElements.elements();

         while (en.hasMoreElements()) {
            this.removeWord((String)en.nextElement(), programmatically);
         }
      }
   }

   public int size() {
      return this.getEntryNo();
   }

   public boolean contains(Object element) {
      return this.contains(element, true, true);
   }

   public boolean contains(Object element, boolean aCaseSensitive, boolean aStrictCase) {
      return this.getFrequency((String)element, aCaseSensitive, aStrictCase) != -1;
   }

   public int getFrequency(String aWord, boolean aCaseSensitive, boolean aStrictCase) {
      int len = aWord.length();
      boolean isUpperCase = aCaseSensitive && !aStrictCase;
      if (isUpperCase) {
         for (int i = 0; i < len; i++) {
            if (!CharacterUtilities.isUpperCase(aWord.charAt(i))) {
               isUpperCase = false;
               break;
            }
         }
      }

      this._resultContainer.reset(3, len, len, true, true);
      this.getVariants(aWord, this._resultContainer);
      int count = this._resultContainer.getVariantsCount();

      label55:
      for (int i = 0; i < count; i++) {
         this._resultContainer.getVariantAt(i, this._extTempVariant);
         char[] data = this._extTempVariant._variants;

         for (int j = 0; j < this._extTempVariant._length; j++) {
            if (aWord.charAt(j) != data[this._extTempVariant._offset + j]) {
               boolean is_incorrect = true;
               if (!aCaseSensitive || isUpperCase) {
                  if (CaseCorrector.toUpperCase(data[this._extTempVariant._offset + j], super._currentLocale)
                     == CaseCorrector.toUpperCase(aWord.charAt(j), super._currentLocale)) {
                     is_incorrect = false;
                  }
               } else if (j == 0 && !aStrictCase && CaseCorrector.toUpperCase(data[this._extTempVariant._offset + j], super._currentLocale) == aWord.charAt(j)) {
                  is_incorrect = false;
               }

               if (is_incorrect) {
                  continue label55;
               }
            }
         }

         return this._resultContainer.getOriginalFrequency(this._extTempVariant);
      }

      return -1;
   }

   public Enumeration getElements() {
      Vector entries = (Vector)(new Object());
      synchronized (LearningDataManager.getLock()) {
         this.getEntries(entries);
      }

      return entries.elements();
   }

   public int getElements(Object[] elements) {
      Vector entries = (Vector)(new Object());
      synchronized (LearningDataManager.getLock()) {
         this.getEntries(entries);
      }

      Array.resize(elements, entries.size());

      for (int i = 0; i < entries.size(); i++) {
         Arrays.add(elements, entries.elementAt(i));
      }

      return elements.length;
   }

   @Override
   public byte minTimeStamp(byte ts1, byte ts2) {
      return (byte)this.getTrimController().min(ts1 & 255, ts2 & 255);
   }

   public boolean isSplitOccurred() {
      return (this._internalEvent & 1) != 0;
   }

   public boolean isPromoteOccurred() {
      return (this._internalEvent & 4) != 0;
   }

   public boolean isTrimOccurred() {
      return (this._internalEvent & 2) != 0;
   }

   @Override
   public void splitOccurred(int aNewTableCount) {
      this._internalEvent = (byte)(this._internalEvent | 1);
   }

   WordLearningReader$TimeStampTrimController getTrimController() {
      return this.getTrimControllerForSetSize(0, 1, null);
   }

   protected WordLearningReader$TimeStampTrimController getTrimControllerForTrim(int aRemoveInterval, CustomWordsSyncManager sm) {
      int currTimeStamp = ((WordLearningHeader)super._header).getTimeStamp(false) & 255;
      if (this._trimControllerForTrim == null) {
         this._trimControllerForTrim = new WordLearningReader$TimeStampTrimController();
      }

      if (this._trimControllerForTrim.isDirty()) {
         int leastTimeStamp = ((WordLearningHeader)super._header).getLeastTimeStamp();
         int wordsToRemoveCount = ((WordLearningHeader)super._header).getMaxTick() + 1;
         this._trimControllerForTrim.init(leastTimeStamp, currTimeStamp, aRemoveInterval, -1, wordsToRemoveCount);
      } else {
         this._trimControllerForTrim.reset(currTimeStamp);
      }

      if (sm != null) {
         sm.setLocaleAndType(LearningDataManager.constructKey(null, super._header.getLocaleStr(), super._type));
      }

      this._trimControllerForTrim._sm = sm;
      return this._trimControllerForTrim;
   }

   private WordLearningReader$TimeStampTrimController getTrimControllerForSetSize(int aRemoveInterval, int aToTrimSize, CustomWordsSyncManager sm) {
      if (this._trimControllerForSetSize == null) {
         this._trimControllerForSetSize = new WordLearningReader$TimeStampTrimController();
      }

      int currTimeStamp = ((WordLearningHeader)super._header).getTimeStamp(false) & 255;
      int leastTimeStamp = ((WordLearningHeader)super._header).getLeastTimeStamp();
      int wordsToRemoveCount = aRemoveInterval * (((WordLearningHeader)super._header).getMaxTick() + 1);
      this._trimControllerForSetSize.init(leastTimeStamp, currTimeStamp, aRemoveInterval, aToTrimSize, wordsToRemoveCount);
      if (sm != null) {
         sm.setLocaleAndType(LearningDataManager.constructKey(null, super._header.getLocaleStr(), super._type));
      }

      this._trimControllerForSetSize._sm = sm;
      return this._trimControllerForSetSize;
   }

   private TrimController getTrimControllerForPromote(int aRemoveInterval) {
      WordLearningReader$PromoteTimeStampController promoteTimeStampController = new WordLearningReader$PromoteTimeStampController();
      int currTimeStamp = ((WordLearningHeader)super._header).getTimeStamp(false) & 255;
      int leastTimeStamp = ((WordLearningHeader)super._header).getLeastTimeStamp();
      promoteTimeStampController.init(leastTimeStamp, currTimeStamp, aRemoveInterval);
      return promoteTimeStampController;
   }

   @Override
   protected void resetPrivateMembers() {
      if (this._trimControllerForTrim != null) {
         this._trimControllerForTrim.setDirty();
      }
   }
}
