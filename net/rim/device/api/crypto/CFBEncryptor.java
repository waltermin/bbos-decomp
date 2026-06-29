package net.rim.device.api.crypto;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.Arrays;

public final class CFBEncryptor extends StreamEncryptor {
   private byte[] _CFBRegister;
   private byte[] _encryptedCFBRegister;
   private int _encryptedCFBRegisterMaxOffset;
   private int _encryptedCFBRegisterOffset;
   private SymmetricKeyEncryptorEngine _engine;
   private InitializationVector _iv;

   public CFBEncryptor(SymmetricKeyEncryptorEngine engine, InitializationVector iv, OutputStream output, boolean eightBitCFB) {
      super(output);
      if (engine == null) {
         throw new Object();
      }

      int blockLength = engine.getBlockLength();
      if (iv == null) {
         iv = (InitializationVector)(new Object(blockLength));
      }

      if (iv.getLength() != blockLength) {
         throw new Object();
      }

      this._engine = engine;
      this._iv = iv;
      this._CFBRegister = this._iv.getData();
      this._encryptedCFBRegister = new byte[blockLength];
      this._encryptedCFBRegisterMaxOffset = eightBitCFB ? 1 : blockLength;
      this._encryptedCFBRegisterOffset = this._encryptedCFBRegisterMaxOffset;
   }

   @Override
   public final String getAlgorithm() {
      return ((StringBuffer)(new Object())).append(this._engine.getAlgorithm()).append("/CFB").toString();
   }

   public final InitializationVector getIV() {
      return this._iv;
   }

   @Override
   protected final void encrypt(byte[] plaintext, int plaintextOffset, int plaintextLength, byte[] ciphertext) {
      int cipherOffset = 0;
      if (plaintext != null
         && plaintextOffset >= 0
         && plaintextLength >= 0
         && plaintext.length - plaintextLength >= plaintextOffset
         && ciphertext != null
         && plaintextLength <= ciphertext.length) {
         while (plaintextLength > 0) {
            if (this._encryptedCFBRegisterOffset == this._encryptedCFBRegisterMaxOffset) {
               this._engine.encrypt(this._CFBRegister, 0, this._encryptedCFBRegister, 0);
               this._encryptedCFBRegisterOffset = 0;
            }

            int length = this._encryptedCFBRegisterMaxOffset - this._encryptedCFBRegisterOffset;
            if (plaintextLength < length) {
               length = plaintextLength;
            }

            for (int i = length; i > 0; i--) {
               ciphertext[cipherOffset++] = (byte)(plaintext[plaintextOffset++] ^ this._encryptedCFBRegister[this._encryptedCFBRegisterOffset++]);
            }

            plaintextLength -= length;
            if (this._encryptedCFBRegisterMaxOffset > 1) {
               System.arraycopy(ciphertext, cipherOffset - length, this._CFBRegister, this._encryptedCFBRegisterOffset - length, length);
            } else {
               System.arraycopy(this._CFBRegister, 1, this._CFBRegister, 0, this._CFBRegister.length - 1);
               this._CFBRegister[this._CFBRegister.length - 1] = ciphertext[cipherOffset - 1];
            }
         }
      } else {
         throw new Object();
      }
   }

   public static final void selfTest() {
      byte[] PLAIN_TEXT = new byte[]{65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78};
      byte[] CIPHER_TEXT = new byte[]{70, 64, -100, -125, -95, 126, -117, 42, 0, 9, -44, -54, -23, -49};
      byte[] IV = new byte[]{4, -1, -36, -60, -31, 53, -55, 95};
      int length = PLAIN_TEXT.length;
      ByteArrayOutputStream out = (ByteArrayOutputStream)(new Object());

      try {
         CFBEncryptor encryptor = new CFBEncryptor((SymmetricKeyEncryptorEngine)(new Object()), (InitializationVector)(new Object(IV)), out, true);
         encryptor.write(PLAIN_TEXT, 0, length);
         encryptor.close();
         if (Arrays.equals(out.toByteArray(), 0, CIPHER_TEXT, 0, length)) {
            return;
         }
      } finally {
         throw new Object();
      }

      throw new Object();
   }

   static {
      long ID_TEST_CFBENCRYPTOR = -1618593204045216075L;
      ApplicationRegistry appRegistry = ApplicationRegistry.getApplicationRegistry();
      if (appRegistry.getOrWaitFor(ID_TEST_CFBENCRYPTOR) == null) {
         selfTest();
         appRegistry.put(ID_TEST_CFBENCRYPTOR, appRegistry);
      }
   }
}
