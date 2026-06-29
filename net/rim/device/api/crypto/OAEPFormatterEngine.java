package net.rim.device.api.crypto;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.Arrays;

public final class OAEPFormatterEngine implements BlockFormatterEngine {
   private int _digestLength;
   private byte[] _digestOfParameters;
   private int _inputBlockLength;
   private int _outputBlockLength;
   private PublicKeyEncryptorEngine _engine;
   private static final byte[] NO_PARAMETERS = new byte[0];
   static final long ID_TEST_FORMATTER_OAEP = 8725101638433241759L;

   public OAEPFormatterEngine(PublicKeyEncryptorEngine engine) {
      this(engine, (Digest)(new Object()), NO_PARAMETERS);
   }

   public OAEPFormatterEngine(PublicKeyEncryptorEngine engine, byte[] parameters) {
      this(engine, (Digest)(new Object()), parameters);
   }

   public OAEPFormatterEngine(PublicKeyEncryptorEngine engine, Digest digest) {
      this(engine, digest, NO_PARAMETERS);
   }

   public OAEPFormatterEngine(PublicKeyEncryptorEngine engine, Digest digest, byte[] parameters) {
      if (engine != null && digest != null && parameters != null) {
         this._digestLength = digest.getDigestLength();
         digest.reset();
         digest.update(parameters);
         this._digestOfParameters = digest.getDigest();
         int engineBlockLength = engine.getBlockLength();
         this._inputBlockLength = engineBlockLength - (this._digestLength + this._digestOfParameters.length + 2);
         if (this._inputBlockLength <= 0) {
            throw new Object();
         }

         this._outputBlockLength = engineBlockLength;
         this._engine = engine;
      } else {
         throw new Object();
      }
   }

   @Override
   public final String getAlgorithm() {
      return ((StringBuffer)(new Object())).append(this._engine.getAlgorithm()).append("/OAEP").toString();
   }

   @Override
   public final int getInputBlockLength() {
      return this._inputBlockLength;
   }

   @Override
   public final int getOutputBlockLength() {
      return this._outputBlockLength;
   }

   @Override
   public final int formatAndEncrypt(byte[] input, int inputOffset, int inputLength, byte[] output, int outputOffset, boolean lastBlock) {
      return this.formatAndEncrypt(input, inputOffset, inputLength, output, outputOffset);
   }

   @Override
   public final int formatAndEncrypt(byte[] input, int inputOffset, int inputLength, byte[] output, int outputOffset) {
      if (input != null
         && inputOffset >= 0
         && inputLength >= 0
         && input.length - inputLength >= inputOffset
         && output != null
         && outputOffset >= 0
         && output.length - this._outputBlockLength >= outputOffset) {
         int paddingLength = this._inputBlockLength - inputLength;
         if (paddingLength < 0) {
            throw new Object();
         }

         byte[] encodedMessage = new byte[this._outputBlockLength];
         int encodedMessageOffset = 0;
         encodedMessage[encodedMessageOffset++] = 0;
         int seedOffset = encodedMessageOffset;
         RandomSource.getBytes(encodedMessage, encodedMessageOffset, this._digestLength);
         encodedMessageOffset += this._digestLength;
         int dbOffset = encodedMessageOffset;
         System.arraycopy(this._digestOfParameters, 0, encodedMessage, encodedMessageOffset, this._digestOfParameters.length);
         encodedMessageOffset += this._digestOfParameters.length;

         for (int i = 0; i < paddingLength; i++) {
            encodedMessage[encodedMessageOffset++] = 0;
         }

         encodedMessage[encodedMessageOffset++] = 1;
         System.arraycopy(input, inputOffset, encodedMessage, encodedMessageOffset, inputLength);
         int dbLength = encodedMessage.length - dbOffset;
         PseudoRandomSource mgf = (PseudoRandomSource)(new Object(encodedMessage, seedOffset, this._digestLength));
         mgf.xorBytes(encodedMessage, dbOffset, dbLength);
         mgf = (PseudoRandomSource)(new Object(encodedMessage, dbOffset, dbLength));
         mgf.xorBytes(encodedMessage, seedOffset, this._digestLength);
         this._engine.encrypt(encodedMessage, 0, output, outputOffset);
         return this._outputBlockLength;
      } else {
         throw new Object();
      }
   }

   public static final void selfTest() {
      try {
         RSACryptoSystem cryptoSystem = (RSACryptoSystem)(new Object(1024));
         RSAPublicKey publicKeyRSA = (RSAPublicKey)(new Object(cryptoSystem, SelfTestData_PK1.RSA_E, SelfTestData_PK1.RSA_N));
         RSAPrivateKey privateKeyRSA = (RSAPrivateKey)(new Object(
            cryptoSystem,
            SelfTestData_PK1.RSA_E,
            Arrays.copy(SelfTestData_PK1.RSA_D),
            SelfTestData_PK1.RSA_N,
            Arrays.copy(SelfTestData_PK1.RSA_P),
            Arrays.copy(SelfTestData_PK1.RSA_Q),
            Arrays.copy(SelfTestData_PK1.RSA_DMODPM1),
            Arrays.copy(SelfTestData_PK1.RSA_DMODQM1),
            Arrays.copy(SelfTestData_PK1.RSA_QINVMODP)
         ));
         OAEPFormatterEngine formatter = new OAEPFormatterEngine((PublicKeyEncryptorEngine)(new Object(publicKeyRSA)));
         int blockLength = formatter.getOutputBlockLength();
         byte[] target = new byte[blockLength];
         formatter.formatAndEncrypt(SelfTestData_PK1.PLAIN_TEXT_PKCS1_RSA, 0, SelfTestData_PK1.PLAIN_TEXT_PKCS1_RSA.length, target, 0, false);
         OAEPUnformatterEngine unformatter = new OAEPUnformatterEngine((PrivateKeyDecryptorEngine)(new Object(privateKeyRSA)));
         unformatter.decryptAndUnformat(target, 0, target, 0, false);
         if (Arrays.equals(target, 0, SelfTestData_PK1.PLAIN_TEXT_PKCS1_RSA, 0, SelfTestData_PK1.PLAIN_TEXT_PKCS1_RSA.length)) {
            return;
         }
      } finally {
         throw new Object();
      }

      throw new Object();
   }

   static {
      ApplicationRegistry appRegistry = ApplicationRegistry.getApplicationRegistry();
      if (appRegistry.getOrWaitFor(ID_TEST_FORMATTER_OAEP) == null) {
         selfTest();
         appRegistry.put(ID_TEST_FORMATTER_OAEP, appRegistry);
      }
   }
}
