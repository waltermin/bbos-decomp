package net.rim.device.api.system;

public final class Peripheral {
   public static final boolean isSupported() {
      return false;
   }

   private Peripheral() {
   }

   public static final boolean powerOn() {
      return false;
   }

   public static final boolean powerOff() {
      return false;
   }
}
