package net.rim.wica.runtime.persistence.internal;

import net.rim.device.api.util.Persistable;
import net.rim.wica.runtime.persistence.WebResource;

final class CacheEntry implements Persistable {
   private CacheEntry _previous;
   private CacheEntry _next;
   private Object _key;
   private Object _value;
   private long _creationTime = System.currentTimeMillis();
   private long _lastAccessTime = this._creationTime;
   private int _hits = 1;

   CacheEntry(Object key, Object value) {
      this._key = key;
      this._value = value;
   }

   final long getLastAccessTime() {
      return this._lastAccessTime;
   }

   final long getCreationTime() {
      return this._creationTime;
   }

   final int getHits() {
      return this._hits;
   }

   final Object getKey() {
      return this._key;
   }

   final long getExpirationTime() {
      return !(this._value instanceof WebResource) ? 0 : ((WebResource)this._value).getExpiration();
   }

   final CacheEntry getNext() {
      return this._next;
   }

   final CacheEntry getPrevious() {
      return this._previous;
   }

   final Object getValue() {
      this._lastAccessTime = System.currentTimeMillis();
      this._hits++;
      return this._value;
   }

   final boolean isValid() {
      return this._value instanceof WebResource ? this.getExpirationTime() > System.currentTimeMillis() : true;
   }

   final void setNext(CacheEntry next) {
      this._next = next;
   }

   final void setPrevious(CacheEntry previous) {
      this._previous = previous;
   }
}
