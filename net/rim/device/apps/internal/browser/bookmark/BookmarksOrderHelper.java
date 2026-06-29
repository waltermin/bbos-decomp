package net.rim.device.apps.internal.browser.bookmark;

import net.rim.device.api.util.Comparator;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.messaging.Folder;
import net.rim.device.apps.internal.browser.channel.ChannelModel;
import net.rim.device.apps.internal.browser.page.PageModel;
import net.rim.device.apps.internal.browser.store.BrowserFolders;
import net.rim.vm.Memory;

final class BookmarksOrderHelper implements Comparator {
   private static BookmarksOrderHelper _instance;

   static final BookmarksOrderHelper getInstance() {
      if (_instance == null) {
         _instance = new BookmarksOrderHelper();
      }

      return _instance;
   }

   public BookmarksOrderHelper() {
   }

   @Override
   public final int compare(Object obj1, Object obj2) {
      if (obj1 instanceof PageModel && obj2 instanceof PageModel) {
         long timestamp1 = ((PageModel)obj1).getTimeStamp();
         long timestamp2 = ((PageModel)obj2).getTimeStamp();
         if (timestamp1 < timestamp2) {
            return -1;
         } else {
            return timestamp1 == timestamp2 ? Memory.objectToInt(obj1) - Memory.objectToInt(obj2) : 1;
         }
      } else if (obj1 instanceof Object && obj2 instanceof Object) {
         if (obj1 instanceof ProvisionedBookmarksFolder && !(obj2 instanceof ProvisionedBookmarksFolder)) {
            return -1;
         } else if (!(obj1 instanceof ProvisionedBookmarksFolder) && obj2 instanceof ProvisionedBookmarksFolder) {
            return 1;
         } else {
            Folder folder1 = (Folder)obj1;
            Folder folder2 = (Folder)obj2;
            long folder1LUID = folder1.getLUID();
            long folder2LUID = folder2.getLUID();
            if (folder1LUID == BrowserFolders.BROWSER_WAP_BOOKMARKS_FOLDER_ID && folder2LUID != BrowserFolders.BROWSER_WAP_BOOKMARKS_FOLDER_ID) {
               return -1;
            } else if (folder2LUID == BrowserFolders.BROWSER_WAP_BOOKMARKS_FOLDER_ID && folder1LUID != BrowserFolders.BROWSER_WAP_BOOKMARKS_FOLDER_ID) {
               return 1;
            } else if (folder1LUID == BrowserFolders.BROWSER_CHANNELS_FOLDER_ID && folder2LUID != BrowserFolders.BROWSER_CHANNELS_FOLDER_ID) {
               return 1;
            } else {
               return folder2LUID == BrowserFolders.BROWSER_CHANNELS_FOLDER_ID && folder1LUID != BrowserFolders.BROWSER_CHANNELS_FOLDER_ID
                  ? -1
                  : StringUtilities.compareToIgnoreCase(folder1.getFriendlyName(), folder2.getFriendlyName());
            }
         }
      } else {
         if (obj1 instanceof Object && obj2 instanceof PageModel) {
            return -1;
         }

         if (obj1 instanceof PageModel && obj2 instanceof Object) {
            return 1;
         }

         if (obj1 instanceof ChannelModel && obj2 instanceof ChannelModel) {
            long timestamp1 = ((ChannelModel)obj1).getTimestamp();
            long timestamp2 = ((ChannelModel)obj2).getTimestamp();
            if (timestamp1 < timestamp2) {
               return -1;
            } else {
               return timestamp1 == timestamp2 ? StringUtilities.compareToIgnoreCase(obj1.toString(), obj2.toString()) : 1;
            }
         } else if (obj1 instanceof Object && obj2 instanceof ChannelModel) {
            return -1;
         } else if (obj1 instanceof ChannelModel && obj2 instanceof Object) {
            return 1;
         } else {
            throw new Object();
         }
      }
   }
}
