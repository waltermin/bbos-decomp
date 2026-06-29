package net.rim.device.api.notification;

public interface Consequence extends NotificationsConstants {
   void startNotification(long var1, long var3, long var5, Object var7, Object var8);

   void stopNotification(long var1, long var3, long var5, Object var7, Object var8);

   Object newConfiguration(long var1, long var3, byte var5, int var6, Object var7);
}
