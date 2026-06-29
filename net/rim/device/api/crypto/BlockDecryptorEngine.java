package net.rim.device.api.crypto;

public interface BlockDecryptorEngine {
   void decrypt(byte[] var1, int var2, byte[] var3, int var4);

   int getBlockLength();

   String getAlgorithm();
}
