package net.rim.device.api.crypto;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.Arrays;

public final class OAEPUnformatterEngine implements BlockUnformatterEngine {
   private int _digestLength;
   private byte[] _digestOfParameters;
   private int _inputBlockLength;
   private int _outputBlockLength;
   private PrivateKeyDecryptorEngine _engine;
   private static final byte[] NO_PARAMETERS = new byte[0];

   public OAEPUnformatterEngine(PrivateKeyDecryptorEngine engine) {
      this(engine, new SHA1Digest(), NO_PARAMETERS);
   }

   public OAEPUnformatterEngine(PrivateKeyDecryptorEngine engine, byte[] parameters) {
      this(engine, new SHA1Digest(), parameters);
   }

   public OAEPUnformatterEngine(PrivateKeyDecryptorEngine engine, Digest digest) {
      this(engine, digest, NO_PARAMETERS);
   }

   public OAEPUnformatterEngine(PrivateKeyDecryptorEngine engine, Digest digest, byte[] parameters) {
      if (engine != null && digest != null && parameters != null) {
         this._digestLength = digest.getDigestLength();
         digest.reset();
         digest.update(parameters);
         this._digestOfParameters = digest.getDigest();
         this._inputBlockLength = engine.getBlockLength();
         this._outputBlockLength = this._inputBlockLength - (this._digestLength + this._digestOfParameters.length + 2);
         if (this._outputBlockLength <= 0) {
            throw new IllegalArgumentException();
         }

         this._engine = engine;
      } else {
         throw new IllegalArgumentException();
      }
   }

   @Override
   public final String getAlgorithm() {
      return this._engine.getAlgorithm() + "/OAEP";
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
   public final int decryptAndUnformat(byte[] input, int inputOffset, byte[] output, int outputOffset, boolean lastBlock) {
      return this.decryptAndUnformat(input, inputOffset, output, outputOffset);
   }

   @Override
   public final int decryptAndUnformat(byte[] input, int inputOffset, byte[] output, int outputOffset) throws DecodeException {
      if (input != null
         && inputOffset >= 0
         && input.length - this._inputBlockLength >= inputOffset
         && output != null
         && outputOffset >= 0
         && output.length - this._outputBlockLength >= outputOffset) {
         byte[] encodedMessage = new byte[this._inputBlockLength];
         this._engine.decrypt(input, inputOffset, encodedMessage, 0);
         int encodedMessageOffset = 0;
         boolean zeroAtStart = encodedMessage[encodedMessageOffset++] == 0;
         int seedOffset = encodedMessageOffset;
         encodedMessageOffset += this._digestLength;
         int dbLength = encodedMessage.length - encodedMessageOffset;
         PseudoRandomSource mgf = new PKCS1MGF1PseudoRandomSource(encodedMessage, encodedMessageOffset, dbLength);
         mgf.xorBytes(encodedMessage, seedOffset, this._digestLength);
         mgf = new PKCS1MGF1PseudoRandomSource(encodedMessage, seedOffset, this._digestLength);
         mgf.xorBytes(encodedMessage, encodedMessageOffset, dbLength);
         if (zeroAtStart && Arrays.equals(encodedMessage, encodedMessageOffset, this._digestOfParameters, 0, this._digestOfParameters.length)) {
            encodedMessageOffset += this._digestOfParameters.length;

            while (encodedMessage[encodedMessageOffset] == 0) {
               encodedMessageOffset++;
            }

            if (encodedMessage[encodedMessageOffset++] != 1) {
               throw new DecodeException();
            }

            int outputLength = encodedMessage.length - encodedMessageOffset;
            System.arraycopy(encodedMessage, encodedMessageOffset, output, outputOffset, outputLength);
            return outputLength;
         } else {
            throw new DecodeException();
         }
      } else {
         throw new IllegalArgumentException();
      }
   }

   static {
      ApplicationRegistry appRegistry = ApplicationRegistry.getApplicationRegistry();
      if (appRegistry.get(OAEPFormatterEngine.ID_TEST_FORMATTER_OAEP) == null) {
      }
   }
}
