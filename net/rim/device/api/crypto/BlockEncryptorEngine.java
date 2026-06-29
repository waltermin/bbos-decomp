package net.rim.device.api.crypto;

public interface BlockEncryptorEngine {
   void encrypt(byte[] var1, int var2, byte[] var3, int var4);

   int getBlockLength();

   String getAlgorithm();
}
