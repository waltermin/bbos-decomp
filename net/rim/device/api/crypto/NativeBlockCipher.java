package net.rim.device.api.crypto;

final class NativeBlockCipher {
   private int _blockLength;
   private Object _engineContext;
   private int _cryptFunctionPointer;

   private NativeBlockCipher() {
   }

   public static final native int getVersion();

   public static final native NativeBlockCipher initializeAES(byte[] var0, int var1, boolean var2);

   public static final native NativeBlockCipher initializeAES(byte[] var0, int var1, boolean var2, boolean var3);

   public static final native NativeBlockCipher initializeCAST128(byte[] var0, boolean var1);

   public static final native NativeBlockCipher initializeDES(byte[] var0, boolean var1);

   public static final native NativeBlockCipher initializeRC2(byte[] var0, int var1, boolean var2);

   public static final native NativeBlockCipher initializeRC5(byte[] var0, int var1, int var2, boolean var3);

   public static final native NativeBlockCipher initializeSkipjack(byte[] var0, boolean var1);

   public final native void crypt(byte[] var1, int var2, byte[] var3, int var4);
}
