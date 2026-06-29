package net.rim.device.apps.internal.browser.api;

import net.rim.device.api.browser.field.Event;
import net.rim.device.api.io.http.HttpHeaders;

public final class CacheSubDataEvent extends Event {
   private byte[] _data;
   private HttpHeaders _headers;
   private int _statusCode;
   private String _url;

   public CacheSubDataEvent(Object src, String url, HttpHeaders headers, byte[] data, int statusCode) {
      super(4, src);
      this._url = url;
      this._headers = headers;
      this._data = data;
      this._statusCode = statusCode;
   }

   public final byte[] getData() {
      return this._data;
   }

   public final HttpHeaders getHeaders() {
      return this._headers;
   }

   public final int getStatusCode() {
      return this._statusCode;
   }

   public final String getUrl() {
      return this._url;
   }
}
