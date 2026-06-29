package net.rim.device.apps.internal.browser.bookmark;

import net.rim.device.api.ui.UiApplication;
import net.rim.device.apps.api.messaging.ui.SelectFolderVerb;
import net.rim.device.apps.internal.browser.resources.BrowserResources;

final class BookmarksFolderList$SelectVerb extends SelectFolderVerb {
   private boolean _selected;
   private final BookmarksFolderList this$0;

   public BookmarksFolderList$SelectVerb(BookmarksFolderList _1) {
      super(602416);
      this.this$0 = _1;
   }

   @Override
   public final boolean selectionMade() {
      return this._selected;
   }

   @Override
   public final void clearSelection() {
      this._selected = false;
   }

   @Override
   public final String toString() {
      return BrowserResources.getString(167);
   }

   @Override
   public final Object invoke(Object context) {
      this.this$0._selectedFolder = this.this$0.getFocusedFolder();
      this._selected = true;
      BookmarksFolderList.setDefaultFolderID(this.this$0._selectedFolder.getLUID());
      UiApplication.getUiApplication().popScreen(this.this$0);
      return this.this$0._selectedFolder;
   }
}
