package net.rim.device.apps.internal.browser.download;

public interface SavingDownloadManagerListener {
   void error(String var1);

   void error(int var1);

   void error(Throwable var1);

   void progressCompleted();
}
