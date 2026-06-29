package net.rim.device.apps.internal.browser.bookmark;

import net.rim.device.apps.internal.browser.core.BrowserDaemonRegistry;
import net.rim.device.apps.internal.browser.core.BrowserHotkeys;
import net.rim.device.apps.internal.browser.core.BrowserSession;
import net.rim.device.apps.internal.browser.resources.BrowserResources;
import net.rim.device.apps.internal.browser.verbs.BrowserVerb;

public final class BookmarksVerb extends BrowserVerb {
   private boolean _setIsInitialScreen;
   private BookmarksScreen _bookmarksScreen;

   public BookmarksVerb() {
      super(16864293, BrowserResources.getResourceBundle(), 102);
      BrowserHotkeys.registerBrowserHotKey(334, this);
   }

   public final void setIsInitialScreen(boolean value) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   @Override
   public final Object invoke(Object context) {
      BrowserDaemonRegistry.getInstance().setReleaseLock(false);
      this._bookmarksScreen = new BookmarksScreen(BookmarksFolderList.getDefaultFolderID());
      this._bookmarksScreen.setIsInitialScreen(this._setIsInitialScreen);
      this._setIsInitialScreen = false;
      this._bookmarksScreen.run();
      return null;
   }

   @Override
   public final void cleanup() {
      if (this._bookmarksScreen != null) {
         this._bookmarksScreen.cleanup();
      }

      this._bookmarksScreen = null;
      BrowserHotkeys.deregisterBrowserHotKey(334);
   }

   @Override
   public final boolean isEnabled() {
      BrowserSession session = BrowserSession.getCurrentSession();
      return session != null ? session.getConfig().getPropertyAsInt(7) == 0 : true;
   }

   @Override
   public final boolean isModal() {
      return true;
   }
}
