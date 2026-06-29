package net.rim.device.apps.internal.browser.stack;

import java.util.Enumeration;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.api.util.LongEnumeration;
import net.rim.device.api.util.LongHashtable;

final class LongTermCache extends LongHashtable {
   private static final long RAW_DATA_CACHE_KEY;
   private static PersistentObject _persistentObject = RIMPersistentStore.getPersistentObject(1811606973577470395L);
   private static LongHashtable _persistedHashtable;

   LongTermCache() {
      super(_persistedHashtable);
   }

   public final void commit() {
      _persistentObject.commit();
   }

   @Override
   public final Object put(long key, Object value) {
      CacheNode node = (CacheNode)value;
      if (node.getContents().isDataClosed()) {
         if (!node.checkCrypt(false, true)) {
            node.reCrypt(false, true);
         }

         _persistedHashtable.put(key, node);
      }

      return super.put(key, value);
   }

   @Override
   public final Object remove(long key) {
      _persistedHashtable.remove(key);
      return super.remove(key);
   }

   @Override
   public final void clear() {
      _persistedHashtable.clear();
      super.clear();
   }

   public final void flushCache() {
      LongEnumeration e = this.keys();

      while (e.hasMoreElements()) {
         long key = e.nextElement();
         CacheNode node = (CacheNode)this.get(key);
         if (!node.checkCrypt(false, true)) {
            node.reCrypt(false, true);
         }

         _persistedHashtable.put(key, node);
      }
   }

   public final void persistentContentModeChanged(int generation) {
      Enumeration enumeration = _persistedHashtable.elements();

      while (enumeration.hasMoreElements()) {
         CacheNode node = (CacheNode)enumeration.nextElement();
         if (!node.checkCrypt(false, true)) {
            node.reCrypt(false, true);
         }
      }

      _persistentObject.commit();
   }

   static {
      Object contents = _persistentObject.getContents();
      if (!(contents instanceof Object)) {
         _persistedHashtable = (LongHashtable)(new Object());
         _persistentObject.setContents(_persistedHashtable, 51);
         _persistentObject.commit();
      } else {
         _persistedHashtable = (LongHashtable)contents;
      }
   }
}
