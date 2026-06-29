package net.rim.plazmic.internal.mediaengine.format.v1_2;

import java.io.IOException;
import net.rim.plazmic.internal.mediaengine.MediaFactory;
import net.rim.plazmic.mediaengine.MediaException;

public class PMEInputStream {
   private byte[] _data;
   private int _offset;
   private int _checksum;

   public void set(byte[] data, int offset, boolean reset) {
      this._data = data;
      this._offset = offset;
      if (reset) {
         this._checksum = 0;
      }
   }

   public int available() {
      return this._data.length - this._offset;
   }

   public final int readInt() throws IOException {
      if (this._data.length - this._offset < 4) {
         throw new IOException();
      }

      int ch1 = this._data[this._offset++];
      int ch2 = this._data[this._offset++];
      int ch3 = this._data[this._offset++];
      int ch4 = this._data[this._offset++];
      return (ch1 << 24) + (ch2 << 16) + (ch3 << 8) + (ch4 << 0);
   }

   public final void skipInt() throws IOException {
      if (this._data.length - this._offset < 4) {
         throw new IOException();
      }

      this._offset += 4;
   }

   public String readString(String encoding) throws MediaException {
      if (this._data.length - this._offset < 2) {
         throw new MediaException(4);
      }

      int length = this.readData(1);
      if (this._data.length - this._offset < length) {
         throw new MediaException(4);
      }

      String result = MediaFactory.getPlatform().createString(this._data, this._offset, length, encoding);

      while (length > 0) {
         this.readData(4);
         length--;
      }

      return result;
   }

   public void skipString() throws MediaException {
      if (this._data.length - this._offset < 2) {
         throw new MediaException(4);
      }

      int length = this.readData(1);
      if (this._data.length - this._offset < length) {
         throw new MediaException(4);
      }

      while (length > 0) {
         this.skipData(4);
         length--;
      }
   }

   public void readDataArray(int[] array, int index, int length, int mask) {
      int bounds = index + length;

      while (index < bounds) {
         array[index++] = this.readData(mask);
      }
   }

   public void skipDataArray(int length, int mask) {
      for (int i = 0; i < length; i++) {
         this.skipData(mask);
      }
   }

   public int readData(int mask) {
      int result = 0;
      boolean signed = (mask & 4) != 0;

      for (int i = mask & 3; i > 0; i--) {
         result = (result | this._data[this._offset++] & 255) << 8;
      }

      result |= this._data[this._offset++] & 255;
      if (signed) {
         switch (mask) {
            case 3:
               break;
            case 4:
            default:
               result = (byte)result;
               break;
            case 5:
               result = (short)result;
               break;
            case 6:
               if (result > 8388608) {
                  int diff = result - 8388608;
                  result = -8388607 + diff - 1;
               }
               break;
            case 7:
               result = result;
         }
      }

      this._checksum += result;
      return result;
   }

   public void skipData(int mask) {
      this._offset += (mask & 3) + 1;
   }

   public void validateChecksum() throws MediaException {
      int pmeChecksum = this.readData(7);
      this._checksum -= pmeChecksum;
      if (pmeChecksum != this._checksum) {
         throw new MediaException(10);
      }
   }

   public void skipChecksum() {
      this.skipData(7);
   }

   public final String readUTF() throws MediaException {
      if (this._data.length - this._offset < 2) {
         throw new MediaException(4);
      }

      int ch1 = this._data[this._offset++];
      int ch2 = this._data[this._offset++];
      int utflen = (ch1 << 8) + (ch2 << 0);
      if (this._data.length - this._offset < utflen) {
         throw new MediaException(4);
      }

      int dataOffset = this._offset;
      this._offset += utflen;
      StringBuffer str = new StringBuffer(utflen);
      int count = 0;

      while (count < utflen) {
         int c = this._data[dataOffset + count] & 255;
         switch (c >> 4) {
            case -1:
            case 8:
            case 9:
            case 10:
            case 11:
               throw new MediaException(4);
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            default:
               count++;
               str.append((char)c);
               break;
            case 12:
            case 13:
               count += 2;
               if (count > utflen) {
                  throw new MediaException(4);
               }

               int char2 = this._data[dataOffset + count - 1];
               if ((char2 & 192) != 128) {
                  throw new MediaException(4);
               }

               str.append((char)((c & 31) << 6 | char2 & 63));
               break;
            case 14:
               count += 3;
               if (count > utflen) {
                  throw new MediaException(4);
               }

               int char2 = this._data[dataOffset + count - 2];
               int char3 = this._data[dataOffset + count - 1];
               if ((char2 & 192) != 128 || (char3 & 192) != 128) {
                  throw new MediaException(4);
               }

               str.append((char)((c & 15) << 12 | (char2 & 63) << 6 | (char3 & 63) << 0));
         }
      }

      return new String(str);
   }
}
