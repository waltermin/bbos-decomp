package java.lang;

import com.sun.cldc.i18n.Helper;
import net.rim.device.api.util.StringUtilities;
import net.rim.vm.Memory;

public final class String {
   private final native boolean usesBytes();

   private final native void arrayToThisString(Object var1);

   private final native Object getCopyAsArray();

   private static final native Object newArrayWithPossibleDemotion(char[] var0, int var1, int var2);

   public final native int length();

   public final native char charAt(int var1);

   private final native Object getSubArray(int var1, int var2);

   private String(Object array) {
      this.arrayToThisString(array);
   }

   private static final String arrayToString(Object array) {
      return new String(array);
   }

   public String() {
      this.arrayToThisString(new byte[0]);
   }

   public String(String other) {
      this.arrayToThisString(other.getCopyAsArray());
   }

   public String(char[] value) {
      this(value, 0, value.length);
   }

   public String(char[] value, int offset, int length) {
      if (offset < 0) {
         throw new StringIndexOutOfBoundsException(offset);
      }

      if (length < 0) {
         throw new StringIndexOutOfBoundsException(length);
      }

      if (offset > value.length - length) {
         throw new StringIndexOutOfBoundsException(offset + length);
      }

      this.arrayToThisString(newArrayWithPossibleDemotion(value, offset, length));
   }

   public String(byte[] value, int offset, int length, String enc) {
      if (offset < 0) {
         throw new StringIndexOutOfBoundsException(offset);
      }

      if (length < 0) {
         throw new StringIndexOutOfBoundsException(length);
      }

      if (offset > value.length - length) {
         throw new StringIndexOutOfBoundsException(offset + length);
      }

      this.arrayToThisString(Helper.byteToCharArray(value, offset, length, enc));
   }

   public String(byte[] bytes, String enc) {
      this(bytes, 0, bytes.length, enc);
   }

   public String(byte[] bytes, int off, int len) {
      if (off >= 0 && off <= bytes.length && len >= 0 && len <= bytes.length && off + len <= bytes.length) {
         byte[] newValue = new byte[len];
         System.arraycopy(bytes, off, newValue, 0, len);
         this.arrayToThisString(newValue);
      } else {
         throw new StringIndexOutOfBoundsException(len);
      }
   }

   public String(byte[] bytes) {
      this(bytes, 0, bytes.length);
   }

   public String(StringBuffer buffer) {
      synchronized (buffer) {
         Object o = buffer.getValue();
         int length = buffer.length();
         Object newValue;
         if (o instanceof byte[]) {
            newValue = new byte[length];
            System.arraycopy(o, 0, newValue, 0, length);
         } else {
            newValue = newArrayWithPossibleDemotion((char[])o, 0, length);
         }

         this.arrayToThisString(newValue);
      }
   }

   public final native void getChars(int var1, int var2, char[] var3, int var4);

   public final byte[] getBytes(String enc) {
      if (this.usesBytes()) {
         return Helper.charToByteArray(this.toCharArray(), 0, this.length(), enc);
      }

      char[] val = (char[])this.getCopyAsArray();
      return Helper.charToByteArray(val, 0, val.length, enc);
   }

   public final byte[] getBytes() {
      if (this.usesBytes()) {
         return (byte[])this.getCopyAsArray();
      }

      byte[] bytesToReturn = new byte[this.length()];
      this.getBytesChar(bytesToReturn, 0);
      return bytesToReturn;
   }

   private final native void getBytesChar(byte[] var1, int var2);

   @Override
   public final native boolean equals(Object var1);

   public final native int compareTo(String var1);

   public final native boolean regionMatches(boolean var1, int var2, String var3, int var4, int var5);

   public final native boolean startsWith(String var1, int var2);

   public final boolean startsWith(String prefix) {
      return this.startsWith(prefix, 0);
   }

   public final boolean endsWith(String suffix) {
      return this.startsWith(suffix, this.length() - suffix.length());
   }

   @Override
   public final native int hashCode();

   public final int indexOf(int ch) {
      return this.indexOf(ch, 0);
   }

   public final int indexOf(int ch, int fromIndex) {
      return StringUtilities.indexOf(this, ch, fromIndex, Integer.MAX_VALUE);
   }

   public final int lastIndexOf(int ch) {
      return this.lastIndexOf(ch, this.length() - 1);
   }

   public final native int lastIndexOf(int var1, int var2);

   public final int indexOf(String str) {
      return this.indexOf(str, 0);
   }

   public final native int indexOf(String var1, int var2);

   public final String substring(int beginIndex) {
      return this.substring(beginIndex, this.length());
   }

   public final String substring(int beginIndex, int endIndex) {
      if (beginIndex < 0) {
         throw new StringIndexOutOfBoundsException(beginIndex);
      }

      int count = this.length();
      if (endIndex > count) {
         throw new StringIndexOutOfBoundsException(endIndex);
      }

      if (beginIndex > endIndex) {
         throw new StringIndexOutOfBoundsException(endIndex - beginIndex);
      }

      if (beginIndex == 0 && endIndex == count) {
         return this;
      }

      int newCount = endIndex - beginIndex;
      return newCount == 0 ? "" : arrayToString(this.getSubArray(beginIndex, newCount));
   }

   private final native void concatBytes(byte[] var1, String var2);

   public final String concat(String str) {
      int otherLen = str.length();
      if (otherLen == 0) {
         return this;
      } else {
         int len = this.length();
         if (this.usesBytes() && str.usesBytes()) {
            byte[] newValue = new byte[len + otherLen];
            this.concatBytes(newValue, str);
            return arrayToString(newValue);
         } else {
            char[] buf = new char[len + otherLen];
            this.getChars(0, len, buf, 0);
            str.getChars(0, otherLen, buf, len);
            return arrayToString(buf);
         }
      }
   }

   public final String replace(char oldChar, char newChar) {
      if (oldChar == newChar) {
         return this;
      }

      int count = this.length();
      int i = 0;

      while (i < count && this.charAt(i) != oldChar) {
         i++;
      }

      if (i == count) {
         return this;
      }

      Object buf;
      if (this.usesBytes() && newChar < 256) {
         buf = new byte[count];
      } else {
         buf = new char[count];
      }

      this.doReplace(buf, oldChar, newChar);
      return arrayToString(buf);
   }

   private final native void doReplace(Object var1, char var2, char var3);

   public final native String toLowerCase();

   public final native String toUpperCase();

   public final String trim() {
      int count = this.length();
      int len = count;
      int st = 0;

      while (st < len && this.charAt(st) <= ' ') {
         st++;
      }

      while (st < len && this.charAt(len - 1) <= ' ') {
         len--;
      }

      return st <= 0 && len >= count ? this : this.substring(st, len);
   }

   @Override
   public final String toString() {
      return this;
   }

   public final char[] toCharArray() {
      int count = this.length();
      char[] result = new char[count];
      this.getChars(0, count, result, 0);
      return result;
   }

   public static final String valueOf(Object obj) {
      return obj == null ? "null" : obj.toString();
   }

   public static final String valueOf(char[] data) {
      return new String(data);
   }

   public static final String valueOf(char[] data, int offset, int count) {
      return new String(data, offset, count);
   }

   public static final String valueOf(boolean b) {
      return b ? "true" : "false";
   }

   public static final String valueOf(char c) {
      if (c <= 255) {
         byte[] data = new byte[]{(byte)c};
         return arrayToString(data);
      } else {
         char[] data = new char[]{c};
         return arrayToString(data);
      }
   }

   public static final String valueOf(int i) {
      return Integer.toString(i, 10);
   }

   public static final String valueOf(long l) {
      return Long.toString(l, 10);
   }

   final boolean copyInto(Object dst, int dstOffset) {
      if (!(dst instanceof byte[])) {
         return false;
      }

      if (!this.usesBytes()) {
         return false;
      }

      this.getBytesChar((byte[])dst, dstOffset);
      return true;
   }

   public final boolean equalsIgnoreCase(String anotherString) {
      if (anotherString != null) {
         int count = this.length();
         if (count == anotherString.length()) {
            return this.regionMatches(true, 0, anotherString, 0, count);
         }
      }

      return false;
   }

   public static final String valueOf(float f) {
      return Float.toString(f);
   }

   public static final String valueOf(double d) {
      return Double.toString(d);
   }

   public final String intern() {
      return Memory.stringIntern(this);
   }
}
