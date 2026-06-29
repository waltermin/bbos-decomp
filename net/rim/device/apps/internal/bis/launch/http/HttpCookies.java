package net.rim.device.apps.internal.bis.launch.http;

import java.util.Vector;
import net.rim.device.api.util.StringTokenizer;

public final class HttpCookies {
   Vector cookies = new Vector();

   public final String getCookieHeader() {
      String cookieHeader = "";

      for (int i = 0; i < this.cookies.size(); i++) {
         if (i > 0) {
            cookieHeader = cookieHeader + ", ";
         }

         cookieHeader = cookieHeader + this.cookies.elementAt(i).toString();
      }

      return cookieHeader;
   }

   public final void clear() {
      this.cookies.removeAllElements();
   }

   public final void parseSetCookieHeader(String setCookieString) {
      StringTokenizer tokenizer = new StringTokenizer(setCookieString, ";,");

      while (tokenizer.hasMoreTokens()) {
         String token = tokenizer.nextToken().trim();
         String lowerToken = token.toLowerCase();
         int equalsIndex = lowerToken.indexOf(61);
         if (!lowerToken.startsWith("expires") && !lowerToken.startsWith("path") && equalsIndex > 0) {
            String name = token.substring(0, equalsIndex);
            String value = token.substring(equalsIndex + 1);
            this.updateCookie(name, value);
         }
      }
   }

   public final void updateCookie(String name, String value) {
      boolean exists = false;

      for (int i = 0; i < this.cookies.size(); i++) {
         HttpCookies$Cookie cookie = (HttpCookies$Cookie)this.cookies.elementAt(i);
         if (name != null && name.equalsIgnoreCase(cookie.getName())) {
            exists = true;
            cookie.setValue(value);
         }
      }

      if (!exists) {
         HttpCookies$Cookie cookie = new HttpCookies$Cookie(name, value);
         this.cookies.addElement(cookie);
      }
   }
}
