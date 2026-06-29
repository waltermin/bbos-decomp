package net.rim.device.apps.internal.browser.store;

import net.rim.device.api.collection.CollectionEventSource;
import net.rim.device.api.collection.util.CollectionListenerManager;
import net.rim.device.api.i18n.Locale;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.synchronization.OTASyncCapable;
import net.rim.device.api.synchronization.OTASyncPriorityProvider;
import net.rim.device.api.synchronization.SyncCollection;
import net.rim.device.api.synchronization.SyncCollectionSchema;
import net.rim.device.api.synchronization.SyncCollectionStatistics;
import net.rim.device.api.synchronization.SyncCollectionStatisticsManager;
import net.rim.device.api.synchronization.SyncConverter;
import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.apps.api.framework.model.ConversionProvider;
import net.rim.device.apps.api.framework.model.SyncBuffer;
import net.rim.device.apps.internal.browser.core.BrowserDaemonRegistry;
import net.rim.device.apps.internal.browser.core.UrlCollectionListener;
import net.rim.device.apps.internal.browser.resources.BrowserResources;
import net.rim.device.apps.internal.browser.util.VisitedURLStore;

public final class BrowserURLCollection
   implements SyncCollection,
   SyncCollectionStatistics,
   SyncConverter,
   CollectionEventSource,
   OTASyncCapable,
   OTASyncPriorityProvider,
   UrlCollectionListener {
   private CollectionListenerManager _collectionListenerManager = (CollectionListenerManager)(new Object());
   public static String BROWSER_URLS_DB_NAME = "Browser Urls";
   public static int SYNC_VERSION = 1;

   @Override
   public final void collectionChanged(String url, int action) {
      switch (action) {
         case 0:
         default:
            this._collectionListenerManager.fireElementAdded(this, new BrowserURLSyncObject(url));
            return;
         case 1:
            this._collectionListenerManager.fireElementRemoved(this, new BrowserURLSyncObject(url));
         case -1:
      }
   }

   @Override
   public final void addCollectionListener(Object listener) {
      if (this._collectionListenerManager.isEmpty()) {
         BrowserDaemonRegistry.addBrowserUrlCollectionListener(this);
      }

      this._collectionListenerManager.addCollectionListener(listener);
   }

   @Override
   public final void removeCollectionListener(Object listener) {
      this._collectionListenerManager.removeCollectionListener(listener);
      if (this._collectionListenerManager.isEmpty()) {
         BrowserDaemonRegistry.removeBrowserUrlCollectionListener(this);
      }
   }

   @Override
   public final boolean addSyncObject(SyncObject syncObject) {
      BrowserURLSyncObject object = (BrowserURLSyncObject)syncObject;
      VisitedURLStore visitedURLStore = VisitedURLStore.getInstance();
      visitedURLStore.add(object.getURL());
      this._collectionListenerManager.fireElementAdded(this, syncObject);
      return true;
   }

   @Override
   public final void beginTransaction() {
   }

   @Override
   public final void clearSyncObjectDirty(SyncObject arg0) {
   }

   @Override
   public final void endTransaction() {
   }

   @Override
   public final SyncConverter getSyncConverter() {
      return this;
   }

   @Override
   public final String getSyncName() {
      return BROWSER_URLS_DB_NAME;
   }

   @Override
   public final String getSyncName(Locale locale) {
      ResourceBundle bundle = BrowserResources.getResourceBundle().getBundle(locale);
      return bundle != null ? bundle.getString(865) : null;
   }

   @Override
   public final SyncObject getSyncObject(int uid) {
      VisitedURLStore visitedURLStore = VisitedURLStore.getInstance();
      int length = visitedURLStore.size();

      for (int i = 0; i < length; i++) {
         String url = visitedURLStore.getRecentElementAt(i);
         if (url != null && url.hashCode() == uid) {
            return new BrowserURLSyncObject(url);
         }
      }

      return null;
   }

   @Override
   public final int getSyncObjectCount() {
      VisitedURLStore visitedURLStore = VisitedURLStore.getInstance();
      return visitedURLStore.size();
   }

   @Override
   public final SyncObject[] getSyncObjects() {
      VisitedURLStore visitedURLStore = VisitedURLStore.getInstance();
      int length = visitedURLStore.size();
      SyncObject[] objects = new Object[length];

      for (int i = length - 1; i >= 0; i--) {
         String url = visitedURLStore.getRecentElementAt(i);
         objects[length - 1 - i] = new BrowserURLSyncObject(url);
      }

      return objects;
   }

   @Override
   public final int getSyncVersion() {
      return SYNC_VERSION;
   }

   @Override
   public final boolean isSyncObjectDirty(SyncObject arg0) {
      return false;
   }

   @Override
   public final boolean removeAllSyncObjects() {
      VisitedURLStore visitedURLStore = VisitedURLStore.getInstance();
      visitedURLStore.clear();
      return true;
   }

   @Override
   public final boolean removeSyncObject(SyncObject syncObject) {
      BrowserURLSyncObject object = (BrowserURLSyncObject)syncObject;
      VisitedURLStore visitedURLStore = VisitedURLStore.getInstance();
      visitedURLStore.delete(object.getURL());
      this._collectionListenerManager.fireElementRemoved(this, syncObject);
      return true;
   }

   @Override
   public final void setSyncObjectDirty(SyncObject arg0) {
   }

   @Override
   public final boolean updateSyncObject(SyncObject arg0, SyncObject arg1) {
      return false;
   }

   @Override
   public final int getSyncCollectionSize() {
      return SyncCollectionStatisticsManager.getSyncCollectionSize(this);
   }

   @Override
   public final SyncCollectionSchema getSchema() {
      return null;
   }

   @Override
   public final int getSyncPriority() {
      return 7;
   }

   @Override
   public final SyncObject convert(DataBuffer param1, int param2, int param3) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: iload 2
      // 01: getstatic net/rim/device/apps/internal/browser/store/BrowserURLCollection.SYNC_VERSION I
      // 04: if_icmpeq 09
      // 07: aconst_null
      // 08: areturn
      // 09: aload 1
      // 0a: invokevirtual net/rim/device/api/util/DataBuffer.readShort ()S
      // 0d: pop
      // 0e: aload 1
      // 0f: invokevirtual net/rim/device/api/util/DataBuffer.readByte ()B
      // 12: bipush 25
      // 14: if_icmpeq 19
      // 17: aconst_null
      // 18: areturn
      // 19: aload 1
      // 1a: invokevirtual net/rim/device/api/util/DataBuffer.readCompressedInt ()I
      // 1d: pop
      // 1e: aload 1
      // 1f: invokevirtual net/rim/device/api/util/DataBuffer.readUTF ()Ljava/lang/String;
      // 22: astore 4
      // 24: goto 2f
      // 27: astore 5
      // 29: aconst_null
      // 2a: areturn
      // 2b: astore 5
      // 2d: aconst_null
      // 2e: areturn
      // 2f: new net/rim/device/apps/internal/browser/store/BrowserURLSyncObject
      // 32: dup
      // 33: aload 4
      // 35: invokespecial net/rim/device/apps/internal/browser/store/BrowserURLSyncObject.<init> (Ljava/lang/String;)V
      // 38: areturn
      // try (5 -> 13): 21 null
      // try (14 -> 20): 21 null
      // try (5 -> 13): 24 null
      // try (14 -> 20): 24 null
   }

   @Override
   public final boolean convert(SyncObject syncObject, DataBuffer buffer, int version) {
      if (syncObject == null) {
         return false;
      }

      BrowserURLSyncObject object = (BrowserURLSyncObject)syncObject;
      ConversionProvider converter = object;
      SyncBuffer syncBuffer = (SyncBuffer)(new Object(buffer, version, object.getUID()));
      return converter.convert(null, syncBuffer);
   }
}
