package net.rim.device.api.crypto;

public class AbstractDigest {
   byte[] buffer;

   protected AbstractDigest() {
   }

   public String getAlgorithm() {
      throw null;
   }

   public void reset() {
      throw null;
   }

   public void update(int data) {
      if (this.buffer == null) {
         this.buffer = new byte[1];
      }

      this.buffer[0] = (byte)data;
      this.update(this.buffer, 0, 1);
   }

   public void update(byte[] data) {
      if (data == null) {
         throw new IllegalArgumentException();
      }

      this.update(data, 0, data.length);
   }

   public void update(byte[] _1, int _2, int _3) {
      throw null;
   }

   public int getDigestLength() {
      throw null;
   }

   public int getBlockLength() {
      return 0;
   }

   public byte[] getDigest() {
      return this.getDigest(true);
   }

   public byte[] getDigest(boolean resetDigest) {
      byte[] buffer = new byte[this.getDigestLength()];
      this.getDigest(buffer, 0, resetDigest);
      return buffer;
   }

   public int getDigest(byte[] buffer, int offset) {
      return this.getDigest(buffer, offset, true);
   }

   public int getDigest(byte[] _1, int _2, boolean _3) {
      throw null;
   }
}
