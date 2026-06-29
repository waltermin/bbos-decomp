package net.rim.device.api.crypto.pgp;

import java.io.OutputStream;
import net.rim.device.api.crypto.CryptoTokenException;
import net.rim.device.api.crypto.RandomSource;
import net.rim.device.api.crypto.StreamEncryptor;
import net.rim.device.api.crypto.SymmetricKeyEncryptorEngine;

public final class PGPCFBEncryptor extends StreamEncryptor {
   private byte[] _CFBRegister;
   private byte[] _encryptedCFBRegister;
   private int _encryptedCFBRegisterMaxOffset;
   private int _encryptedCFBRegisterOffset;
   private SymmetricKeyEncryptorEngine _engine;
   private int _feedBackRegisterLength;
   private boolean _firstEncryption;
   private boolean _dontPerformResync;

   public PGPCFBEncryptor(SymmetricKeyEncryptorEngine engine, OutputStream output) {
      this(engine, output, false);
   }

   public PGPCFBEncryptor(SymmetricKeyEncryptorEngine engine, OutputStream output, boolean dontPerformResync) {
      super(output);
      if (engine != null && engine.getBlockLength() >= 1) {
         int blockLength = engine.getBlockLength();
         this._engine = engine;
         this._feedBackRegisterLength = engine.getBlockLength();
         this._CFBRegister = new byte[this._feedBackRegisterLength];
         this._encryptedCFBRegister = new byte[this._feedBackRegisterLength];
         this._encryptedCFBRegister = new byte[blockLength];
         this._encryptedCFBRegisterMaxOffset = blockLength;
         this._encryptedCFBRegisterOffset = this._encryptedCFBRegisterMaxOffset;
         this._firstEncryption = true;
         this._dontPerformResync = dontPerformResync;
      } else {
         throw new IllegalArgumentException();
      }
   }

   @Override
   public final String getAlgorithm() {
      return this._engine.getAlgorithm() + "/PGP/CFB";
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   protected final void encrypt(byte[] plaintext, int plaintextOffset, int plaintextLength, byte[] ciphertext) throws CryptoTokenException {
      if (plaintext != null
         && plaintextOffset >= 0
         && plaintextLength <= plaintext.length
         && plaintext.length - plaintextLength >= plaintextOffset
         && ciphertext != null) {
         if (this._firstEncryption) {
            int randomDataLength = this._feedBackRegisterLength + 2;
            byte[] randomDataCipherText = new byte[randomDataLength];
            byte[] randomData = new byte[randomDataLength];
            RandomSource.getBytes(randomData, 0, this._feedBackRegisterLength);
            randomData[this._feedBackRegisterLength] = randomData[this._feedBackRegisterLength - 2];
            randomData[this._feedBackRegisterLength + 1] = randomData[this._feedBackRegisterLength - 1];
            this._engine.encrypt(this._CFBRegister, 0, this._encryptedCFBRegister, 0);

            for (int i = 0; i < this._feedBackRegisterLength; i++) {
               randomDataCipherText[i] = (byte)(this._encryptedCFBRegister[i] ^ randomData[i]);
            }

            System.arraycopy(randomDataCipherText, 0, this._CFBRegister, 0, this._feedBackRegisterLength);
            this._engine.encrypt(this._CFBRegister, 0, this._encryptedCFBRegister, 0);
            randomDataCipherText[this._feedBackRegisterLength] = (byte)(this._encryptedCFBRegister[0] ^ randomData[this._feedBackRegisterLength]);
            randomDataCipherText[this._feedBackRegisterLength + 1] = (byte)(this._encryptedCFBRegister[1] ^ randomData[this._feedBackRegisterLength + 1]);
            if (!this._dontPerformResync) {
               System.arraycopy(randomDataCipherText, 2, this._CFBRegister, 0, this._feedBackRegisterLength);
            } else {
               this._CFBRegister[0] = randomDataCipherText[this._feedBackRegisterLength];
               this._CFBRegister[1] = randomDataCipherText[this._feedBackRegisterLength + 1];
               this._encryptedCFBRegisterOffset = 2;
            }

            boolean var10 = false /* VF: Semaphore variable */;

            try {
               var10 = true;
               super._out.write(randomDataCipherText, 0, randomDataCipherText.length);
               var10 = false;
            } finally {
               if (var10) {
                  throw new CryptoTokenException();
               }
            }

            this._firstEncryption = false;
         }

         int cipherOffset = 0;

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
            System.arraycopy(ciphertext, cipherOffset - length, this._CFBRegister, this._encryptedCFBRegisterOffset - length, length);
         }
      } else {
         throw new IllegalArgumentException();
      }
   }
}
