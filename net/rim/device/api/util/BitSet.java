package net.rim.device.api.util;

import net.rim.vm.Array;

public class BitSet implements net.rim.vm.Persistable {
   private int[] _data;
   private int _numSet;

   public BitSet() {
      this(128);
   }

   public BitSet(int size) {
      if (size < 0) {
         throw new IllegalArgumentException();
      }

      this._data = new int[size + 31 >> 5];
      this._numSet = -1;
   }

   public BitSet(BitSet srcBitSet) {
      int[] src = srcBitSet._data;
      this._data = new int[src.length];
      System.arraycopy(src, 0, this._data, 0, src.length);
      this._numSet = srcBitSet._numSet;
   }

   public void fastSet(int index) {
      int bit = 1 << (index & 31);
      int elt = index >> 5;
      if (elt >= this._data.length) {
         Array.resize(this._data, elt + 1);
      }

      this._data[elt] = this._data[elt] | bit;
      this._numSet = -1;
   }

   public void set(int index) {
      int bit = 1 << (index & 31);
      int elt = index >> 5;
      if (elt >= this._data.length) {
         Array.resize(this._data, elt + 1);
      }

      int d = this._data[elt];
      if ((d & bit) == 0) {
         d |= bit;
         if (this._numSet != -1) {
            this._numSet++;
         }

         this._data[elt] = d;
      }
   }

   public void fastClear(int index) {
      int bit = 1 << (index & 31);
      int elt = index >> 5;
      if (elt < this._data.length) {
         this._data[elt] = this._data[elt] & ~bit;
         this._numSet = -1;
      }
   }

   public void clear(int index) {
      int bit = 1 << (index & 31);
      int elt = index >> 5;
      if (elt < this._data.length) {
         int d = this._data[elt];
         if ((d & bit) != 0) {
            d &= ~bit;
            this._data[elt] = d;
            if (this._numSet != -1) {
               this._numSet--;
            }
         }
      }
   }

   public void reset() {
      for (int i = 0; i < this._data.length; i++) {
         this._data[i] = 0;
      }

      this._numSet = 0;
   }

   public boolean isSet(int index) {
      try {
         int element = index >> 5;
         if (element < this._data.length) {
            return (this._data[element] & 1 << (index & 31)) != 0;
         }
      } catch (Exception var3) {
      }

      return false;
   }

   public int getNumSet() {
      if (this._numSet == -1) {
         this._numSet = 0;

         for (int i = 0; i < this._data.length; i++) {
            for (int data = this._data[i]; data != 0; data &= data - 1) {
               this._numSet++;
            }
         }
      }

      return this._numSet;
   }

   @Override
   public boolean equals(Object obj) {
      if (!(obj instanceof BitSet)) {
         return false;
      }

      BitSet b = (BitSet)obj;
      if (this.getNumSet() != b.getNumSet()) {
         return false;
      }

      int len = this._data.length;
      if (b._data.length < len) {
         len = b._data.length;
      }

      for (int i = 0; i < len; i++) {
         if (this._data[i] != b._data[i]) {
            return false;
         }
      }

      return true;
   }

   public int getNextSet(int index) {
      int bit = 1 << (index & 31);
      int elt = index >> 5;
      int mask = -bit;

      try {
         while ((this._data[elt] & mask) == 0) {
            elt++;
            index = index + 32 & -32;
            bit = 1;
            mask = -1;
         }

         for (int bits = this._data[elt]; (bits & bit) == 0; index++) {
            bit <<= 1;
         }

         return index;
      } catch (ArrayIndexOutOfBoundsException e) {
         return -1;
      }
   }

   public int getPreviousSet(int index) {
      int bit = 1 << (index & 31);
      int elt = index >> 5;
      if (elt >= this._data.length) {
         elt = this._data.length - 1;
         bit = Integer.MIN_VALUE;
         index = this._data.length * 32 - 1;
      }

      int mask = bit | bit - 1;

      try {
         while ((this._data[elt] & mask) == 0) {
            elt--;
            index = (index & -32) - 1;
            bit = Integer.MIN_VALUE;
            mask = -1;
         }

         for (int bits = this._data[elt]; (bits & bit) == 0; index--) {
            bit >>= 1;
         }

         return index;
      } catch (ArrayIndexOutOfBoundsException e) {
         return -1;
      }
   }

   public int getFirstSet() {
      return this.getNextSet(0);
   }

   public int getLastSet() {
      return this.getPreviousSet(this._data.length * 32 - 1);
   }

   public void and(BitSet mask) {
      int size = this._data.length;
      int maskSize = mask._data.length;
      if (maskSize < size) {
         size = maskSize;
         Array.resize(this._data, size);
      }

      for (int i = 0; i < size; i++) {
         this._data[i] = this._data[i] & mask._data[i];
      }

      this._numSet = -1;
   }

   public void or(BitSet mask) {
      int size = this._data.length;
      int maskSize = mask._data.length;
      if (maskSize > size) {
         Array.resize(this._data, maskSize);
      }

      size = maskSize;

      for (int i = 0; i < size; i++) {
         this._data[i] = this._data[i] | mask._data[i];
      }

      this._numSet = -1;
   }

   public void not() {
      int size = this._data.length;

      for (int i = 0; i < size; i++) {
         this._data[i] = ~this._data[i];
      }

      if (this._numSet != -1) {
         this._numSet = this._data.length * 32 - this._numSet;
      }
   }

   public void xor(BitSet mask) {
      int size = this._data.length;
      int maskSize = mask._data.length;
      if (maskSize > size) {
         Array.resize(this._data, maskSize);
      }

      size = maskSize;

      for (int i = 0; i < size; i++) {
         this._data[i] = this._data[i] ^ mask._data[i];
      }

      this._numSet = -1;
   }
}
