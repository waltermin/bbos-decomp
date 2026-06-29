package net.rim.tid.im.util.algorithm;

public class ByteArray {
   int offset;
   int count;
   byte[] value;

   public ByteArray(byte[] value) {
      this(value, 0, value.length);
   }

   public ByteArray() {
      this(null, 0, 0);
   }

   public int length() {
      return this.count;
   }

   public ByteArray(byte[] value, int offset, int count) {
      this.value = value;
      this.offset = offset;
      this.count = count;
   }

   public void init(byte[] aValue, int anOffset, int aCount) {
      this.value = aValue;
      this.offset = anOffset;
      this.count = aCount;
   }

   public byte byteAt(int index) {
      return this.value[index + this.offset];
   }

   public ByteArray subArray(int beginIndex, int endIndex) {
      return new ByteArray(this.value, this.offset + beginIndex, endIndex - beginIndex);
   }

   public boolean equals(byte[] toCompare) {
      if (toCompare.length != this.count) {
         return false;
      }

      int len = this.offset + this.count;
      int i = this.offset;

      for (int j = 0; i < len; j++) {
         if (this.value[i] != toCompare[j]) {
            return false;
         }

         i++;
      }

      return true;
   }

   @Override
   public String toString() {
      StringBuffer buf = new StringBuffer();

      for (int i = this.offset; i < this.offset + this.count; i++) {
         buf.append("," + this.value[i]);
      }

      return buf.toString();
   }
}
