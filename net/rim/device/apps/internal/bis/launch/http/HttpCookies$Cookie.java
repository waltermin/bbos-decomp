package net.rim.device.apps.internal.bis.launch.http;

final class HttpCookies$Cookie {
   String name;
   String value;

   HttpCookies$Cookie(String name, String value) {
      this.name = name;
      this.value = value;
   }

   public final String getName() {
      return this.name;
   }

   public final void setValue(String value) {
      this.value = value;
   }

   @Override
   public final String toString() {
      return ((StringBuffer)(new Object())).append(this.name).append("=").append(this.value).toString();
   }
}
