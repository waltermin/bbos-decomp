package net.rim.device.api.crypto;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.Arrays;

public final class PSSSignatureVerifier implements SignatureVerifier {
   private RSAPublicKey _key;
   private int _length;
   private Digest _digest;
   private Digest _digest2;
   private byte[] _signature;
   private static final long ID_TEST_VERIFIER_PSS = 7436046114253033322L;

   public PSSSignatureVerifier(RSAPublicKey key, byte[] signature, int signatureOffset) {
      this(key, new SHA1Digest(), signature, signatureOffset);
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public PSSSignatureVerifier(RSAPublicKey key, Digest digest, byte[] signature, int signatureOffset) throws InvalidSignatureEncodingException {
      if (key != null && digest != null && signature != null && signatureOffset >= 0) {
         this._key = key;
         this._length = key.getRSACryptoSystem().getModulusLength();
         this._digest = digest;
         boolean var7 = false /* VF: Semaphore variable */;

         try {
            var7 = true;
            this._digest2 = DigestFactory.getInstance(digest.getAlgorithm());
            var7 = false;
         } finally {
            if (var7) {
               throw new RuntimeException();
            }
         }

         if (signatureOffset >= 0 && signatureOffset <= signature.length - this._length) {
            this._signature = new byte[this._length];
            System.arraycopy(signature, signatureOffset, this._signature, 0, this._length);
         } else {
            throw new InvalidSignatureEncodingException();
         }
      } else {
         throw new IllegalArgumentException();
      }
   }

   @Override
   public final String getAlgorithm() {
      return "RSA_PSS/" + this._digest.getAlgorithm();
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

   @Override
   public final boolean verify() {
      int digestLength = this._digest.getDigestLength();
      byte[] encodedMessage = new byte[this._length];
      this._key.getRSACryptoToken().verifyRSA(this._key.getRSACryptoSystem(), this._key.getCryptoTokenData(), this._signature, 0, encodedMessage, 0);
      int encodedMessageOffset = this._length;
      if (encodedMessage[--encodedMessageOffset] == -68 && (encodedMessage[0] & 128) == 0) {
         encodedMessageOffset -= digestLength;
         PseudoRandomSource mgf = new PKCS1MGF1PseudoRandomSource(encodedMessage, encodedMessageOffset, digestLength, this._digest2);
         mgf.xorBytes(encodedMessage, 0, encodedMessageOffset);
         this._digest2.reset();
         byte mask = -128;
         byte[] n = this._key.getN();

         for (int i = 7; i >= 0 && (n[0] & 1 << i) == 0; i--) {
            mask = (byte)(mask >> 1);
         }

         encodedMessage[0] = (byte)(encodedMessage[0] & ~mask);
         int saltOffset = 0;

         while (encodedMessage[saltOffset] == 0) {
            if (++saltOffset == encodedMessageOffset) {
               return false;
            }
         }

         if (encodedMessage[saltOffset++] != 1) {
            return false;
         }

         byte[] message = this._digest.getDigest(false);

         for (int i = 0; i < 8; i++) {
            this._digest2.update(0);
         }

         this._digest2.update(message);
         this._digest2.update(encodedMessage, saltOffset, encodedMessageOffset - saltOffset);
         return Arrays.equals(encodedMessage, encodedMessageOffset, this._digest2.getDigest(), 0, digestLength);
      } else {
         return false;
      }
   }

   public static final void selfTest() {
      try {
         RSACryptoSystem cryptoSystemRSA = new RSACryptoSystem(1024);
         RSAPublicKey pubKey = new RSAPublicKey(cryptoSystemRSA, SelfTestData_PK1.RSA_E, SelfTestData_PK1.RSA_N);
         PSSSignatureVerifier verifier = new PSSSignatureVerifier(pubKey, SelfTestData_PK2.SIGNATURE_PSS_RSA, 0);
         verifier.update(SelfTestData_PK1.PLAIN_TEXT_PKCS1_RSA);
         if (verifier.verify()) {
            return;
         }
      } finally {
         throw new CryptoSelfTestError();
      }

      throw new CryptoSelfTestError();
   }

   static {
      ApplicationRegistry appRegistry = ApplicationRegistry.getApplicationRegistry();
      if (appRegistry.getOrWaitFor(ID_TEST_VERIFIER_PSS) == null) {
         selfTest();
         appRegistry.put(ID_TEST_VERIFIER_PSS, appRegistry);
      }
   }
}
