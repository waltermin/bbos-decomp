package net.rim.device.api.gps;

import net.rim.device.api.system.Application;
import net.rim.device.internal.system.EventDispatchManager;

public final class LCS {
   public static final int LCS_NOTIFICATION_ONLY;
   public static final int LCS_VERIFICATION_DEFAULT_ALLOWED;
   public static final int LCS_VERIFICATION_DEFAULT_NOT_ALLOWED;

   public static final void addListener(Application app, LCSListener listener) {
      EventDispatchManager dispatchManager = EventDispatchManager.getInstance();
      synchronized (dispatchManager) {
         if (dispatchManager.getDispatcher(23) == null) {
            dispatchManager.setDispatcher(23, new GPSEventDispatcher());
         }
      }

      app.addListener(23, listener);
   }

   public static final void removeListener(Application app, LCSListener listener) {
      app.removeListener(23, listener);
   }

   public static final native void assistDataRequestFailed();

   public static final native boolean getTxRrlp(byte[] var0, int var1);

   public static final native boolean processRrlp(byte[] var0, int var1);

   public static final native boolean verificationResponse(boolean var0);

   public static final native boolean getLCSClientName(byte[] var0);
}
