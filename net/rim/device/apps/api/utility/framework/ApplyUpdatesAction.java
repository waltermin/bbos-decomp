package net.rim.device.apps.api.utility.framework;

import net.rim.device.api.collection.BulkUpdateCollectionListener;
import net.rim.device.api.collection.Collection;
import net.rim.device.api.collection.CollectionListener;
import net.rim.device.api.collection.util.CollectionListenerAction;

class ApplyUpdatesAction implements CollectionListenerAction {
   private Object[] _fromArray;
   private Object[] _toArray;

   ApplyUpdatesAction(Object[] fromArray, Object[] toArray) {
      this._fromArray = fromArray;
      this._toArray = toArray;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public void invoke(Collection collection, CollectionListener listener) {
      BulkUpdateCollectionListener bulkUpdateListener = null;
      if (listener instanceof Object) {
         bulkUpdateListener = (BulkUpdateCollectionListener)listener;
         bulkUpdateListener.beginBulkUpdate(collection);
      }

      boolean var10 = false /* VF: Semaphore variable */;

      try {
         var10 = true;
         int count = this._fromArray.length;

         for (int i = 0; i < count; i++) {
            Object oldElement = this._fromArray[i];
            Object newElement = this._toArray[i];
            if (oldElement != null) {
               if (newElement == null) {
                  listener.elementRemoved(collection, oldElement);
               } else {
                  listener.elementUpdated(collection, oldElement, newElement);
               }
            } else {
               listener.elementAdded(collection, newElement);
            }
         }

         var10 = false;
      } finally {
         if (var10) {
            if (bulkUpdateListener != null) {
               bulkUpdateListener.endBulkUpdate(collection);
            }
         }
      }

      if (bulkUpdateListener != null) {
         bulkUpdateListener.endBulkUpdate(collection);
      }
   }
}
