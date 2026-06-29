package net.rim.device.apps.internal.browser.debug;

import java.util.Enumeration;
import java.util.Hashtable;
import net.rim.device.api.io.http.HttpHeaders;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.component.SeparatorField;
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
      MainScreen screen = new MainScreen();
      screen.setTitle(new LabelField(BrowserResources.getString(372)));
      screen.add(new EditField(BrowserResources.getString(277), this._url, Integer.MAX_VALUE, 9007199254740992L));
      screen.add(new LabelField(BrowserResources.getString(534) + CommonResources.getString(this._sticky ? 100 : 101)));
      DebugListItem.GMT_CAL.setTimeLong(this._timestamp);
      screen.add(new LabelField(BrowserResources.getString(373) + DebugListItem.DATE_FORMAT.format(DebugListItem.GMT_CAL, new StringBuffer(), null) + " GMT"));
      String time;
      if (this._expiry != -1) {
         DebugListItem.GMT_CAL.setTimeLong(this._expiry);
         time = DebugListItem.DATE_FORMAT.format(DebugListItem.GMT_CAL, new StringBuffer(), null).toString();
      } else {
         time = CommonResources.getString(2014);
      }

      screen.add(new LabelField(BrowserResources.getString(374) + time + " GMT"));
      screen.add(new SeparatorField());
      screen.add(new LabelField(BrowserResources.getString(393)));
      if (this._requestHeaders != null) {
         Enumeration e = this._requestHeaders.keys();

         while (e.hasMoreElements()) {
            String name = (String)e.nextElement();
            screen.add(new LabelField(name + " = " + this._requestHeaders.get(name) + '\n'));
         }
      } else {
         screen.add(new LabelField(BrowserResources.getString(377)));
      }

      screen.add(new SeparatorField());
      screen.add(new LabelField(BrowserResources.getString(394)));
      if (this._cresult == null) {
         screen.add(new LabelField(BrowserResources.getString(377)));
         return screen;
      }

      StringBuffer buffer = new StringBuffer();
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
         screen.add(new RichTextField(buffer.toString()));
         screen.add(new LabelField(BrowserResources.getString(377)));
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

      screen.add(new RichTextField(buffer.toString()));
      return screen;
   }
}
