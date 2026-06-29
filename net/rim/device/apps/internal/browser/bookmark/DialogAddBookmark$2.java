package net.rim.device.apps.internal.browser.bookmark;

import java.util.Enumeration;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.apps.api.messaging.Folder;
import net.rim.device.apps.internal.browser.resources.BrowserResources;

class DialogAddBookmark$2 extends MenuItem {
   private final DialogAddBookmark this$0;

   DialogAddBookmark$2(DialogAddBookmark _1, String x0, int x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = _1;
   }

   @Override
   public void run() {
      Folder bookmarksFolder = BookmarksFolderList.getDefaultFolder();
      BookmarksFolderList folderList = new BookmarksFolderList(bookmarksFolder, BrowserResources.getString(102));
      folderList.run();
      Folder newFolder = folderList.getSelectedFolder();
      if (newFolder != null) {
         this.this$0._selectedFolder = newFolder;
         this.this$0._folderNameField.setChoices(new String[]{this.this$0._selectedFolder.getFriendlyName()});
      } else {
         Folder parent = this.this$0._selectedFolder.getParentFolder();
         boolean isChild = false;
         if (parent != null) {
            Enumeration children = parent.getSubFolders();

            while (children.hasMoreElements()) {
               if (children.nextElement() == this.this$0._selectedFolder) {
                  isChild = true;
               }
            }
         }

         if (!isChild) {
            this.this$0._selectedFolder = BookmarksFolderList.getDefaultFolder();
            this.this$0._folderNameField.setChoices(new String[]{this.this$0._selectedFolder.getFriendlyName()});
         }
      }
   }
}
