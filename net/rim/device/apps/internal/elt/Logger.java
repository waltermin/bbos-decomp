package net.rim.device.apps.internal.elt;

import net.rim.device.api.system.EventLogger;
import net.rim.device.internal.system.InternalServices;

final class Logger {
   static final void register() {
      EventLogger.register(7659638648801846908L, "net.rim.elt", 2);
   }

   static final void logError(Object caller, String message) {
      message = ((StringBuffer)(new Object())).append(getClazz(caller)).append(message).toString();
      EventLogger.logEvent(7659638648801846908L, message.getBytes(), 2);
   }

   static final void logEvent(Object caller, String message, boolean logAll) {
      if (logAll || !InternalServices.isDeviceSecure()) {
         message = ((StringBuffer)(new Object())).append(getClazz(caller)).append(message).toString();
         EventLogger.logEvent(7659638648801846908L, message.getBytes(), 0);
      }
   }

   private static final String getClazz(Object caller) {
      try {
         String clazz = caller instanceof Object ? caller.toString() : caller.getClass().getName();
         int ix = clazz.lastIndexOf(46);
         if (ix > 0) {
            clazz = clazz.substring(ix + 1, clazz.length());
         }

         return ((StringBuffer)(new Object())).append(clazz).append(": ").toString();
      } finally {
         ;
      }
   }
}
