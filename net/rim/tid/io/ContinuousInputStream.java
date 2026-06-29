package net.rim.tid.io;

import java.io.InputStream;
import net.rim.tid.util.Utils;

public class ContinuousInputStream extends InputStream {
   private int iMark;
   private int iDataBlock;
   private int iPtr;
   private int iOffset;
   private int iStart;
   private int iLength;
   private byte[][] iBuffer;

   public ContinuousInputStream(byte[] aBlock) {
      this.init(aBlock);
   }

   public ContinuousInputStream(ContinuousByteArray aData) {
      this.init(aData);
   }

   public ContinuousInputStream(ContinuousByteArray aData, int beginIndex, int endIndex) {
      this.init(aData, beginIndex, endIndex);
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public void init(ContinuousByteArray aData, int beginIndex, int endIndex) {
      byte[][] aBuffer = aData.iBuffer;
      int aLength = endIndex - beginIndex;
      int aStart = beginIndex + aData.iStart;
      int first_block = 0;
      int last_block = 0;
      this.iStart = aStart;
      this.iLength = aLength;
      if (aLength == 0) {
         this.iBuffer = new byte[0][0];
         this.iStart = 0;
      }

      label65:
      try {
         while (this.iStart >= aBuffer[first_block].length) {
            this.iStart = this.iStart - aBuffer[first_block++].length;
         }
      } catch (Throwable var12) {
         e.printStackTrace();
         break label65;
      }

      aStart = this.iStart;

      for (last_block = first_block; aLength > aBuffer[last_block].length - aStart; aStart = 0) {
         aLength -= aBuffer[last_block++].length - aStart;
      }

      int len = last_block - first_block + 1;
      if (aBuffer.length == len) {
         this.iBuffer = aBuffer;
      } else {
         this.iBuffer = new byte[len][];

         for (int i = 0; i < len; i++) {
            this.iBuffer[i] = aBuffer[first_block + i];
         }
      }

      this.iOffset = 0;
      this.iPtr = this.iStart;
      this.iDataBlock = 0;
   }

   public void init(byte[] aBlock) {
      this.iBuffer = new byte[1][];
      this.iBuffer[0] = aBlock;
      this.iLength = aBlock.length;
      this.iStart = 0;
      this.seek(0);
   }

   public void init(ContinuousByteArray aData) {
      this.iBuffer = aData.iBuffer;
      this.iLength = aData.iLength;
      this.iStart = aData.iStart;
      this.seek(0);
   }

   public ContinuousInputStream(byte[][] aBuffer) {
      this.iBuffer = aBuffer;
      this.iStart = 0;
      this.iLength = 0;

      for (int i = 0; i < aBuffer.length; i++) {
         this.iLength = this.iLength + aBuffer[i].length;
      }

      this.seek(0);
   }

   public byte[][] getBuffer() {
      return this.iBuffer;
   }

   public ContinuousByteArray getAllData() {
      ContinuousByteArray result = new ContinuousByteArray(this.iBuffer);
      result.iLength = this.iLength;
      result.iStart = this.iStart;
      return result;
   }

   @Override
   public int available() {
      return this.iLength - this.iOffset;
   }

   @Override
   public void mark(int readlimit) {
      this.iMark = this.iOffset;
   }

   @Override
   public boolean markSupported() {
      return true;
   }

   public int getCurrentOffset() {
      return this.iPtr;
   }

   public int getPosition() {
      return this.iOffset;
   }

   @Override
   public int read() {
      if (this.iPtr == this.iBuffer[this.iDataBlock].length) {
         if (this.iBuffer.length == this.iDataBlock + 1) {
            return -1;
         }

         this.iDataBlock++;
         this.iPtr = 0;
      }

      this.iOffset++;
      return this.iBuffer[this.iDataBlock][this.iPtr++] & 0xFF;
   }

   int readAll(int length) {
      int result = 0;
      this.iOffset += length;
      length += this.iPtr;
      byte[] block = this.iBuffer[this.iDataBlock];

      while (this.iPtr < length) {
         if (this.iPtr == block.length) {
            this.iDataBlock++;
            length -= this.iPtr;
            this.iPtr = 0;
            block = this.iBuffer[this.iDataBlock];
         }

         result <<= 8;
         result |= block[this.iPtr] & 255;
         this.iPtr++;
      }

      return result;
   }

   public char readChar() {
      return (char)this.readAll(2);
   }

   public int readInt() {
      return Utils.bytes2Int(this.read(), this.read(), this.read(), this.read());
   }

   public int readInt3() {
      return this.readAll(3);
   }

   @Override
   public int read(byte[] b) {
      return this.read(b, 0, b.length);
   }

   @Override
   public int read(byte[] b, int offset, int length) {
      int to_read = length;
      int actually_read = 0;

      while (to_read > 0) {
         int l = Math.min(to_read, this.iBuffer[this.iDataBlock].length - this.iPtr);
         if (l == 0) {
            if (this.iBuffer.length == this.iDataBlock + 1) {
               return actually_read;
            }

            this.iDataBlock++;
            this.iPtr = 0;
            l = Math.min(to_read, this.iBuffer[this.iDataBlock].length);
         }

         System.arraycopy(this.iBuffer[this.iDataBlock], this.iPtr, b, offset, l);
         this.iPtr += l;
         this.iOffset += l;
         offset += l;
         to_read -= l;
      }

      return actually_read;
   }

   public ContinuousByteArray read(int length) {
      ContinuousByteArray cba = new ContinuousByteArray(this.iBuffer, this.iOffset, length);
      this.skip(length);
      return cba;
   }

   @Override
   public void reset() {
      this.seek(this.iMark);
   }

   public void seek(int aOffset) {
      this.iPtr = this.iStart;
      this.iOffset = 0;
      this.iDataBlock = 0;
      this.skip(aOffset);
   }

   @Override
   public long skip(long n) {
      this.iPtr = (int)(this.iPtr + n);

      for (this.iOffset = (int)(this.iOffset + n); this.iDataBlock < this.iBuffer.length; this.iDataBlock++) {
         if (this.iBuffer[this.iDataBlock].length > this.iPtr) {
            return n;
         }

         this.iPtr = this.iPtr - this.iBuffer[this.iDataBlock].length;
      }

      return -1;
   }
}
