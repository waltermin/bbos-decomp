package net.rim.device.api.crypto;

public interface BlockFormatterEngine {
   int formatAndEncrypt(byte[] var1, int var2, int var3, byte[] var4, int var5, boolean var6);

   int formatAndEncrypt(byte[] var1, int var2, int var3, byte[] var4, int var5);

   int getInputBlockLength();

   int getOutputBlockLength();

   String getAlgorithm();
}
