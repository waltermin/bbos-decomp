package net.rim.device.api.crypto;

final class NativeDigest {
   private int _digestLength;
   private Object _engineContext;
   private int _resetFunctionPointer;
   private int _updateFunctionPointer;
   private int _getDigestFunctionPointer;

   private NativeDigest() {
   }

   public static final native int getVersion();

   public static final native NativeDigest initializeMD2();

   public static final native NativeDigest initializeMD4();

   public static final native NativeDigest initializeMD5();

   public static final native NativeDigest initializeRIPEMD128();

   public static final native NativeDigest initializeRIPEMD160();

   public static final native NativeDigest initializeSHA1();

   public static final native NativeDigest initializeSHA224();

   public static final native NativeDigest initializeSHA256();

   public static final native NativeDigest initializeSHA384();

   public static final native NativeDigest initializeSHA512();

   public final native void reset();

   public final void update(int data) {
      this.update((byte)data);
   }

   private final native void update(byte var1);

   public final native void update(byte[] var1, int var2, int var3);

   public final native void getDigest(byte[] var1, int var2, boolean var3);
}
