package net.rim.device.apps.internal.phone.api;

import net.rim.device.api.system.EventLogger;

public final class PhoneLogger {
   private static final long GUID;

   public static final void init() {
      EventLogger.register(5330340012958674867L, "PhoneApp", 2);
   }

   public static final void log(int eventId) {
      EventLogger.logEvent(5330340012958674867L, eventId);
   }

   public static final void log(String data) {
      if (data != null && data.length() > 0) {
         EventLogger.logEvent(5330340012958674867L, data.getBytes(), 0);
         System.out.println(data);
      }
   }

   public static final void log(StringBuffer buf) {
      if (buf != null && buf.length() > 0) {
         EventLogger.logEvent(5330340012958674867L, buf.toString().getBytes(), 0);
         System.out.println(buf.toString());
      }
   }

   public static final void log(byte[] data) {
      if (data != null && data.length > 0) {
         EventLogger.logEvent(5330340012958674867L, data, 0);
      }
   }
}
