package net.rim.device.api.crypto.pgp;

import java.io.EOFException;
import java.io.InputStream;
import net.rim.device.api.crypto.CryptoTokenException;
import net.rim.device.api.crypto.StreamDecryptor;
import net.rim.device.api.crypto.SymmetricKeyEncryptorEngine;
import net.rim.device.api.util.Arrays;

public final class PGPCFBDecryptor extends StreamDecryptor {
   private byte[] _CFBRegister;
   private byte[] _encryptedCFBRegister;
   private int _encryptedCFBRegisterMaxOffset;
   private int _encryptedCFBRegisterOffset;
   private SymmetricKeyEncryptorEngine _engine;

   public PGPCFBDecryptor(SymmetricKeyEncryptorEngine engine, InputStream input) {
      this(engine, input, false);
   }

   public PGPCFBDecryptor(SymmetricKeyEncryptorEngine engine, InputStream input, boolean dontPerformResync) throws EOFException, CryptoTokenException {
      super(input);
      if (engine != null && engine.getBlockLength() >= 1) {
         this._engine = engine;
         int blockLength = engine.getBlockLength();
         this._CFBRegister = new byte[blockLength];
         this._encryptedCFBRegister = new byte[blockLength];
         this._encryptedCFBRegisterMaxOffset = blockLength;
         this._encryptedCFBRegisterOffset = this._encryptedCFBRegisterMaxOffset;
         int randomDataLength = blockLength + 2;
         byte[] randomDataPlainText = new byte[randomDataLength];
         byte[] randomDataCipherText = new byte[randomDataLength];
         int randomDataReadLength = super._inputStream.read(randomDataCipherText);
         if (randomDataReadLength < randomDataLength) {
            throw new EOFException();
         }

         this._engine.encrypt(this._CFBRegister, 0, this._encryptedCFBRegister, 0);

         for (int i = 0; i < blockLength; i++) {
            randomDataPlainText[i] = (byte)(this._encryptedCFBRegister[i] ^ randomDataCipherText[i]);
         }

         System.arraycopy(randomDataCipherText, 0, this._CFBRegister, 0, blockLength);
         this._engine.encrypt(this._CFBRegister, 0, this._encryptedCFBRegister, 0);
         randomDataPlainText[blockLength] = (byte)(this._encryptedCFBRegister[0] ^ randomDataCipherText[blockLength]);
         randomDataPlainText[blockLength + 1] = (byte)(this._encryptedCFBRegister[1] ^ randomDataCipherText[blockLength + 1]);
         if (!Arrays.equals(randomDataPlainText, blockLength - 2, randomDataPlainText, blockLength, 2)) {
            throw new CryptoTokenException();
         }

         if (!dontPerformResync) {
            System.arraycopy(randomDataCipherText, 2, this._CFBRegister, 0, blockLength);
         } else {
            this._CFBRegister[0] = randomDataCipherText[blockLength];
            this._CFBRegister[1] = randomDataCipherText[blockLength + 1];
            this._encryptedCFBRegisterOffset = 2;
         }
      } else {
         throw new IllegalArgumentException();
      }
   }

   @Override
   public final String getAlgorithm() {
      return this._engine.getAlgorithm() + "/PGP/CFB";
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
            System.arraycopy(data, dataOffset, this._CFBRegister, this._encryptedCFBRegisterOffset, xorLength);

            for (int i = xorLength; i > 0; i--) {
               data[dataOffset++] ^= this._encryptedCFBRegister[this._encryptedCFBRegisterOffset++];
            }

            dataLength -= xorLength;
         }
      } else {
         throw new IllegalArgumentException();
      }
   }
}
