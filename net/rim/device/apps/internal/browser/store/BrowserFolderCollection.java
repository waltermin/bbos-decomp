package net.rim.device.apps.internal.browser.store;

import java.util.Enumeration;
import java.util.Vector;
import net.rim.device.api.collection.CollectionEventSource;
import net.rim.device.api.collection.ReadableList;
import net.rim.device.api.collection.util.CollectionListenerManager;
import net.rim.device.api.i18n.Locale;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.synchronization.OTASyncCapable;
import net.rim.device.api.synchronization.OTASyncPriorityAndDependencyProvider;
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

public final class BrowserFolderCollection
   implements SyncCollection,
   OTASyncCapable,
   OTASyncPriorityAndDependencyProvider,
   CollectionEventSource,
   FolderEventListener,
   SyncCollectionStatistics {
   private SyncConverter _converter = new BrowserFolderConverter();
   private CollectionListenerManager _collectionListenerManager = (CollectionListenerManager)(new Object());
   public static String BROWSER_FOLDERS_DB_NAME = "Browser Folders";
   private static int SYNC_VERSION = 1;

   public BrowserFolderCollection() {
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
         case 200:
         default:
            this._collectionListenerManager.fireElementAdded(this, param);
            return;
         case 201:
            this._collectionListenerManager.fireElementRemoved(this, param);
            return;
         case 202:
            this._collectionListenerManager.fireElementUpdated(this, null, param);
         case 199:
      }
   }

   @Override
   public final boolean addSyncObject(SyncObject object) {
      return this.addOrUpdateSyncObject(object);
   }

   private final boolean addOrUpdateSyncObject(SyncObject object) {
      BrowserFolderSyncObject browserSyncObject = (BrowserFolderSyncObject)object;
      long parentFolderLUID = BrowserFolders.makeLUID(browserSyncObject.getParentFolderUID());
      SimpleFolder parentFolder = (SimpleFolder)FolderHierarchies.getFolder(BrowserFolders.RIM_BROWSER_BOOKMARKS_HIERARCHY_ID, parentFolderLUID);
      if (parentFolder == null) {
         SimpleFolder rootFolder = SimpleFolder.getInstance(BrowserFolders.BROWSER_FAMILY, BrowserFolders.RIM_BROWSER_BOOKMARKS_HIERARCHY_ID);
         parentFolder = (SimpleFolder)(new Object(
            BrowserFolders.BROWSER_FAMILY, parentFolderLUID, null, BrowserFolders.BROWSER_FOLDER_COLLECTION_CLASS, rootFolder, 1
         ));
         rootFolder.putFolder(parentFolder);
      }

      long folderLUID = BrowserFolders.makeLUID(browserSyncObject.getUID());
      SimpleFolder folder = (SimpleFolder)FolderHierarchies.getFolder(BrowserFolders.RIM_BROWSER_BOOKMARKS_HIERARCHY_ID, folderLUID);
      if (folder == null) {
         folder = (SimpleFolder)(new Object(
            BrowserFolders.BROWSER_FAMILY, folderLUID, browserSyncObject.getFriendlyName(), BrowserFolders.BROWSER_FOLDER_COLLECTION_CLASS, parentFolder, 1
         ));
         BrowserFolders.addSubFolder(parentFolder, folder);
         return true;
      } else {
         folder.setFriendlyName(browserSyncObject.getFriendlyName());
         SimpleFolder oldParentFolder = (SimpleFolder)folder.getParentFolder();
         if (oldParentFolder != parentFolder) {
            oldParentFolder.removeSubFolder(folder);
            folder.setParentFolder(parentFolder);
            BrowserFolders.addSubFolder(parentFolder, folder);
            return true;
         } else {
            BrowserDaemonRegistry.broadCastEvent(202, browserSyncObject);
            return true;
         }
      }
   }

   @Override
   public final boolean updateSyncObject(SyncObject oldObject, SyncObject newObject) {
      if (this.addOrUpdateSyncObject(newObject)) {
         this.clearSyncObjectDirty(newObject);
         return true;
      } else {
         return false;
      }
   }

   @Override
   public final boolean removeSyncObject(SyncObject object) {
      long folderLUID = BrowserFolders.makeLUID(object.getUID());
      SimpleFolder folder = (SimpleFolder)FolderHierarchies.getFolder(BrowserFolders.RIM_BROWSER_BOOKMARKS_HIERARCHY_ID, folderLUID);
      if (folder == null) {
         return false;
      }

      SimpleFolder rootFolder = SimpleFolder.getInstance(BrowserFolders.BROWSER_FAMILY, BrowserFolders.RIM_BROWSER_BOOKMARKS_HIERARCHY_ID);
      this.removeSubFolders(folder, rootFolder, true);
      return true;
   }

   @Override
   public final boolean removeAllSyncObjects() {
      SimpleFolder rootFolder = SimpleFolder.getInstance(BrowserFolders.BROWSER_FAMILY, BrowserFolders.RIM_BROWSER_BOOKMARKS_HIERARCHY_ID);
      SimpleFolder messagesFolder = (SimpleFolder)FolderHierarchies.getFolder(
         BrowserFolders.RIM_BROWSER_MESSAGES_HIERARCHY_ID, BrowserFolders.BROWSER_MESSAGES_FOLDER_ID
      );
      this.removeSubFolders(messagesFolder, rootFolder, false);
      SimpleFolder bookmarksFolder = (SimpleFolder)FolderHierarchies.getFolder(
         BrowserFolders.RIM_BROWSER_BOOKMARKS_HIERARCHY_ID, BrowserFolders.BROWSER_BOOKMARKS_FOLDER_ID
      );
      this.removeSubFolders(bookmarksFolder, rootFolder, false);
      SimpleFolder channelsFolder = (SimpleFolder)FolderHierarchies.getFolder(
         BrowserFolders.RIM_BROWSER_CHANNELS_HIERARCHY_ID, BrowserFolders.BROWSER_CHANNELS_FOLDER_ID
      );
      this.removeSubFolders(channelsFolder, rootFolder, false);
      if (!BrowserDaemonRegistry.getInstance().isSerialSync()) {
         SimpleFolder wapBookmarksFolder = (SimpleFolder)(new Object(
            BrowserFolders.BROWSER_FAMILY,
            BrowserFolders.BROWSER_WAP_BOOKMARKS_FOLDER_ID,
            BrowserResources.getString(570),
            BrowserFolders.BROWSER_FOLDER_COLLECTION_CLASS,
            bookmarksFolder,
            1
         ));
         bookmarksFolder.putFolder(wapBookmarksFolder);
         SimpleFolder mdsBookmarksFolder = (SimpleFolder)(new Object(
            BrowserFolders.BROWSER_FAMILY,
            BrowserFolders.BROWSER_MDS_BOOKMARKS_FOLDER_ID,
            BrowserResources.getString(568),
            BrowserFolders.BROWSER_FOLDER_COLLECTION_CLASS,
            bookmarksFolder,
            1
         ));
         bookmarksFolder.putFolder(mdsBookmarksFolder);
      }

      this._collectionListenerManager.fireReset(this);
      return true;
   }

   @Override
   public final SyncObject[] getSyncObjects() {
      Vector syncObjects = (Vector)(new Object());
      SimpleFolder messagesFolder = (SimpleFolder)FolderHierarchies.getFolder(
         BrowserFolders.RIM_BROWSER_MESSAGES_HIERARCHY_ID, BrowserFolders.BROWSER_MESSAGES_FOLDER_ID
      );
      this.addSubFolders(messagesFolder, syncObjects);
      SimpleFolder bookmarksFolder = (SimpleFolder)FolderHierarchies.getFolder(
         BrowserFolders.RIM_BROWSER_BOOKMARKS_HIERARCHY_ID, BrowserFolders.BROWSER_BOOKMARKS_FOLDER_ID
      );
      this.addSubFolders(bookmarksFolder, syncObjects);
      SimpleFolder channelsFolder = (SimpleFolder)FolderHierarchies.getFolder(
         BrowserFolders.RIM_BROWSER_CHANNELS_HIERARCHY_ID, BrowserFolders.BROWSER_CHANNELS_FOLDER_ID
      );
      this.addSubFolders(channelsFolder, syncObjects);
      SyncObject[] objects = new Object[syncObjects.size()];

      for (int i = 0; i < syncObjects.size(); i++) {
         objects[i] = (SyncObject)syncObjects.elementAt(i);
      }

      return objects;
   }

   @Override
   public final SyncObject getSyncObject(int uid) {
      Folder folder = FolderHierarchies.getFolder(BrowserFolders.RIM_BROWSER_BOOKMARKS_HIERARCHY_ID, BrowserFolders.makeLUID(uid));
      if (folder != null && folder.getFriendlyName() != null) {
         Folder parentFolder = folder.getParentFolder();
         return parentFolder == null ? null : new BrowserFolderSyncObject(uid, (int)parentFolder.getLUID(), folder.getFriendlyName());
      } else {
         return null;
      }
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
      SimpleFolder messagesFolder = (SimpleFolder)FolderHierarchies.getFolder(
         BrowserFolders.RIM_BROWSER_MESSAGES_HIERARCHY_ID, BrowserFolders.BROWSER_MESSAGES_FOLDER_ID
      );
      int count = this.countSubFolders(messagesFolder);
      SimpleFolder bookmarksFolder = (SimpleFolder)FolderHierarchies.getFolder(
         BrowserFolders.RIM_BROWSER_BOOKMARKS_HIERARCHY_ID, BrowserFolders.BROWSER_BOOKMARKS_FOLDER_ID
      );
      count += this.countSubFolders(bookmarksFolder);
      SimpleFolder channelsFolder = (SimpleFolder)FolderHierarchies.getFolder(
         BrowserFolders.RIM_BROWSER_CHANNELS_HIERARCHY_ID, BrowserFolders.BROWSER_CHANNELS_FOLDER_ID
      );
      return count + this.countSubFolders(channelsFolder);
   }

   @Override
   public final int getSyncVersion() {
      return SYNC_VERSION;
   }

   @Override
   public final String getSyncName() {
      return BROWSER_FOLDERS_DB_NAME;
   }

   @Override
   public final String getSyncName(Locale locale) {
      ResourceBundle bundle = BrowserResources.getResourceBundle().getBundle(locale);
      return bundle != null ? bundle.getString(864) : null;
   }

   @Override
   public final SyncConverter getSyncConverter() {
      return this._converter;
   }

   @Override
   public final void beginTransaction() {
      BrowserDaemonRegistry.getInstance().setFoldersModified(true);
   }

   @Override
   public final void endTransaction() {
   }

   @Override
   public final int getSyncPriority() {
      return 1;
   }

   @Override
   public final int getDependencyLevel() {
      return 1;
   }

   private final void removeSubFolders(SimpleFolder folder, SimpleFolder rootFolder, boolean removeThisFolder) {
      Enumeration e = folder.getSubFolders();

      while (e.hasMoreElements()) {
         SimpleFolder subFolder = (SimpleFolder)e.nextElement();
         this.removeSubFolders(subFolder, rootFolder, true);
      }

      if (removeThisFolder) {
         SimpleFolder parentFolder = (SimpleFolder)folder.getParentFolder();
         parentFolder.removeSubFolder(folder);
         BrowserDaemonRegistry.broadCastEvent(201, new BrowserFolderSyncObject((int)folder.getLUID(), (int)parentFolder.getLUID(), folder.getFriendlyName()));
         ReadableList contents = (ReadableList)folder.getContainedItems();
         if (contents.size() > 0) {
            folder.setFriendlyName(null);
            folder.setParentFolder(rootFolder);
            rootFolder.putFolder(folder);
         }
      }
   }

   private final int countSubFolders(Folder folder) {
      int count = 0;
      Enumeration e = folder.getSubFolders();

      while (e.hasMoreElements()) {
         count = ++count + this.countSubFolders((Folder)e.nextElement());
      }

      return count;
   }

   private final void addSubFolders(Folder folder, Vector vector) {
      Enumeration e = folder.getSubFolders();

      while (e.hasMoreElements()) {
         Folder subFolder = (Folder)e.nextElement();
         Folder parentFolder = subFolder.getParentFolder();
         if (parentFolder != null) {
            vector.addElement(new BrowserFolderSyncObject((int)subFolder.getLUID(), (int)parentFolder.getLUID(), subFolder.getFriendlyName()));
            this.addSubFolders(subFolder, vector);
         }
      }
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
