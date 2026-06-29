package net.rim.tid.im;

import net.rim.tid.util.SLTextDataContainer;

public class SupplementaryInputDataContainer implements ISupplementaryInputData {
   private StringBuffer _originatingKeys;
   private int _originatingKeysCommittedCount;
   private StringBuffer _alternativeReading;
   private StringBuffer _originalReading;
   private int _alternativeReadingCommittedCharacterCount;
   private int _originalReadingCommCharCount;
   private SLTextDataContainer _supplementarySearchReadings;

   public void setOriginalReadingCommCharCount(int originalReadingCommCharCount) {
      this._originalReadingCommCharCount = originalReadingCommCharCount;
   }

   public void setOriginatingKeys(StringBuffer originatingKeys, int committed) {
      this._originatingKeys = originatingKeys;
      this._originatingKeysCommittedCount = committed;
   }

   public void setOriginalReading(StringBuffer originalReading) {
      this._originalReading = originalReading;
   }

   public void setAlternativeIdeographicReading(StringBuffer alternativeReading, int committed) {
      this._alternativeReading = alternativeReading;
      this._alternativeReadingCommittedCharacterCount = committed;
   }

   public void setSupplementarySearchReadings(SLTextDataContainer alternativeReadings) {
      this._supplementarySearchReadings = alternativeReadings;
   }

   @Override
   public StringBuffer getAlternativeIdeographicReading() {
      return this._alternativeReading;
   }

   @Override
   public String[] getSupplementarySearchReadings() {
      if (this._supplementarySearchReadings == null) {
         return null;
      }

      String[] result = new Object[this._supplementarySearchReadings._count];
      StringBuffer buffer = (StringBuffer)(new Object());
      int index = 0;

      while (this._supplementarySearchReadings.next(buffer)) {
         result[index++] = buffer.toString();
      }

      return result;
   }

   @Override
   public int getAlternativeReadingCommittedCharacterCount() {
      return this._alternativeReadingCommittedCharacterCount;
   }

   @Override
   public StringBuffer getOriginalReading() {
      return this._originalReading;
   }

   @Override
   public int getOriginalReadingCommitedCharacterCount() {
      return this._originalReadingCommCharCount;
   }

   @Override
   public int getOriginatingKeysCommittedCount() {
      return this._originatingKeysCommittedCount;
   }

   @Override
   public StringBuffer getOriginatingKeys() {
      return this._originatingKeys;
   }
}
