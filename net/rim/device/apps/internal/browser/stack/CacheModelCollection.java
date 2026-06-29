package net.rim.device.apps.internal.browser.stack;

import java.util.Enumeration;
import java.util.Vector;
import net.rim.device.api.i18n.Locale;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.synchronization.SyncCollection;
import net.rim.device.api.synchronization.SyncCollectionStatistics;
import net.rim.device.api.synchronization.SyncCollectionStatisticsManager;
import net.rim.device.api.synchronization.SyncConverter;
import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.apps.internal.browser.core.BrowserDaemonRegistry;
import net.rim.device.apps.internal.browser.multipart.MultipartUtilities;
import net.rim.device.apps.internal.browser.resources.BrowserResources;

public final class CacheModelCollection implements SyncCollection, SyncCollectionStatistics {
   private SyncConverter _converter = new CacheModelConverter();
   private static final String DATABASE_NAME;
   private static final int SYNC_VERSION;

   @Override
   public final boolean addSyncObject(SyncObject object) {
      RawDataCache rawDataCache = BrowserDaemonRegistry.getInstance().getRawDataCache();
      CacheModel cacheModel = (CacheModel)object;
      CacheNode cacheNode = cacheModel.getCacheNode();
      CacheResult cacheResult = cacheModel.getCacheResult();
      if (cacheResult.getData() != null) {
         cacheNode.setUrl(cacheResult.getURLWithoutFragment());
         rawDataCache.addNode(cacheNode, cacheModel.isSticky());
         if (cacheModel.isSticky() && !cacheNode.getAvailableOffline()) {
            MultipartUtilities.addToCacheIfMultipart(cacheResult);
         }

         rawDataCache.commit();
      }

      return true;
   }

   @Override
   public final boolean updateSyncObject(SyncObject oldObject, SyncObject newObject) {
      return false;
   }

   @Override
   public final boolean removeSyncObject(SyncObject object) {
      RawDataCache rawDataCache = BrowserDaemonRegistry.getInstance().getRawDataCache();
      CacheModel cacheModel = (CacheModel)object;
      CacheResult cacheResult = cacheModel.getCacheResult();
      rawDataCache.remove(cacheResult.getURLWithoutFragment(), true);
      rawDataCache.commit();
      return true;
   }

   @Override
   public final boolean removeAllSyncObjects() {
      RawDataCache rawDataCache = BrowserDaemonRegistry.getInstance().getRawDataCache();
      rawDataCache.clearShortTermCache();
      rawDataCache.clearLongTermCache(false);
      rawDataCache.commit();
      return true;
   }

   @Override
   public final SyncObject[] getSyncObjects() {
      RawDataCache rawDataCache = BrowserDaemonRegistry.getInstance().getRawDataCache();
      Vector syncObjects = (Vector)(new Object());
      Enumeration e = rawDataCache.getLongTermCacheElements();

      while (e.hasMoreElements()) {
         CacheNode node = (CacheNode)e.nextElement();
         if (node.getContents().getParentCacheResult() == null) {
            syncObjects.addElement(new CacheModel(node, true));
         }
      }

      SyncObject[] objects = new Object[syncObjects.size()];
      syncObjects.copyInto(objects);
      return objects;
   }

   @Override
   public final SyncObject getSyncObject(int uid) {
      return null;
   }

   @Override
   public final boolean isSyncObjectDirty(SyncObject object) {
      return false;
   }

   @Override
   public final void setSyncObjectDirty(SyncObject object) {
   }

   @Override
   public final void clearSyncObjectDirty(SyncObject object) {
   }

   @Override
   public final int getSyncObjectCount() {
      RawDataCache rawDataCache = BrowserDaemonRegistry.getInstance().getRawDataCache();
      return rawDataCache.getLongTermCacheCount();
   }

   @Override
   public final int getSyncVersion() {
      return 1;
   }

   @Override
   public final String getSyncName() {
      return "Browser Data Cache";
   }

   @Override
   public final String getSyncName(Locale locale) {
      ResourceBundle bundle = BrowserResources.getResourceBundle().getBundle(locale);
      return bundle != null ? bundle.getString(863) : null;
   }

   @Override
   public final SyncConverter getSyncConverter() {
      return this._converter;
   }

   @Override
   public final void beginTransaction() {
   }

   @Override
   public final void endTransaction() {
   }

   @Override
   public final synchronized int getSyncCollectionSize() {
      return SyncCollectionStatisticsManager.getSyncCollectionSize(this);
   }
}
