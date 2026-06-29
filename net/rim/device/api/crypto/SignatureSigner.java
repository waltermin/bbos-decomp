package net.rim.device.api.crypto;

public interface SignatureSigner {
   String getAlgorithm();

   String getDigestAlgorithm();

   void reset();

   void update(int var1);

   void update(byte[] var1);

   void update(byte[] var1, int var2, int var3);
}
