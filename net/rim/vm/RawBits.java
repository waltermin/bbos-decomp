package net.rim.vm;

public final class RawBits {
   private RawBits() {
   }

   public static final native int get(float var0);

   public static final native long get(double var0);

   public static final native float put(int var0);

   public static final native double put(long var0);
}
