package net.rim.device.internal.crypto;

public final class WTLSSessionKey {
   public static final native boolean writeKey(int var0, int var1, byte[] var2, int var3);

   public static final native byte[] readKey(int var0, int var1);
}
