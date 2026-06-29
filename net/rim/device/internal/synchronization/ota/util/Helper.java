package net.rim.device.internal.synchronization.ota.util;

public final class Helper {
   private Helper() {
   }

   public static final boolean getFlagValue(int flags, int bit) {
      return (flags & bit) == bit;
   }

   public static final int setFlagValue(int flags, boolean value, int bit) {
      return value ? flags | bit : flags & ~bit;
   }
}
