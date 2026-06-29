package net.rim.device.api.crypto;

import net.rim.device.api.crypto.oid.OID;
import net.rim.device.api.crypto.oid.OIDs;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.Arrays;

public final class PKCS1SignatureSigner implements SignatureSigner {
   private RSAPrivateKey _key;
   private int _length;
   private OID _digestOid;
   private Digest _digest;
   private boolean _useASN1;
   private static boolean _selfTest;
   private static final long ID_TEST_SIGNER_PKCS1 = 2943045568920207055L;

   public final void sign(byte[] signature, int signatureOffset) {
      if (signature != null && signatureOffset >= 0 && signatureOffset <= signature.length - this._length) {
         byte[] encodedMessage = new byte[this._length];
         byte[] digestData;
         if (this._useASN1) {
            if (!PKCS1v2SignaturesFacade.available()) {
               throw new Object();
            }

            digestData = PKCS1v2SignaturesFacade.sign(this._digestOid, this._digest);
         } else {
            digestData = this._digest.getDigest(false);
         }

         int paddingLength = this._length - digestData.length - 3;
         int encodedMessageOffset = 0;
         encodedMessage[encodedMessageOffset++] = 0;
         encodedMessage[encodedMessageOffset++] = 1;

         for (int i = 0; i < paddingLength; i++) {
            encodedMessage[encodedMessageOffset++] = -1;
         }

         encodedMessage[encodedMessageOffset++] = 0;
         System.arraycopy(digestData, 0, encodedMessage, encodedMessageOffset, digestData.length);
         RSACryptoToken token = this._key.getRSACryptoToken();
         token.signRSA(this._key.getRSACryptoSystem(), this._key.getCryptoTokenData(), encodedMessage, 0, signature, signatureOffset);
         if (_selfTest && token instanceof SoftwareRSACryptoToken) {
            selfTest(this._key, signature, signatureOffset, encodedMessage);
         }
      } else {
         throw new Object();
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
      return ((StringBuffer)(new Object("RSA_PKCS1_V"))).append(this._useASN1 ? "20/" : "15/").append(this._digest.getAlgorithm()).toString();
   }

   public PKCS1SignatureSigner(RSAPrivateKey key, Digest digest, boolean useASN1) {
      if (key == null) {
         throw new Object();
      }

      this._key = key;
      this._length = key.getRSACryptoSystem().getModulusLength();
      if (digest == null) {
         if (useASN1) {
            digest = (Digest)(new Object());
         } else {
            digest = (Digest)(new Object());
         }
      } else if (digest instanceof NullDigest) {
         useASN1 = false;
      }

      this._digest = digest;
      this._useASN1 = useASN1;
      if (this._useASN1) {
         this._digestOid = OIDs.getAssociatedOID(3134008036018563479L, digest.getAlgorithm());
         if (this._digestOid == null || !PKCS1v2SignaturesFacade.available()) {
            throw new Object();
         }
      }
   }

   public static final boolean isVersion20Supported() {
      return PKCS1v2SignaturesFacade.available();
   }

   public PKCS1SignatureSigner(RSAPrivateKey key, Digest digest) {
      this(key, digest, true);
   }

   public PKCS1SignatureSigner(RSAPrivateKey key, boolean useASN1) {
      this(key, null, useASN1);
   }

   public PKCS1SignatureSigner(RSAPrivateKey key) {
      this(key, null, true);
   }

   static final void selfTest() {
      selfTest(null, null, 0, null);
   }

   static final void selfTest(RSAPrivateKey privKey, byte[] signature, int offset, byte[] encodedMessage) {
      _selfTest = false;

      try {
         if (privKey != null && signature != null && encodedMessage != null && privKey.getE() != null && privKey.getN() != null) {
            byte[] messageToVerify = new byte[encodedMessage.length];
            RSAPublicKey pubKey = new RSAPublicKey((RSACryptoSystem)privKey.getCryptoSystem(), privKey.getE(), privKey.getN());
            RSACryptoToken rsaToken = privKey.getRSACryptoToken();
            rsaToken.verifyRSA(pubKey.getRSACryptoSystem(), pubKey.getCryptoTokenData(), signature, offset, messageToVerify, 0);
            if (!Arrays.equals(messageToVerify, 0, encodedMessage, 0, encodedMessage.length)) {
               throw new Object();
            }
         } else {
            RSACryptoSystem cryptoSystemRSA = new RSACryptoSystem(1024);
            privKey = new RSAPrivateKey(
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
            RSAPublicKey pubKey = new RSAPublicKey(cryptoSystemRSA, SelfTestData_PK1.RSA_E, SelfTestData_PK1.RSA_N);
            PKCS1SignatureSigner signer = new PKCS1SignatureSigner(privKey, (Digest)(new Object()), false);
            byte[] messageToVerify = new byte[signer.getLength()];
            signer.update(SelfTestData_PK1.PLAIN_TEXT_PKCS1_RSA);
            signer.sign(messageToVerify, 0);
            PKCS1SignatureVerifier verifier = new PKCS1SignatureVerifier(pubKey, messageToVerify, 0);
            verifier.update(SelfTestData_PK1.PLAIN_TEXT_PKCS1_RSA);
            if (!verifier.verify()) {
               throw new Object();
            }
         }
      } finally {
         _selfTest = true;
         throw new Object();
      }
   }

   static {
      ApplicationRegistry appRegistry = ApplicationRegistry.getApplicationRegistry();
      if (appRegistry.getOrWaitFor(ID_TEST_SIGNER_PKCS1) == null) {
         selfTest();
         appRegistry.put(ID_TEST_SIGNER_PKCS1, appRegistry);
      }
   }
}
