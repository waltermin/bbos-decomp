package net.rim.device.internal.synchronization.ota.util;

import net.rim.device.api.crypto.AbstractDigest;
import net.rim.device.api.util.Arrays;

public final class Hash {
   private static final byte BYTEHASH;
   private static final byte SHORTHASH;
   private static final byte INTHASH;
   private static final byte LONGHASH;

   public static final short bytesToShort(byte[] data) {
      return (short)core((AbstractDigest)(new Object()), null, data, 0, data == null ? 0 : data.length, (byte)1);
   }

   public static final short bytesToShort(AbstractDigest digest, byte[] digestBuffer, byte[] data) {
      return (short)core(digest, digestBuffer, data, 0, data == null ? 0 : data.length, (byte)1);
   }

   public static final short bytesToShort(AbstractDigest digest, byte[] digestBuffer, byte[] data, int offset, int length) {
      return (short)core(digest, digestBuffer, data, offset, length, (byte)1);
   }

   public static final int bytesToInt(byte[] data) {
      return (int)core((AbstractDigest)(new Object()), null, data, 0, data == null ? 0 : data.length, (byte)3);
   }

   public static final int bytesToInt(AbstractDigest digest, byte[] digestBuffer, byte[] data) {
      return (int)core(digest, digestBuffer, data, 0, data == null ? 0 : data.length, (byte)3);
   }

   public static final int bytesToInt(AbstractDigest digest, byte[] digestBuffer, byte[] data, int offset, int length) {
      return (int)core(digest, digestBuffer, data, offset, length, (byte)3);
   }

   public static final long bytesToLong(byte[] data) {
      return core((AbstractDigest)(new Object()), null, data, 0, data == null ? 0 : data.length, (byte)7);
   }

   public static final long bytesToLong(AbstractDigest digest, byte[] digestBuffer, byte[] data) {
      return core(digest, digestBuffer, data, 0, data == null ? 0 : data.length, (byte)7);
   }

   public static final long bytesToLong(AbstractDigest digest, byte[] digestBuffer, byte[] data, int offset, int length) {
      return core(digest, digestBuffer, data, offset, length, (byte)7);
   }

   private static final long core(AbstractDigest digest, byte[] digestBuffer, byte[] data, int offset, int length, byte index) {
      if (data != null && data.length != 0) {
         if (digestBuffer != null) {
            Arrays.fill(digestBuffer, (byte)0, 0, index);
         }

         digest.update(data, offset, length);
         byte[] result;
         if (digestBuffer != null) {
            digest.getDigest(digestBuffer, 0);
            result = digestBuffer;
         } else {
            result = digest.getDigest();
         }

         digest.reset();
         long ret = 0;

         while (index > -1) {
            ret <<= 8;
            ret |= result[index--] & 0xFF;
         }

         if (ret == 0) {
            ret += 1;
         }

         return ret;
      } else {
         return 0;
      }
   }
}
