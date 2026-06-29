package net.rim.device.apps.internal.ribbon.system;

import net.rim.device.api.collection.CollectionEventSource;
import net.rim.device.api.collection.ReadableLongMap;
import net.rim.device.api.collection.util.CollectionListenerManager;
import net.rim.device.api.collection.util.LongHashtableCollection;

public final class AggregatedNetworkProps implements ReadableLongMap, CollectionEventSource {
   private LongHashtableCollection _props = new LongHashtableCollection();
   private CollectionListenerManager _manager = new CollectionListenerManager();

   public final void internalSet(long key, Object o) {
      synchronized (this._props) {
         if (o == null) {
            this._props.remove(key);
         } else {
            this._props.put(key, o);
         }
      }

      this._manager.fireReset(this);
   }

   @Override
   public final void removeCollectionListener(Object listener) {
      this._manager.removeCollectionListener(listener);
   }

   @Override
   public final boolean contains(long key) {
      synchronized (this._props) {
         return this._props.contains(key);
      }
   }

   @Override
   public final Object get(long key) {
      synchronized (this._props) {
         return this._props.get(key);
      }
   }

   @Override
   public final long getKey(Object element) {
      synchronized (this._props) {
         return this._props.getKey(element);
      }
   }

   @Override
   public final int size() {
      synchronized (this._props) {
         return this._props.size();
      }
   }

   @Override
   public final void addCollectionListener(Object listener) {
      this._manager.addCollectionListener(listener);
   }
}
