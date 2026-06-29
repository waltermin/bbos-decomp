package net.rim.device.api.crypto;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.Arrays;

public final class X931SignatureVerifier implements SignatureVerifier {
   private RSAPublicKey _key;
   private int _length;
   private Digest _digest;
   private byte[] _signature;
   private static final long ID_TEST_VERIFIER_X931 = -1181815458422461446L;

   public X931SignatureVerifier(RSAPublicKey key, byte[] signature, int signatureOffset) {
      this(key, (Digest)(new Object()), signature, signatureOffset);
   }

   public X931SignatureVerifier(RSAPublicKey key, Digest digest, byte[] signature, int signatureOffset) {
      if (key != null && digest != null && signature != null && signatureOffset >= 0) {
         this._key = key;
         this._length = key.getRSACryptoSystem().getModulusLength();
         this._digest = digest;
         if (signatureOffset > signature.length - this._length) {
            throw new Object();
         }

         this._signature = new byte[this._length];
         System.arraycopy(signature, signatureOffset, this._signature, 0, this._length);
      } else {
         throw new Object();
      }
   }

   @Override
   public final String getAlgorithm() {
      return ((StringBuffer)(new Object("RSA_X931/"))).append(this._digest.getAlgorithm()).toString();
   }

   @Override
   public final void update(int data) {
      this._digest.update(data);
   }

   @Override
   public final void update(byte[] data) {
      this.update(data, 0, data == null ? 0 : data.length);
   }

   @Override
   public final void update(byte[] data, int offset, int length) {
      this._digest.update(data, offset, length);
   }

   private final boolean privateVerifier(byte[] encodedMessage) {
      int encodedMessageOffset = 0;
      if (encodedMessage[encodedMessageOffset++] != 107) {
         return false;
      }

      int paddingLength;
      for (paddingLength = 0; encodedMessage[encodedMessageOffset] == -69 && encodedMessageOffset < this._length; paddingLength++) {
         encodedMessageOffset++;
      }

      if (encodedMessageOffset != this._length && encodedMessage[encodedMessageOffset++] == -70) {
         paddingLength++;
         encodedMessageOffset += this._digest.getDigestLength();
         if (encodedMessageOffset >= this._length) {
            return false;
         }

         if (encodedMessage[encodedMessageOffset++] != X931SignatureSigner.getDigestByte(this._digest)) {
            return false;
         }

         if (encodedMessage[encodedMessageOffset++] != -52) {
            return false;
         }

         byte[] message = this._digest.getDigest(false);
         return Arrays.equals(encodedMessage, paddingLength + 1, message, 0, this._digest.getDigestLength());
      } else {
         return false;
      }
   }

   @Override
   public final boolean verify() {
      byte[] encodedMessage = new byte[this._length];
      this._key.getRSACryptoToken().verifyRSA(this._key.getRSACryptoSystem(), this._key.getCryptoTokenData(), this._signature, 0, encodedMessage, 0);
      if ((encodedMessage[this._length - 1] & 15) == 12) {
         return this.privateVerifier(encodedMessage);
      }

      byte[] N = this._key.getN();
      CryptoByteArrayArithmetic.subtract(N, encodedMessage, N, encodedMessage);
      return (encodedMessage[this._length - 1] & 15) == 12 ? this.privateVerifier(encodedMessage) : false;
   }

   public static final void selfTest() {
      byte[] SIGNATURE = new byte[]{
         49,
         41,
         43,
         27,
         -112,
         -98,
         -102,
         51,
         -27,
         -64,
         -64,
         -71,
         15,
         -121,
         52,
         70,
         -94,
         104,
         -53,
         82,
         -69,
         -29,
         88,
         -91,
         40,
         -36,
         -73,
         99,
         119,
         -34,
         -90,
         25,
         45,
         96,
         -101,
         -24,
         -55,
         46,
         68,
         41,
         -51,
         55,
         51,
         37,
         -34,
         65,
         -116,
         -30,
         -68,
         60,
         -87,
         27,
         -72,
         -20,
         117,
         62,
         27,
         52,
         -73,
         -70,
         36,
         -37,
         -34,
         -121,
         72,
         95,
         -47,
         -29,
         83,
         -104,
         -87,
         -4,
         51,
         -115,
         115,
         -112,
         99,
         74,
         -9,
         -38,
         -62,
         -76,
         14,
         -115,
         2,
         -98,
         -116,
         36,
         75,
         -95,
         18,
         88,
         17,
         123,
         -109,
         -105,
         43,
         -69,
         -60,
         -87,
         10,
         -72,
         25,
         -97,
         -34,
         79,
         -101,
         26,
         -77,
         -38,
         56,
         56,
         119,
         -79,
         -38,
         -80,
         111,
         86,
         -75,
         14,
         106,
         -37,
         46,
         71,
         -123,
         -51,
         65,
         -126
      };

      try {
         RSACryptoSystem cryptoSystem = (RSACryptoSystem)(new Object(1024));
         RSAPublicKey pubKey = (RSAPublicKey)(new Object(cryptoSystem, SelfTestData_PK1.RSA_E, SelfTestData_PK1.RSA_N));
         X931SignatureVerifier verifier = new X931SignatureVerifier(pubKey, SIGNATURE, 0);
         verifier.update(SelfTestData_PK1.PLAIN_TEXT_PKCS1_RSA);
         if (verifier.verify()) {
            return;
         }
      } finally {
         throw new Object();
      }

      throw new Object();
   }

   static {
      ApplicationRegistry appRegistry = ApplicationRegistry.getApplicationRegistry();
      if (appRegistry.getOrWaitFor(-1181815458422461446L) == null) {
         selfTest();
         appRegistry.put(-1181815458422461446L, appRegistry);
      }
   }
}
