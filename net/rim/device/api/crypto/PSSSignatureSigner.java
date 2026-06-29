package net.rim.device.api.crypto;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.Arrays;

public final class PSSSignatureSigner implements SignatureSigner {
   private RSAPrivateKey _key;
   private int _length;
   private Digest _digest;
   private Digest _digest2;
   private byte[] _salt;

   public final void sign(byte[] signature, int signatureOffset) {
      if (signature != null && signatureOffset >= 0 && signatureOffset <= signature.length - this._length) {
         int digestLength = this._digest.getDigestLength();
         byte[] encodedMessage = new byte[this._length];
         this._digest.getDigest(encodedMessage, 0, false);

         for (int i = 0; i < 8; i++) {
            this._digest2.update(0);
         }

         this._digest2.update(encodedMessage, 0, digestLength);
         int encodedMessageOffset = 0;
         int numZeroes = this._length - digestLength - this._salt.length - 2;

         while (encodedMessageOffset < numZeroes) {
            encodedMessage[encodedMessageOffset++] = 0;
         }

         encodedMessage[encodedMessageOffset++] = 1;
         this._digest2.update(this._salt, 0, this._salt.length);
         System.arraycopy(this._salt, 0, encodedMessage, encodedMessageOffset, this._salt.length);
         encodedMessageOffset += this._salt.length;
         this._digest2.getDigest(encodedMessage, encodedMessageOffset);
         PseudoRandomSource mgf = new PKCS1MGF1PseudoRandomSource(encodedMessage, encodedMessageOffset, digestLength, this._digest2);
         mgf.xorBytes(encodedMessage, 0, encodedMessageOffset);
         this._digest2.reset();
         byte[] n = this._key.getN();
         byte mask = -128;

         do {
            encodedMessage[0] = (byte)(encodedMessage[0] & ~mask);
            mask = (byte)(mask >> 1);
         } while (CryptoByteArrayArithmetic.compare(encodedMessage, n) >= 0 && mask != 0);

         encodedMessageOffset += digestLength;
         encodedMessage[encodedMessageOffset] = -68;
         this._key.getRSACryptoToken().signRSA(this._key.getRSACryptoSystem(), this._key.getCryptoTokenData(), encodedMessage, 0, signature, signatureOffset);
      } else {
         throw new IllegalArgumentException();
      }
   }

   public final int getLength() {
      return this._length;
   }

   @Override
   public final void reset() {
      this._digest.reset();
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
   public final String getDigestAlgorithm() {
      return this._digest.getAlgorithm();
   }

   @Override
   public final String getAlgorithm() {
      return "RSA_PSS/" + this._digest.getAlgorithm();
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public PSSSignatureSigner(RSAPrivateKey key, Digest digest, byte[] salt) {
      if (key != null && digest != null) {
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

         int digestLength = this._digest.getDigestLength();
         int maxSaltLength = this._length - 2 - digestLength;
         if (maxSaltLength < 0) {
            throw new IllegalArgumentException();
         }

         if (salt == null) {
            salt = RandomSource.getBytes(maxSaltLength);
         }

         if (salt.length > maxSaltLength) {
            throw new IllegalArgumentException();
         }

         this._salt = salt;
      } else {
         throw new IllegalArgumentException();
      }
   }

   public PSSSignatureSigner(RSAPrivateKey key, Digest digest) {
      this(key, digest, null);
   }

   public PSSSignatureSigner(RSAPrivateKey key) {
      this(key, new SHA1Digest(), null);
   }

   public static final void selfTest() {
      byte[] signature = new byte[128];

      try {
         RSACryptoSystem cryptoSystemRSA = new RSACryptoSystem(1024);
         RSAPrivateKey privKey = new RSAPrivateKey(
            cryptoSystemRSA,
            SelfTestData_PK1.RSA_E,
            Arrays.copy(SelfTestData_PK1.RSA_D),
            SelfTestData_PK1.RSA_N,
            Arrays.copy(SelfTestData_PK1.RSA_P),
            Arrays.copy(SelfTestData_PK1.RSA_Q),
            Arrays.copy(SelfTestData_PK1.RSA_DMODPM1),
            Arrays.copy(SelfTestData_PK1.RSA_DMODQM1),
            Arrays.copy(SelfTestData_PK1.RSA_QINVMODP)
         );
         PSSSignatureSigner signer = new PSSSignatureSigner(privKey, new SHA1Digest(), SelfTestData_PK2.PSS_SALT);
         signer.update(SelfTestData_PK1.PLAIN_TEXT_PKCS1_RSA);
         signer.sign(signature, 0);
         if (Arrays.equals(signature, 0, SelfTestData_PK2.SIGNATURE_PSS_RSA, 0, signature.length)) {
            return;
         }
      } finally {
         throw new CryptoSelfTestError();
      }

      throw new CryptoSelfTestError();
   }

   static {
      long ID_TEST_SIGNER_PSS = -1590764493645493284L;
      ApplicationRegistry appRegistry = ApplicationRegistry.getApplicationRegistry();
      if (appRegistry.getOrWaitFor(ID_TEST_SIGNER_PSS) == null) {
         selfTest();
         appRegistry.put(ID_TEST_SIGNER_PSS, appRegistry);
      }
   }
}
