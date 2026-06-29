package net.rim.device.api.crypto;

final class TestEngine implements SymmetricKeyEncryptorEngine, SymmetricKeyDecryptorEngine {
   public static final int BLOCK_LENGTH = 8;

   public TestEngine() {
   }

   @Override
   public final void encrypt(byte[] plainText, int plainTextOffset, byte[] cipherText, int cipherTextOffset) {
      if (plainText != null
         && plainTextOffset >= 0
         && plainText.length >= 8
         && plainText.length - 8 >= plainTextOffset
         && cipherText != null
         && cipherTextOffset >= 0
         && cipherText.length >= 8
         && cipherText.length - 8 >= cipherTextOffset) {
         for (int i = 0; i < 8; i++) {
            cipherText[cipherTextOffset + i] = (byte)(plainText[plainTextOffset + i] + 3);
         }
      } else {
         throw new IllegalArgumentException();
      }
   }

   @Override
   public final void decrypt(byte[] cipherText, int cipherTextOffset, byte[] plainText, int plainTextOffset) {
      if (plainText != null
         && plainTextOffset >= 0
         && plainText.length >= 8
         && plainText.length - 8 >= plainTextOffset
         && cipherText != null
         && cipherTextOffset >= 0
         && cipherText.length >= 8
         && cipherText.length - 8 >= cipherTextOffset) {
         for (int i = 0; i < 8; i++) {
            plainText[plainTextOffset + i] = (byte)(cipherText[cipherTextOffset + i] - 3);
         }
      } else {
         throw new IllegalArgumentException();
      }
   }

   @Override
   public final String getAlgorithm() {
      return "TEST_ENGINE";
   }

   @Override
   public final int getBlockLength() {
      return 8;
   }
}
