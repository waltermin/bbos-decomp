package net.rim.device.api.crypto;

public interface MAC {
   String getAlgorithm();

   void reset();

   void update(int var1);

   void update(byte[] var1);

   void update(byte[] var1, int var2, int var3);

   int getLength();

   byte[] getMAC();

   byte[] getMAC(boolean var1);

   int getMAC(byte[] var1, int var2);

   int getMAC(byte[] var1, int var2, boolean var3);

   boolean checkMAC(byte[] var1);

   boolean checkMAC(byte[] var1, boolean var2);

   boolean checkMAC(byte[] var1, int var2);

   boolean checkMAC(byte[] var1, int var2, boolean var3);
}
