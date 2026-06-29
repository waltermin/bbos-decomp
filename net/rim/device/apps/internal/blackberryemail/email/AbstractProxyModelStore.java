package net.rim.device.apps.internal.blackberryemail.email;

import net.rim.device.api.collection.Collection;
import net.rim.device.api.collection.CollectionEventSource;
import net.rim.device.api.collection.CollectionListener;
import net.rim.device.api.collection.CollectionListenerWithHint;
import net.rim.device.api.collection.ReadableList;
import net.rim.device.api.util.IntEnumeration;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.api.util.IntIntHashtable;
import net.rim.device.api.util.Persistable;

public class AbstractProxyModelStore implements ProxyModelStore, Persistable, CollectionListener, CollectionListenerWithHint {
   protected long _rootId;
   protected IntHashtable _data;
   protected IntIntHashtable _refCounts;
   protected int _index;

   public synchronized int refCount(int objecthandle, int delta) {
      int count = this._refCounts.get(objecthandle);
      if (count < 0) {
         return count;
      } else {
         count += delta;
         if (count == 0) {
            this.delete(objecthandle);
            return count;
         } else {
            this._refCounts.put(objecthandle, count);
            return count;
         }
      }
   }

   protected void collectionEvent(Collection collection, Object element, int delta) {
      if (element instanceof Object) {
         ReadableList l = (ReadableList)element;
         int size = l.size();

         for (int i = 0; i < size; i++) {
            Object o = l.getAt(i);
            if (o instanceof ProxyModel) {
               ProxyModel pm = (ProxyModel)o;
               if (pm.getRootId() == this._rootId) {
                  this.refCount(pm.getObjectHandle(), delta);
               }
            }
         }
      }
   }

   public void removeCollectionEventSource(CollectionEventSource c) {
      c.removeCollectionListener(this);
   }

   public void addCollectionEventSource(CollectionEventSource c) {
      c.addCollectionListener(this);
   }

   @Override
   public synchronized Object delete(int objecthandle) {
      Object o = this._data.remove(objecthandle);
      this._refCounts.remove(objecthandle);
      return o;
   }

   @Override
   public long getId() {
      return this._rootId;
   }

   @Override
   public void elementAdded(Collection collection, Object element) {
      this.collectionEvent(collection, element, 1);
   }

   @Override
   public void elementRemoved(Collection collection, Object element) {
      this.collectionEvent(collection, element, -1);
   }

   @Override
   public Object get(int objecthandle) {
      return this._data.get(objecthandle);
   }

   @Override
   public void elementUpdated(Collection collection, Object oldElement, Object newElement) {
   }

   @Override
   public void reset(Collection collection) {
   }

   @Override
   public void reset(Collection collection, Object hint) {
   }

   @Override
   public synchronized int add(Object o) {
      if (this._data.contains(o)) {
         IntEnumeration e = this._data.keys();

         while (e.hasMoreElements()) {
            int key = e.nextElement();
            if (this._data.get(key).equals(o)) {
               return key;
            }
         }

         throw new Object("AbstractProxyModelStore: add() - illegal state!");
      } else {
         this._data.put(++this._index, o);
         this._refCounts.put(this._index, 0);
         return this._index;
      }
   }

   protected AbstractProxyModelStore(long rootId) {
      ProxyModelStoreManager.getInstance().register(rootId, this);
      this._rootId = rootId;
      this._data = (IntHashtable)(new Object());
      this._refCounts = (IntIntHashtable)(new Object());
      this._index = -1;
   }
}
