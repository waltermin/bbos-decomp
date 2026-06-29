package net.rim.device.api.notification;

public interface NotificationsEngine extends NotificationsConstants {
   long NOTIFICATIONS_ENGINE_GUID = 6720217471165517311L;

   void triggerImmediateEvent(long var1, Object var3, int var4, long var5, Object var7, Object var8);

   void cancelImmediateEvent(long var1, Object var3, int var4, long var5, Object var7, Object var8);

   void negotiateDeferredEvent(long var1, Object var3, int var4, long var5, Object var7, long var8, int var10, Object var11);

   void cancelDeferredEvent(long var1, Object var3, int var4, long var5, Object var7, int var8, Object var9);

   void cancelAllDeferredEvents(long var1, Object var3, int var4, int var5, Object var6);

   long getLastEventDate();

   int getDeferredEventCount(long var1);

   boolean isImmediateEventOccuring(long var1);

   Object[] getDeferredEvents(long var1);

   long[] getDeferredEventIds(long var1);
}
