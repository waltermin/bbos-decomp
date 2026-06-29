package net.rim.device.api.collection.util;

import java.util.Enumeration;
import java.util.Hashtable;
import net.rim.device.api.util.BitSet;
import net.rim.device.api.util.ToIntHashtable;

class KeywordPrefixCache {
   private Hashtable _primaryCache;
   private Hashtable _secondaryCache;
   private Hashtable _returnResultCache;
   private ToIntHashtable _timestamps;
   private int _timestamp;
   private static final int MAX_ENTRY_COUNT;

   KeywordPrefixCache() {
      int initialSize = 171;
      this._primaryCache = new Hashtable(initialSize);
      this._secondaryCache = new Hashtable(initialSize);
      this._returnResultCache = new Hashtable(initialSize);
      this._timestamps = new ToIntHashtable(initialSize);
   }

   void reset() {
      this._primaryCache.clear();
      this._secondaryCache.clear();
      this._returnResultCache.clear();
      this._timestamps.clear();
      this._timestamp = 0;
   }

   private void removeOldest() {
      Enumeration enumerator = this._timestamps.keys();
      String oldestKey = null;
      int oldestTimestamp = this._timestamp;

      while (enumerator.hasMoreElements()) {
         String key = (String)enumerator.nextElement();
         int timestamp = this._timestamps.get(key);
         if (timestamp < oldestTimestamp) {
            oldestKey = key;
            oldestTimestamp = timestamp;
         }
      }

      this._primaryCache.remove(oldestKey);
      this._secondaryCache.remove(oldestKey);
      this._timestamps.remove(oldestKey);
   }

   synchronized BitSet getPrimaryEntry(String key) {
      BitSet result = (BitSet)this._primaryCache.get(key);
      if (result != null) {
         this._timestamps.put(key, this._timestamp++);
         this._timestamps.put(key, this._timestamp++);
      }

      return result;
   }

   synchronized BitSet getSecondaryEntry(String key) {
      BitSet result = (BitSet)this._secondaryCache.get(key);
      if (result != null) {
         this._timestamps.put(key, this._timestamp++);
      }

      return result;
   }

   synchronized BitSet getReturnResultEntry(String key) {
      BitSet result = (BitSet)this._returnResultCache.get(key);
      if (result != null) {
         this._timestamps.put(key, this._timestamp++);
      }

      return result;
   }

   synchronized void putPrimaryEntry(String key, BitSet matches) {
      if (this._primaryCache.size() == 128) {
         this.removeOldest();
      }

      this._primaryCache.put(key, new BitSet(matches));
      this._timestamps.put(key, this._timestamp++);
   }

   synchronized void putSecondaryEntry(String key, BitSet matches) {
      if (this._secondaryCache.size() == 128) {
         this.removeOldest();
      }

      this._secondaryCache.put(key, new BitSet(matches));
      this._timestamps.put(key, this._timestamp++);
   }

   synchronized void putReturnResultEntry(String key, BitSet result) {
      if (this._returnResultCache.size() == 128) {
         this.removeOldest();
      }

      this._returnResultCache.put(key, new BitSet(result));
      this._timestamps.put(key, this._timestamp++);
   }
}
