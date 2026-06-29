package net.rim.device.api.crypto;

import net.rim.device.api.util.CRC32;

public final class HashCodeCalculator {
   private static SHA1Digest _digest = new SHA1Digest();

   private HashCodeCalculator() {
   }

   public static final int getCRC32(byte[] data) {
      return data == null ? 0 : CRC32.update(-data.length, data, 0, data.length);
   }

   public static final int getCRC32(byte[] data, int offset, int length) {
      if (data == null) {
         return 0;
      } else if (offset >= 0 && length >= 0 && data.length - length >= offset) {
         return CRC32.update(-length, data, offset, length);
      } else {
         throw new IllegalArgumentException();
      }
   }

   public static final long getDigest64(byte[] data) {
      byte[] hash = getDigest(data);
      return (long)hash[0] << 56
         | (long)hash[1] << 48
         | (long)hash[2] << 40
         | (long)hash[3] << 32
         | (long)hash[4] << 24
         | (long)hash[5] << 16
         | (long)hash[6] << 8
         | hash[7];
   }

   public static final int getDigest32(byte[] data) {
      byte[] hash = getDigest(data);
      return hash[0] << 24 | hash[1] << 16 | hash[2] << 8 | hash[3];
   }

   private static final synchronized byte[] getDigest(byte[] data) {
      if (data == null) {
         return new byte[8];
      }

      _digest.reset();
      _digest.update(data);
      return _digest.getDigest();
   }
}
