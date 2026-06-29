package net.rim.device.api.browser.field;

import javax.microedition.io.HttpConnection;
import javax.microedition.io.InputConnection;
import net.rim.device.api.io.http.HttpHeaders;

public final class RequestedResource {
   private int _flags;
   private InputConnection _inputConnection;
   private String _url;
   private HttpHeaders _requestHeaders;
   private boolean _cacheOnly;
   private String _method;
   private byte[] _requestData;

   public RequestedResource(String url) {
      this(url, (HttpHeaders)(new Object()), 0);
   }

   public RequestedResource(String url, HttpHeaders requestHeaders, int flags) {
      this._url = url;
      this._flags = flags;
      this._requestHeaders = requestHeaders;
   }

   public RequestedResource(String url, HttpHeaders requestHeaders, int flags, String requestMethod, byte[] requestData) {
      this._url = url;
      this._flags = flags;
      this._requestHeaders = requestHeaders;
      this._method = requestMethod;
      this._requestData = requestData;
   }

   public final boolean isCacheOnly() {
      return this._cacheOnly;
   }

   public final void setCacheOnly(boolean cacheOnly) {
      this._cacheOnly = cacheOnly;
   }

   public final int getFlags() {
      return this._flags;
   }

   public final String getRequestMethod() {
      if (this._method != null) {
         return this._method;
      } else {
         return this._requestData != null ? "POST" : "GET";
      }
   }

   public final byte[] getRequestData() {
      return this._requestData;
   }

   public final HttpConnection getHttpConnection() {
      return (HttpConnection)(!(this._inputConnection instanceof Object) ? null : this._inputConnection);
   }

   public final String getUrl() {
      return this._url;
   }

   public final HttpHeaders getRequestHeaders() {
      return this._requestHeaders;
   }

   public final void setHttpConnection(HttpConnection connection) {
      this.setInputConnection(connection);
   }

   public final void setInputConnection(InputConnection connection) {
      this._inputConnection = connection;
   }

   public final InputConnection getInputConnection() {
      return this._inputConnection;
   }
}
