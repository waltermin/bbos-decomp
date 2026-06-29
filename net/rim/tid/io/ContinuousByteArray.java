package net.rim.tid.io;

import java.io.OutputStream;

public class ContinuousByteArray {
   private ContinuousByteArray$Position iPosition = new ContinuousByteArray$Position();
   private ContinuousByteArray$BlockIndexes lastBlockIndex = new ContinuousByteArray$BlockIndexes();
   int iStart;
   int iLength;
   byte[][][] iBuffer;

   public ContinuousByteArray() {
      this(new byte[0]);
   }

   public ContinuousByteArray(byte[] aBlock, int aStart, int aLength) {
      this.iBuffer = new byte[1][][];
      this.iBuffer[0] = (byte[][])aBlock;
      this.iStart = aStart;
      this.iLength = aLength;
   }

   public ContinuousByteArray(byte[] aBlock) {
      this(aBlock, 0, aBlock.length);
   }

   public ContinuousByteArray(byte[][][] aBuffer, int aStart, int aLength) {
      int first_block = 0;
      int last_block = 0;
      this.iStart = aStart;
      this.iLength = aLength;
      if (aLength == 0) {
         this.iBuffer = new byte[0][0][];
         this.iStart = 0;
      }

      while (this.iStart >= aBuffer[first_block].length) {
         this.iStart = this.iStart - aBuffer[first_block++].length;
      }

      aStart = this.iStart;

      for (last_block = first_block; aLength > aBuffer[last_block].length - aStart; aStart = 0) {
         aLength -= aBuffer[last_block++].length - aStart;
      }

      int len = last_block - first_block + 1;
      if (aBuffer.length == len) {
         this.iBuffer = aBuffer;
      } else {
         this.iBuffer = new byte[len][][];

         for (int i = 0; i < len; i++) {
            this.iBuffer[i] = aBuffer[first_block + i];
         }
      }
   }

   public ContinuousByteArray(byte[][][] aBuffer) {
      this.iBuffer = aBuffer;
      this.iStart = 0;
      this.iLength = 0;

      for (int i = 0; i < aBuffer.length; i++) {
         this.iLength = this.iLength + aBuffer[i].length;
      }
   }

   public byte[] getSingleBlock() {
      if (this.iBuffer.length != 1) {
         throw new Object("Array consists of multiple blocks");
      } else {
         return (byte[])this.iBuffer[0];
      }
   }

   public int length() {
      return this.iLength;
   }

   public byte byteAt(int index) {
      int i = 0;
      int start = this.iStart;

      for (i = 0; i < this.iBuffer.length && this.iBuffer[i].length - start <= index; i++) {
         index -= this.iBuffer[i].length - start;
         start = 0;
      }

      return (byte)this.iBuffer[i][start + index];
   }

   public ContinuousByteArray$BlockIndexes getLastBlockRequested() {
      return this.lastBlockIndex;
   }

   public int charAt(int index) {
      int result = 0;
      int i = 0;
      int start = this.iStart;

      for (i = 0; i < this.iBuffer.length && this.iBuffer[i].length - start <= index; i++) {
         index -= this.iBuffer[i].length - start;
         start = 0;
      }

      result = (this.iBuffer[i][start + index] & 255) << 8;
      return this.iBuffer[i].length == start + index - 1 ? result | this.iBuffer[++i][0] & 0xFF : result | this.iBuffer[i][start + index + 1] & 0xFF;
   }

   public int intAt(int index) {
      int result = 0;
      int i = 0;
      int start = this.iStart;

      for (i = 0; i < this.iBuffer.length && this.iBuffer[i].length - start <= index; i++) {
         index -= this.iBuffer[i].length - start;
         start = 0;
      }

      int ptr = start + index;
      result = (this.iBuffer[i][ptr++] & 255) << 24;
      if (this.iBuffer[i].length == ptr) {
         i++;
         ptr = 0;
      }

      result |= (this.iBuffer[i][ptr++] & 255) << 16;
      if (this.iBuffer[i].length == ptr) {
         i++;
         ptr = 0;
      }

      result |= (this.iBuffer[i][ptr++] & 255) << 8;
      if (this.iBuffer[i].length == ptr) {
         i++;
         ptr = 0;
      }

      return result | this.iBuffer[i][ptr++] & 0xFF;
   }

   public ContinuousByteArray subArray(int beginIndex, int endIndex) {
      return new ContinuousByteArray(this.iBuffer, this.iStart + beginIndex, endIndex - beginIndex);
   }

   public boolean equals(byte[] toCompare) {
      if (toCompare.length != this.iLength) {
         return false;
      }

      int len = this.iLength;
      int j = 0;
      int index = this.iStart;

      for (int i = 0; i < this.iBuffer.length; i++) {
         int l = this.iBuffer[i].length;
         if (l <= index) {
            index -= this.iBuffer[i].length;
         } else {
            int till = index + len;
            if (till >= l) {
               till = l;
               len -= l - index;
            }

            for (int k = index; k < till; j++) {
               if (this.iBuffer[i][k] != toCompare[j]) {
                  return false;
               }

               k++;
            }

            if (till < l) {
               return true;
            }

            index = 0;
         }
      }

      return true;
   }

   @Override
   public String toString() {
      StringBuffer buf = (StringBuffer)(new Object());
      int len = this.iLength;
      int index = this.iStart;

      for (int i = 0; i < this.iBuffer.length; i++) {
         int l = this.iBuffer[i].length;
         if (l > index) {
            int till = index + len;
            if (till >= l) {
               till = l;
               len -= l - index;
            }

            for (int k = index; k < till; k++) {
               buf.append(((StringBuffer)(new Object(","))).append((int)this.iBuffer[i][k]).toString());
            }

            if (till < l) {
               break;
            }

            index = 0;
         } else {
            index -= this.iBuffer[i].length;
         }
      }

      return buf.toString();
   }

   public void writeInto(OutputStream os, int start, int length) {
      if (this.iBuffer != null && this.iBuffer.length != 0) {
         start += this.iStart;

         int block;
         for (block = 0; block < this.iBuffer.length && this.iBuffer[block].length <= start; block++) {
            start -= this.iBuffer[block].length;
            if (block == this.iBuffer.length - 1) {
               break;
            }
         }

         for (int i = block; length != 0; i++) {
            int toWrite = this.iBuffer[i].length - start - length < 0 ? this.iBuffer[i].length - start : length;
            os.write((byte[])this.iBuffer[i], start, toWrite);
            start = 0;
            length -= toWrite;
         }
      }
   }

   public void writeInto(OutputStream os) {
      this.writeInto(os, 0, this.length());
   }

   public void writeInto(OutputStream os, int start) {
      this.writeInto(os, start, this.length() - start);
   }

   public byte[][][] getData() {
      return this.iBuffer;
   }

   public ContinuousByteArray$Position getPosition(int aOffset) {
      int i = 0;
      int start = this.iStart;
      boolean found = false;

      for (i = 0; i < this.iBuffer.length; i++) {
         if (this.iBuffer[i].length - start > aOffset) {
            found = true;
            break;
         }

         aOffset -= this.iBuffer[i].length - start;
         start = 0;
      }

      if (found) {
         this.iPosition.iBlock = (byte[])this.iBuffer[i];
         this.iPosition.iOffset = start + aOffset;
      } else {
         this.iPosition.iBlock = null;
      }

      return this.iPosition;
   }
}
