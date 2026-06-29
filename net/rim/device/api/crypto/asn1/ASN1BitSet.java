package net.rim.device.api.crypto.asn1;

import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.Persistable;

public class ASN1BitSet implements Persistable {
   private byte[] _data;
   private int _length;

   public ASN1BitSet(byte[] data, int size) {
      if (data != null && size >= 0 && size <= data.length << 3 && size >= (data.length << 3) - 7) {
         this._data = data;
         this._length = size;
         if (this._data.length > 0 && (this._length & 7) != 0) {
            this._data[this._data.length - 1] = (byte)(this._data[this._data.length - 1] & (byte)(255 << 8 - (this._length & 7)));
         }
      } else {
         throw new Object();
      }
   }

   public static ASN1BitSet append(ASN1BitSet bitSetOne, ASN1BitSet bitSetTwo) {
      byte[] bitSetOneData = bitSetOne._data;
      int bitSetOneDataLength = bitSetOneData.length;
      byte[] bitSetTwoData = bitSetTwo._data;
      int bitSetTwoDataLength = bitSetTwoData.length;
      byte[] newByteArray = new byte[bitSetOne._length + bitSetTwo._length + 7 >> 3];
      System.arraycopy(bitSetOneData, 0, newByteArray, 0, bitSetOneDataLength);
      int numBitsUsedInLastByte = bitSetOne._length & 7;
      if (numBitsUsedInLastByte == 0) {
         System.arraycopy(bitSetTwoData, 0, newByteArray, bitSetOneDataLength, bitSetTwoDataLength);
      } else {
         int unusedBitsInLastByteMask = (byte)(255 >> numBitsUsedInLastByte);
         int numBytesNeededInNextByte = 8 - numBitsUsedInLastByte;
         int startPos = bitSetOneDataLength - 1;

         for (int i = 0; i < bitSetTwoDataLength; i++) {
            newByteArray[startPos + i] = (byte)(newByteArray[startPos + i] | bitSetTwoData[i] >> numBitsUsedInLastByte & unusedBitsInLastByteMask);
            newByteArray[startPos + i + 1] = (byte)(newByteArray[startPos + i + 1] | bitSetTwoData[i] << numBytesNeededInNextByte);
         }
      }

      return new ASN1BitSet(newByteArray, bitSetOne._length + bitSetTwo._length);
   }

   public byte[] toByteArray() {
      return Arrays.copy(this._data);
   }

   public int getLength() {
      return this._length;
   }

   public boolean isSet(int index) {
      if (index >= 0 && index < this._length) {
         return (this._data[index >> 3] & 1 << 7 - (index & 7)) != 0;
      } else {
         throw new Object();
      }
   }

   public int getNumSet() {
      int numSet = 0;

      for (int i = 0; i < this._data.length; i++) {
         for (byte data = this._data[i]; data != 0; data = (byte)(data & data - 1)) {
            numSet++;
         }
      }

      return numSet;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }

      if (!(obj instanceof ASN1BitSet)) {
         return false;
      }

      ASN1BitSet b = (ASN1BitSet)obj;
      return this._length == b._length && Arrays.equals(this._data, b._data);
   }

   public int getNextSet(int index) {
      if (index >= 0 && index < this._length) {
         int bit = 1 << 7 - (index & 7);
         int element = index >> 3;

         for (int mask = bit | bit - 1; (this._data[element] & mask) == 0; mask = 255) {
            if (++element >= this._data.length) {
               return -1;
            }

            index = index + 8 & -8;
            bit = 128;
         }

         for (byte bits = this._data[element]; (bits & bit) == 0 && bit != 0; index++) {
            bit >>= 1;
         }

         return index >= this._length ? -1 : index;
      } else {
         throw new Object();
      }
   }

   public int getPreviousSet(int index) {
      if (index >= 0 && index < this._length) {
         int bit = 1 << 7 - (index & 7);
         int element = index >> 3;

         for (int mask = -bit; (this._data[element] & mask) == 0; mask = 255) {
            if (--element < 0) {
               return -1;
            }

            index = (index & -8) - 1;
            bit = 1;
         }

         for (byte bits = this._data[element]; (bits & bit) == 0; index--) {
            bit <<= 1;
         }

         return index;
      } else {
         throw new Object();
      }
   }

   public int getFirstSet() {
      if (this._length == 0) {
         return -1;
      } else {
         return this.isSet(0) ? 0 : this.getNextSet(0);
      }
   }

   public int getLastSet() {
      if (this._length == 0) {
         return -1;
      } else {
         return this.isSet(this._length - 1) ? this._length - 1 : this.getPreviousSet(this._length - 1);
      }
   }
}
