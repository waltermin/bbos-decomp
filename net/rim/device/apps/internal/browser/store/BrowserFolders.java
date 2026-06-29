package net.rim.device.apps.internal.browser.store;

import java.util.Enumeration;
import net.rim.device.api.collection.IntRangedActionTarget;
import net.rim.device.api.collection.ReadableList;
import net.rim.device.api.collection.WritableSet;
import net.rim.device.api.crypto.RandomSource;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.apps.api.messaging.FolderHierarchies;
import net.rim.device.apps.api.messaging.FolderMerge;
import net.rim.device.apps.api.messaging.util.SimpleFolder;
import net.rim.device.apps.api.utility.lowMemory.PurgeManager;
import net.rim.device.apps.internal.browser.bookmark.Bookmarks;
import net.rim.device.apps.internal.browser.core.BrowserDaemonRegistry;
import net.rim.device.apps.internal.browser.page.PageModel;
import net.rim.device.apps.internal.browser.resources.BrowserResources;

public final class BrowserFolders {
   private static final long UPPER_BITS = -5045751545258311680L;
   public static final long RIM_BROWSER_BOOKMARKS_HIERARCHY_ID = makeLUID(1039255463);
   public static final long RIM_BROWSER_MESSAGES_HIERARCHY_ID = makeLUID(-1772343459);
   public static final long RIM_BROWSER_CHANNELS_HIERARCHY_ID = makeLUID(340675538);
   public static final long BROWSER_MESSAGES_FOLDER_ID = makeLUID(-969055791);
   public static final long BROWSER_BOOKMARKS_FOLDER_ID = makeLUID(450403296);
   public static final long BROWSER_CHANNELS_FOLDER_ID = makeLUID(1330623133);
   public static final long BROWSER_WAPPUSH_FOLDER_ID = makeLUID(-1165862434);
   public static final long BROWSER_OFFLINE_QUEUES_FOLDER_ID = makeLUID(-1594407800);
   public static final long BROWSER_WAP_BOOKMARKS_FOLDER_ID = makeLUID(-1978145278);
   public static final long BROWSER_MDS_BOOKMARKS_FOLDER_ID = makeLUID(745089672);
   public static String BROWSER_FOLDER_COLLECTION_CLASS = "net.rim.device.apps.api.messaging.util.PersistedSortedCollection";
   public static long BROWSER_FAMILY = 1009835367349917143L;

   public static final void registerOnceOnSystemStart() {
      SimpleFolder bookmarksHierarchy = SimpleFolder.getInstance(BROWSER_FAMILY, RIM_BROWSER_BOOKMARKS_HIERARCHY_ID);
      SimpleFolder messagesHierarchy = SimpleFolder.getInstance(BROWSER_FAMILY, RIM_BROWSER_MESSAGES_HIERARCHY_ID);
      SimpleFolder channelsHierarchy = SimpleFolder.getInstance(BROWSER_FAMILY, RIM_BROWSER_CHANNELS_HIERARCHY_ID);
      SimpleFolder messagesFolder = null;
      SimpleFolder bookmarksFolder = null;
      SimpleFolder channelsFolder = null;
      SimpleFolder wappushFolder = null;
      SimpleFolder offlineQueuesFolder = null;
      SimpleFolder wapBookmarksFolder = null;
      SimpleFolder mdsBookmarksFolder = null;
      if (bookmarksHierarchy == null) {
         bookmarksHierarchy = SimpleFolder.createInstance(BROWSER_FAMILY, RIM_BROWSER_BOOKMARKS_HIERARCHY_ID, BrowserResources.getString(157));
      } else {
         bookmarksFolder = (SimpleFolder)bookmarksHierarchy.getFolder(BROWSER_BOOKMARKS_FOLDER_ID);
         wapBookmarksFolder = (SimpleFolder)bookmarksHierarchy.getFolder(BROWSER_WAP_BOOKMARKS_FOLDER_ID);
         mdsBookmarksFolder = (SimpleFolder)bookmarksHierarchy.getFolder(BROWSER_MDS_BOOKMARKS_FOLDER_ID);
      }

      if (bookmarksFolder == null) {
         bookmarksFolder = new SimpleFolder(
            BROWSER_FAMILY, BROWSER_BOOKMARKS_FOLDER_ID, BrowserResources.getString(144), BROWSER_FOLDER_COLLECTION_CLASS, bookmarksHierarchy, 1
         );
         bookmarksHierarchy.putFolder(bookmarksFolder);
      }

      if (wapBookmarksFolder == null) {
         wapBookmarksFolder = new SimpleFolder(
            BROWSER_FAMILY, BROWSER_WAP_BOOKMARKS_FOLDER_ID, BrowserResources.getString(570), BROWSER_FOLDER_COLLECTION_CLASS, bookmarksFolder, 1
         );
         bookmarksFolder.putFolder(wapBookmarksFolder);
      }

      if (mdsBookmarksFolder == null) {
         mdsBookmarksFolder = new SimpleFolder(
            BROWSER_FAMILY, BROWSER_MDS_BOOKMARKS_FOLDER_ID, BrowserResources.getString(568), BROWSER_FOLDER_COLLECTION_CLASS, bookmarksFolder, 1
         );
         bookmarksFolder.putFolder(mdsBookmarksFolder);
      }

      if (messagesHierarchy == null) {
         messagesHierarchy = SimpleFolder.createInstance(BROWSER_FAMILY, RIM_BROWSER_MESSAGES_HIERARCHY_ID, BrowserResources.getString(122), 1);
      } else {
         messagesFolder = (SimpleFolder)messagesHierarchy.getFolder(BROWSER_MESSAGES_FOLDER_ID);
         wappushFolder = (SimpleFolder)messagesHierarchy.getFolder(BROWSER_WAPPUSH_FOLDER_ID);
         offlineQueuesFolder = (SimpleFolder)messagesHierarchy.getFolder(BROWSER_OFFLINE_QUEUES_FOLDER_ID);
      }

      if (messagesFolder == null) {
         messagesFolder = new SimpleFolder(
            BROWSER_FAMILY, BROWSER_MESSAGES_FOLDER_ID, BrowserResources.getResourceBundle(), 123, BROWSER_FOLDER_COLLECTION_CLASS, messagesHierarchy, 1
         );
         messagesHierarchy.putFolder(messagesFolder);
      }

      if (wappushFolder == null) {
         wappushFolder = new SimpleFolder(
            BROWSER_FAMILY, BROWSER_WAPPUSH_FOLDER_ID, BrowserResources.getResourceBundle(), 586, BROWSER_FOLDER_COLLECTION_CLASS, messagesHierarchy, 1
         );
         messagesHierarchy.putFolder(wappushFolder);
      }

      if (offlineQueuesFolder == null) {
         offlineQueuesFolder = new SimpleFolder(
            BROWSER_FAMILY, BROWSER_OFFLINE_QUEUES_FOLDER_ID, BrowserResources.getString(705), BROWSER_FOLDER_COLLECTION_CLASS, messagesHierarchy
         );
         messagesHierarchy.putFolder(offlineQueuesFolder);
      }

      if (channelsHierarchy == null) {
         channelsHierarchy = SimpleFolder.createInstance(BROWSER_FAMILY, RIM_BROWSER_CHANNELS_HIERARCHY_ID, BrowserResources.getString(306));
      } else {
         channelsFolder = (SimpleFolder)channelsHierarchy.getFolder(BROWSER_CHANNELS_FOLDER_ID);
      }

      if (channelsFolder == null) {
         channelsFolder = new SimpleFolder(
            BROWSER_FAMILY, BROWSER_CHANNELS_FOLDER_ID, BrowserResources.getString(307), BROWSER_FOLDER_COLLECTION_CLASS, channelsHierarchy, 1
         );
         channelsHierarchy.putFolder(channelsFolder);
      }

      if (bookmarksHierarchy != null
         && messagesHierarchy != null
         && channelsHierarchy != null
         && bookmarksFolder != null
         && messagesFolder != null
         && channelsFolder != null) {
         FolderHierarchies.registerFolderHierarchy(bookmarksHierarchy);
         FolderHierarchies.registerFolderHierarchy(messagesHierarchy);
         FolderHierarchies.registerFolderHierarchy(channelsHierarchy);
         FolderMerge.registerMergedFolder(-5581791943352753293L, messagesFolder);
         FolderMerge.registerMergedFolder(2993144521330132876L, messagesFolder);
         FolderMerge.registerMergedFolder(-5581791943352753293L, wappushFolder);
         FolderMerge.registerMergedFolder(2993144521330132876L, wappushFolder);
         FolderMerge.registerMergedFolder(7509894771240321003L, wappushFolder);
         PurgeManager.getInstance().addCollection(messagesFolder.getContainedItems());
         PurgeManager.getInstance().addCollection(wappushFolder.getContainedItems());
         messagesFolder.initializeOnSystemStart();
         wappushFolder.initializeOnSystemStart();
         offlineQueuesFolder.initializeOnSystemStart();
         bookmarksFolder.initializeOnSystemStart();
         channelsFolder.initializeOnSystemStart();
         BrowserPersistentContentListener listener = new BrowserPersistentContentListener();
         PersistentContent.addListener(listener);
      }
   }

   public static final long makeLUID(int uid) {
      return uid | -5045751545258311680L;
   }

   public static final long makeUniqueLUID(SimpleFolder folder) {
      long luid = makeLUID(RandomSource.getInt());

      while (!isUniqueLUID(luid, folder)) {
         luid = makeLUID(RandomSource.getInt());
      }

      return luid;
   }

   private static final boolean isUniqueLUID(long luid, SimpleFolder folder) {
      while (folder != null) {
         if (folder.getFolder(luid) != null) {
            return false;
         }

         folder = (SimpleFolder)folder.getParentFolder();
      }

      return true;
   }

   public static final void cleanupFolders() {
      SimpleFolder rootFolder = SimpleFolder.getInstance(BROWSER_FAMILY, RIM_BROWSER_BOOKMARKS_HIERARCHY_ID);
      Enumeration subFolders = rootFolder.getSubFolders();

      while (subFolders.hasMoreElements()) {
         SimpleFolder subFolder = (SimpleFolder)subFolders.nextElement();
         if (subFolder.getFriendlyName() == null) {
            deleteFolder(subFolder);
         }
      }
   }

   public static final void updateFolders() {
      synchronized (FolderHierarchies.getLockObject()) {
         SimpleFolder bookmarksHierarchy = SimpleFolder.getInstance(BROWSER_FAMILY, RIM_BROWSER_BOOKMARKS_HIERARCHY_ID);
         if (bookmarksHierarchy != null) {
            SimpleFolder bookmarksFolder = (SimpleFolder)bookmarksHierarchy.getFolder(BROWSER_BOOKMARKS_FOLDER_ID);
            if (bookmarksFolder != null) {
               SimpleFolder wapBookmarksFolder = (SimpleFolder)bookmarksHierarchy.getFolder(BROWSER_WAP_BOOKMARKS_FOLDER_ID);
               SimpleFolder mdsBookmarksFolder = (SimpleFolder)bookmarksHierarchy.getFolder(BROWSER_MDS_BOOKMARKS_FOLDER_ID);
               if (wapBookmarksFolder == null || mdsBookmarksFolder == null) {
                  boolean pre360 = true;
                  long oldMdsFolderId = makeLUID("BlackBerry Browser".hashCode());
                  Enumeration subFolders = bookmarksFolder.getSubFolders();

                  while (subFolders.hasMoreElements()) {
                     SimpleFolder subFolder = (SimpleFolder)subFolders.nextElement();
                     long subFolderId = subFolder.getLUID();
                     if (subFolderId == oldMdsFolderId) {
                        pre360 = false;
                        break;
                     }

                     if (subFolderId != BROWSER_WAP_BOOKMARKS_FOLDER_ID && subFolderId != BROWSER_MDS_BOOKMARKS_FOLDER_ID) {
                        String subFolderName = subFolder.getFriendlyName();
                        String subFolderParentName = bookmarksFolder.getFriendlyName();
                        String subFolderFullName = subFolderParentName + subFolderName;
                        if (subFolderId == makeLUID(subFolderFullName.hashCode())) {
                           continue;
                        }

                        pre360 = false;
                        break;
                     }

                     pre360 = false;
                     break;
                  }

                  if (pre360) {
                     wapBookmarksFolder = new SimpleFolder(
                        BROWSER_FAMILY, BROWSER_WAP_BOOKMARKS_FOLDER_ID, BrowserResources.getString(570), BROWSER_FOLDER_COLLECTION_CLASS, bookmarksFolder, 1
                     );
                     mdsBookmarksFolder = new SimpleFolder(
                        BROWSER_FAMILY, BROWSER_MDS_BOOKMARKS_FOLDER_ID, BrowserResources.getString(568), BROWSER_FOLDER_COLLECTION_CLASS, bookmarksFolder, 1
                     );
                     copyFolderContents(bookmarksFolder, wapBookmarksFolder);
                     copyFolderContents(bookmarksFolder, mdsBookmarksFolder);
                     deleteFolderContents(bookmarksFolder, false);
                     bookmarksFolder.putFolder(wapBookmarksFolder);
                     bookmarksFolder.putFolder(mdsBookmarksFolder);
                  } else {
                     if (wapBookmarksFolder == null) {
                        wapBookmarksFolder = new SimpleFolder(
                           BROWSER_FAMILY,
                           BROWSER_WAP_BOOKMARKS_FOLDER_ID,
                           BrowserResources.getString(570),
                           BROWSER_FOLDER_COLLECTION_CLASS,
                           bookmarksFolder,
                           1
                        );
                        bookmarksFolder.putFolder(wapBookmarksFolder);
                     }

                     if (mdsBookmarksFolder == null) {
                        mdsBookmarksFolder = new SimpleFolder(
                           BROWSER_FAMILY,
                           BROWSER_MDS_BOOKMARKS_FOLDER_ID,
                           BrowserResources.getString(568),
                           BROWSER_FOLDER_COLLECTION_CLASS,
                           bookmarksFolder,
                           1
                        );
                        bookmarksFolder.putFolder(mdsBookmarksFolder);
                     }

                     subFolders = bookmarksFolder.getSubFolders();

                     while (subFolders.hasMoreElements()) {
                        SimpleFolder subFolder = (SimpleFolder)subFolders.nextElement();
                        long subFolderId = subFolder.getLUID();
                        if (subFolderId == oldMdsFolderId) {
                           copyFolderContents(subFolder, mdsBookmarksFolder);
                           deleteFolder(subFolder);
                        } else if (subFolderId != BROWSER_WAP_BOOKMARKS_FOLDER_ID && subFolderId != BROWSER_MDS_BOOKMARKS_FOLDER_ID) {
                           String subFolderName = subFolder.getFriendlyName();
                           String subFolderParentName = bookmarksFolder.getFriendlyName();
                           String subFolderFullName = subFolderParentName + subFolderName;
                           if (subFolderId != makeLUID(subFolderFullName.hashCode())) {
                              copyFolderContents(subFolder, wapBookmarksFolder);
                              deleteFolder(subFolder);
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   public static final void copyFolderContents(SimpleFolder srcFolder, SimpleFolder destFolder) {
      Enumeration subFolders = srcFolder.getSubFolders();

      while (subFolders.hasMoreElements()) {
         SimpleFolder oldSubFolder = (SimpleFolder)subFolders.nextElement();
         String newSubFolderName = oldSubFolder.getFriendlyName();
         SimpleFolder newSubFolder = null;
         Enumeration destSubFolders = destFolder.getSubFolders();

         while (destSubFolders.hasMoreElements()) {
            SimpleFolder destSubFolder = (SimpleFolder)destSubFolders.nextElement();
            if (destSubFolder.getFriendlyName().equals(newSubFolderName)) {
               newSubFolder = destSubFolder;
               break;
            }
         }

         if (newSubFolder == null) {
            long newSubFolderId = makeUniqueLUID(destFolder);
            newSubFolder = new SimpleFolder(BROWSER_FAMILY, newSubFolderId, newSubFolderName, BROWSER_FOLDER_COLLECTION_CLASS, destFolder, 1);
            destFolder.putFolder(newSubFolder);
         }

         copyFolderContents(oldSubFolder, newSubFolder);
      }

      ReadableList collection = (ReadableList)srcFolder.getContainedItems();

      for (int i = 0; i < collection.size(); i++) {
         PageModel pageModel = (PageModel)collection.getAt(i);
         if (!pageModel.isHomePage()) {
            Bookmarks.addBookmarkToFolder(pageModel, destFolder);
         }
      }
   }

   public static final void deleteFolder(SimpleFolder folder) {
      deleteFolderContents(folder, true);
   }

   public static final void deleteFolderContents(SimpleFolder folder, boolean deleteRoot) {
      Enumeration subFolders = folder.getSubFolders();

      while (subFolders.hasMoreElements()) {
         deleteFolder((SimpleFolder)subFolders.nextElement());
      }

      WritableSet writableSet = (WritableSet)folder.getContainedItems();
      ReadableList readableList = (ReadableList)writableSet;
      ((IntRangedActionTarget)writableSet).apply(Integer.MIN_VALUE, Integer.MAX_VALUE, -198247372487919817L, null);
      int numberOfBookmarks = readableList.size();

      for (int i = numberOfBookmarks - 1; i >= 0; i--) {
         if (readableList.getAt(i) instanceof PageModel) {
            Bookmarks.deleteBookmark((PageModel)readableList.getAt(i), folder);
         }
      }

      if (deleteRoot) {
         SimpleFolder parentFolder = (SimpleFolder)folder.getParentFolder();
         parentFolder.removeSubFolder(folder);
         BrowserDaemonRegistry.broadCastEvent(
            201, new BrowserFolderSyncObject((int)folder.getLUID(), (int)folder.getParentFolder().getLUID(), folder.getFriendlyName())
         );
      }
   }

   public static final void addSubFolder(SimpleFolder parent, SimpleFolder child) {
      parent.putFolder(child);
      BrowserDaemonRegistry.broadCastEvent(200, new BrowserFolderSyncObject((int)child.getLUID(), (int)parent.getLUID(), child.getFriendlyName()));
   }

   public static final void renameFolder(SimpleFolder folder, String newName) {
      folder.setFriendlyName(newName);
      BrowserDaemonRegistry.broadCastEvent(202, new BrowserFolderSyncObject((int)folder.getLUID(), (int)folder.getParentFolder().getLUID(), newName));
   }
}
