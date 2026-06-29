package net.rim.device.apps.internal.browser.wappush;

import net.rim.device.api.browser.push.PushProcessor;
import net.rim.device.api.collection.ReadableList;
import net.rim.device.api.collection.WritableSet;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.apps.api.messaging.Folder;
import net.rim.device.apps.api.messaging.FolderHierarchies;
import net.rim.device.apps.internal.browser.store.BrowserFolders;

public final class WAPPushExpiryManager implements Runnable {
   private int _currentTimer = -1;
   private long _nextExpiry = Long.MAX_VALUE;
   private static final long APP_REGISTRY_KEY = -7842278682478177781L;
   private static final long MAX_INVOKE_LATER_TIME = 108000000L;

   public static final WAPPushExpiryManager getInstance() {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      WAPPushExpiryManager instance = (WAPPushExpiryManager)ar.getOrWaitFor(-7842278682478177781L);
      if (instance == null) {
         instance = new WAPPushExpiryManager();
         ar.put(-7842278682478177781L, instance);
      }

      return instance;
   }

   private WAPPushExpiryManager() {
      this.run();
   }

   public final synchronized void addExpiry(long thisExpiry) {
      if (thisExpiry < this._nextExpiry) {
         Application app = PushProcessor.getInstance().getDispatchApplication();
         if (this._currentTimer != -1) {
            app.cancelInvokeLater(this._currentTimer);
         }

         this._currentTimer = app.invokeLater(this, Math.min(Math.max(1, thisExpiry - System.currentTimeMillis()), 108000000), false);
         this._nextExpiry = thisExpiry;
      }
   }

   @Override
   public final synchronized void run() {
      long currentTime = System.currentTimeMillis();
      Folder browserFolder = FolderHierarchies.getFolder(BrowserFolders.RIM_BROWSER_MESSAGES_HIERARCHY_ID, BrowserFolders.BROWSER_WAPPUSH_FOLDER_ID);
      if (browserFolder != null) {
         boolean foundOne = true;

         while (foundOne) {
            foundOne = false;
            WritableSet browserItems = (WritableSet)browserFolder.getContainedItems();
            ReadableList browserList = (ReadableList)browserFolder.getContainedItems();
            int count = browserList.size();

            for (int i = 0; i < count; i++) {
               Object anObject = browserList.getAt(i);
               if (anObject instanceof SICModel) {
                  SICModel si = (SICModel)anObject;
                  long expiry = si.getExpiry();
                  if (expiry != 0 && expiry < currentTime) {
                     si.changeStatus(0);
                     browserItems.remove(si);
                     foundOne = true;
                     break;
                  }
               }
            }
         }

         this._nextExpiry = this.getNextExpiry();
         if (this._nextExpiry != Long.MAX_VALUE) {
            this._currentTimer = PushProcessor.getInstance()
               .getDispatchApplication()
               .invokeLater(this, Math.min(Math.max(1, this._nextExpiry - currentTime), 108000000), false);
            return;
         }

         this._currentTimer = -1;
      }
   }

   private final long getNextExpiry() {
      long minTime = Long.MAX_VALUE;
      Folder browserFolder = FolderHierarchies.getFolder(BrowserFolders.RIM_BROWSER_MESSAGES_HIERARCHY_ID, BrowserFolders.BROWSER_WAPPUSH_FOLDER_ID);
      if (browserFolder != null) {
         ReadableList browserList = (ReadableList)browserFolder.getContainedItems();
         int count = browserList.size();

         for (int i = 0; i < count; i++) {
            Object anObject = browserList.getAt(i);
            if (anObject instanceof SICModel) {
               SICModel si = (SICModel)anObject;
               minTime = Math.min(si.getExpiry(), minTime);
            }
         }
      }

      return minTime;
   }
}
