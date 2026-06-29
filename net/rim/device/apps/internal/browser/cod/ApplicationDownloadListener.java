package net.rim.device.apps.internal.browser.cod;

import net.rim.device.api.system.CodeModuleGroup;

interface ApplicationDownloadListener {
   int PROGRESS_DOWNLOADING;
   int PROGRESS_INSTALLING;

   void progressUpdate(int var1, int var2);

   void finishedDownload(int var1, String var2, boolean var3);

   boolean performUpgrade();

   boolean performUpgrade(String var1, String var2);

   boolean installUnsigned();

   boolean overwriteModule(String var1, String var2, CodeModuleGroup var3);

   boolean newerModule(String var1, String var2, String var3);

   boolean removeRecordStores();
}
