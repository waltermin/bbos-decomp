package net.rim.vm;

public final class ClassInfo {
   private ClassInfo() {
   }

   public static final native String getModuleInfo(Class var0, int var1, boolean var2);

   public static final native int getClassFlags(Class var0);

   public static final native int encodeBytes(byte[] var0, int var1);
}
