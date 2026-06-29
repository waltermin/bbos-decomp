package net.rim.device.apps.internal.browser.bookmark;

import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.messaging.Folder;
import net.rim.device.apps.internal.browser.page.PageModel;
import net.rim.device.apps.internal.browser.resources.BrowserResources;

final class EditBookmarkVerb extends Verb {
   private PageModel _bookmarkNode;
   private Folder _currentFolder;

   EditBookmarkVerb(PageModel bookmarkNode, Folder currentFolder, int resourceID, int ordering) {
      super(ordering, BrowserResources.getResourceBundle(), resourceID);
      this._bookmarkNode = bookmarkNode;
      this._currentFolder = currentFolder;
   }

   @Override
   public final Object invoke(Object context) {
      switch (super._rbKey) {
         case 145:
            String title = this._bookmarkNode.getTitle();
            String[] name = new String[]{title != null && title.length() != 0 ? title : this._bookmarkNode.getUrl()};
            String message = MessageFormat.format(BrowserResources.getString(282), name);
            if (Dialog.ask(2, message) == 3) {
               Bookmarks.deleteBookmark(this._bookmarkNode, this._currentFolder);
            }
         default:
            return null;
         case 149:
            Bookmarks.editBookmark(this._bookmarkNode, this._currentFolder);
            return null;
      }
   }
}
