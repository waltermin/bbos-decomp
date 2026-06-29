package net.rim.wica.runtime.persistence.internal;

import java.util.Enumeration;
import java.util.Hashtable;
import net.rim.wica.runtime.persistence.Recryptable;
import net.rim.wica.runtime.persistence.Resource;

final class ResourceCache implements Recryptable {
   private WicletStoreImpl _storage;
   private Hashtable _cache;

   final synchronized int size() {
      int size = 0;
      Enumeration e = this._cache.elements();

      while (e.hasMoreElements()) {
         CacheEntry entry = (CacheEntry)e.nextElement();
         size += ((Resource)entry.getValue()).size();
      }

      return size;
   }

   final synchronized int evict() {
      int evicted = 0;
      Enumeration e = this._cache.elements();

      while (e.hasMoreElements()) {
         CacheEntry entry = (CacheEntry)e.nextElement();
         if (!entry.isValid()) {
            this._cache.remove(entry.getKey());
            evicted++;
         }
      }

      this._storage.save();
      return evicted;
   }

   final synchronized Resource get(String uri) {
      Resource result = null;
      CacheEntry entry = (CacheEntry)this._cache.get(uri);
      if (entry != null) {
         result = (Resource)entry.getValue();
      }

      return result;
   }

   final synchronized Resource put(Resource resource) {
      String uri = resource.getUri();
      this._cache.put(uri, new CacheEntry(uri, resource));
      this._storage.save();
      return resource;
   }

   @Override
   public final void recrypt() {
      Enumeration e = this._cache.elements();

      while (e.hasMoreElements()) {
         CacheEntry entry = (CacheEntry)e.nextElement();
         Object value = entry.getValue();
         if (value instanceof Recryptable) {
            ((Recryptable)value).recrypt();
         }
      }
   }

   ResourceCache(WicletStoreImpl storage) {
      this._storage = storage;
      this._cache = storage.getModel()._resourceCache;
   }
}
