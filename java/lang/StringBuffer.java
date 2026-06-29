package java.lang;

import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.StringUtilities;
import net.rim.vm.Array;

public final class StringBuffer {
   private Object value;
   private int count;

   public StringBuffer() {
      this(16);
   }

   public StringBuffer(int length) {
      this.value = new byte[length];
   }

   public StringBuffer(String str) {
      this(str.length() + 16);
      this.append(str);
   }

   public final int length() {
      return this.count;
   }

   public final int capacity() {
      return !(this.value instanceof byte[]) ? ((char[])this.value).length : ((byte[])this.value).length;
   }

   public final synchronized void ensureCapacity(int minimumCapacity) {
      if (minimumCapacity > this.capacity()) {
         this.expandCapacity(minimumCapacity);
      }
   }

   private final void expandCapacity(int minimumCapacity) {
      int oldCapacity = this.capacity();
      int newCapacity = (oldCapacity + 1) * 2;
      if (minimumCapacity > newCapacity) {
         newCapacity = minimumCapacity;
      }

      if (newCapacity < 4096) {
         Array.resize(this.value, newCapacity);
      } else {
         int incSize = minimumCapacity - oldCapacity;
         if (incSize <= 0) {
            incSize = 1;
         }

         Array.extend(this.value, incSize);
      }
   }

   public final synchronized void setLength(int newLength) {
      if (newLength < 0) {
         throw new StringIndexOutOfBoundsException(newLength);
      }

      if (newLength > this.capacity()) {
         this.expandCapacity(newLength);
      }

      if (this.count < newLength) {
         if (!(this.value instanceof byte[])) {
            Arrays.fill((char[])this.value, '\u0000', this.count, newLength - this.count);
         } else {
            Arrays.fill((byte[])this.value, (byte)0, this.count, newLength - this.count);
         }
      }

      this.count = newLength;
   }

   public final synchronized char charAt(int index) {
      if (index < 0 || index >= this.count) {
         throw new StringIndexOutOfBoundsException(index);
      } else {
         return !(this.value instanceof byte[]) ? ((char[])this.value)[index] : (char)(((byte[])this.value)[index] & 0xFF);
      }
   }

   public final synchronized void getChars(int srcBegin, int srcEnd, char[] dst, int dstBegin) {
      if (srcBegin < 0 || dstBegin < 0) {
         throw new StringIndexOutOfBoundsException(srcBegin);
      }

      if (srcEnd < 0 || srcEnd > this.count) {
         throw new StringIndexOutOfBoundsException(srcEnd);
      }

      if (srcBegin > srcEnd) {
         throw new StringIndexOutOfBoundsException("srcBegin > srcEnd");
      }

      if (dstBegin + (srcEnd - srcBegin) > dst.length) {
         throw new StringIndexOutOfBoundsException("dst overflow");
      }

      if (!(this.value instanceof byte[])) {
         System.arraycopy(this.value, srcBegin, dst, dstBegin, srcEnd - srcBegin);
      } else {
         byte[] localValue = (byte[])this.value;
         int len = srcEnd - srcBegin;

         for (int i = 0; i < len; i++) {
            dst[dstBegin++] = (char)(localValue[srcBegin++] & 0xFF);
         }
      }
   }

   private final native void promote();

   public final synchronized void setCharAt(int index, char ch) {
      if (index < 0 || index >= this.count) {
         throw new StringIndexOutOfBoundsException(index);
      }

      if (ch > 255) {
         this.promote();
         ((char[])this.value)[index] = ch;
      } else if (!(this.value instanceof byte[])) {
         ((char[])this.value)[index] = ch;
      } else {
         ((byte[])this.value)[index] = (byte)ch;
      }
   }

   public final synchronized StringBuffer append(Object obj) {
      if (!(obj instanceof StringBuffer)) {
         return this.append(String.valueOf(obj));
      }

      StringBuffer sb = (StringBuffer)obj;
      StringUtilities.append(this, sb, 0, sb.length());
      return this;
   }

   public final synchronized StringBuffer append(String str) {
      if (str == null) {
         str = String.valueOf(str);
      }

      this.doAppend(str);
      return this;
   }

   private final native void doAppend(String var1);

   public final StringBuffer append(char[] str) {
      return this.append(str, 0, str.length);
   }

   public final synchronized StringBuffer append(char[] str, int offset, int len) {
      int newcount = this.count + len;
      this.promote();
      if (newcount > this.capacity()) {
         this.expandCapacity(newcount);
      }

      System.arraycopy(str, offset, this.value, this.count, len);
      this.count = newcount;
      return this;
   }

   public final StringBuffer append(boolean b) {
      return this.append(String.valueOf(b));
   }

   public final synchronized StringBuffer append(char c) {
      int newcount = this.count + 1;
      if (newcount > this.capacity()) {
         this.expandCapacity(newcount);
      }

      if (c <= 255) {
         if (!(this.value instanceof byte[])) {
            ((char[])this.value)[this.count++] = c;
            return this;
         } else {
            ((byte[])this.value)[this.count++] = (byte)c;
            return this;
         }
      } else {
         this.promote();
         ((char[])this.value)[this.count++] = c;
         return this;
      }
   }

   public final StringBuffer append(int i) {
      return this.append((long)i);
   }

   public final synchronized StringBuffer append(long l) {
      Object o = this.value;
      if (!(o instanceof byte[])) {
         char[] var5 = (char[])o;
         this.count = this.count + formatNumeric(var5, this.count, 10, l);
         return this;
      } else {
         byte[] bytes = (byte[])o;
         this.count = this.count + formatNumeric(bytes, this.count, 10, l);
         return this;
      }
   }

   public final synchronized StringBuffer delete(int start, int end) {
      if (start < 0) {
         throw new StringIndexOutOfBoundsException(start);
      }

      if (end > this.count) {
         end = this.count;
      }

      if (start > end) {
         throw new StringIndexOutOfBoundsException();
      }

      int len = end - start;
      if (len > 0) {
         System.arraycopy(this.value, start + len, this.value, start, this.count - end);
         this.count -= len;
      }

      return this;
   }

   public final synchronized StringBuffer deleteCharAt(int index) {
      if (index >= 0 && index < this.count) {
         System.arraycopy(this.value, index + 1, this.value, index, this.count - index - 1);
         this.count--;
         return this;
      } else {
         throw new StringIndexOutOfBoundsException();
      }
   }

   public final synchronized StringBuffer insert(int offset, Object obj) {
      return this.insert(offset, String.valueOf(obj));
   }

   public final synchronized StringBuffer insert(int offset, String str) {
      if (offset >= 0 && offset <= this.count) {
         if (str == null) {
            str = String.valueOf(str);
         }

         int len = str.length();
         int newcount = this.count + len;
         if (newcount > this.capacity()) {
            this.expandCapacity(newcount);
         }

         System.arraycopy(this.value, offset, this.value, offset + len, this.count - offset);
         this.count = newcount;
         if (!str.copyInto(this.value, offset)) {
            this.promote();
            str.getChars(0, len, (char[])this.value, offset);
         }

         return this;
      } else {
         throw new StringIndexOutOfBoundsException();
      }
   }

   public final synchronized StringBuffer insert(int offset, char[] str) {
      if (offset >= 0 && offset <= this.count) {
         int len = str.length;
         int newcount = this.count + len;
         if (newcount > this.capacity()) {
            this.expandCapacity(newcount);
         }

         System.arraycopy(this.value, offset, this.value, offset + len, this.count - offset);
         this.count = newcount;
         this.promote();
         System.arraycopy(str, 0, this.value, offset, len);
         return this;
      } else {
         throw new StringIndexOutOfBoundsException();
      }
   }

   public final StringBuffer insert(int offset, boolean b) {
      return this.insert(offset, String.valueOf(b));
   }

   public final synchronized StringBuffer insert(int offset, char c) {
      int newcount = this.count + 1;
      if (newcount > this.capacity()) {
         this.expandCapacity(newcount);
      }

      System.arraycopy(this.value, offset, this.value, offset + 1, this.count - offset);
      this.count = newcount;
      if (c <= 255) {
         if (!(this.value instanceof byte[])) {
            ((char[])this.value)[offset] = c;
            return this;
         } else {
            ((byte[])this.value)[offset] = (byte)c;
            return this;
         }
      } else {
         this.promote();
         ((char[])this.value)[offset] = c;
         return this;
      }
   }

   public final StringBuffer insert(int offset, int i) {
      return this.insert(offset, String.valueOf(i));
   }

   public final StringBuffer insert(int offset, long l) {
      return this.insert(offset, String.valueOf(l));
   }

   public final native StringBuffer reverse();

   @Override
   public final String toString() {
      return this.count == 0 ? "" : new String(this);
   }

   final Object getValue() {
      return this.value;
   }

   static final native int formatNumeric(byte[] var0, int var1, int var2, long var3);

   static final native int formatNumeric(char[] var0, int var1, int var2, long var3);

   public final StringBuffer append(float f) {
      return this.append(String.valueOf(f));
   }

   public final StringBuffer append(double d) {
      return this.append(String.valueOf(d));
   }

   public final StringBuffer insert(int offset, float f) {
      return this.insert(offset, String.valueOf(f));
   }

   public final StringBuffer insert(int offset, double d) {
      return this.insert(offset, String.valueOf(d));
   }
}
