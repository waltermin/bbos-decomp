package net.rim.device.apps.internal.bis;

import net.rim.device.api.system.EventLogger;

public final class BISEventLogger {
   public static final void register() {
      EventLogger.register(-6302558530025407697L, "net_rim_bis_client", 2);
   }

   public static final boolean logEvent(String value, int level) {
      boolean result = false;
      if (value != null) {
         result = EventLogger.logEvent(-6302558530025407697L, value.getBytes(), level);
      }

      return result;
   }
}
