package net.rim.device.apps.internal.bis.api.io.http;

import net.rim.device.api.util.Comparator;

final class HttpCookies$1 implements Comparator {
   private final HttpCookies this$0;

   HttpCookies$1(HttpCookies _1) {
      this.this$0 = _1;
   }

   @Override
   public final int compare(Object o1, Object o2) {
      return ((String)o1).compareTo((String)o2);
   }
}
