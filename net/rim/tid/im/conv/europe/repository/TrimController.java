package net.rim.tid.im.conv.europe.repository;

public class TrimController {
   protected int _trimmedSize;
   protected int _leastTimeStamp;
   protected int _outdatedIntervalEnd;
   protected boolean _removeIntervalIsSplit;
   protected int _currentTimeStamp;
   protected boolean _isCurrentTimeGreater;
   protected int _removeInterval;
   protected int _wordsToRemoveInitCount;
   protected int _wordsToRemoveCount;
   public static final byte REMOVE_WORD_ACTION = 1;
   public static final byte PROMOTE_TS_ACTION = 2;

   public void init(int aLeastTimeStamp, int aCurrTimeStamp, int aRemoveInterval) {
      this._leastTimeStamp = aLeastTimeStamp;
      this._removeInterval = aRemoveInterval;
      this._outdatedIntervalEnd = this._leastTimeStamp + aRemoveInterval;
      if (this._outdatedIntervalEnd > 255) {
         this._outdatedIntervalEnd -= 256;
      }

      this.reset(aCurrTimeStamp);
   }

   public void reset(int aCurrTimeStamp) {
      this._trimmedSize = 0;
      this._currentTimeStamp = aCurrTimeStamp;
      this._isCurrentTimeGreater = this._currentTimeStamp >= this._leastTimeStamp;
      this._wordsToRemoveCount = this._wordsToRemoveInitCount;
      int currentRemoveInterval = this._leastTimeStamp <= this._outdatedIntervalEnd
         ? this._outdatedIntervalEnd - this._leastTimeStamp
         : 255 - this._outdatedIntervalEnd + this._leastTimeStamp;
      if (currentRemoveInterval < this._removeInterval * 4) {
         this._outdatedIntervalEnd++;
         if (this._outdatedIntervalEnd > 255) {
            this._outdatedIntervalEnd -= 256;
         }
      }

      this._removeIntervalIsSplit = this._leastTimeStamp > this._outdatedIntervalEnd;
   }

   public boolean isInOutdatedInterval(int aTimeStamp) {
      return this._removeIntervalIsSplit
         ? aTimeStamp >= this._leastTimeStamp || aTimeStamp < this._outdatedIntervalEnd
         : aTimeStamp < this._outdatedIntervalEnd && aTimeStamp >= this._leastTimeStamp;
   }

   public void printTrimMessage() {
      if (this._removeIntervalIsSplit) {
         System.out.println("Trimming learning database from time stamps [0.." + this._outdatedIntervalEnd + "[ and [" + this._leastTimeStamp + ".." + "255]");
      } else {
         System.out.println("Trimming learning database from time stamps [" + this._leastTimeStamp + ".." + this._outdatedIntervalEnd + "[");
      }

      System.out.println("Current time stamp " + this._currentTimeStamp);
   }

   public int getOutdatedIntervalLength() {
      return this._outdatedIntervalEnd;
   }

   public int min(int aTimeStamp1, int aTimeStamp2) {
      if (aTimeStamp1 == Integer.MAX_VALUE) {
         return aTimeStamp2;
      }

      int min;
      int max;
      if (aTimeStamp1 > aTimeStamp2) {
         min = aTimeStamp2;
         max = aTimeStamp1;
      } else {
         min = aTimeStamp1;
         max = aTimeStamp2;
      }

      return min < this._leastTimeStamp && max >= this._leastTimeStamp ? max : min;
   }

   public int max(int aTimeStamp1, int aTimeStamp2) {
      int min;
      int max;
      if (aTimeStamp1 > aTimeStamp2) {
         min = aTimeStamp2;
         max = aTimeStamp1;
      } else {
         min = aTimeStamp1;
         max = aTimeStamp2;
      }

      return min < this._leastTimeStamp && max >= this._leastTimeStamp ? min : max;
   }

   public boolean isCurrentTimeGreater() {
      return this._isCurrentTimeGreater;
   }

   public int getCurrentTimeStamp() {
      return this._currentTimeStamp;
   }

   public boolean isOutdatedIntervalSplit() {
      return this._removeIntervalIsSplit;
   }

   public int getTrimmedSize() {
      return this._trimmedSize;
   }

   public void trimWords(int aCount) {
   }

   public void trimWord(char[] aBuffer, int aStart, int aLength) {
   }

   public void trim(int aSize) {
      this._trimmedSize += aSize;
   }

   public boolean isTrimmingFinished() {
      throw null;
   }

   public int getNewLeastTimeStamp() {
      throw null;
   }

   public boolean allowSecondPass() {
      throw null;
   }

   public boolean leastTimeStampChanged() {
      throw null;
   }

   public void complexTableFinished(int _1, int _2) {
      throw null;
   }

   public int getStoredStartPos(int aComplexTableId, int aOrigStartPos) {
      return aOrigStartPos;
   }

   public byte getAction() {
      throw null;
   }
}
