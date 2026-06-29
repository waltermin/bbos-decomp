package net.rim.device.internal.diagnostics;

public interface StateGroupManager {
   void addGroup(long var1, int var3, String var4, int var5);

   void removeGroup(long var1);

   void addInstance(long var1, int var3);

   void removeInstance(long var1, int var3);

   long[] getGroups(boolean var1);

   int[] getInstances(long var1);

   long[] getItems(long var1, boolean var3);

   int getOrder(long var1);

   String getBundleName(long var1);

   int getResourceID(long var1);
}
