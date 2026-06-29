package net.rim.device.api.crypto;

public interface Digest {
   String getAlgorithm();

   void reset();

   void update(int var1);

   void update(byte[] var1);

   void update(byte[] var1, int var2, int var3);

   int getDigestLength();

   int getBlockLength();

   byte[] getDigest();

   byte[] getDigest(boolean var1);

   int getDigest(byte[] var1, int var2);

   int getDigest(byte[] var1, int var2, boolean var3);
}
