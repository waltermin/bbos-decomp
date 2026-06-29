package net.rim.vm;

public class VMPreserve {
   private VMPreserve() {
   }

   public static native int getSavedLastLine();

   public static native boolean getSavedInGC();

   public static native boolean getSavedIsIdle();

   public static native String getSavedLastNative();

   public static native boolean getPreviousStartupFailed();
}
