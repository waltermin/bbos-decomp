package net.rim.device.internal.synchronization;

public interface OTAUpgradeControlCallback {
   boolean stop();

   void updateProgress(int var1);

   void updateDatabaseProgress(String var1, int var2);

   void testReset(int var1);
}
