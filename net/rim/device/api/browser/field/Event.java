package net.rim.device.api.browser.field;

import javax.microedition.io.HttpConnection;
import net.rim.device.apps.api.utility.general.URI;

public class Event {
   Object _src;
   private int _uid;
   public static final int PUBLIC_EVENT_THRESHOLD = 10000;
   public static final int EVENT_DEVICE_DATA_CONVERSION_OCCURRED = 0;
   public static final int EVENT_LOADING_IMAGES = 1;
   public static final int EVENT_DEVICE_DATA_CONVERSION_REQUEST = 2;
   public static final int EVENT_LOADING_STATUS_CHANGE = 3;
   public static final int EVENT_CACHE_SUB_DATA = 4;
   public static final int EVENT_INLINE_IMAGE_LOADING = 5;
   public static final int EVENT_OPTIONS_CHANGED = 6;
   public static final int EVENT_RELOAD = 7;
   public static final int EVENT_DATA_MODIFICATION = 8;
   public static final int EVENT_DEVICE_DATA_WRONG_CONTENT_TYPE = 9;
   public static final int EVENT_BROWSER_CONTENT_CHANGED = 10001;
   public static final int EVENT_CLOSE = 10002;
   public static final int EVENT_EXECUTING_SCRIPT = 10003;
   public static final int EVENT_FULL_WINDOW = 10004;
   public static final int EVENT_HISTORY = 10005;
   public static final int EVENT_REDIRECT = 10006;
   public static final int EVENT_SET_HEADER = 10007;
   public static final int EVENT_SET_HTTP_COOKIE = 10008;
   public static final int EVENT_STOP = 10009;
   public static final int EVENT_URL_REQUESTED = 10010;
   public static final int EVENT_TICK_CONTENT_READ = 10011;
   public static final int EVENT_UI_DIRECTION_REQUEST = 10012;
   public static final int EVENT_ERROR_DISPLAY = 10013;
   public static final int EVENT_CANCEL_REQUEST_RESOURCE = 10014;

   public Event(int uid, Object src) {
      this._uid = uid;
      this._src = src;
   }

   public final Object getSource() {
      return this._src;
   }

   public final String getSourceURL() {
      if (!(this._src instanceof BrowserContent)) {
         return !(this._src instanceof Object) ? null : ((HttpConnection)this._src).getURL();
      } else {
         return ((BrowserContent)this._src).getURL();
      }
   }

   public final int getUID() {
      return this._uid;
   }

   protected String resolveUrl(String url) {
      if (!(this._src instanceof BrowserContent)) {
         return this._src instanceof Object ? URI.getAbsoluteURL(url, ((HttpConnection)this._src).getURL()) : URI.getAbsoluteURL(url, null);
      } else {
         return ((BrowserContent)this._src).resolveUrl(url);
      }
   }
}
