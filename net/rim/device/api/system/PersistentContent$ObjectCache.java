package net.rim.device.api.system;

import java.util.Enumeration;
import net.rim.device.api.memorycleaner.MemoryCleanerDaemon;
import net.rim.device.api.memorycleaner.MemoryCleanerListener;
import net.rim.device.api.util.IntHashtable;

final class PersistentContent$ObjectCache implements MemoryCleanerListener {
   private IntHashtable _hashtable = new IntHashtable(131);
   private int[] _hashes = new int[131];
   private int _victim;
   private static final int SIZE;

   PersistentContent$ObjectCache() {
      for (int i = 130; i >= 0; this._hashes[i] = i--) {
         this._hashtable.put(i, new PersistentContent$ObjectCacheElement());
      }

      MemoryCleanerDaemon.addListener(this, false);
   }

   final Object get(char[] encoding, boolean firstChunkOnly) {
      int hash = net.rim.vm.Memory.objectToInt(encoding);
      PersistentContent$ObjectCacheElement element = (PersistentContent$ObjectCacheElement)this._hashtable.get(hash);
      return element == null ? null : element.get(encoding, firstChunkOnly);
   }

   final void put(char[] encoding, boolean firstChunkOnly, Object object) {
      int hash = net.rim.vm.Memory.objectToInt(encoding);
      PersistentContent$ObjectCacheElement element = (PersistentContent$ObjectCacheElement)this._hashtable.get(hash);
      if (element == null) {
         if (++this._victim == 131) {
            this._victim = 0;
         }

         element = (PersistentContent$ObjectCacheElement)this._hashtable.remove(this._hashes[this._victim]);
         if (element == null) {
            element = new PersistentContent$ObjectCacheElement();
         }

         this._hashtable.put(hash, element);
         this._hashes[this._victim] = hash;
      }

      element.put(encoding, firstChunkOnly, object);
   }

   @Override
   public final boolean cleanNow(int event) {
      boolean gc = false;
      Enumeration enumeration = this._hashtable.elements();

      while (enumeration.hasMoreElements()) {
         PersistentContent$ObjectCacheElement element = (PersistentContent$ObjectCacheElement)enumeration.nextElement();
         gc |= element.cleanNow();
      }

      return gc;
   }

   @Override
   public final String getDescription() {
      return "Content Protection Cache";
   }
}
