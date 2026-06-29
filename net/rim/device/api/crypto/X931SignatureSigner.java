package net.rim.device.api.crypto;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.Arrays;

public final class X931SignatureSigner implements SignatureSigner {
   private RSAPrivateKey _key;
   private int _length;
   private Digest _digest;
   private byte _digestByte;
   private static final long ID_TEST_SIGNER_X931 = 9145168413953113866L;

   public final void initialize(RSAPrivateKey key, Digest digest) {
      if (key != null && digest != null) {
         this._key = key;
         this._length = key.getRSACryptoSystem().getModulusLength();
         this._digest = digest;
         this._digestByte = getDigestByte(digest);
         int paddingLength = this._length - this._digest.getDigestLength() - 3;
         if (paddingLength <= 0) {
            throw new IllegalArgumentException();
         }
      } else {
         throw new IllegalArgumentException();
      }
   }

   public final void sign(byte[] signature, int signatureOffset) {
      if (signature != null && signatureOffset >= 0 && signatureOffset <= signature.length - this._length) {
         byte[] digestData = this._digest.getDigest(false);
         byte[] encodedMessage = new byte[this._length];
         int encodedMessageOffset = 0;
         encodedMessage[encodedMessageOffset++] = 107;
         int paddingLength = this._length - digestData.length - 3;

         for (int i = 0; i < paddingLength - 1; i++) {
            encodedMessage[encodedMessageOffset++] = -69;
         }

         encodedMessage[encodedMessageOffset++] = -70;
         System.arraycopy(digestData, 0, encodedMessage, encodedMessageOffset, digestData.length);
         encodedMessageOffset += digestData.length;
         encodedMessage[encodedMessageOffset++] = this._digestByte;
         encodedMessage[encodedMessageOffset++] = -52;
         RSACryptoToken token = this._key.getRSACryptoToken();
         token.signRSA(this._key.getRSACryptoSystem(), this._key.getCryptoTokenData(), encodedMessage, 0, signature, signatureOffset);
         byte[] interInt = encodedMessage;
         byte[] N = this._key.getN();
         CryptoByteArrayArithmetic.subtract(N, 0, N.length, signature, signatureOffset, this._length, N, 0, N.length, interInt, 0, interInt.length);
         int compare = CryptoByteArrayArithmetic.compare(signature, signatureOffset, this._length, interInt, 0, interInt.length);
         if (compare > 0) {
            System.arraycopy(interInt, 0, signature, signatureOffset, this._length);
         }
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
      return "RSA_X931/" + this._digest.getAlgorithm();
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public X931SignatureSigner(RSAPrivateKey key) {
      try {
         this.initialize(key, new SHA1Digest());
      } catch (Throwable var4) {
         throw new RuntimeException(e.toString());
      }
   }

   public X931SignatureSigner(RSAPrivateKey key, Digest digest) {
      this.initialize(key, digest);
   }

   static final byte getDigestByte(Digest digest) throws CryptoUnsupportedOperationException {
      if (digest instanceof SHA1Digest) {
         return 51;
      } else if (digest instanceof SHA256Digest) {
         return 52;
      } else if (digest instanceof SHA384Digest) {
         return 54;
      } else if (digest instanceof SHA512Digest) {
         return 53;
      } else if (digest instanceof RIPEMD160Digest) {
         return 49;
      } else {
         throw new CryptoUnsupportedOperationException();
      }
   }

   static final void selfTest() {
      try {
         RSACryptoSystem cryptoSystem = new RSACryptoSystem(1024);
         RSAPrivateKey privKey = new RSAPrivateKey(
            cryptoSystem,
            SelfTestData_PK1.RSA_E,
            Arrays.copy(SelfTestData_PK1.RSA_D),
            SelfTestData_PK1.RSA_N,
            Arrays.copy(SelfTestData_PK1.RSA_P),
            Arrays.copy(SelfTestData_PK1.RSA_Q),
            Arrays.copy(SelfTestData_PK1.RSA_DMODPM1),
            Arrays.copy(SelfTestData_PK1.RSA_DMODQM1),
            Arrays.copy(SelfTestData_PK1.RSA_QINVMODP)
         );
         RSAPublicKey pubKey = new RSAPublicKey(cryptoSystem, SelfTestData_PK1.RSA_E, SelfTestData_PK1.RSA_N);
         X931SignatureSigner signer = new X931SignatureSigner(privKey);
         byte[] messageToVerify = new byte[signer.getLength()];
         signer.update(SelfTestData_PK1.PLAIN_TEXT_PKCS1_RSA);
         signer.sign(messageToVerify, 0);
         X931SignatureVerifier verifier = new X931SignatureVerifier(pubKey, messageToVerify, 0);
         verifier.update(SelfTestData_PK1.PLAIN_TEXT_PKCS1_RSA);
         if (!verifier.verify()) {
            throw new CryptoSelfTestError();
         }
      } finally {
         throw new CryptoSelfTestError();
      }
   }

   static {
      ApplicationRegistry appRegistry = ApplicationRegistry.getApplicationRegistry();
      if (appRegistry.getOrWaitFor(9145168413953113866L) == null) {
         selfTest();
         appRegistry.put(9145168413953113866L, appRegistry);
      }
   }
}
