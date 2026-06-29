package net.rim.device.api.crypto;

import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.NumberUtilities;
import net.rim.vm.Array;

public class CryptoInteger {
   private byte[] _value;
   private int _flags;
   private int _offset;
   private int _hashCode;
   private static final int MAX_LENGTH = 256;
   private static final int OFFSET_IS_VALID = 1;
   private static final int HASH_CODE_IS_VALID = 2;

   public CryptoInteger(int value) {
      if (value < 0) {
         throw new Object();
      }

      this._value = this.intToByteArray(value);
      this._flags = 0;
   }

   public CryptoInteger(byte[] value) {
      this(value, 0, value == null ? 0 : value.length);
   }

   public CryptoInteger(byte[] value, int offset, int length) {
      if (value != null && offset >= 0 && length >= 1 && length <= 256 && value.length - length >= offset) {
         this._value = new byte[length];
         System.arraycopy(value, offset, this._value, 0, length);
         this._flags = 0;
      } else {
         throw new Object();
      }
   }

   public CryptoInteger(String value) {
      if (value == null) {
         throw new Object();
      }

      int length = value.length();
      if (length >= 2 && length <= 512 && (length & 1) == 0) {
         this._value = new byte[length >> 1];
         int shift = 4;

         for (int i = 0; i < length; i++) {
            char c = value.charAt(i);
            if (c >= '0' && c <= '9') {
               c = (char)(c - '0');
            } else if (c >= 'A' && c <= 'F') {
               c = (char)(c - '7');
            } else {
               if (c < 'a' || c > 'f') {
                  throw new Object();
               }

               c = (char)(c - 'W');
            }

            this._value[i >> 1] = (byte)(this._value[i >> 1] | (byte)(c << shift));
            shift ^= 4;
         }

         this._flags = 0;
      } else {
         throw new Object();
      }
   }

   public CryptoInteger(CryptoInteger other) {
      if (other == null) {
         throw new Object();
      }

      this._value = Arrays.copy(other._value);
      this._flags = other._flags;
      this._offset = other._offset;
      this._hashCode = other._hashCode;
   }

   protected CryptoInteger() {
      this._flags = 0;
      this._value = new byte[1];
   }

   public boolean isOdd() {
      return (this._value[this._value.length - 1] & 1) != 0;
   }

   public int compareTo(CryptoInteger other) {
      try {
         return CryptoByteArrayArithmetic.compare(this._value, other._value);
      } finally {
         throw new Object();
      }
   }

   public int compareTo(int other) {
      return other < 0 ? 1 : CryptoByteArrayArithmetic.compare(this._value, this.intToByteArray(other));
   }

   private void resizeAndShift(CryptoInteger modulus) {
      try {
         int oldLength = this._value.length;
         int newLength = modulus._value.length;
         if (oldLength < newLength) {
            Array.resize(this._value, newLength);
            int i = oldLength - 1;

            int j;
            for (j = newLength - 1; i >= 0; j--) {
               this._value[j] = this._value[i];
               i--;
            }

            while (j >= 0) {
               this._value[j] = 0;
               j--;
            }
         }
      } finally {
         throw new Object();
      }
   }

   private byte[] intToByteArray(int value) {
      return new byte[]{(byte)(value >> 24), (byte)(value >> 16), (byte)(value >> 8), (byte)value};
   }

   public void increment(CryptoInteger a, CryptoInteger modulus) {
      if (a == null) {
         throw new Object();
      }

      this._flags = 0;
      this.resizeAndShift(modulus);
      CryptoByteArrayArithmetic.add(
         this._value, 0, this._value.length, a._value, 0, a._value.length, modulus._value, 0, modulus._value.length, this._value, 0, this._value.length
      );
   }

   public void increment(int a, CryptoInteger modulus) {
      this._flags = 0;
      this.resizeAndShift(modulus);
      if (a >= 0) {
         byte[] n = this.intToByteArray(a);
         CryptoByteArrayArithmetic.add(
            this._value, 0, this._value.length, n, 0, 4, modulus._value, 0, modulus._value.length, this._value, 0, this._value.length
         );
      } else {
         a = -a;
         byte[] n = this.intToByteArray(a);
         CryptoByteArrayArithmetic.subtract(
            this._value, 0, this._value.length, n, 0, 4, modulus._value, 0, modulus._value.length, this._value, 0, this._value.length
         );
      }
   }

   public void decrement(CryptoInteger a, CryptoInteger modulus) {
      if (a == null) {
         throw new Object();
      }

      this._flags = 0;
      this.resizeAndShift(modulus);
      CryptoByteArrayArithmetic.subtract(
         this._value, 0, this._value.length, a._value, 0, a._value.length, modulus._value, 0, modulus._value.length, this._value, 0, this._value.length
      );
   }

   public void decrement(int a, CryptoInteger modulus) {
      this._flags = 0;
      this.resizeAndShift(modulus);
      if (a >= 0) {
         byte[] n = this.intToByteArray(a);
         CryptoByteArrayArithmetic.subtract(
            this._value, 0, this._value.length, n, 0, 4, modulus._value, 0, modulus._value.length, this._value, 0, this._value.length
         );
      } else {
         a = -a;
         byte[] n = this.intToByteArray(a);
         CryptoByteArrayArithmetic.add(
            this._value, 0, this._value.length, n, 0, 4, modulus._value, 0, modulus._value.length, this._value, 0, this._value.length
         );
      }
   }

   public void multiplyByTwo(CryptoInteger modulus) {
      this._flags = 0;
      this.resizeAndShift(modulus);
      CryptoByteArrayArithmetic.multiplyByTwo(this._value, 0, this._value.length, modulus._value, 0, modulus._value.length, this._value, 0, this._value.length);
   }

   public void divideByTwo(CryptoInteger modulus) {
      this._flags = 0;
      this.resizeAndShift(modulus);
      CryptoByteArrayArithmetic.divideByTwo(this._value, 0, this._value.length, modulus._value, 0, modulus._value.length, this._value, 0, this._value.length);
   }

   private CryptoInteger oneParamHelper(CryptoInteger modulus) {
      try {
         this.updateOffset();
         modulus.updateOffset();
         byte[] value = new byte[modulus._value.length - modulus._offset];
         return new CryptoInteger(value);
      } finally {
         throw new Object();
      }
   }

   private CryptoInteger twoParamHelper(CryptoInteger a, CryptoInteger modulus) {
      try {
         a.updateOffset();
         return this.oneParamHelper(modulus);
      } finally {
         throw new Object();
      }
   }

   public CryptoInteger add(CryptoInteger a, CryptoInteger modulus) {
      CryptoInteger result = this.twoParamHelper(a, modulus);
      CryptoByteArrayArithmetic.add(
         this._value,
         this._offset,
         this._value.length - this._offset,
         a._value,
         a._offset,
         a._value.length - a._offset,
         modulus._value,
         modulus._offset,
         modulus._value.length - modulus._offset,
         result._value,
         0,
         result._value.length
      );
      return result;
   }

   public CryptoInteger subtract(CryptoInteger a, CryptoInteger modulus) {
      CryptoInteger result = this.twoParamHelper(a, modulus);
      CryptoByteArrayArithmetic.subtract(
         this._value,
         this._offset,
         this._value.length - this._offset,
         a._value,
         a._offset,
         a._value.length - a._offset,
         modulus._value,
         modulus._offset,
         modulus._value.length - modulus._offset,
         result._value,
         0,
         result._value.length
      );
      return result;
   }

   public CryptoInteger multiply(CryptoInteger a, CryptoInteger modulus) {
      CryptoInteger result = this.twoParamHelper(a, modulus);
      CryptoByteArrayArithmetic.multiply(
         this._value,
         this._offset,
         this._value.length - this._offset,
         a._value,
         a._offset,
         a._value.length - a._offset,
         modulus._value,
         modulus._offset,
         modulus._value.length - modulus._offset,
         result._value,
         0,
         result._value.length
      );
      return result;
   }

   public CryptoInteger square(CryptoInteger modulus) {
      CryptoInteger result = this.oneParamHelper(modulus);
      CryptoByteArrayArithmetic.square(
         this._value,
         this._offset,
         this._value.length - this._offset,
         modulus._value,
         modulus._offset,
         modulus._value.length - modulus._offset,
         result._value,
         0,
         result._value.length
      );
      return result;
   }

   public CryptoInteger invert(CryptoInteger modulus) {
      CryptoInteger result = this.oneParamHelper(modulus);
      CryptoByteArrayArithmetic.invert(
         this._value,
         this._offset,
         this._value.length - this._offset,
         modulus._value,
         modulus._offset,
         modulus._value.length - modulus._offset,
         result._value,
         0,
         result._value.length
      );
      return result;
   }

   public CryptoInteger mod(CryptoInteger modulus) {
      CryptoInteger result = this.oneParamHelper(modulus);
      CryptoByteArrayArithmetic.mod(
         this._value,
         this._offset,
         this._value.length - this._offset,
         modulus._value,
         modulus._offset,
         modulus._value.length - modulus._offset,
         result._value,
         0,
         result._value.length
      );
      return result;
   }

   public CryptoInteger gcd(CryptoInteger a) {
      try {
         this.updateOffset();
         a.updateOffset();
         byte[] value = new byte[Math.max(this._value.length - this._offset, a._value.length - a._offset)];
         CryptoByteArrayArithmetic.gcd(
            this._value, this._offset, this._value.length - this._offset, a._value, a._offset, a._value.length - a._offset, value, 0, value.length
         );
         return new CryptoInteger(value);
      } finally {
         throw new Object();
      }
   }

   public CryptoInteger exponent(CryptoInteger exponent, CryptoInteger modulus) {
      CryptoInteger result = this.twoParamHelper(exponent, modulus);
      CryptoByteArrayArithmetic.exponent(
         this._value,
         this._offset,
         this._value.length - this._offset,
         exponent._value,
         exponent._offset,
         exponent._value.length - exponent._offset,
         modulus._value,
         modulus._offset,
         modulus._value.length - modulus._offset,
         result._value,
         0,
         result._value.length
      );
      return result;
   }

   private void updateOffset() {
      if ((this._flags & 1) == 0) {
         this._flags |= 1;
         int maxOffset = this._value.length - 1;

         for (this._offset = 0; this._offset < maxOffset; this._offset++) {
            if (this._value[this._offset] != 0) {
               return;
            }
         }
      }
   }

   public byte[] toByteArray() {
      this.updateOffset();
      byte[] value = new byte[this._value.length - this._offset];
      System.arraycopy(this._value, this._offset, value, 0, value.length);
      return value;
   }

   public byte[] toByteArray(int length) {
      if (length < 0) {
         throw new Object();
      } else {
         byte[] value = new byte[length];
         int diffLength = length - this._value.length;
         if (diffLength >= 0) {
            System.arraycopy(this._value, 0, value, diffLength, this._value.length);
            return value;
         } else {
            System.arraycopy(this._value, -diffLength, value, 0, value.length);
            return value;
         }
      }
   }

   @Override
   public String toString() {
      this.updateOffset();
      StringBuffer sb = (StringBuffer)(new Object(this._value.length - this._offset << 1));

      for (int i = this._offset; i < this._value.length; i++) {
         sb.append(NumberUtilities.intToHexDigit(this._value[i] >> 4));
         sb.append(NumberUtilities.intToHexDigit(this._value[i]));
      }

      return sb.toString();
   }

   public String toString(int numberOfBytes) {
      if (numberOfBytes < 0) {
         throw new Object();
      }

      StringBuffer sb = (StringBuffer)(new Object(numberOfBytes << 1));

      while (numberOfBytes > this._value.length) {
         sb.append("00");
         numberOfBytes--;
      }

      for (int i = this._value.length - numberOfBytes; i < this._value.length; i++) {
         sb.append(NumberUtilities.intToHexDigit(this._value[i] >> 4));
         sb.append(NumberUtilities.intToHexDigit(this._value[i]));
      }

      return sb.toString();
   }

   @Override
   public int hashCode() {
      this.updateOffset();
      if ((this._flags & 2) == 0) {
         this._flags |= 2;
         this._hashCode = HashCodeCalculator.getCRC32(this._value, this._offset, this._value.length - this._offset);
      }

      return this._hashCode;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (obj instanceof CryptoInteger) {
         this.updateOffset();
         CryptoInteger other = (CryptoInteger)obj;
         other.updateOffset();
         int length = this._value.length - this._offset;
         return length == other._value.length - other._offset && Arrays.equals(this._value, this._offset, other._value, other._offset, length);
      } else {
         return false;
      }
   }
}
