package net.rim.device.apps.internal.blackberryemail.folder;

import net.rim.device.cldc.io.utility.URIEncoder;

class IconFetcher$Worker$URL {
   private StringBuffer url;
   private final IconFetcher$Worker this$1;

   public IconFetcher$Worker$URL(IconFetcher$Worker _1, String baseURL) {
      this.this$1 = _1;
      this.url = new StringBuffer(baseURL);
   }

   public void addParam(String param, int value) {
      this.addParam(param, String.valueOf(value));
   }

   public void addParam(String param, String value) {
      if (this.url.toString().indexOf("?") != -1) {
         this.url.append('&');
      } else {
         this.url.append('?');
      }

      URIEncoder.encode(this.url, param, "iso-8859-1", true);
      this.url.append('=').append(value);
   }

   @Override
   public String toString() {
      return this.url.toString();
   }
}
