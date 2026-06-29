package net.rim.device.api.crypto;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.vm.TraceBack;

public final class RandomSource {
   private static final long ID_TEST;

   private static final void selfTest() {
      FIPS186PseudoRandomSource.selfTest();
   }

   private RandomSource() {
   }

   public static final native long getLong();

   public static final native int getInt();

   public static final long getLong(long num) {
      if (num <= 0) {
         throw new IllegalArgumentException();
      }

      if ((num & -num) == num) {
         return getLong() & num - 1;
      }

      long bits;
      long val;
      do {
         bits = getLong() >>> 1;
         val = bits % num;
      } while (bits - val + (num - 1) < 0);

      return val;
   }

   public static final int getInt(int num) {
      if (num <= 0) {
         throw new IllegalArgumentException();
      }

      if ((num & -num) == num) {
         return getInt() & num - 1;
      }

      int bits;
      int val;
      do {
         bits = getInt() >>> 1;
         val = bits % num;
      } while (bits - val + (num - 1) < 0);

      return val;
   }

   public static final native void getBytes(byte[] var0, int var1, int var2);

   public static final byte[] getBytes(int length) {
      if (length < 0) {
         throw new IllegalArgumentException();
      }

      byte[] buffer = new byte[length];
      getBytes(buffer, 0, length);
      return buffer;
   }

   public static final void getBytes(byte[] buffer) {
      if (buffer == null) {
         throw new IllegalArgumentException();
      }

      getBytes(buffer, 0, buffer == null ? 0 : buffer.length);
   }

   public static final native void add(Object var0);

   static {
      ApplicationRegistry appRegistry = ApplicationRegistry.getApplicationRegistry();
      Object instance = appRegistry.getOrWaitFor(2906436475455030299L);
      if (instance == null && !TraceBack.getCallingClasses()[1].getName().equals("net.rim.device.api.crypto.Crypto1SelfTestModule")) {
         selfTest();
         appRegistry.put(2906436475455030299L, appRegistry);
      }
   }
}
