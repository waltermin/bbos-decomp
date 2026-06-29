package net.rim.device.apps.internal.contentinjector;

import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.PersistentStore;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.Comparator;

final class ContentInjector$Cache implements Comparator {
   private String[] _cache;
   private static final long ID = 3344829125041281212L;

   public static final ContentInjector$Cache loadCache() {
      PersistentObject po = PersistentStore.getPersistentObject(3344829125041281212L);
      synchronized (po) {
         String[] cache = (Object[])po.getContents();
         if (cache == null) {
            cache = new Object[0];
         }

         po.setContents(cache, 51);
         po.commit();
         return new ContentInjector$Cache(cache);
      }
   }

   private ContentInjector$Cache(String[] cache) {
      this._cache = cache;
   }

   public final void add(String url) {
      synchronized (this._cache) {
         Arrays.add(this._cache, url);
         Arrays.sort(this._cache, this);
         PersistentObject.commit(this._cache);
      }
   }

   public final void remove(String url) {
      synchronized (this._cache) {
         int index = Arrays.binarySearch(this._cache, url, this, 0, this._cache.length);
         if (index >= 0) {
            Arrays.removeAt(this._cache, index);
         }

         PersistentObject.commit(this._cache);
      }
   }

   public final boolean hit(String url) {
      synchronized (this._cache) {
         return Arrays.binarySearch(this._cache, url, this, 0, this._cache.length) >= 0;
      }
   }

   @Override
   public final int compare(Object o1, Object o2) {
      if (o1 instanceof Object && o2 instanceof Object) {
         String s1 = (String)o1;
         String s2 = (String)o2;
         return s1.compareTo(s2);
      } else {
         return 0;
      }
   }

   @Override
   public final boolean equals(Object obj) {
      if (obj == this) {
         return true;
      }

      if (!(obj instanceof ContentInjector$Cache)) {
         return false;
      }

      ContentInjector$Cache c = (ContentInjector$Cache)obj;
      return c._cache == this._cache;
   }
}
