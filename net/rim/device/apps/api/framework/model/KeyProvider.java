package net.rim.device.apps.api.framework.model;

public interface KeyProvider extends RIMModel {
   long TIME_DATE_KEY;

   int getKeys(Object var1, Object[] var2, int var3, long var4);

   int getKeys(Object var1, int[] var2, int var3, long var4);

   int getKeys(Object var1, long[] var2, int var3, long var4);
}
