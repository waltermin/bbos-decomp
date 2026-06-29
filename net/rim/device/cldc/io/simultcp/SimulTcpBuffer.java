package net.rim.device.cldc.io.simultcp;

final class SimulTcpBuffer {
   byte[] data;
   int start = 0;
   int length = 0;

   SimulTcpBuffer(byte[] rawData, int offset, int theLength) {
      this.data = rawData;
      this.start = offset;
      this.length = theLength;
   }

   private final int adjust(int param) {
      return (param + this.data.length) % this.data.length;
   }

   public final synchronized void read(byte[] b, int off, int len) {
      if (this.start + len > this.data.length) {
         int copyLength = this.data.length - this.start;
         System.arraycopy(this.data, this.start, b, off, copyLength);
         System.arraycopy(this.data, this.adjust(this.start + copyLength), b, off + copyLength, len - copyLength);
      } else {
         System.arraycopy(this.data, this.start, b, off, len);
      }

      this.start = this.adjust(this.start + len);
      this.length -= len;
   }

   public final synchronized void write(int intData) {
      this.data[this.adjust(this.start + this.length)] = (byte)(intData & 0xFF);
      this.length++;
   }

   public final synchronized void write(byte[] output, int offset, int len) {
      if (len >= 1) {
         if (this.start + this.length + len < this.data.length) {
            System.arraycopy(output, offset, this.data, this.start + this.length, len);
         } else {
            int copyLength = this.data.length - (this.start + this.length);
            System.arraycopy(output, offset, this.data, this.start + this.length, copyLength);
            System.arraycopy(output, offset + copyLength, this.data, 0, len - copyLength);
         }

         this.length += len;
      }
   }

   @Override
   public final String toString() {
      byte[] stringy = new byte[this.length];
      if (this.start + stringy.length - 1 > this.data.length) {
         int copyLength = this.data.length - this.start;
         System.arraycopy(this.data, this.start, stringy, 0, copyLength);
         System.arraycopy(this.data, this.adjust(this.start + copyLength), stringy, copyLength, stringy.length - copyLength);
      } else {
         System.arraycopy(this.data, this.start, stringy, 0, stringy.length);
      }

      return (String)(new Object(stringy));
   }

   public final synchronized int getLength() {
      return this.data == null ? 0 : this.length;
   }

   public final synchronized void fillArray(byte[] theArray, int theLength) {
      this.read(theArray, 0, theLength);
   }

   public final synchronized void clear() {
      this.start = 0;
      this.length = 0;
   }

   public final synchronized void reset(byte[] rawData, int offset, int theLength) {
      this.start = offset;
      this.length = theLength;
      this.data = rawData;
   }
}
