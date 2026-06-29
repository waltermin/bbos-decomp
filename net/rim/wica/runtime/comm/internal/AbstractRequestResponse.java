package net.rim.wica.runtime.comm.internal;

import net.rim.device.api.io.http.HttpHeaders;

public class AbstractRequestResponse {
   protected byte[] _data;
   protected HttpHeaders _headers;

   public AbstractRequestResponse() {
   }

   public AbstractRequestResponse(byte[] data) {
      this._data = data;
   }

   public byte[] getData() {
      return this._data;
   }

   public String getHeader(String key) {
      return !this.hasHeaders() ? null : this._headers.getPropertyValue(key);
   }

   public HttpHeaders getHeaders() {
      return this._headers;
   }

   public boolean hasData() {
      return this._data != null && this._data.length > 0;
   }

   public boolean hasHeaders() {
      return this._headers != null && this._headers.size() > 0;
   }

   public void setData(byte[] data) {
      this._data = data;
   }

   public void setHeader(String key, String value) {
      if (this._headers == null) {
         this._headers = (HttpHeaders)(new Object());
      }

      this._headers.addProperty(key, value);
   }

   public void setHeaders(HttpHeaders headers) {
      this._headers = headers;
   }
}
