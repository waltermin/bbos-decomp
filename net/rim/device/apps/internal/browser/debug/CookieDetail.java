package net.rim.device.apps.internal.browser.debug;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.internal.browser.cookie.Cookie;
import net.rim.device.apps.internal.browser.resources.BrowserResources;
import net.rim.device.apps.internal.browser.util.Asserts;

final class CookieDetail implements DebugListItem {
   private Cookie _cookie;

   CookieDetail(Cookie cookie) {
      Asserts.productionArgumentAssert(cookie != null);
      this._cookie = cookie;
   }

   private final void append(StringBuffer buffer, String title, String str) {
      if (str != null && str.length() != 0) {
         buffer.append(title);
         buffer.append(str);
         buffer.append('\n');
      }
   }

   final Cookie getCookie() {
      return this._cookie;
   }

   @Override
   public final String getLabel() {
      return this._cookie.getDomain();
   }

   @Override
   public final Screen getScreen() {
      MainScreen screen = new MainScreen();
      screen.setTitle(new LabelField(BrowserResources.getString(376)));
      StringBuffer info = new StringBuffer();
      this.append(info, BrowserResources.getString(400), this._cookie.getRequestHost());
      this.append(info, BrowserResources.getString(401), this._cookie.getRequestURI());
      String cookieName = this._cookie.getName();
      if (cookieName != null) {
         this.append(info, CommonResources.getString(2002), cookieName);
      }

      this.append(info, BrowserResources.getString(403), this._cookie.getValue());
      this.append(info, BrowserResources.getString(404), this._cookie.getDomain());
      this.append(info, BrowserResources.getString(405), this._cookie.getPath());
      long timestamp = this._cookie.getMaxAge();
      if (timestamp > 0) {
         DebugListItem.GMT_CAL.setTimeLong(this._cookie.getTimeCreated() + timestamp);
         String str = DebugListItem.DATE_FORMAT.format(DebugListItem.GMT_CAL, new StringBuffer(), null) + " GMT";
         this.append(info, BrowserResources.getString(408), str);
      }

      timestamp = this._cookie.getTimeCreated();
      if (timestamp > 0) {
         DebugListItem.GMT_CAL.setTimeLong(timestamp);
         String str = DebugListItem.DATE_FORMAT.format(DebugListItem.GMT_CAL, new StringBuffer(), null) + " GMT";
         this.append(info, BrowserResources.getString(373), str);
      }

      info.append(BrowserResources.getString(410) + this._cookie.isSecure());
      Field field = new RichTextField(info.toString());
      screen.add(field);
      return screen;
   }
}
