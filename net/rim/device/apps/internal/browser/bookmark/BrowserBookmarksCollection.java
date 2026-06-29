package net.rim.device.apps.internal.browser.bookmark;

import net.rim.device.api.collection.CollectionEventSource;
import net.rim.device.api.collection.util.CollectionListenerManager;
import net.rim.device.api.synchronization.OTASyncCapable;
import net.rim.device.api.synchronization.OTASyncPriorityProvider;
import net.rim.device.api.synchronization.SyncCollectionSchema;
import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.apps.internal.browser.core.BrowserDaemonRegistry;
import net.rim.device.apps.internal.browser.store.BrowserRepositoryCollection;
import net.rim.device.apps.internal.browser.store.FolderEventListener;

public final class BrowserBookmarksCollection
   extends BrowserRepositoryCollection
   implements OTASyncCapable,
   OTASyncPriorityProvider,
   CollectionEventSource,
   FolderEventListener {
   private CollectionListenerManager _collectionListenerManager = new CollectionListenerManager();

   public BrowserBookmarksCollection() {
      super(BrowserRepositoryCollection.BROWSER_BOOKMARKS_DB_NAME, 866);
      BrowserDaemonRegistry.addFolderEventListener(this);
   }

   @Override
   public final synchronized void addCollectionListener(Object listener) {
      this._collectionListenerManager.addCollectionListener(listener);
   }

   @Override
   public final synchronized void removeCollectionListener(Object listener) {
      this._collectionListenerManager.removeCollectionListener(listener);
   }

   @Override
   public final void browserEventOccured(int eventId, Object param) {
      switch (eventId) {
         case 100:
         default:
            this._collectionListenerManager.fireElementAdded(this, param);
            return;
         case 101:
            this._collectionListenerManager.fireElementRemoved(this, param);
            return;
         case 102:
            this._collectionListenerManager.fireElementUpdated(this, null, param);
         case 99:
      }
   }

   @Override
   public final boolean addSyncObject(SyncObject object) {
      if (super.addSyncObject(object)) {
         BrowserDaemonRegistry.broadCastEvent(100, object);
         return true;
      } else {
         return false;
      }
   }

   @Override
   public final boolean removeSyncObject(SyncObject object) {
      if (super.removeSyncObject(object)) {
         BrowserDaemonRegistry.broadCastEvent(101, object);
         return true;
      } else {
         return false;
      }
   }

   @Override
   public final boolean updateSyncObject(SyncObject oldObject, SyncObject newObject) {
      if (super.updateSyncObject(oldObject, newObject)) {
         BrowserDaemonRegistry.broadCastEvent(102, newObject);
         return true;
      } else {
         return false;
      }
   }

   @Override
   public final int getSyncPriority() {
      return 7;
   }

   @Override
   public final SyncCollectionSchema getSchema() {
      return null;
   }
}
