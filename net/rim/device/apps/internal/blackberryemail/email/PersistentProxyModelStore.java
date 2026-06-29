package net.rim.device.apps.internal.blackberryemail.email;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.ObjectGroup;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.api.util.Persistable;
import net.rim.vm.WeakReference;

public class PersistentProxyModelStore extends AbstractProxyModelStore implements Persistable {
   private boolean _group;
   private long _ramCacheId;
   private static final long RAMCACHE_ID;

   public static final PersistentProxyModelStore getInstance(long rootId, boolean group) {
      PersistentObject po = RIMPersistentStore.getPersistentObject(rootId);
      synchronized (po) {
         if (po.getContents() == null) {
            po.setContents(new PersistentProxyModelStore(rootId, group), 51);
            po.commit();
         }
      }

      return (PersistentProxyModelStore)po.getContents();
   }

   protected PersistentProxyModelStore(long rootId, boolean group) {
      super(rootId);
      this._group = group;
      this._ramCacheId = rootId + System.identityHashCode(this) + -4403285339980975672L;
   }

   @Override
   public int add(Object o) {
      if (o == null) {
         return -1;
      }

      if (this._group) {
         ObjectGroup.createGroupIgnoreTooBig(o);
      }

      int handle = super.add(o);
      PersistentObject.commit(this);
      return handle;
   }

   @Override
   public synchronized Object get(int objecthandle) {
      IntHashtable ramCache = this.getRamCache();
      WeakReference wr = (WeakReference)ramCache.get(objecthandle);
      Object o = null;
      if (wr != null) {
         o = wr.get();
         if (o == null) {
            ramCache.remove(objecthandle);
         }
      }

      if (o == null) {
         o = super._data.get(objecthandle);
         if (o != null) {
            if (this._group) {
               o = ObjectGroup.expandGroup(o);
            }

            ramCache.put(objecthandle, new Object(o));
         }
      }

      return o;
   }

   private IntHashtable getRamCache() {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      IntHashtable ramCache = (IntHashtable)ar.getOrWaitFor(this._ramCacheId);
      if (ramCache == null) {
         ramCache = (IntHashtable)(new Object());
         ar.put(this._ramCacheId, ramCache);
      }

      return ramCache;
   }

   public synchronized void update(Object element, ProxyModel pm) {
      int handle = pm.getObjectHandle();
      IntHashtable ramCache = this.getRamCache();
      if (ramCache.containsKey(handle)) {
         ramCache.remove(handle);
      }

      if (this.isGrouped() && !ObjectGroup.isInGroup(element)) {
         ObjectGroup.createGroupIgnoreTooBig(element);
      }

      if (element != null) {
         super._data.put(handle, element);
      }

      PersistentObject.commit(this);
   }

   @Override
   public synchronized Object delete(int objecthandle) {
      IntHashtable ramCache = this.getRamCache();
      ramCache.remove(objecthandle);
      Object o = super.delete(objecthandle);
      if (this._group && o != null) {
         o = ObjectGroup.expandGroup(o);
      }

      PersistentObject.commit(this);
      return o;
   }

   @Override
   public int refCount(int objecthandle, int delta) {
      int count = super.refCount(objecthandle, delta);
      PersistentObject.commit(this);
      return count;
   }

   public boolean isGrouped() {
      return this._group;
   }
}
