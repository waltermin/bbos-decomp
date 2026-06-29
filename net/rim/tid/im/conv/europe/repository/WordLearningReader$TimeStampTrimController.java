package net.rim.tid.im.conv.europe.repository;

import net.rim.tid.OTAsync.CustomWordsSyncManager;
import net.rim.vm.Array;

class WordLearningReader$TimeStampTrimController extends TrimController {
   private int _toTrimSize;
   private boolean _isValidToTrimSize;
   private int[] _nextSearchPos = new int[5];
   private int _nextSearchChainLen = 0;
   private boolean _isDirty = true;
   private CustomWordsSyncManager _sm;

   public void init(int aLeastTimeStamp, int aCurrTimeStamp, int aRemoveInterval, int aToTrimSize, int aWordsToRemoveCount) {
      super._wordsToRemoveInitCount = aWordsToRemoveCount;
      this._toTrimSize = aToTrimSize;
      this._isValidToTrimSize = aToTrimSize != -1;
      this._nextSearchChainLen = 0;
      this._isDirty = false;
      super.init(aLeastTimeStamp, aCurrTimeStamp, aRemoveInterval);
   }

   @Override
   public boolean isTrimmingFinished() {
      return super._wordsToRemoveCount <= 0 || this._isValidToTrimSize && super._trimmedSize >= this._toTrimSize;
   }

   @Override
   public int getNewLeastTimeStamp() {
      if (!this._isValidToTrimSize) {
         return super._leastTimeStamp;
      } else {
         return super._wordsToRemoveCount != 0 && super._trimmedSize >= this._toTrimSize ? super._leastTimeStamp : super._outdatedIntervalEnd;
      }
   }

   @Override
   public boolean allowSecondPass() {
      return !this._isValidToTrimSize;
   }

   @Override
   public boolean leastTimeStampChanged() {
      return super._trimmedSize > 0;
   }

   @Override
   public void complexTableFinished(int aComplexTableId, int aLastPos) {
      if (!this._isValidToTrimSize) {
         if (super._wordsToRemoveCount > 0) {
            this.advanceIntervals();
            return;
         }

         if (this._nextSearchPos.length <= aComplexTableId) {
            Array.resize(this._nextSearchPos, aComplexTableId + 1);
         }

         this._nextSearchPos[aComplexTableId] = aLastPos;
         if (this._nextSearchChainLen == 0) {
            this._nextSearchChainLen = aComplexTableId + 1;
         }
      }
   }

   private void advanceIntervals() {
      super._leastTimeStamp = super._leastTimeStamp + super._removeInterval;
      if (super._leastTimeStamp > 255) {
         super._leastTimeStamp -= 256;
      }

      int newIntervalEnd = super._leastTimeStamp + super._removeInterval;
      if (newIntervalEnd > 255) {
         newIntervalEnd -= 256;
      }

      super._outdatedIntervalEnd = this.max(super._outdatedIntervalEnd, newIntervalEnd);
      super._removeIntervalIsSplit = super._leastTimeStamp > super._outdatedIntervalEnd;
      super._isCurrentTimeGreater = super._currentTimeStamp >= super._leastTimeStamp;
      this._nextSearchChainLen = 0;
   }

   @Override
   public int getStoredStartPos(int aComplexTableId, int aOrigStartPos) {
      if (this._nextSearchChainLen == 0) {
         return aOrigStartPos;
      }

      int ret = aOrigStartPos + this._nextSearchPos[aComplexTableId];
      if (aComplexTableId == this._nextSearchChainLen - 1) {
         this._nextSearchChainLen = 0;
         ret++;
      }

      return ret;
   }

   @Override
   public void trimWords(int aCount) {
      super._wordsToRemoveCount -= aCount;
   }

   @Override
   public void trimWord(char[] aBuffer, int aStart, int aLength) {
      this._sm.addWord(aBuffer, aStart, aLength);
      super._wordsToRemoveCount--;
   }

   public boolean isDirty() {
      return this._isDirty;
   }

   private void setDirty() {
      this._isDirty = true;
   }

   @Override
   public byte getAction() {
      return 1;
   }
}
