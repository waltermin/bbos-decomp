package net.rim.device.api.browser.field;

import net.rim.device.api.io.http.HttpHeaders;

public class UrlRequestedEvent extends Event {
   private String _url;
   private byte[] _postData;
   private HttpHeaders _headers;
   private int _flags;
   private boolean _programmatic;

   public UrlRequestedEvent(Object src, String url, byte[] postData, HttpHeaders headers, boolean programmatic, int flags) {
      super(10010, src);
      this._postData = postData;
      this._headers = headers;
      this._url = this.resolveUrl(url);
      this._flags = flags;
      this._programmatic = programmatic;
   }

   public String getURL() {
      return this._url;
   }

   public byte[] getPostData() {
      return this._postData;
   }

   public HttpHeaders getHeaders() {
      return this._headers;
   }

   public int getFlags() {
      return this._flags;
   }

   public boolean isProgrammatic() {
      return this._programmatic;
   }
}
