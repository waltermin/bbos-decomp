package net.rim.wica.runtime.persistence;

import net.rim.device.api.io.http.HttpDateParser;
import net.rim.device.api.io.http.HttpHeaders;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.util.Persistable;

public final class WebResource extends Resource implements Persistable, Recryptable {
   private HttpHeaders _headers;
   private long _timestamp;
   private long _expiration;
   private long _lastModified;
   private static final long DEFAULT_EXPIRY;

   public WebResource(String uri, byte[] data, HttpHeaders headers) {
      super(uri, data);
      this.updateResource(headers);
   }

   private final void updateResource(HttpHeaders headers) {
      this._timestamp = System.currentTimeMillis();
      this._headers = headers;
      this._lastModified = HttpDateParser.parse(this._headers.getPropertyValue("Last-Modified"));
      this._expiration = this._timestamp + 43200000;
      String date = headers.getPropertyValue("Expires");
      if (date != null) {
         this._expiration = HttpDateParser.parse(date);
      } else {
         if (this._lastModified != 0) {
            long freshnessLifetime = (this._timestamp - this._lastModified) / 10;
            if (freshnessLifetime > 43200000) {
               this._expiration = this._timestamp + freshnessLifetime;
            }
         }
      }
   }

   @Override
   public final String getContentType() {
      return this._headers != null ? this._headers.getPropertyValue("Content-Type") : super.getContentType();
   }

   public final long getExpiration() {
      return this._expiration;
   }

   public final HttpHeaders getHeaders() {
      return this._headers;
   }

   public final long getLastModified() {
      return this._lastModified;
   }

   public final long getTimestamp() {
      return this._timestamp;
   }

   public final void setExpiration(long expiration) {
      this._expiration = expiration;
   }

   public final void setHeaders(HttpHeaders headers) {
      this.updateResource(headers);
   }

   public final void setLastModified(long lastModified) {
      this._lastModified = lastModified;
   }

   public final void setTimestamp(long timestamp) {
      this._timestamp = timestamp;
   }

   @Override
   public final void recrypt() {
      String uri = this.getUri();
      if (!PersistentContent.checkEncoding(uri)) {
         this.setUri((String)PersistentContent.reEncode(uri, false, true));
      }

      if (PersistentContent.isEncryptionEnabled()) {
         if (this._headers != null) {
            this._headers.reCrypt();
         }

         byte[] data = this.getData();
         if (data != null) {
            this.setData((byte[])PersistentContent.encode(data));
         }
      }
   }
}
