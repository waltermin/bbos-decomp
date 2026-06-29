package net.rim.wica.runtime.persistence;

import net.rim.device.api.util.Persistable;

public class Resource implements Persistable {
   private String _uri;
   private String _contentType;
   private byte[] _data;

   public Resource(String uri, byte[] data) {
      this._uri = uri;
      this._data = data;
   }

   public String getContentType() {
      return this._contentType;
   }

   public byte[] getData() {
      return this._data;
   }

   public String getUri() {
      return this._uri;
   }

   public void setContentType(String contentType) {
      this._contentType = contentType;
   }

   public void setData(byte[] data) {
      this._data = data;
   }

   public void setUri(String uri) {
      this._uri = uri;
   }

   public int size() {
      return this._data != null ? this._data.length : 0;
   }
}
