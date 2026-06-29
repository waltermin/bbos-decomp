package net.rim.device.apps.internal.browser.store;

import java.util.Enumeration;
import java.util.Vector;
import net.rim.device.api.collection.IntRangedActionTarget;
import net.rim.device.api.collection.ReadableList;
import net.rim.device.api.collection.WritableSet;
import net.rim.device.api.i18n.Locale;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.synchronization.SyncCollection;
import net.rim.device.api.synchronization.SyncCollectionStatistics;
import net.rim.device.api.synchronization.SyncCollectionStatisticsManager;
import net.rim.device.api.synchronization.SyncConverter;
import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.apps.api.messaging.Folder;
import net.rim.device.apps.api.messaging.FolderHierarchies;
import net.rim.device.apps.api.messaging.util.SimpleFolder;
import net.rim.device.apps.internal.browser.core.BrowserDaemonRegistry;
import net.rim.device.apps.internal.browser.page.BrowserPageModel;
import net.rim.device.apps.internal.browser.resources.BrowserResources;
import net.rim.device.apps.internal.browser.util.Asserts;

public class BrowserRepositoryCollection implements SyncCollection, SyncCollectionStatistics {
   private SyncConverter _converter = new BrowserRepositoryConverter();
   private String _databaseName;
   private Folder _folder;
   private long _hierarchy;
   private int _localizationId;
   public static String BROWSER_BOOKMARKS_DB_NAME = "Browser Bookmarks";
   public static String BROWSER_MESSAGES_DB_NAME = "Browser Messages";
   private static int SYNC_VERSION = 1;

   public BrowserRepositoryCollection(String databaseName, int localizationId) {
      Asserts.productionAssert(databaseName.equals(BROWSER_MESSAGES_DB_NAME) || databaseName.equals(BROWSER_BOOKMARKS_DB_NAME));
      this._databaseName = databaseName;
      this._localizationId = localizationId;
      if (databaseName.equals(BROWSER_BOOKMARKS_DB_NAME)) {
         this._hierarchy = BrowserFolders.RIM_BROWSER_BOOKMARKS_HIERARCHY_ID;
         this._folder = FolderHierarchies.getFolder(this._hierarchy, BrowserFolders.BROWSER_BOOKMARKS_FOLDER_ID);
      } else {
         this._hierarchy = BrowserFolders.RIM_BROWSER_MESSAGES_HIERARCHY_ID;
         this._folder = FolderHierarchies.getFolder(this._hierarchy, BrowserFolders.BROWSER_MESSAGES_FOLDER_ID);
      }
   }

   @Override
   public boolean addSyncObject(SyncObject object) {
      return this.addSyncObjectInternal(object);
   }

   private boolean addSyncObjectInternal(SyncObject object) {
      BrowserPageModel browserPageModel = (BrowserPageModel)object;
      long parentFolderLUID = browserPageModel.getFolderId();
      SimpleFolder parentFolder = (SimpleFolder)FolderHierarchies.getFolder(this._hierarchy, parentFolderLUID);
      if (parentFolder == null) {
         SimpleFolder rootFolder = SimpleFolder.getInstance(BrowserFolders.BROWSER_FAMILY, BrowserFolders.RIM_BROWSER_BOOKMARKS_HIERARCHY_ID);
         parentFolder = new SimpleFolder(BrowserFolders.BROWSER_FAMILY, parentFolderLUID, null, BrowserFolders.BROWSER_FOLDER_COLLECTION_CLASS, rootFolder, 1);
         rootFolder.putFolder(parentFolder);
      }

      WritableSet collection = (WritableSet)parentFolder.getContainedItems();
      collection.add(browserPageModel);
      return true;
   }

   @Override
   public boolean updateSyncObject(SyncObject oldObject, SyncObject newObject) {
      if (this.removeSyncObjectInternal(oldObject) && this.addSyncObjectInternal(newObject)) {
         this.clearSyncObjectDirty(newObject);
         return true;
      } else {
         return false;
      }
   }

   @Override
   public boolean removeSyncObject(SyncObject object) {
      return this.removeSyncObjectInternal(object);
   }

   private boolean removeSyncObjectInternal(SyncObject object) {
      BrowserPageModel browserPageModel = (BrowserPageModel)object;
      long parentFolderLUID = browserPageModel.getFolderId();
      SimpleFolder parentFolder = (SimpleFolder)FolderHierarchies.getFolder(this._hierarchy, parentFolderLUID);
      if (parentFolder != null) {
         WritableSet collection = (WritableSet)parentFolder.getContainedItems();
         collection.remove(object);
         return true;
      } else {
         return false;
      }
   }

   @Override
   public boolean removeAllSyncObjects() {
      this.removeSubItems(this._folder);
      return true;
   }

   private void removeSubItems(Folder folder) {
      WritableSet collection = (WritableSet)folder.getContainedItems();
      ((IntRangedActionTarget)collection).apply(Integer.MIN_VALUE, Integer.MAX_VALUE, -198247372487919817L, null);
      collection.removeAll();
      Enumeration e = folder.getSubFolders();

      while (e.hasMoreElements()) {
         this.removeSubItems((Folder)e.nextElement());
      }
   }

   @Override
   public SyncObject[] getSyncObjects() {
      Vector syncObjects = new Vector();
      this.addSubItems(this._folder, syncObjects);
      SyncObject[] objects = new SyncObject[syncObjects.size()];
      syncObjects.copyInto(objects);
      return objects;
   }

   @Override
   public SyncObject getSyncObject(int uid) {
      return this.findSyncObject(this._folder, uid);
   }

   private void addSubItems(Folder folder, Vector vector) {
      synchronized (FolderHierarchies.getLockObject()) {
         ReadableList collection = (ReadableList)folder.getContainedItems();

         for (int i = 0; i < collection.size(); i++) {
            vector.addElement(collection.getAt(i));
         }
      }

      Enumeration e = folder.getSubFolders();

      while (e.hasMoreElements()) {
         this.addSubItems((Folder)e.nextElement(), vector);
      }
   }

   private SyncObject findSyncObject(Folder folder, int uid) {
      synchronized (FolderHierarchies.getLockObject()) {
         ReadableList collection = (ReadableList)folder.getContainedItems();
         int numElements = collection.size();

         for (int i = 0; i < numElements; i++) {
            SyncObject element = (SyncObject)collection.getAt(i);
            if (element.getUID() == uid) {
               return element;
            }
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
   public boolean isSyncObjectDirty(SyncObject object) {
      return false;
   }

   @Override
   public void setSyncObjectDirty(SyncObject object) {
   }

   @Override
   public void clearSyncObjectDirty(SyncObject object) {
   }

   @Override
   public int getSyncObjectCount() {
      return this.countSubItems(this._folder);
   }

   private int countSubItems(Folder folder) {
      int count = 0;
      count += ((ReadableList)folder.getContainedItems()).size();
      Enumeration e = folder.getSubFolders();

      while (e.hasMoreElements()) {
         count += this.countSubItems((Folder)e.nextElement());
      }

      return count;
   }

   @Override
   public int getSyncVersion() {
      return SYNC_VERSION;
   }

   @Override
   public String getSyncName() {
      return this._databaseName;
   }

   @Override
   public String getSyncName(Locale locale) {
      ResourceBundle bundle = BrowserResources.getResourceBundle().getBundle(locale);
      return bundle != null ? bundle.getString(this._localizationId) : null;
   }

   @Override
   public SyncConverter getSyncConverter() {
      return this._converter;
   }

   @Override
   public void beginTransaction() {
      BrowserDaemonRegistry.getInstance().setFoldersModified(true);
   }

   @Override
   public void endTransaction() {
   }

   @Override
   public synchronized int getSyncCollectionSize() {
      return SyncCollectionStatisticsManager.getSyncCollectionSize(this);
   }
}
