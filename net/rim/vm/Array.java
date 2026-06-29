package net.rim.vm;

public final class Array {
   private Array() {
   }

   public static final native void resize(Object var0, int var1);

   public static final native void extend(Object var0, int var1);

   public static final native int getSectionSize(Object var0);

   public static final native void setSectionSize(Object var0, int var1);

   public static final native boolean isContiguous(Object var0);

   public static final native byte[] allocContiguousByteArray(int var0);

   public static final native int maxContiguousByteArraySize();

   public static final native void markAsMixed(int[] var0);

   public static final native void markAsMixed(long[] var0);

   public static final native boolean hasFileSupport();

   public static final native Object writeToFile(Object var0);

   public static final native byte[] readByteArrayFromFile(Object var0);

   public static final native char[] readCharArrayFromFile(Object var0);
}
