package net.rim.vm;

public final class DirtyBits {
   private DirtyBits() {
   }

   public static final native void setDirty(Object var0);

   public static final native void setClean(Object var0);

   public static final native boolean isDirty(Object var0);

   public static final native void commit();
}
