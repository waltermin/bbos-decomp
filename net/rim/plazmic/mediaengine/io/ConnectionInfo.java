package net.rim.plazmic.mediaengine.io;

public class ConnectionInfo {
   private String _contentType;
   private Object _connection;
   private long _length = -1;
   private String _source;
   private static final int LENGTH_UNKNOWN;

   public String getContentType() {
      return this._contentType;
   }

   public void setContentType(String type) {
      this._contentType = type;
   }

   public Object getConnection() {
      return this._connection;
   }

   public void setConnection(Object connection) {
      this._connection = connection;
   }

   public long getLength() {
      return this._length;
   }

   public void setLength(long length) {
      this._length = length;
   }

   public void setSource(String url) {
      this._source = url;
   }

   public String getSource() {
      return this._source;
   }

   public void dispose() {
      this._connection = null;
      this._contentType = null;
      this._length = -1;
   }
}
