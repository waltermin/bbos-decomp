package net.rim.device.internal.EScreens;

public interface EScreenRoot {
   int ACCESS_LEVEL_NONE;
   int ACCESS_LEVEL_FULL;
   int ACCESS_LEVEL_TEMPORARY;
   int ACCESS_LEVEL_CRIPPLED;

   void initEScreens(int var1);

   void startupEScreens();
}
