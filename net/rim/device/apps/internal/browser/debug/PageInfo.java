package net.rim.device.apps.internal.browser.debug;

import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.apps.internal.browser.core.BrowserDaemonRegistry;
import net.rim.device.apps.internal.browser.core.BrowserImpl;
import net.rim.device.apps.internal.browser.page.BrowserContentImpl;
import net.rim.device.apps.internal.browser.page.Page;

final class PageInfo implements DebugListItem {
   private String _data;
   private String _url;
   private static final String PAGE_INFO = "Page Info";

   public PageInfo() {
      BrowserImpl browser = BrowserDaemonRegistry.getInstance();
      Page page = browser.getCurrentPage();
      if (page != null) {
         BrowserContentImpl browserContent = page.getBrowserContent();
         this._data = browserContent.toString();
         this._url = browserContent.getURL();
      }
   }

   @Override
   public final String getLabel() {
      return "Page Info";
   }

   @Override
   public final Screen getScreen() {
      MainScreen screen = new PageInfo$PageInfoScreen(this);
      screen.setTitle(new LabelField("Page Info"));
      if (this._data != null) {
         RichTextField field = new RichTextField(this._data);
         screen.add(field);
      }

      return screen;
   }
}
