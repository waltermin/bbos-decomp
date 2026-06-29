package net.rim.device.apps.internal.blackberryemail.folder;

import net.rim.device.api.synchronization.SyncManager;
import net.rim.device.internal.proxy.Proxy;

public final class PackageManager {
   private PackageManager() {
   }

   public static final void registerOnceOnSystemStart() {
      EmailHierarchy.getAnonymousEmailHierarchy();

      for (int i = EmailHierarchy.getEmailHierarchyCount() - 1; i >= 0; i--) {
         EmailHierarchy.getEmailHierarchy(i).register();
      }

      SyncManager syncManager = SyncManager.getInstance();
      syncManager.enableSynchronization(EmailFolderId.getSyncCollection());
      EmailFolderSync folderSync = new EmailFolderSync();
      syncManager.enableSynchronization(folderSync);
      syncManager.addSyncEventListener(folderSync);
      syncManager.enableSynchronization(new GhostMessageSync());
      Proxy.getInstance().addGlobalEventListener(new EmailFolderRegistrar());
      RibbonUpdater.handleDeviceInitialization();
   }
}
