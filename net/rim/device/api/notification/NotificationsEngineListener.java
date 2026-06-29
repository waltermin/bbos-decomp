package net.rim.device.api.notification;

public interface NotificationsEngineListener extends NotificationsConstants {
   void proceedWithDeferredEvent(long var1, long var3, Object var5, Object var6);

   void deferredEventWasSuperseded(long var1, long var3, Object var5, Object var6);

   void notificationsEngineStateChanged(int var1, long var2, long var4, Object var6, Object var7);
}
