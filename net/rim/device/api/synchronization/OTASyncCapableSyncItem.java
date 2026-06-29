package net.rim.device.api.synchronization;

import net.rim.device.api.collection.CollectionEventSource;
import net.rim.device.api.collection.util.CollectionListenerManager;
import net.rim.vm.WeakReference;

public class OTASyncCapableSyncItem extends SyncItem implements OTASyncCapable, CollectionEventSource {
   private CollectionListenerManager _collectionListeners = new CollectionListenerManager();

   protected OTASyncCapableSyncItem() {
   }

   public void fireSyncItemUpdated() {
      this._collectionListeners.fireElementUpdated(this, null, this);
   }

   @Override
   public SyncCollectionSchema getSchema() {
      return null;
   }

   @Override
   public void addCollectionListener(Object listener) {
      this._collectionListeners.addCollectionListener(new WeakReference(listener));
   }

   @Override
   public void removeCollectionListener(Object listener) {
      this._collectionListeners.removeCollectionListener(listener);
   }
}
