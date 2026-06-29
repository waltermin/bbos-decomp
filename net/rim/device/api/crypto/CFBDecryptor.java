package net.rim.device.api.crypto;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.Arrays;

public final class CFBDecryptor extends StreamDecryptor {
   private byte[] _CFBRegister;
   private byte[] _encryptedCFBRegister;
   private int _encryptedCFBRegisterMaxOffset;
   private int _encryptedCFBRegisterOffset;
   private SymmetricKeyEncryptorEngine _engine;
   private InitializationVector _iv;

   public CFBDecryptor(SymmetricKeyEncryptorEngine engine, InitializationVector iv, InputStream input, boolean eightBitCFB) {
      super(input);
      if (engine == null) {
         throw new IllegalArgumentException();
      }

      int blockLength = engine.getBlockLength();
      if (iv == null) {
         iv = new InitializationVector(blockLength);
      }

      if (iv.getLength() != blockLength) {
         throw new IllegalArgumentException();
      }

      this._engine = engine;
      this._iv = iv;
      this._CFBRegister = iv.getData();
      this._encryptedCFBRegister = new byte[blockLength];
      this._encryptedCFBRegisterMaxOffset = eightBitCFB ? 1 : blockLength;
      this._encryptedCFBRegisterOffset = this._encryptedCFBRegisterMaxOffset;
   }

   @Override
   public final String getAlgorithm() {
      return this._engine.getAlgorithm() + "/CFB";
   }

   public final InitializationVector getIV() {
      return this._iv;
   }

   @Override
   protected final void decrypt(byte[] data, int dataOffset, int dataLength) {
      if (data != null && dataOffset >= 0 && dataLength >= 0 && data.length - dataLength >= dataOffset) {
         while (dataLength > 0) {
            if (this._encryptedCFBRegisterOffset == this._encryptedCFBRegisterMaxOffset) {
               this._engine.encrypt(this._CFBRegister, 0, this._encryptedCFBRegister, 0);
               this._encryptedCFBRegisterOffset = 0;
            }

            int xorLength = Math.min(dataLength, this._encryptedCFBRegisterMaxOffset - this._encryptedCFBRegisterOffset);
            if (this._encryptedCFBRegisterMaxOffset > 1) {
               System.arraycopy(data, dataOffset, this._CFBRegister, this._encryptedCFBRegisterOffset, xorLength);
            } else {
               System.arraycopy(this._CFBRegister, 1, this._CFBRegister, 0, this._CFBRegister.length - 1);
               this._CFBRegister[this._CFBRegister.length - 1] = data[dataOffset];
            }

            for (int i = xorLength; i > 0; i--) {
               data[dataOffset++] ^= this._encryptedCFBRegister[this._encryptedCFBRegisterOffset++];
            }

            dataLength -= xorLength;
         }
      } else {
         throw new IllegalArgumentException();
      }
   }

   public static final void selfTest() {
      byte[] PLAIN_TEXT = new byte[]{65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78};
      byte[] CIPHER_TEXT = new byte[]{70, 64, -100, -125, -95, 126, -117, 42, 0, 9, -44, -54, -23, -49};
      byte[] IV = new byte[]{4, -1, -36, -60, -31, 53, -55, 95};
      int length = PLAIN_TEXT.length;
      byte[] target = new byte[length];
      ByteArrayInputStream out = new ByteArrayInputStream(CIPHER_TEXT);

      try {
         CFBDecryptor decryptor = new CFBDecryptor(new TestEngine(), new InitializationVector(IV), out, true);
         decryptor.read(target, 0, length);
         if (Arrays.equals(target, 0, PLAIN_TEXT, 0, length)) {
            return;
         }
      } finally {
         throw new CryptoSelfTestError();
      }

      throw new CryptoSelfTestError();
   }

   static {
      long ID_TEST_DECRYPTOR_CFB = -5872563563934134499L;
      ApplicationRegistry appRegistry = ApplicationRegistry.getApplicationRegistry();
      if (appRegistry.getOrWaitFor(ID_TEST_DECRYPTOR_CFB) == null) {
         selfTest();
         appRegistry.put(ID_TEST_DECRYPTOR_CFB, appRegistry);
      }
   }
}
