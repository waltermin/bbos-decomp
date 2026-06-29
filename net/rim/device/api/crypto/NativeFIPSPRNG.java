package net.rim.device.api.crypto;

final class NativeFIPSPRNG {
   private Object _context;

   private NativeFIPSPRNG() {
   }

   public static final native int getVersion();

   public static final native NativeFIPSPRNG initialize(byte[] var0, int var1, int var2, byte[] var3, int var4, int var5, boolean var6);

   public final native void xorBytes(byte[] var1, int var2, int var3);
}
