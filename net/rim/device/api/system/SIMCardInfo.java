package net.rim.device.api.system;

public final class SIMCardInfo {
   private SIMCardInfo() {
   }

   public static final byte[] getIMSI() {
      return SIMCard.getIMSI();
   }
}
