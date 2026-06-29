package net.rim.device.apps.api.utility.general;

import java.util.Enumeration;
import java.util.Hashtable;
import net.rim.vm.Persistable;

public class LRUCache implements Persistable {
   private Hashtable _cache;
   private Object[] _keys;

   public LRUCache() {
      this(8);
   }

   public LRUCache(int size) {
      this._cache = (Hashtable)(new Object(size));
      this._keys = new Object[size];
   }

   public synchronized void put(Object key, Object value) {
      int copyLength = this.findIndex(key);
      if (copyLength == -1) {
         copyLength = this._keys.length - 1;
         Object lruKey = this._keys[copyLength];
         if (lruKey != null) {
            this._cache.remove(lruKey);
         }
      }

      System.arraycopy(this._keys, 0, this._keys, 1, copyLength);
      this._keys[0] = key;
      this._cache.put(key, value);
   }

   public synchronized Object get(Object key) {
      return key != null ? this._cache.get(key) : null;
   }

   public synchronized Object getAt(int index) {
      if (index < this.size() && index >= 0) {
         Object key = this._keys[index];
         return this._cache.get(key);
      } else {
         return null;
      }
   }

   public synchronized Object replaceAt(int index, Object value) {
      if (index < this.size() && index >= 0) {
         Object key = this._keys[index];
         return this._cache.put(key, value);
      } else {
         return null;
      }
   }

   public synchronized boolean remove(Object key) {
      int index = this.findIndex(key);
      if (index == -1) {
         return false;
      }

      this._cache.remove(key);
      System.arraycopy(this._keys, index + 1, this._keys, index, this._keys.length - index - 1);
      this._keys[this._keys.length - 1] = null;
      return true;
   }

   private int findIndex(Object key) {
      int hashCode = key.hashCode();

      for (int i = 0; i < this._keys.length; i++) {
         if (this._keys[i] != null && this._keys[i].hashCode() == hashCode && this._keys[i].equals(key)) {
            return i;
         }
      }

      return -1;
   }

   public synchronized boolean clear() {
      int size = this._cache.size();
      this._cache.clear();

      for (int i = this._keys.length - 1; i >= 0; i--) {
         this._keys[i] = null;
      }

      return size > 0;
   }

   public int size() {
      return this._cache.size();
   }

   public Enumeration keys() {
      return this._cache.keys();
   }
}
