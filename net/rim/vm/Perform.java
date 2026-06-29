package net.rim.vm;

public final class Perform implements VMConstants {
   private Perform() {
   }

   public static final native boolean start();

   public static final native void end();
}
