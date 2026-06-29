package net.rim.tid.im.conv;

import net.rim.device.api.util.CharacterUtilities;
import net.rim.tid.awt.im.InputContext;
import net.rim.tid.itie.ISecureInputMethodBuffer;
import net.rim.tid.util.Utils;
import net.rim.vm.Array;

public class SLCurrentVariant implements ISecureInputMethodBuffer {
   public char[] _variants;
   public int _offset;
   public int _length;
   public boolean _hadCaseControl;

   public SLCurrentVariant() {
      this(0);
   }

   public SLCurrentVariant(int size) {
      this._variants = new char[size];
      this.enableSecureClean();
   }

   public SLCurrentVariant(int size, char[] buffer, int start, int len) {
      this._variants = new char[size];
      System.arraycopy(buffer, start, this._variants, 0, len);
      this._length = len;
      this.enableSecureClean();
   }

   private void enableSecureClean() {
      InputContext.getInstance(false).addSecureBuffer(this);
   }

   public void set(StringBuffer data, int start, int len) {
      this.insureCapacity(len);
      data.getChars(start, start + len, this._variants, 0);
      this._offset = 0;
      this._length = len;
   }

   public void set(String data) {
      this.set(data, 0, data.length());
   }

   public void set(String data, int begin, int end) {
      this._length = end - begin;
      this.insureCapacity(this._length);
      data.getChars(begin, end, this._variants, 0);
      this._offset = 0;
   }

   public void set(char[] data, int len) {
      this.set(data, 0, len);
   }

   public void set(char[] data, int start, int len) {
      this.insureCapacity(len);
      System.arraycopy(data, start, this._variants, 0, len);
      this._offset = 0;
      this._length = len;
   }

   public void set(SLCurrentVariant aVariant) {
      this.set(aVariant._variants, aVariant._offset, aVariant._length);
   }

   public void insureCapacity(int size) {
      if (this._variants.length < size) {
         Array.resize(this._variants, size);
      }
   }

   public boolean equals(String aString, int start, int end) {
      if (this._length != end - start) {
         return false;
      }

      for (int i = 0; i < this._length; i++) {
         if (this._variants[this._offset + i] != aString.charAt(start + i)) {
            return false;
         }
      }

      return true;
   }

   public boolean equals(StringBuffer aString, int start, int end) {
      if (this._length != end - start) {
         return false;
      }

      for (int i = 0; i < this._length; i++) {
         if (this._variants[this._offset + i] != aString.charAt(start + i)) {
            return false;
         }
      }

      return true;
   }

   public boolean equalsIgnoreCase(StringBuffer aString, int start, int end) {
      if (this._length != end - start) {
         return false;
      }

      for (int i = 0; i < this._length; i++) {
         if (Utils.toLowerCase(this._variants[this._offset + i]) != Utils.toLowerCase(aString.charAt(start + i))) {
            return false;
         }
      }

      return true;
   }

   @Override
   public boolean equals(Object aObj) {
      if (!(aObj instanceof Object)) {
         return false;
      }

      String str = (String)aObj;
      return this.equals(str, 0, str.length());
   }

   public boolean equals(SLCurrentVariant toCompare) {
      if (this._length != toCompare._length) {
         return false;
      }

      for (int i = 0; i < this._length; i++) {
         if (this._variants[this._offset + i] != toCompare._variants[toCompare._offset + i]) {
            return false;
         }
      }

      return true;
   }

   public boolean equalsIgnoreCase(SLCurrentVariant toCompare) {
      if (this._length != toCompare._length) {
         return false;
      }

      for (int i = 0; i < this._length; i++) {
         if (Utils.toLowerCase(this._variants[this._offset + i]) != Utils.toLowerCase(toCompare._variants[toCompare._offset + i])) {
            return false;
         }
      }

      return true;
   }

   public boolean startsWith(SLCurrentVariant aVariant) {
      if (this._length < aVariant._length) {
         return false;
      }

      for (int i = 0; i < aVariant._length; i++) {
         if (this._variants[this._offset + i] != aVariant._variants[aVariant._offset + i]) {
            return false;
         }
      }

      return true;
   }

   public boolean startsWithIgnoreCase(SLCurrentVariant aVariant) {
      if (this._length < aVariant._length) {
         return false;
      }

      for (int i = 0; i < aVariant._length; i++) {
         if (Utils.toLowerCase(this._variants[this._offset + i]) != Utils.toLowerCase(aVariant._variants[aVariant._offset + i])) {
            return false;
         }
      }

      return true;
   }

   public void modifyCharCases(SLCurrentVariant matrix) {
      int mOffset = matrix._offset;
      char[] mVariants = matrix._variants;
      int mLen = matrix._length;

      for (int i = 0; i < this._length && i < mLen; i++) {
         if (CharacterUtilities.isLowerCase(mVariants[i + mOffset])) {
            this._variants[i + this._offset] = Utils.toLowerCase(this._variants[i + this._offset]);
         } else {
            this._variants[i + this._offset] = Utils.toUpperCase(this._variants[i + this._offset]);
         }
      }
   }

   public void toLowerCase(int begin, int end) {
      end += this._offset;

      for (int i = begin + this._offset; i < end; i++) {
         this._variants[i] = Utils.toLowerCase(this._variants[i]);
      }
   }

   @Override
   public String toString() {
      return (String)(new Object(this._variants, this._offset, this._length));
   }

   public final boolean matchCase(boolean[] matrix, int len) {
      if (len > this._length) {
         throw new Object();
      }

      for (int i = 0; i < len; i++) {
         if (matrix[i] != CharacterUtilities.isUpperCase(this._variants[this._offset + i])) {
            return false;
         }
      }

      return true;
   }

   @Override
   public int hashCode() {
      return this.hashCode(false);
   }

   public int lcHashCode() {
      return this.hashCode(true);
   }

   private int hashCode(boolean ignoreCase) {
      return Utils.hashCode(this._variants, this._offset, this._offset + this._length, ignoreCase);
   }

   public void copyInto(char[] to, int offset) {
      System.arraycopy(this._variants, this._offset, to, offset, this._length);
   }

   public final char charAt(int index) {
      if (index >= 0 && index < this._length) {
         return this._variants[this._offset + index];
      } else {
         throw new Object(index);
      }
   }

   public final char lastChar() {
      if (this._length == 0) {
         throw new Object(-1);
      } else {
         return this._variants[this._offset + this._length - 1];
      }
   }

   public void reverse() {
      int start = this._offset;

      for (int end = this._offset + this._length - 1; start < end; end--) {
         char tmp = this._variants[end];
         this._variants[end] = this._variants[start];
         this._variants[start] = tmp;
         start++;
      }
   }

   @Override
   public boolean runSecureClean() {
      synchronized (this) {
         if (this._variants != null && this._variants.length != 0) {
            this._variants = new char[Math.max(0, this._length)];
            this._offset = 0;
            this._length = 0;
            return true;
         } else {
            return false;
         }
      }
   }
}
