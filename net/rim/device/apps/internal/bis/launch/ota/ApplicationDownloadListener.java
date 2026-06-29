package net.rim.device.apps.internal.bis.launch.ota;

public interface ApplicationDownloadListener {
   void startingDownload();

   void startingInstallation();

   void finishedInstallation(int var1, String var2, boolean var3);
}
