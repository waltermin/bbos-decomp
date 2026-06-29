package net.rim.wica.runtime.lifecycle;

public final class UninstallType {
   public static final int USER = 0;
   public static final int SERVER = 1;
   public static final int UPGRADE = 2;
   public static final int INTERNAL = 32;

   private UninstallType() {
   }
}
