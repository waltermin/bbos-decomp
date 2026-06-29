package net.rim.device.apps.internal.browser.store;

import java.util.Enumeration;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.system.PersistentContentListener;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.api.util.LongHashtable;
import net.rim.device.apps.api.framework.model.EncryptableProvider;
import net.rim.device.apps.api.messaging.FolderHierarchies;
import net.rim.device.apps.internal.browser.core.BrowserDaemonRegistry;
import net.rim.device.apps.internal.browser.core.BrowserImpl;
import net.rim.device.apps.internal.browser.stack.RawDataCache;

final class BrowserPersistentContentListener implements PersistentContentListener {
   @Override
   public final void persistentContentStateChanged(int state) {
      if (state == 2) {
         BrowserImpl browserImpl = BrowserDaemonRegistry.getInstance();
         if (browserImpl != null) {
            RawDataCache cache = browserImpl.getRawDataCache();
            cache.clearShortTermCache();
            cache.flushLongTermCache();
            cache.commit();
            if (!browserImpl.isBrowserClosed()) {
               ApplicationManager.getApplicationManager().requestForegroundForConsole();
               browserImpl.closeBrowser(false);
            }
         }
      }
   }

   @Override
   public final void persistentContentModeChanged(int generation) {
      synchronized (FolderHierarchies.getLockObject()) {
         PersistentObject collectionsPersistentObject = RIMPersistentStore.getPersistentObject(BrowserFolders.BROWSER_FAMILY);
         if (collectionsPersistentObject != null) {
            LongHashtable collections = (LongHashtable)collectionsPersistentObject.getContents();
            if (collections != null) {
               Enumeration enumeration = collections.elements();

               while (enumeration.hasMoreElements()) {
                  if (generation != PersistentContent.getModeGeneration()) {
                     return;
                  }

                  Object container = enumeration.nextElement();
                  if (!(container instanceof Object[])) {
                     throw new RuntimeException("CPE2");
                  }

                  Object[] pages = (Object[])container;

                  for (int i = pages.length - 1; i >= 0; i--) {
                     Object page = pages[i];
                     if (!(page instanceof EncryptableProvider)) {
                        throw new RuntimeException("CPE1");
                     }

                     EncryptableProvider encryptable = (EncryptableProvider)page;
                     if (!encryptable.checkCrypt(true, true)) {
                        encryptable.reCrypt(true, true);
                     }
                  }
               }
            }
         }
      }
   }
}
