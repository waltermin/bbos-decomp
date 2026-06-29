package net.rim.device.internal.EScreens;

public interface EScreenRoot {
   int ACCESS_LEVEL_NONE = -1;
   int ACCESS_LEVEL_FULL = 0;
   int ACCESS_LEVEL_TEMPORARY = 1;
   int ACCESS_LEVEL_CRIPPLED = 2;

   void initEScreens(int var1);

   void startupEScreens();
}
