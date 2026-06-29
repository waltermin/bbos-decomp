package net.rim.device.cldc.io.waphttp;

import net.rim.device.api.io.http.HttpHeaders;
import net.rim.device.api.util.Persistable;

final class WAPSession implements Persistable {
   private int _maxServerSDUSize;
   private String _key;
   private long _id;
   private HttpHeaders _headers;

   public WAPSession(String key, HttpHeaders headers) {
      this._key = key;
      this._headers = headers;
   }

   @Override
   public final boolean equals(Object anObject) {
      return this._key.equals(anObject);
   }

   public final void setId(long id) {
      this._id = id;
   }

   public final long getId() {
      return this._id;
   }

   public final String getKey() {
      return this._key;
   }

   public final HttpHeaders getHeaders() {
      return this._headers;
   }

   public final void setMaxServerSDUSize(int value) {
      this._maxServerSDUSize = value;
   }

   public final int getMaxServerSDUSize() {
      return this._maxServerSDUSize;
   }

   public final boolean checkCrypt() {
      return this._headers.checkCrypt();
   }

   public final void reCrypt() {
      this._headers.reCrypt();
   }
}
