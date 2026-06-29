package net.rim.device.apps.internal.bis.api.io.http;

import java.util.Vector;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.Comparator;
import net.rim.device.api.util.StringTokenizer;

public final class HttpCookies {
   Vector cookies = new Vector();

   public final String getCookieHeader() {
      String[] cookiesArray = new String[this.cookies.size()];

      for (int i = 0; i < this.cookies.size(); i++) {
         cookiesArray[i] = this.cookies.elementAt(i).toString();
      }

      Comparator stringComparator = new HttpCookies$1(this);
      Arrays.sort(cookiesArray, stringComparator);
      String cookieHeader = "";

      for (int i = 0; i < cookiesArray.length; i++) {
         if (i > 0) {
            cookieHeader = cookieHeader + ", ";
         }

         cookieHeader = cookieHeader + cookiesArray[i];
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
