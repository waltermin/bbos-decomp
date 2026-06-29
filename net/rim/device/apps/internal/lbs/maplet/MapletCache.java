package net.rim.device.apps.internal.lbs.maplet;

import net.rim.device.api.lowmemory.LowMemoryListener;
import net.rim.device.api.lowmemory.LowMemoryManager;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.IntEnumeration;
import net.rim.device.apps.internal.lbs.LBSOptions;

public final class MapletCache implements LowMemoryListener {
   private PersistentObject _persistentObject = RIMPersistentStore.getPersistentObject(-7263572457632149725L);
   private LRUCache _maplets;
   private MapletCacheSyncCollection _syncCollection;
   private static MapletCache _instance;
   private static final long GUID;
   private static final int EMPTY_KEY;

   public static final void registerOnStartup() {
      getInstance();
   }

   public static final MapletCache getInstance() {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      _instance = (MapletCache)ar.getOrWaitFor(-7263572457632149725L);
      if (_instance == null) {
         _instance = new MapletCache();
         ar.put(-7263572457632149725L, _instance);
      }

      return _instance;
   }

   public static final int getSyncCollectionSize() {
      return getInstance()._syncCollection.getSyncCollectionSize();
   }

   MapletCache() {
      synchronized (this._persistentObject) {
         Object o = this._persistentObject.getContents();
         if (o instanceof LRUCache) {
            this._maplets = (LRUCache)o;
         }

         if (this._maplets == null) {
            this._maplets = new LRUCache();
            this._persistentObject.setContents(this._maplets, 51);
            this._persistentObject.commit();
         }
      }

      this._syncCollection = new MapletCacheSyncCollection(this);
      LowMemoryManager.addLowMemoryListener(this);
   }

   public final void add(Maplet maplet) {
      this._maplets.put(maplet.getHashKey(), maplet);
      synchronized (this._maplets) {
         this.commit();
      }
   }

   public final void addEmptyMaplet(int x, int y, int level) {
      Maplet emptyMaplet = (Maplet)this._maplets.get(1);
      if (emptyMaplet == null) {
         byte version = (byte)(LBSOptions.getInt(3743068244816784828L, 1) & 0xFF);
         emptyMaplet = new Maplet(version);
         this._maplets.put(1, emptyMaplet);
      }

      this._maplets.put(Maplet.getHashKey(x, y, level), emptyMaplet);
   }

   public final Maplet getMaplet(int x, int y, int level) {
      return (Maplet)this._maplets.get(Maplet.getHashKey(x, y, level));
   }

   public final void clear() {
      synchronized (this._maplets) {
         this._maplets.clear();
      }

      this.commit();
      LBSOptions.setInt(3743068244816784828L, 1);
   }

   public final void remove(Maplet maplet) {
      this._maplets.remove(maplet.getHashKey());
   }

   public final void commit() {
      this._persistentObject.commit();
   }

   public final Maplet[] getMaplets(MapRect rect, int zoom) {
      Maplet[] maplets = new Maplet[0];
      MapRect mapletRect = MapRect.getMapletRect(rect, zoom);
      int mapletSize = Maplet.getMapletSize(zoom);

      for (int x = mapletRect._left; x < mapletRect._right; x += mapletSize) {
         for (int y = mapletRect._bottom; y < mapletRect._top; y += mapletSize) {
            Maplet maplet = this.getMaplet(x, y, Maplet.getMapletLevel(zoom));
            if (maplet != null) {
               Arrays.add(maplets, maplet);
            }
         }
      }

      return maplets;
   }

   final Maplet[] getMaplets() {
      synchronized (this._maplets) {
         Maplet[] maplets = new Maplet[this._maplets.size()];
         int index = 0;
         IntEnumeration keys = this._maplets.keys();

         while (keys.hasMoreElements()) {
            maplets[index++] = (Maplet)this._maplets.get(keys.nextElement());
         }

         return maplets;
      }
   }

   final int size() {
      return this._maplets.size();
   }

   @Override
   public final boolean freeStaleObject(int priority) {
      boolean freed = false;
      if (priority == 1) {
         freed = this._maplets.freeStaleMaplets(LRUCache.AGE_THRESHOLD);
      } else {
         this._maplets.freeStaleMaplets(0);
      }

      if (freed) {
         this.commit();
      }

      return freed;
   }
}
