package net.rim.tid.im.util.algorithm;

import net.rim.tid.io.ContinuousByteArray;

public class CharArray {
   ContinuousByteArray value;

   public CharArray() {
   }

   public CharArray(ContinuousByteArray value) {
      this.value = value;
   }

   public char charAt(int index) {
      int ind = index * 2;
      int byte1 = this.value.byteAt(ind) & 255;
      int byte2 = this.value.byteAt(ind + 1) & 255;
      return (char)(byte1 << 8 | byte2);
   }

   public short shortAt(int index) {
      return (short)this.charAt(index);
   }

   public char intAt(int index) {
      int ind = index * 4;
      int byte1 = this.value.byteAt(ind) & 255;
      int byte2 = this.value.byteAt(ind + 1) & 255;
      int byte3 = this.value.byteAt(ind + 2) & 255;
      int byte4 = this.value.byteAt(ind + 3) & 255;
      return (char)(byte1 << 24 | byte2 << 16 | byte3 << 8 | byte4);
   }

   public int length() {
      return this.value == null ? 0 : this.value.length() / 2;
   }

   public int byteLength() {
      return this.value == null ? 0 : this.value.length();
   }
}
