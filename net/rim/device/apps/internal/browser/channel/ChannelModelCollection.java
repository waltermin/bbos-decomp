package net.rim.device.apps.internal.browser.channel;

import java.util.Enumeration;
import java.util.Vector;
import net.rim.device.api.collection.CollectionEventSource;
import net.rim.device.api.collection.ReadableList;
import net.rim.device.api.collection.WritableSet;
import net.rim.device.api.collection.util.CollectionListenerManager;
import net.rim.device.api.i18n.Locale;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.synchronization.OTASyncCapable;
import net.rim.device.api.synchronization.SyncCollection;
import net.rim.device.api.synchronization.SyncCollectionSchema;
import net.rim.device.api.synchronization.SyncCollectionStatistics;
import net.rim.device.api.synchronization.SyncCollectionStatisticsManager;
import net.rim.device.api.synchronization.SyncConverter;
import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.apps.api.messaging.Folder;
import net.rim.device.apps.api.messaging.FolderHierarchies;
import net.rim.device.apps.api.messaging.util.SimpleFolder;
import net.rim.device.apps.internal.browser.core.BrowserDaemonRegistry;
import net.rim.device.apps.internal.browser.resources.BrowserResources;
import net.rim.device.apps.internal.browser.store.BrowserFolders;
import net.rim.device.apps.internal.browser.store.FolderEventListener;

public final class ChannelModelCollection implements SyncCollection, OTASyncCapable, CollectionEventSource, FolderEventListener, SyncCollectionStatistics {
   private SyncConverter _converter;
   private Folder _folder;
   private CollectionListenerManager _collectionListenerManager = new CollectionListenerManager();
   private static final String DATABASE_NAME = "Browser Channels";
   private static final int SYNC_VERSION = 1;

   public ChannelModelCollection() {
      this._converter = new ChannelModelConverter();
      this._folder = FolderHierarchies.getFolder(BrowserFolders.RIM_BROWSER_CHANNELS_HIERARCHY_ID, BrowserFolders.BROWSER_CHANNELS_FOLDER_ID);
      BrowserDaemonRegistry.addFolderEventListener(this);
   }

   @Override
   public final void addCollectionListener(Object listener) {
      this._collectionListenerManager.addCollectionListener(listener);
   }

   @Override
   public final void removeCollectionListener(Object listener) {
      this._collectionListenerManager.removeCollectionListener(listener);
   }

   @Override
   public final void browserEventOccured(int eventId, Object param) {
      switch (eventId) {
         case 150:
         default:
            this._collectionListenerManager.fireElementAdded(this, param);
            return;
         case 151:
            this._collectionListenerManager.fireElementRemoved(this, param);
            return;
         case 152:
            this._collectionListenerManager.fireElementUpdated(this, null, param);
         case 149:
      }
   }

   @Override
   public final boolean addSyncObject(SyncObject object) {
      ChannelModel channelModel = (ChannelModel)object;
      long parentFolderLUID = channelModel.getParentFolderLUID();
      synchronized (FolderHierarchies.getLockObject()) {
         SimpleFolder parentFolder = (SimpleFolder)FolderHierarchies.getFolder(BrowserFolders.RIM_BROWSER_CHANNELS_HIERARCHY_ID, parentFolderLUID);
         if (parentFolder == null) {
            SimpleFolder rootFolder = (SimpleFolder)FolderHierarchies.getFolder(
               BrowserFolders.RIM_BROWSER_CHANNELS_HIERARCHY_ID, BrowserFolders.RIM_BROWSER_CHANNELS_HIERARCHY_ID
            );
            parentFolder = new SimpleFolder(
               BrowserFolders.BROWSER_FAMILY, parentFolderLUID, null, BrowserFolders.BROWSER_FOLDER_COLLECTION_CLASS, rootFolder, 1
            );
         }

         ReadableList collection = (ReadableList)parentFolder.getContainedItems();
         int collectionSize = collection.size();

         for (int i = 0; i < collectionSize; i++) {
            ChannelModel c = (ChannelModel)collection.getAt(i);
            if (c.getID().equals(channelModel.getID())) {
               if (channelModel.getTimestamp() <= c.getTimestamp()) {
                  BrowserDaemonRegistry.broadCastEvent(150, channelModel);
                  BrowserDaemonRegistry.broadCastEvent(151, channelModel);
                  return true;
               }

               ((WritableSet)collection).remove(c);
               BrowserDaemonRegistry.broadCastEvent(151, c);
               Channels.removeChannelFromRibbon(c);
               break;
            }
         }

         long newKey = collectionSize;
         channelModel.setTimestamp(newKey);
         if (!channelModel.checkCrypt(true, true)) {
            channelModel.reCrypt(true, true);
         }

         ((WritableSet)collection).add(channelModel);
      }

      Channels.addChannelToRibbon(channelModel);
      BrowserDaemonRegistry.broadCastEvent(150, channelModel);
      return true;
   }

   @Override
   public final boolean updateSyncObject(SyncObject oldObject, SyncObject newObject) {
      if (!this.removeSyncObject(oldObject, false)) {
         return false;
      }

      ChannelModel newChannelModel = (ChannelModel)newObject;
      long newParentFolderLUID = newChannelModel.getParentFolderLUID();
      synchronized (FolderHierarchies.getLockObject()) {
         SimpleFolder parentFolder = (SimpleFolder)FolderHierarchies.getFolder(BrowserFolders.RIM_BROWSER_CHANNELS_HIERARCHY_ID, newParentFolderLUID);
         if (parentFolder == null) {
            SimpleFolder rootFolder = (SimpleFolder)FolderHierarchies.getFolder(
               BrowserFolders.RIM_BROWSER_CHANNELS_HIERARCHY_ID, BrowserFolders.RIM_BROWSER_CHANNELS_HIERARCHY_ID
            );
            parentFolder = new SimpleFolder(
               BrowserFolders.BROWSER_FAMILY, newParentFolderLUID, null, BrowserFolders.BROWSER_FOLDER_COLLECTION_CLASS, rootFolder, 1
            );
         }

         ((WritableSet)parentFolder.getContainedItems()).add(newChannelModel);
      }

      Channels.addChannelToRibbon(newChannelModel);
      this.clearSyncObjectDirty(newChannelModel);
      BrowserDaemonRegistry.broadCastEvent(152, newChannelModel);
      return true;
   }

   @Override
   public final boolean removeSyncObject(SyncObject object) {
      return this.removeSyncObject(object, true);
   }

   private final boolean removeSyncObject(SyncObject object, boolean sendNotifications) {
      ChannelModel channelModel = (ChannelModel)object;
      long parentFolderLUID = channelModel.getParentFolderLUID();
      synchronized (FolderHierarchies.getLockObject()) {
         SimpleFolder parentFolder = (SimpleFolder)FolderHierarchies.getFolder(BrowserFolders.RIM_BROWSER_CHANNELS_HIERARCHY_ID, parentFolderLUID);
         if (parentFolder != null) {
            WritableSet collection = (WritableSet)parentFolder.getContainedItems();
            collection.remove(channelModel);
            Channels.removeChannelFromRibbon(channelModel);
            if (sendNotifications) {
               BrowserDaemonRegistry.broadCastEvent(151, channelModel);
            }

            return true;
         } else {
            return false;
         }
      }
   }

   @Override
   public final boolean removeAllSyncObjects() {
      synchronized (FolderHierarchies.getLockObject()) {
         this.removeSubItems(this._folder);
      }

      this._collectionListenerManager.fireReset(this);
      return true;
   }

   private final void removeSubItems(Folder folder) {
      ReadableList collection = (ReadableList)folder.getContainedItems();

      for (int i = collection.size() - 1; i >= 0; i--) {
         ChannelModel channelModel = (ChannelModel)collection.getAt(i);
         ((WritableSet)collection).remove(channelModel);
         Channels.removeChannelFromRibbon(channelModel);
      }

      Enumeration e = folder.getSubFolders();

      while (e.hasMoreElements()) {
         this.removeSubItems((Folder)e.nextElement());
      }
   }

   @Override
   public final SyncObject[] getSyncObjects() {
      Vector syncObjects = new Vector();
      this.addSubItems(this._folder, syncObjects);
      SyncObject[] objects = new SyncObject[syncObjects.size()];
      syncObjects.copyInto(objects);
      return objects;
   }

   @Override
   public final SyncObject getSyncObject(int uid) {
      return this.findSyncObject(this._folder, uid);
   }

   private final void addSubItems(Folder folder, Vector vector) {
      ReadableList collection = (ReadableList)folder.getContainedItems();

      for (int i = 0; i < collection.size(); i++) {
         vector.addElement(collection.getAt(i));
      }

      Enumeration e = folder.getSubFolders();

      while (e.hasMoreElements()) {
         this.addSubItems((Folder)e.nextElement(), vector);
      }
   }

   private final SyncObject findSyncObject(Folder folder, int uid) {
      ReadableList collection = (ReadableList)folder.getContainedItems();
      int numElements = collection.size();

      for (int i = 0; i < numElements; i++) {
         SyncObject element = (SyncObject)collection.getAt(i);
         if (element.getUID() == uid) {
            return element;
         }
      }

      Enumeration e = folder.getSubFolders();

      while (e.hasMoreElements()) {
         SyncObject element = this.findSyncObject((Folder)e.nextElement(), uid);
         if (element != null) {
            return element;
         }
      }

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
      return this.countSubItems(this._folder);
   }

   private final int countSubItems(Folder folder) {
      int count = 0;
      count += ((ReadableList)folder.getContainedItems()).size();
      Enumeration e = folder.getSubFolders();

      while (e.hasMoreElements()) {
         count += this.countSubItems((Folder)e.nextElement());
      }

      return count;
   }

   @Override
   public final int getSyncVersion() {
      return 1;
   }

   @Override
   public final String getSyncName() {
      return "Browser Channels";
   }

   @Override
   public final String getSyncName(Locale locale) {
      ResourceBundle bundle = BrowserResources.getResourceBundle().getBundle(locale);
      return bundle != null ? bundle.getString(862) : null;
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

   @Override
   public final SyncCollectionSchema getSchema() {
      return null;
   }
}
