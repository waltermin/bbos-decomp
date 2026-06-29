package net.rim.device.api.crypto;

public interface BlockUnformatterEngine {
   int decryptAndUnformat(byte[] var1, int var2, byte[] var3, int var4, boolean var5);

   int decryptAndUnformat(byte[] var1, int var2, byte[] var3, int var4);

   int getInputBlockLength();

   int getOutputBlockLength();

   String getAlgorithm();
}
