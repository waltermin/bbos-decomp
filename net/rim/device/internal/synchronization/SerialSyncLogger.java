package net.rim.device.internal.synchronization;

import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.EventLogger;
import net.rim.device.internal.synchronization.ota.util.ReusableObjectPool;
import net.rim.device.internal.synchronization.ota.util.ReusableStringBuffer;

public final class SerialSyncLogger {
   public static final long SERIALSYNC_EVENTLOGGER_GUID = 4907703648615910489L;
   public static final String SERIALSYNC_EVENTLOGGER_DISPLAYNAME = "net.rim.serialsync";

   private static final void log(Object message, int logLevel) {
      if (message != null) {
         String xMessageString = message.toString();

         try {
            if (DeviceInfo.isSimulator()) {
               System.out.println(xMessageString);
            }

            EventLogger.logEvent(4907703648615910489L, xMessageString.getBytes(), logLevel);
            if (EventLogger.getMinimumLevel() >= 5 && message instanceof Object) {
               Throwable t = (Throwable)message;
               t.printStackTrace();
               return;
            }
         } finally {
            return;
         }
      }
   }

   private static final ReusableStringBuffer checkOutReusableStringBuffer() {
      ReusableObjectPool xReusableObjectPool = ReusableObjectPool.getSingletonInstance(-7834795833597249705L);
      ReusableStringBuffer xReusableStringBuffer = (ReusableStringBuffer)xReusableObjectPool.checkOut();
      if (xReusableStringBuffer == null) {
         xReusableStringBuffer = new ReusableStringBuffer();
      }

      return xReusableStringBuffer;
   }

   private static final void checkInReusableStringBuffer(ReusableStringBuffer aReusableStringBuffer) {
      ReusableObjectPool xReusableObjectPool = ReusableObjectPool.getSingletonInstance(-7834795833597249705L);
      xReusableObjectPool.checkIn(aReusableStringBuffer);
   }

   public static final void logError(String errorMessage) {
      log(errorMessage, 2);
   }
}
