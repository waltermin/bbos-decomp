package net.rim.device.internal.i18n;

public interface UnicodeServiceProvider {
   byte[] getSupportedEncodings();

   byte resolveEncoding(byte[] var1, byte[] var2);

   String getEncoding(byte var1);

   byte getEncoding(String var1);
}
