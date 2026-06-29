package net.rim.device.apps.internal.browser.debug;

import java.util.Enumeration;
import java.util.Hashtable;
import net.rim.device.api.io.http.HttpHeaders;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.internal.browser.resources.BrowserResources;
import net.rim.device.apps.internal.browser.stack.CacheResult;
import net.rim.device.apps.internal.browser.util.RendererControl;

final class CachedUrlDetail implements DebugListItem {
   private String _url;
   private String _label;
   private long _timestamp;
   private long _expiry;
   private Hashtable _requestHeaders;
   private CacheResult _cresult;
   private boolean _sticky;

   CachedUrlDetail(String url, long timestamp, long expiry, Hashtable requestHeaders, CacheResult cresult, boolean sticky) {
      this._url = url;
      int colonIndex;
      if (url != null && (colonIndex = url.indexOf("://")) != -1) {
         this._label = url.substring(colonIndex + 3);
      } else {
         this._label = url;
      }

      this._sticky = sticky;
      this._cresult = cresult;
      this._requestHeaders = requestHeaders;
      this._expiry = expiry;
      this._timestamp = timestamp;
   }

   public final String getURL() {
      return this._url;
   }

   public final CacheResult getCacheResult() {
      return this._cresult;
   }

   @Override
   public final String getLabel() {
      return this._label;
   }

   @Override
   public final String toString() {
      return this._label;
   }

   @Override
   public final Screen getScreen() {
      MainScreen screen = (MainScreen)(new Object());
      screen.setTitle((Field)(new Object(BrowserResources.getString(372))));
      screen.add((Field)(new Object(BrowserResources.getString(277), this._url, Integer.MAX_VALUE, 9007199254740992L)));
      screen.add(
         (Field)(new Object(
            ((StringBuffer)(new Object())).append(BrowserResources.getString(534)).append(CommonResources.getString(this._sticky ? 100 : 101)).toString()
         ))
      );
      DebugListItem.GMT_CAL.setTimeLong(this._timestamp);
      screen.add(
         (Field)(new Object(
            ((StringBuffer)(new Object()))
               .append(BrowserResources.getString(373))
               .append(DebugListItem.DATE_FORMAT.format(DebugListItem.GMT_CAL, (StringBuffer)(new Object()), null))
               .append(" GMT")
               .toString()
         ))
      );
      String time;
      if (this._expiry != -1) {
         DebugListItem.GMT_CAL.setTimeLong(this._expiry);
         time = DebugListItem.DATE_FORMAT.format(DebugListItem.GMT_CAL, (StringBuffer)(new Object()), null).toString();
      } else {
         time = CommonResources.getString(2014);
      }

      screen.add((Field)(new Object(((StringBuffer)(new Object())).append(BrowserResources.getString(374)).append(time).append(" GMT").toString())));
      screen.add((Field)(new Object()));
      screen.add((Field)(new Object(BrowserResources.getString(393))));
      if (this._requestHeaders != null) {
         Enumeration e = this._requestHeaders.keys();

         while (e.hasMoreElements()) {
            String name = (String)e.nextElement();
            screen.add(
               (Field)(new Object(((StringBuffer)(new Object())).append(name).append(" = ").append(this._requestHeaders.get(name)).append('\n').toString()))
            );
         }
      } else {
         screen.add((Field)(new Object(BrowserResources.getString(377))));
      }

      screen.add((Field)(new Object()));
      screen.add((Field)(new Object(BrowserResources.getString(394))));
      if (this._cresult == null) {
         screen.add((Field)(new Object(BrowserResources.getString(377))));
         return screen;
      }

      StringBuffer buffer = (StringBuffer)(new Object());
      int status = this._cresult.getStatus();
      buffer.append(BrowserResources.getString(396));
      buffer.append(status);
      buffer.append(' ');
      buffer.append(RendererControl.getStatusDescription(status));
      buffer.append('\n');
      buffer.append(BrowserResources.getString(395));
      buffer.append(Integer.toString(this._cresult.getDataLength()));
      buffer.append('\n');
      HttpHeaders responseHeaders = this._cresult == null ? null : this._cresult.getResponseHeaders();
      if (responseHeaders == null) {
         screen.add((Field)(new Object(buffer.toString())));
         screen.add((Field)(new Object(BrowserResources.getString(377))));
         return screen;
      }

      buffer.append(BrowserResources.getString(375));
      buffer.append('\n');

      String key;
      for (int pos = 0; (key = responseHeaders.getPropertyKey(pos)) != null; pos++) {
         buffer.append(key);
         buffer.append(" = ");
         buffer.append(responseHeaders.getPropertyValue(pos));
         buffer.append('\n');
      }

      screen.add((Field)(new Object(buffer.toString())));
      return screen;
   }
}
