package net.rim.device.api.browser.field;

import javax.microedition.io.HttpConnection;
import net.rim.device.apps.api.utility.general.URI;

public class Event {
   Object _src;
   private int _uid;
   public static final int PUBLIC_EVENT_THRESHOLD;
   public static final int EVENT_DEVICE_DATA_CONVERSION_OCCURRED;
   public static final int EVENT_LOADING_IMAGES;
   public static final int EVENT_DEVICE_DATA_CONVERSION_REQUEST;
   public static final int EVENT_LOADING_STATUS_CHANGE;
   public static final int EVENT_CACHE_SUB_DATA;
   public static final int EVENT_INLINE_IMAGE_LOADING;
   public static final int EVENT_OPTIONS_CHANGED;
   public static final int EVENT_RELOAD;
   public static final int EVENT_DATA_MODIFICATION;
   public static final int EVENT_DEVICE_DATA_WRONG_CONTENT_TYPE;
   public static final int EVENT_BROWSER_CONTENT_CHANGED;
   public static final int EVENT_CLOSE;
   public static final int EVENT_EXECUTING_SCRIPT;
   public static final int EVENT_FULL_WINDOW;
   public static final int EVENT_HISTORY;
   public static final int EVENT_REDIRECT;
   public static final int EVENT_SET_HEADER;
   public static final int EVENT_SET_HTTP_COOKIE;
   public static final int EVENT_STOP;
   public static final int EVENT_URL_REQUESTED;
   public static final int EVENT_TICK_CONTENT_READ;
   public static final int EVENT_UI_DIRECTION_REQUEST;
   public static final int EVENT_ERROR_DISPLAY;
   public static final int EVENT_CANCEL_REQUEST_RESOURCE;

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
