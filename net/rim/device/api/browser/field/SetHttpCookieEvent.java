package net.rim.device.api.browser.field;

public class SetHttpCookieEvent extends Event {
   private String _url;
   private String _cookie;

   public SetHttpCookieEvent(Object src, String url, String cookie) {
      super(10008, src);
      this._cookie = cookie;
      this._url = this.resolveUrl(url);
   }

   public String getCookie() {
      return this._cookie;
   }

   public String getURL() {
      return this._url;
   }
}
