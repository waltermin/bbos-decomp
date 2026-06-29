package net.rim.device.api.crypto;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.Persistable;

final class SoftwareHMACCryptoToken extends HMACCryptoToken implements Persistable {
   private static SoftwareHMACCryptoToken _instance = new SoftwareHMACCryptoToken();
   private static final long ID_TEST_HMAC;

   public static final SoftwareHMACCryptoToken getInstance() {
      return _instance;
   }

   private SoftwareHMACCryptoToken() {
   }

   @Override
   public final CryptoTokenMACKeyData createKey(int length) {
      return new SoftwareHMACCryptoToken$HMACKeyData(length);
   }

   @Override
   public final CryptoTokenMACKeyData injectKey(byte[] data, int offset, int length) {
      return new SoftwareHMACCryptoToken$HMACKeyData(data, offset, length);
   }

   @Override
   public final void deleteKey(CryptoTokenMACKeyData data) {
   }

   @Override
   public final int extractKeyLength(CryptoTokenMACKeyData cryptoTokenData) {
      if (!(cryptoTokenData instanceof SoftwareHMACCryptoToken$HMACKeyData)) {
         throw new IllegalArgumentException();
      } else {
         return ((SoftwareHMACCryptoToken$HMACKeyData)cryptoTokenData).getLength();
      }
   }

   @Override
   public final byte[] extractKeyData(CryptoTokenMACKeyData cryptoTokenData) {
      if (!(cryptoTokenData instanceof SoftwareHMACCryptoToken$HMACKeyData)) {
         throw new IllegalArgumentException();
      } else {
         return ((SoftwareHMACCryptoToken$HMACKeyData)cryptoTokenData).getData();
      }
   }

   @Override
   public final CryptoTokenMACContext initialize(CryptoTokenMACKeyData keyData, Digest digest) {
      if (!(keyData instanceof SoftwareHMACCryptoToken$HMACKeyData)) {
         throw new IllegalArgumentException();
      }

      SoftwareHMACCryptoToken$HMACKeyData hMACKeyData = (SoftwareHMACCryptoToken$HMACKeyData)keyData;
      return new SoftwareHMACCryptoToken$HMACContext(hMACKeyData, digest);
   }

   @Override
   public final void reset(CryptoTokenMACContext context) {
      if (!(context instanceof SoftwareHMACCryptoToken$HMACContext)) {
         throw new IllegalArgumentException();
      }

      ((SoftwareHMACCryptoToken$HMACContext)context).reset();
   }

   @Override
   public final int getMAC(CryptoTokenMACContext context, byte[] buffer, int offset, boolean reset) {
      if (!(context instanceof SoftwareHMACCryptoToken$HMACContext)) {
         throw new IllegalArgumentException();
      } else {
         return ((SoftwareHMACCryptoToken$HMACContext)context).getMAC(buffer, offset, reset);
      }
   }

   public static final void selfTest() {
      byte[] HMAC_KEY_DATA = new byte[]{-86, -86, -86, -86, -86, -86, -86, -86, -86, -86, -86, -86, -86, -86, -86, -86, -86, -86, -86, -86};
      byte[] HMAC_DATA = new byte[]{
         -35,
         -35,
         -35,
         -35,
         -35,
         -35,
         -35,
         -35,
         -35,
         -35,
         -35,
         -35,
         -35,
         -35,
         -35,
         -35,
         -35,
         -35,
         -35,
         -35,
         -35,
         -35,
         -35,
         -35,
         -35,
         -35,
         -35,
         -35,
         -35,
         -35,
         -35,
         -35,
         -35,
         -35,
         -35,
         -35,
         -35,
         -35,
         -35,
         -35,
         -35,
         -35,
         -35,
         -35,
         -35,
         -35,
         -35,
         -35,
         -35,
         -35
      };
      byte[] HMAC_MESSAGE = new byte[]{18, 93, 115, 66, -71, -84, 17, -51, -111, -93, -102, -12, -118, -95, 123, 79, 99, -15, 117, -45};

      try {
         HMACKey key = new HMACKey(HMAC_KEY_DATA);
         HMAC hmac = new HMAC(key, new SHA1Digest());
         hmac.update(HMAC_DATA);
         if (hmac.checkMAC(HMAC_MESSAGE)) {
            return;
         }
      } catch (CryptoTokenException var5) {
      } catch (CryptoUnsupportedOperationException var6) {
      }

      throw new CryptoSelfTestError();
   }

   static {
      ApplicationRegistry appRegistry = ApplicationRegistry.getApplicationRegistry();
      if (appRegistry.getOrWaitFor(3649595655998352079L) == null) {
         selfTest();
         appRegistry.put(3649595655998352079L, appRegistry);
      }
   }
}
