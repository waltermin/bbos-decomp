package net.rim.device.apps.internal.browser.wappush;

import java.util.Vector;
import net.rim.device.api.collection.IntRangedActionTarget;
import net.rim.device.api.collection.ReadableList;
import net.rim.device.api.collection.WritableSet;
import net.rim.device.api.i18n.Locale;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.synchronization.SyncCollection;
import net.rim.device.api.synchronization.SyncCollectionStatistics;
import net.rim.device.api.synchronization.SyncCollectionStatisticsManager;
import net.rim.device.api.synchronization.SyncConverter;
import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.apps.api.messaging.Folder;
import net.rim.device.apps.api.messaging.FolderHierarchies;
import net.rim.device.apps.internal.browser.resources.BrowserResources;
import net.rim.device.apps.internal.browser.store.BrowserFolders;

public final class WAPPushRepositoryCollection implements SyncCollection, SyncCollectionStatistics {
   private SyncConverter _converter = new WAPPushRepositoryConverter();
   private Folder _folder = FolderHierarchies.getFolder(BrowserFolders.RIM_BROWSER_MESSAGES_HIERARCHY_ID, BrowserFolders.BROWSER_WAPPUSH_FOLDER_ID);
   private static int SYNC_VERSION = 1;

   @Override
   public final boolean addSyncObject(SyncObject object) {
      if (this._folder != null) {
         WritableSet browserItems = (WritableSet)this._folder.getContainedItems();
         browserItems.add(object);
         return true;
      } else {
         return false;
      }
   }

   @Override
   public final boolean updateSyncObject(SyncObject oldObject, SyncObject newObject) {
      return false;
   }

   @Override
   public final boolean removeSyncObject(SyncObject object) {
      if (this._folder != null) {
         WritableSet collection = (WritableSet)this._folder.getContainedItems();
         collection.remove(object);
         return true;
      } else {
         return false;
      }
   }

   @Override
   public final boolean removeAllSyncObjects() {
      WritableSet collection = (WritableSet)this._folder.getContainedItems();
      ((IntRangedActionTarget)collection).apply(Integer.MIN_VALUE, Integer.MAX_VALUE, -198247372487919817L, null);
      collection.removeAll();
      return true;
   }

   @Override
   public final SyncObject[] getSyncObjects() {
      Vector syncObjects = (Vector)(new Object());
      this.addSubItems(this._folder, syncObjects);
      SyncObject[] objects = new Object[syncObjects.size()];
      syncObjects.copyInto(objects);
      return objects;
   }

   @Override
   public final SyncObject getSyncObject(int uid) {
      synchronized (FolderHierarchies.getLockObject()) {
         ReadableList collection = (ReadableList)this._folder.getContainedItems();
         int numElements = collection.size();

         for (int i = 0; i < numElements; i++) {
            SyncObject element = (SyncObject)collection.getAt(i);
            if (element.getUID() == uid) {
               return element;
            }
         }

         return null;
      }
   }

   private final void addSubItems(Folder folder, Vector vector) {
      synchronized (FolderHierarchies.getLockObject()) {
         ReadableList collection = (ReadableList)folder.getContainedItems();

         for (int i = 0; i < collection.size(); i++) {
            vector.addElement(collection.getAt(i));
         }
      }
   }

   @Override
   public final boolean isSyncObjectDirty(SyncObject object) {
      return false;
   }

   @Override
   public final void setSyncObjectDirty(SyncObject object) {
   }

   @Override
   public final void clearSyncObjectDirty(SyncObject object) {
   }

   @Override
   public final int getSyncObjectCount() {
      return ((ReadableList)this._folder.getContainedItems()).size();
   }

   @Override
   public final int getSyncVersion() {
      return SYNC_VERSION;
   }

   @Override
   public final String getSyncName() {
      return "WAP Push Messages";
   }

   @Override
   public final String getSyncName(Locale locale) {
      ResourceBundle bundle = BrowserResources.getResourceBundle().getBundle(locale);
      return bundle != null ? bundle.getString(868) : null;
   }

   @Override
   public final SyncConverter getSyncConverter() {
      return this._converter;
   }

   @Override
   public final void beginTransaction() {
   }

   @Override
   public final void endTransaction() {
   }

   @Override
   public final synchronized int getSyncCollectionSize() {
      return SyncCollectionStatisticsManager.getSyncCollectionSize(this);
   }
}
