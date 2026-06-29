package net.rim.vm;

public final class PersistentInteger {
   private PersistentInteger() {
   }

   public static final native int getId(long var0, int var2);

   public static final native void set(int var0, int var1);

   public static final native int get(int var0);

   public static final native void delete(int var0);
}
