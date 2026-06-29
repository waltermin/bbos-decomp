package net.rim.device.internal.firewall;

public interface FirewallInterface {
   boolean allowConnection(String var1, String var2, boolean var3);

   boolean allowConnection(String var1, String var2, int var3);

   boolean allowConnection(String var1, String var2, int var3, FirewallContext var4);

   void reset();

   void reset(int var1);

   boolean isEnabled();

   void setEnabled(boolean var1);

   boolean isBlockingEnabled(byte var1);

   boolean isBlockingEnabledByItPolicy(byte var1);

   boolean setBlocking(byte var1, boolean var2);

   void incrementBlockedCount(byte var1);

   int getBlockedCount(byte var1);

   void resetBlockedCount(byte var1);

   void resetBlockedCounts();

   void addBlockedCountListener(BlockedCountListener var1);

   void removeBlockedCountListener(BlockedCountListener var1);
}
