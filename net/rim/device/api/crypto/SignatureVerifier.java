package net.rim.device.api.crypto;

public interface SignatureVerifier {
   String getAlgorithm();

   void update(int var1);

   void update(byte[] var1);

   void update(byte[] var1, int var2, int var3);

   boolean verify();
}
