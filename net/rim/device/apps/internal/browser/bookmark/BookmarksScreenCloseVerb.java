package net.rim.device.apps.internal.browser.bookmark;

import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.browser.core.BrowserDaemonRegistry;
import net.rim.device.internal.i18n.CommonResource;

public final class BookmarksScreenCloseVerb extends Verb {
   BookmarksScreen _bookmarksScreen;

   public BookmarksScreenCloseVerb(BookmarksScreen bookmarksScreen) {
      super(268501008, CommonResource.getBundle(), 9);
      this._bookmarksScreen = bookmarksScreen;
   }

   @Override
   public final Object invoke(Object context) {
      if (this._bookmarksScreen.getIsInitialScreen()) {
         boolean confirm = false;
         if (context instanceof Object) {
            confirm = context;
         }

         BrowserDaemonRegistry.getInstance().closeBrowser(confirm);
         return null;
      } else {
         this._bookmarksScreen.close();
         return null;
      }
   }
}
