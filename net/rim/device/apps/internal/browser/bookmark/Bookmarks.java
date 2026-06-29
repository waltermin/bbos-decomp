package net.rim.device.apps.internal.browser.bookmark;

import java.util.Enumeration;
import java.util.Vector;
import net.rim.device.api.collection.ReadableList;
import net.rim.device.api.collection.WritableSet;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.DateTimeUtilities;
import net.rim.device.apps.api.messaging.Folder;
import net.rim.device.apps.api.messaging.FolderHierarchies;
import net.rim.device.apps.internal.browser.core.BrowserDaemonRegistry;
import net.rim.device.apps.internal.browser.options.BrowserConfigRecord;
import net.rim.device.apps.internal.browser.page.BrowserPageModel;
import net.rim.device.apps.internal.browser.page.PageModel;
import net.rim.device.apps.internal.browser.stack.CacheNode;
import net.rim.device.apps.internal.browser.stack.ModelResult;
import net.rim.device.apps.internal.browser.stack.RawDataCache;
import net.rim.device.apps.internal.browser.store.BrowserFolders;
import net.rim.vm.Array;

public final class Bookmarks {
   public static boolean _debugAutoUpdate = false;

   public static final void addBookmarkToFolder(
      String title,
      String url,
      Folder destFolder,
      boolean homePage,
      String iconUrl,
      byte updateFlags,
      int updateStart,
      int updatePeriod,
      BrowserConfigRecord browserConfig
   ) {
      addBookmarkToFolder(title, url, destFolder, homePage, -1, iconUrl, updateFlags, updateStart, updatePeriod, browserConfig);
   }

   public static final void addBookmarkToFolder(
      String title,
      String url,
      Folder destFolder,
      boolean homePage,
      int position,
      String iconUrl,
      byte updateFlags,
      int updateStart,
      int updatePeriod,
      BrowserConfigRecord browserConfig
   ) {
      ModelResult modelResult = new ModelResult(url, 8449, null);
      modelResult.setHomePage(homePage);
      if (browserConfig != null) {
         modelResult.setConfigUID(browserConfig.getUid());
         modelResult.setConfigType(browserConfig.getPropertyAsInt(12));
         modelResult.setTransportCID(browserConfig.getPropertyAsString(3));
      }

      addBookmarkToFolderInternal(title, modelResult, destFolder, homePage, position, iconUrl, updateFlags, updateStart, updatePeriod, 4);
   }

   private static final void addBookmarkToFolderInternal(
      String title,
      ModelResult modelResult,
      Folder destFolder,
      boolean homePage,
      int position,
      String iconUrl,
      byte updateFlags,
      int updateStart,
      int updatePeriod,
      int pageStatus
   ) {
      if (title == null || title.length() == 0) {
         title = modelResult.getURL();
      }

      synchronized (FolderHierarchies.getLockObject()) {
         WritableSet bookmarksItemsW = (WritableSet)destFolder.getContainedItems();
         ReadableList bookmarksItemsR = (ReadableList)bookmarksItemsW;
         long newBookmarkLUID = BrowserPageModel.makeLUID();
         int numItems = bookmarksItemsR.size();
         if (position < 0 || position > numItems) {
            position = numItems;
         }

         int newKey;
         if (position == 0) {
            newKey = 0;
         } else {
            newKey = (int)((PageModel)bookmarksItemsR.getAt(position - 1)).getTimeStamp() + 1;
         }

         BrowserPageModel node = new BrowserPageModel(
            newBookmarkLUID, newKey, pageStatus, title, modelResult, destFolder.getLUID(), homePage, iconUrl, updateFlags, updateStart, updatePeriod
         );
         if (!node.checkCrypt(true, true)) {
            node.reCrypt(true, true);
         }

         int numToFix = numItems - position;
         if (numToFix > 0 && newKey < ((PageModel)bookmarksItemsR.getAt(position)).getTimeStamp()) {
            numToFix = 0;
         }

         if (numToFix == 0) {
            bookmarksItemsW.add(node);
         } else {
            PageModel[] nodesToFix = new PageModel[numToFix];
            bookmarksItemsR.getAt(position, numToFix, nodesToFix, 0);

            for (int i = numToFix - 1; i >= 0; i--) {
               bookmarksItemsW.remove(nodesToFix[i]);
            }

            newKey++;

            for (int i = 0; i < numToFix; i++) {
               nodesToFix[i].setTimeStamp(newKey++);
               bookmarksItemsW.add(nodesToFix[i]);
               BrowserDaemonRegistry.broadCastEvent(102, nodesToFix[i]);
            }

            bookmarksItemsW.add(node);
         }

         BrowserDaemonRegistry.broadCastEvent(100, node);
      }
   }

   public static final void addBookmarkToFolder(PageModel node, Folder destFolder) {
      addBookmarkToFolder(node, destFolder, -1);
   }

   static final void addBookmarkToFolder(PageModel node, Folder destFolder, int position) {
      addBookmarkToFolderInternal(
         node.getTitle(),
         node.getModelResult(),
         destFolder,
         node.isHomePage(),
         position,
         node.getIconUrl(),
         node.getUpdateFlags(),
         node.getUpdateStart(),
         node.getUpdatePeriod(),
         node.getStatus()
      );
   }

   private static final void removeOfflineContent(PageModel node) {
      String url = node.getUrl();
      if (url != null) {
         RawDataCache cache = BrowserDaemonRegistry.getInstance().getRawDataCache();
         CacheNode cacheNode = cache.getCacheNode(url);
         if (cacheNode != null && cacheNode.getAvailableOffline()) {
            cache.remove(url, true, false);
            cache.commit();
         }
      }
   }

   public static final void deleteBookmark(PageModel node, Folder currentFolder) {
      if (currentFolder != null) {
         removeOfflineContent(node);
         synchronized (FolderHierarchies.getLockObject()) {
            ((WritableSet)currentFolder.getContainedItems()).remove(node);
            BrowserDaemonRegistry.broadCastEvent(101, node);
         }
      }
   }

   public static final void moveBookmarkUpOrDown(PageModel node, Folder currentFolder, int amount) {
      if (currentFolder != null && amount != 0) {
         synchronized (FolderHierarchies.getLockObject()) {
            WritableSet bookmarksItemsW = (WritableSet)currentFolder.getContainedItems();
            ReadableList bookmarksItemsR = (ReadableList)bookmarksItemsW;
            int keyToMove = bookmarksItemsR.getIndex(node);
            int numItems = bookmarksItemsR.size();
            int newPosition = keyToMove + amount;
            if (newPosition < 0) {
               newPosition = 0;
            } else if (newPosition >= numItems) {
               newPosition = numItems - 1;
            }

            if (newPosition != keyToMove) {
               int numToFix;
               int newKey;
               if (newPosition > keyToMove) {
                  numToFix = newPosition - keyToMove;
                  newKey = keyToMove;
               } else {
                  numToFix = keyToMove - newPosition;
                  newKey = newPosition;
               }

               if (newKey != 0) {
                  newKey = (int)((PageModel)bookmarksItemsR.getAt(newKey - 1)).getTimeStamp() + 1;
               }

               bookmarksItemsW.remove(node);
               PageModel[] nodesToFix = new PageModel[numToFix];
               bookmarksItemsR.getAt(Math.min(keyToMove, newPosition), numToFix, nodesToFix, 0);

               for (int i = numToFix - 1; i >= 0; i--) {
                  bookmarksItemsW.remove(nodesToFix[i]);
               }

               if (newPosition < keyToMove) {
                  node.setTimeStamp(newKey++);
               }

               for (int i = 0; i < numToFix; i++) {
                  nodesToFix[i].setTimeStamp(newKey++);
                  bookmarksItemsW.add(nodesToFix[i]);
                  BrowserDaemonRegistry.broadCastEvent(102, nodesToFix[i]);
               }

               if (newPosition > keyToMove) {
                  node.setTimeStamp(newKey++);
               }

               bookmarksItemsW.add(node);
               BrowserDaemonRegistry.broadCastEvent(102, node);
            }
         }
      }
   }

   public static final void editBookmark(PageModel node, Folder currentFolder) {
      if (currentFolder != null) {
         DialogEditBookmark dialogEditBookmark = new DialogEditBookmark(node);
         PageModel newNode = dialogEditBookmark.getEditedNode(node.getModelResult(), node.getLUID(), node.getTimeStamp(), currentFolder.getLUID());
         if (newNode != null) {
            String oldURL = node.getUrl();
            if (oldURL != null && !oldURL.equals(newNode.getUrl())) {
               removeOfflineContent(node);
            }

            synchronized (FolderHierarchies.getLockObject()) {
               WritableSet bookmarksItems = (WritableSet)currentFolder.getContainedItems();
               bookmarksItems.remove(node);
               bookmarksItems.add(newNode);
               BrowserDaemonRegistry.broadCastEvent(102, newNode);
               return;
            }
         }
      }
   }

   public static final long addAutoUpdateItems(Vector itemsToFetch, long lastUpdateTime) {
      synchronized (FolderHierarchies.getLockObject()) {
         long midnight = DateTimeUtilities.getDate(0).getTime().getTime();
         long now = System.currentTimeMillis();
         return getNextAutoUpdateTime(
            FolderHierarchies.getFolder(BrowserFolders.RIM_BROWSER_BOOKMARKS_HIERARCHY_ID, BrowserFolders.BROWSER_BOOKMARKS_FOLDER_ID),
            now,
            midnight,
            lastUpdateTime,
            itemsToFetch
         );
      }
   }

   private static final long getFirstEventOfDay(long startTime, long period) {
      return period >= startTime ? startTime : startTime % period;
   }

   private static final long getNextAutoUpdateTime(Folder folder, long now, long midnight, long lastUpdateTime, Vector itemsToFetch) {
      long value = Long.MAX_VALUE;
      if (folder != null) {
         ReadableList bookmarksCollection = (ReadableList)folder.getContainedItems();
         int numItems = bookmarksCollection.size();

         for (int i = 0; i < numItems; i++) {
            BrowserPageModel model = (BrowserPageModel)bookmarksCollection.getAt(i);
            if (model.getUpdateFlags() == 1) {
               long updatePeriod = model.getUpdatePeriod() * 1000;
               if (_debugAutoUpdate) {
                  updatePeriod /= 60;
               }

               if (updatePeriod > 0) {
                  long nextTime = midnight + getFirstEventOfDay(model.getUpdateStart(), updatePeriod);
                  if (nextTime <= now) {
                     nextTime += (now - nextTime) / updatePeriod * updatePeriod;
                     if (itemsToFetch != null && nextTime > lastUpdateTime) {
                        itemsToFetch.addElement(model);
                     }

                     nextTime += updatePeriod;
                  }

                  value = Math.min(nextTime, value);
               }
            }
         }

         Enumeration subFolders = folder.getSubFolders();

         while (subFolders.hasMoreElements()) {
            value = Math.min(getNextAutoUpdateTime((Folder)subFolders.nextElement(), now, midnight, lastUpdateTime, itemsToFetch), value);
         }
      }

      return value;
   }

   public static final PageModel[] getLastAccessedBookmarks(int count) {
      BookmarksScreen.updateHomePageBookmarks();
      PageModel[] result = new PageModel[0];
      synchronized (FolderHierarchies.getLockObject()) {
         getLastAccessedBookmarks(
            FolderHierarchies.getFolder(BrowserFolders.RIM_BROWSER_BOOKMARKS_HIERARCHY_ID, BrowserFolders.BROWSER_BOOKMARKS_FOLDER_ID), result, count
         );
         return result;
      }
   }

   private static final void getLastAccessedBookmarks(Folder folder, PageModel[] result, int maxCount) {
      if (folder != null) {
         ReadableList bookmarksCollection = (ReadableList)folder.getContainedItems();
         int numItems = bookmarksCollection.size();

         for (int i = 0; i < numItems; i++) {
            BrowserPageModel model = (BrowserPageModel)bookmarksCollection.getAt(i);
            long myModifiedTime = model.getLastAccessedTime();
            boolean addItem = false;
            if (result.length < maxCount) {
               addItem = true;
            } else if (result[result.length - 1].getLastAccessedTime() < myModifiedTime) {
               addItem = true;
            }

            if (addItem) {
               int j;
               for (j = 0; j < result.length; j++) {
                  if (result[j].getLastAccessedTime() < myModifiedTime) {
                     Arrays.insertAt(result, model, j);
                     break;
                  }
               }

               if (j == result.length) {
                  Arrays.add(result, model);
               }
            }
         }

         if (result.length > maxCount) {
            Array.resize(result, maxCount);
         }

         Enumeration subFolders = folder.getSubFolders();

         while (subFolders.hasMoreElements()) {
            getLastAccessedBookmarks((Folder)subFolders.nextElement(), result, maxCount);
         }
      }
   }
}
