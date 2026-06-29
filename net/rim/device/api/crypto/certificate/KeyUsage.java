package net.rim.device.api.crypto.certificate;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.LongHashtable;
import net.rim.vm.Memory;

public final class KeyUsage {
   public static final long DIGITAL_SIGNATURE = 1L;
   public static final long NON_REPUDIATION = 2L;
   public static final long KEY_ENCIPHERMENT = 4L;
   public static final long DATA_ENCIPHERMENT = 8L;
   public static final long KEY_AGREEMENT = 16L;
   public static final long KEY_CERT_SIGN = 32L;
   public static final long CRL_SIGN = 64L;
   public static final long ENCIPHER_ONLY = 128L;
   public static final long DECIPHER_ONLY = 256L;
   public static final long SERVER_AUTHENTICATION = 512L;
   public static final long CLIENT_AUTHENTICATION = 1024L;
   public static final long CODE_SIGNING = 2048L;
   public static final long EMAIL_PROTECTION = 4096L;
   public static final long TIME_STAMPING = 8192L;
   public static final long EMS_CERTIFICATE = 16384L;
   public static final int NUM_KEY_USAGES = 15;
   private static final long ENCRYPT_KEY_USAGE = 20892L;
   private static final long SIGN_KEY_USAGE = 11875L;
   private static LongHashtable _hashtable = ApplicationRegistry.getApplicationRegistry().getLongHashtable(-4426029198997145201L);

   private KeyUsage() {
   }

   public static final Long getLong(long usage) {
      Long l = (Long)_hashtable.get(usage);
      if (l == null) {
         throw new Object();
      } else {
         return l;
      }
   }

   public static final boolean isSignKeyUsage(long keyUsage) {
      return (keyUsage & 11875) != 0;
   }

   public static final boolean isEncryptKeyUsage(long keyUsage) {
      return (keyUsage & 20892) != 0;
   }

   static {
      synchronized (_hashtable) {
         if (_hashtable.size() == 0) {
            _hashtable.put(1, new Object(1));
            _hashtable.put(2, new Object(2));
            _hashtable.put(4, new Object(4));
            _hashtable.put(8, new Object(8));
            _hashtable.put(16, new Object(16));
            _hashtable.put(32, new Object(32));
            _hashtable.put(64, new Object(64));
            _hashtable.put(128, new Object(128));
            _hashtable.put(256, new Object(256));
            _hashtable.put(512, new Object(512));
            _hashtable.put(1024, new Object(1024));
            _hashtable.put(2048, new Object(2048));
            _hashtable.put(4096, new Object(4096));
            _hashtable.put(8192, new Object(8192));
            _hashtable.put(16384, new Object(16384));
            Memory.createGroup(_hashtable);
         }
      }
   }
}
