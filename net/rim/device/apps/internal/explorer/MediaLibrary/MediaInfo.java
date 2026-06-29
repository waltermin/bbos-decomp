package net.rim.device.apps.internal.explorer.MediaLibrary;

public interface MediaInfo {
   int getId();

   String getName();

   String[] getKeywords();

   String[] getPrefixedKeywords();

   void setPreloaded(boolean var1);

   String getLocation();
}
