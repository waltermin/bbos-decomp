package net.rim.device.apps.internal.browser.bookmark;

import net.rim.device.api.ui.UiApplication;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.messaging.Folder;
import net.rim.device.apps.internal.browser.core.BrowserDaemonRegistry;
import net.rim.device.apps.internal.browser.core.BrowserHotkeys;
import net.rim.device.apps.internal.browser.core.BrowserSession;
import net.rim.device.apps.internal.browser.page.Page;
import net.rim.device.apps.internal.browser.page.SplashPage;
import net.rim.device.apps.internal.browser.resources.BrowserResources;
import net.rim.device.apps.internal.browser.verbs.BrowserVerb;

public final class AddBookmarkVerb extends BrowserVerb {
   public AddBookmarkVerb() {
      super(16864256, BrowserResources.getResourceBundle(), 109);
      BrowserHotkeys.registerBrowserHotKey(335, this);
   }

   @Override
   public final Object invoke(Object context) {
      ContextObject contextObject = null;
      if (context instanceof Object) {
         contextObject = (ContextObject)context;
      }

      Page page = null;
      Folder folder = null;
      String title = null;
      String url = null;
      String configUid = null;
      if (contextObject != null) {
         Object object = ContextObject.get(contextObject, -1219344331000926502L);
         if (object instanceof Object) {
            folder = (Folder)object;
         }

         object = ContextObject.get(contextObject, -7261227923983886841L);
         if (object instanceof Object) {
            title = (String)object;
         }

         object = ContextObject.get(contextObject, 253);
         if (object instanceof Object) {
            url = (String)object;
         }

         object = ContextObject.get(contextObject, 867508017068302662L);
         if (object instanceof Object) {
            configUid = (String)object;
         }

         new DialogAddBookmark(url, title, folder, configUid).getUserInput();
         return null;
      } else {
         page = BrowserDaemonRegistry.getInstance().getCurrentPage();
         if (page == null) {
            return null;
         }

         new DialogAddBookmark(page).getUserInput();
         return null;
      }
   }

   @Override
   public final boolean isEnabled() {
      Page page = BrowserDaemonRegistry.getInstance().getCurrentPage();
      if (page != null && !(page instanceof SplashPage)) {
         BrowserSession session = BrowserSession.getCurrentSession();
         return session != null ? session.getConfig().getPropertyAsInt(7) == 0 : true;
      } else {
         return UiApplication.getUiApplication().getActiveScreen() instanceof BookmarksScreen;
      }
   }

   @Override
   public final void cleanup() {
      BrowserHotkeys.deregisterBrowserHotKey(335);
   }
}
