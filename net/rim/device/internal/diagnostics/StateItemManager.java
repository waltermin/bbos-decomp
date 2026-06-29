package net.rim.device.internal.diagnostics;

public interface StateItemManager {
   int INVALID_DISPLAY_MODE;
   int BASIC_DISPLAY_MODE;
   int ADVANCED_DISPLAY_MODE;

   void addItem(long var1, long var3, int var5, int var6, String var7, int var8, int var9);

   void removeItem(long var1, long var3);

   String getBundleName(long var1, long var3);

   int getResourceID(long var1, long var3);

   int getType(long var1, long var3);

   int getOrder(long var1, long var3);

   int getDisplayMode(long var1);

   int getEnumValue(long var1, long var3, int var5);

   String getStringValue(long var1, long var3, int var5);

   long getLongValue(long var1, long var3, int var5);
}
