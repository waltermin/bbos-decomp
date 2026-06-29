package net.rim.device.api.crypto;

public interface PseudoRandomSource {
   String getAlgorithm();

   void xorBytes(byte[] var1, int var2, int var3);

   void xorBytes(byte[] var1);

   void xorBytes(byte[] var1, int var2, int var3, byte[] var4, int var5);

   byte[] xorCopy(byte[] var1);

   byte[] xorCopy(byte[] var1, int var2, int var3);

   void getBytes(byte[] var1, int var2, int var3);

   byte[] getBytes(int var1);

   void getBytes(byte[] var1);

   int getAvailable();

   int getMaxAvailable();
}
