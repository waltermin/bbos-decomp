package net.rim.device.api.crypto;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.Arrays;

public final class PKCS1FormatterEngine implements BlockFormatterEngine {
   private PublicKeyEncryptorEngine _encryptorEngine;
   private int _inputBlockLength;
   private int _outputBlockLength;
   private byte[] _encodedMessage;
   private static final int MIN_OVERHEAD = 11;
   static final long ID_TEST_FORMATTER_PKCS1 = 7451575862581721272L;

   public PKCS1FormatterEngine(PublicKeyEncryptorEngine encryptorEngine) {
      if (encryptorEngine == null) {
         throw new Object();
      }

      this._encryptorEngine = encryptorEngine;
      this._inputBlockLength = encryptorEngine.getBlockLength() - 11;
      this._outputBlockLength = encryptorEngine.getBlockLength();
      this._encodedMessage = new byte[this._outputBlockLength];
   }

   @Override
   public final String getAlgorithm() {
      return ((StringBuffer)(new Object())).append(this._encryptorEngine.getAlgorithm()).append("/PKCS1").toString();
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
      if (input != null
         && inputOffset >= 0
         && inputLength >= 0
         && input.length - inputLength >= inputOffset
         && output != null
         && outputOffset >= 0
         && output.length - this._outputBlockLength >= outputOffset) {
         if (inputLength > this._inputBlockLength) {
            throw new MessageTooLongException();
         }

         int encodedMessageOffset = 0;
         this._encodedMessage[encodedMessageOffset++] = 0;
         this._encodedMessage[encodedMessageOffset++] = 2;
         int randomDataLength = this._encodedMessage.length - (3 + inputLength);
         RandomSource.getBytes(this._encodedMessage, encodedMessageOffset, randomDataLength);

         for (; --randomDataLength >= 0; encodedMessageOffset++) {
            if (this._encodedMessage[encodedMessageOffset] == 0) {
               this._encodedMessage[encodedMessageOffset] = 1;
            }
         }

         this._encodedMessage[encodedMessageOffset++] = 0;
         System.arraycopy(input, inputOffset, this._encodedMessage, encodedMessageOffset, inputLength);
         this._encryptorEngine.encrypt(this._encodedMessage, 0, output, outputOffset);
         return this._outputBlockLength;
      } else {
         throw new Object();
      }
   }

   @Override
   public final int formatAndEncrypt(byte[] input, int inputOffset, int inputLength, byte[] output, int outputOffset) {
      return this.formatAndEncrypt(input, inputOffset, inputLength, output, outputOffset, false);
   }

   public static final void selfTest() {
      try {
         RSACryptoSystem cryptoSystem = new RSACryptoSystem(1024);
         RSAPublicKey publicKeyRSA = new RSAPublicKey(cryptoSystem, SelfTestData_PK1.RSA_E, SelfTestData_PK1.RSA_N);
         RSAPrivateKey privateKeyRSA = new RSAPrivateKey(
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
         PKCS1FormatterEngine formatter = new PKCS1FormatterEngine(new RSAEncryptorEngine(publicKeyRSA));
         int blockLength = formatter.getOutputBlockLength();
         byte[] target = new byte[blockLength];
         formatter.formatAndEncrypt(SelfTestData_PK1.PLAIN_TEXT_PKCS1_RSA, 0, SelfTestData_PK1.PLAIN_TEXT_PKCS1_RSA.length, target, 0, false);
         PKCS1UnformatterEngine unformatter = new PKCS1UnformatterEngine(new RSADecryptorEngine(privateKeyRSA));
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
      if (appRegistry.getOrWaitFor(ID_TEST_FORMATTER_PKCS1) == null) {
         selfTest();
         appRegistry.put(ID_TEST_FORMATTER_PKCS1, appRegistry);
      }
   }
}
