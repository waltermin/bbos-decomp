package net.rim.device.internal.system;

import net.rim.device.api.crypto.RandomSource;
import net.rim.device.api.crypto.SHA1Digest;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.SIMCard;
import net.rim.device.api.system.UnsupportedOperationException;
import net.rim.device.api.util.Arrays;
import net.rim.vm.Array;

public final class DRMServices {
   private static final long LOCK_GUID;
   public static final int DRM_DEVICE_KEY_SIZE;
   public static final int DRM_SUBSCRIBER_KEY_SIZE;
   private static final byte[] DRM_SIM_KEY_SCRAMBLE_SET = new byte[]{52, 4, 101, 57, 84, 103, 72, 41, 116, 88, 51, -109, 18, 50, 101, 56};

   private DRMServices() {
   }

   private static final Object getDRMLock() {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      synchronized (ar) {
         Object lock = ar.get(-1043389362224207904L);
         if (lock == null) {
            lock = new Object();
            ar.put(-1043389362224207904L, lock);
         }

         return lock;
      }
   }

   public static final byte[] getDeviceKey() {
      synchronized (getDRMLock()) {
         byte[] deviceKey = NvStore.readData(17);
         if (deviceKey == null) {
            deviceKey = RandomSource.getBytes(16);
            NvStore.writeData(17, deviceKey);
         }

         return deviceKey;
      }
   }

   public static final byte[] getSubscriberKey() {
      try {
         byte[] simKey = SIMCard.getICCID();
         if (simKey.length < 16) {
            int offset = simKey.length;
            Array.resize(simKey, 16);

            for (int i = simKey.length - 1; i >= offset; i--) {
               simKey[i] = -28;
            }
         }

         for (int i = simKey.length - 1; i >= 0; i--) {
            int index = i % DRM_SIM_KEY_SCRAMBLE_SET.length;
            simKey[i] ^= DRM_SIM_KEY_SCRAMBLE_SET[index];
         }

         return simKey;
      } catch (UnsupportedOperationException var3) {
         return null;
      } catch (Throwable var4) {
         return null;
      }
   }

   private static final byte[] getHash(byte[] data) {
      if (data != null) {
         SHA1Digest digest = new SHA1Digest();
         digest.update(data);
         data = digest.getDigest();
      }

      return data;
   }

   public static final byte[] getDeviceHash() {
      return getHash(getDeviceKey());
   }

   public static final byte[] getSubscriberHash() {
      return getHash(getSubscriberKey());
   }

   public static final boolean checkTrailerBytes(byte[] trailer) {
      byte[] data = getDeviceHash();
      if (Arrays.equals(trailer, data)) {
         return true;
      }

      data = getSubscriberHash();
      return data != null && Arrays.equals(trailer, data);
   }
}
