package net.rim.device.apps.internal.lbs.maplet;

import net.rim.device.api.i18n.Locale;
import net.rim.device.api.synchronization.SyncCollection;
import net.rim.device.api.synchronization.SyncCollectionStatistics;
import net.rim.device.api.synchronization.SyncCollectionStatisticsManager;
import net.rim.device.api.synchronization.SyncConverter;
import net.rim.device.api.synchronization.SyncObject;

public final class MapletCacheSyncCollection implements SyncCollection, SyncCollectionStatistics {
   private MapletCache _mapletCache;
   private boolean _inTransaction;
   public static final int SYNC_VERSION = 1;
   public static final int SYNC_VERSION_DESKTOP = 1;
   public static final int SYNC_VERSION_MIN = 1;
   public static final int SYNC_VERSION_MAX = 1;

   MapletCacheSyncCollection(MapletCache mapletCache) {
      this._mapletCache = mapletCache;
   }

   @Override
   public final boolean addSyncObject(SyncObject object) {
      this._mapletCache.add((Maplet)object);
      return true;
   }

   @Override
   public final boolean updateSyncObject(SyncObject oldObject, SyncObject newObject) {
      return false;
   }

   @Override
   public final boolean removeSyncObject(SyncObject object) {
      this._mapletCache.remove((Maplet)object);
      return false;
   }

   @Override
   public final boolean removeAllSyncObjects() {
      this._mapletCache.clear();
      return true;
   }

   @Override
   public final SyncObject[] getSyncObjects() {
      return this._mapletCache.getMaplets();
   }

   @Override
   public final SyncObject getSyncObject(int uid) {
      return null;
   }

   @Override
   public final boolean isSyncObjectDirty(SyncObject object) {
      return true;
   }

   @Override
   public final void setSyncObjectDirty(SyncObject object) {
   }

   @Override
   public final void clearSyncObjectDirty(SyncObject object) {
   }

   @Override
   public final int getSyncObjectCount() {
      return this._mapletCache.size();
   }

   @Override
   public final int getSyncVersion() {
      return 1;
   }

   @Override
   public final String getSyncName() {
      return "Map Cache";
   }

   @Override
   public final String getSyncName(Locale locale) {
      return null;
   }

   @Override
   public final SyncConverter getSyncConverter() {
      return new MapletCacheSyncCollection$MapletSyncConverter(this);
   }

   @Override
   public final void beginTransaction() {
      this._inTransaction = true;
   }

   @Override
   public final void endTransaction() {
      if (this._inTransaction) {
         this._inTransaction = false;
         this.commit();
      }
   }

   private final void commit() {
      if (!this._inTransaction) {
         this._mapletCache.commit();
      }
   }

   @Override
   public final synchronized int getSyncCollectionSize() {
      return SyncCollectionStatisticsManager.getSyncCollectionSize(this);
   }

   private final void log(Throwable e) {
      System.err.println(e.toString());
      e.printStackTrace();
   }
}
