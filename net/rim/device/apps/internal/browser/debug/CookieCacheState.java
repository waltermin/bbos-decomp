package net.rim.device.apps.internal.browser.debug;

import java.util.Vector;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.LongEnumeration;
import net.rim.device.api.util.LongHashtable;
import net.rim.device.apps.internal.browser.cookie.Cookie;
import net.rim.device.apps.internal.browser.cookie.CookieCache;

final class CookieCacheState extends CacheState {
   private CookieCache _cache = CookieCache.getInstance();

   CookieCacheState() {
      super(175);
   }

   @Override
   final void handleDelete(Object obj) {
      if (obj instanceof CookieDetail) {
         CookieDetail details = (CookieDetail)obj;
         this._cache.remove(details.getCookie());
         this.refreshOptions();
      }
   }

   @Override
   final void handleExpire(Object obj) {
   }

   @Override
   final void handleSave(Object obj) {
   }

   @Override
   final void refreshOptions() {
      Vector tempOptions = new Vector();
      LongEnumeration e = null;
      synchronized (this._cache) {
         LongHashtable ht = this._cache.getCookies();
         e = ht.keys();

         while (e.hasMoreElements()) {
            long key = e.nextElement();
            Cookie[] nodes = (Cookie[])ht.get(key);
            int length = nodes.length;

            for (int i = 0; i < length; i++) {
               tempOptions.addElement(new CookieDetail(nodes[i]));
            }
         }
      }

      super._options = new CookieDetail[tempOptions.size()];
      tempOptions.copyInto(super._options);
      Arrays.sort(super._options, 0, super._options.length, new CookieCacheState$CookieComparator(null));
      super._fields.setSize(super._options.length);
   }
}
