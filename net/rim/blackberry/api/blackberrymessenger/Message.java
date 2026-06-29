package net.rim.blackberry.api.blackberrymessenger;

public class Message {
   private String _contentType;
   private byte[] _data;
   private String _name;
   private int _integer;
   private String _url;
   public static final String DEFAULT_NAME = null;
   public static final int DEFAULT_INTEGER = -1;
   public static final String DEFAULT_URL = null;

   public Message(String contentType, byte[] data) {
      this(contentType, data, DEFAULT_NAME);
   }

   public Message(String contentType, byte[] data, String name) {
      this(contentType, data, name, -1, DEFAULT_URL);
   }

   public Message(String contentType, byte[] data, String name, int integer, String url) {
      this.setContentType(contentType);
      this.setData(data);
      this.setName(name);
      this.setInteger(integer);
      this.setURL(url);
   }

   public String getContentType() {
      return this._contentType;
   }

   public void setContentType(String contentType) {
      if (contentType == null) {
         throw new NullPointerException("contentType==null");
      }

      this._contentType = contentType;
   }

   public byte[] getData() {
      return this._data;
   }

   public void setData(byte[] data) {
      if (data == null) {
         throw new NullPointerException("data==null");
      }

      if (data.length > 15360) {
         throw new IllegalArgumentException("Data size cannot exceed 15 kB");
      }

      this._data = data;
   }

   public String getName() {
      return this._name;
   }

   public void setName(String name) {
      this._name = name;
   }

   public int getInteger() {
      return this._integer;
   }

   public void setInteger(int integer) {
      this._integer = integer;
   }

   public String getURL() {
      return this._url;
   }

   public void setURL(String url) {
      this._url = url;
   }
}
