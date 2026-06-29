package net.rim.device.internal.io.file;

public interface KeyProvider {
   byte[] getDeviceKey();

   byte[] getUserKey(byte[] var1);

   byte[] getUserKey(byte[] var1, byte[] var2);

   void clearPassword();
}
