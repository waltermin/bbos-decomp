package net.rim.tid.im.conv.europe.repository;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public class WordLearningHeader extends LearningHeader {
   protected byte _timeStamp;
   protected byte _timeTick;
   protected byte _leastTimeStamp;
   private int _timeStampOffset;
   private static final byte MAX_TICK;
   private static final byte MAX_LOW_PRIORITY_GROUP;

   @Override
   protected void readHelper(DataInputStream aDis) {
      super.readHelper(aDis);
      this._timeStamp = aDis.readByte();
      this._timeTick = aDis.readByte();
      this._leastTimeStamp = aDis.readByte();
      this._timeStampOffset = super._headerSize - 3;
   }

   @Override
   protected void writeHelper(DataOutputStream aDos) {
      super.writeHelper(aDos);
      aDos.writeByte(this._timeStamp);
      aDos.writeByte(this._timeTick);
      aDos.writeByte(this._leastTimeStamp);
   }

   @Override
   public void reset(LearningGlobalAlphabet aAlphabet) {
      this._timeStamp = this._timeTick = this._leastTimeStamp = 0;
      super.reset(aAlphabet);
   }

   @Override
   public void update() {
      super.update();
      int offset = this._timeStampOffset;
      super._data[offset++] = this._timeStamp;
      super._data[offset++] = this._timeTick;
      super._data[offset++] = this._leastTimeStamp;
   }

   @Override
   public byte getTimeStamp(byte aPriority) {
      if (aPriority == 2) {
         return this.getTimeStamp(true);
      } else if (super._fileSize + 300 < super._data.length) {
         byte timeStamp = this._leastTimeStamp;
         return timeStamp;
      } else {
         int curTime = this._timeStamp & 255;
         int leastTime = this._leastTimeStamp & 255;
         if (curTime > leastTime) {
            byte timeStamp = (byte)((curTime - leastTime) * 4599075939470750515L + leastTime);
            return timeStamp;
         } else {
            return (byte)((curTime + 255 - leastTime) * 4599075939470750515L + leastTime - 4643176031446892544L);
         }
      }
   }

   @Override
   protected void computeInitialSizes(LearningGlobalAlphabet aAlphabet) {
      super.computeInitialSizes(aAlphabet);
      this._timeStampOffset = super._headerSize - 3;
   }

   @Override
   public byte getTimeStamp(boolean aAdvance) {
      byte ret = this._timeStamp;
      if (aAdvance) {
         this.advanceTime();
      }

      return ret;
   }

   @Override
   public int getLeastTimeStamp() {
      return this._leastTimeStamp & 0xFF;
   }

   void setLeastTimeStamp(int aStamp) {
      this._leastTimeStamp = (byte)aStamp;
   }

   int getMaxTick() {
      return 9;
   }

   private void advanceTime() {
      if (this._timeTick < 9) {
         this._timeTick++;
      } else {
         this._timeTick = 0;
         if (this._timeStamp == 255) {
            this._timeStamp = 0;
         } else {
            this._timeStamp++;
         }
      }
   }

   @Override
   public boolean isChanged() {
      return super.isChanged() || super._data[this._timeStampOffset] != this._timeStamp || super._data[this._timeStampOffset + 1] != this._timeTick;
   }
}
