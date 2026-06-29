package net.rim.device.apps.internal.browser.cookie;

import java.util.Enumeration;
import net.rim.device.api.crypto.HashCodeCalculator;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.system.PersistentContentListener;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.LongEnumeration;
import net.rim.device.api.util.LongHashtable;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.utility.general.URI;
import net.rim.device.apps.internal.browser.core.BrowserDaemonRegistry;
import net.rim.device.apps.internal.browser.core.BrowserStateListener;
import net.rim.device.apps.internal.browser.options.GeneralProperty;
import net.rim.device.apps.internal.browser.util.DomainUtilities;
import net.rim.device.apps.internal.browser.util.QuincyUtil;

public final class CookieCache implements BrowserStateListener, PersistentContentListener {
   private int _cookieCacheGeneration;
   private LongHashtable _cookies;
   private static final long APP_REGISTRY_KEY = 7595927212303924497L;
   private static final long COOKIE_CACHE_KEY = 6375174330656707005L;

   @Override
   public final void browserStateChanged(int newState) {
      if (newState == 0) {
         this.clearExpiredCookies();
         this.commit();
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public final void addCookies(String url, String cookieString) {
      boolean acceptCookies = GeneralProperty.getCurrentPropertyAsBoolean(30);
      if (acceptCookies && url != null) {
         URI absUrl = null;
         boolean var18 = false /* VF: Semaphore variable */;

         try {
            var18 = true;
            absUrl = new URI(url);
            var18 = false;
         } finally {
            if (var18) {
               return;
            }
         }

         String requestHost = DomainUtilities.parseAuthority(absUrl);
         String requestURI = DomainUtilities.parsePath(absUrl);
         int index = -1;

         while (true) {
            Cookie currentCookie;
            while (true) {
               if (index >= cookieString.length()) {
                  return;
               }

               int prevIndex = index + 1;
               index = cookieString.indexOf(44, prevIndex);
               if (index < 0) {
                  index = cookieString.length();
               } else {
                  while (index < cookieString.length()) {
                     int commaIndex = cookieString.indexOf(44, index + 1);
                     int semiColonIndex = cookieString.indexOf(59, index + 1);
                     int equalsIndex = cookieString.indexOf(61, index + 1);
                     if (equalsIndex > -1 && (commaIndex == -1 || commaIndex > equalsIndex) && (semiColonIndex == -1 || semiColonIndex > equalsIndex)) {
                        break;
                     }

                     if (commaIndex > -1) {
                        index = commaIndex;
                     } else {
                        index = cookieString.length();
                     }
                  }
               }

               CookieException var24;
               try {
                  try {
                     currentCookie = new Cookie(requestHost, requestURI, cookieString.substring(prevIndex, index));
                     break;
                  } catch (CookieException var20) {
                     var24 = var20;
                  }
               } catch (Throwable var21) {
                  EventLogger.logEvent(1907089860548946979L, ("Invalid cookie " + cookieString).getBytes(), 0);
                  QuincyUtil.sendQuincy(t, false);
                  continue;
               }

               EventLogger.logEvent(1907089860548946979L, (Integer.toString(var24.getCode()) + " :" + cookieString).getBytes(), 5);
            }

            this.addCookie(currentCookie);
         }
      }
   }

   public final void cleanup() {
      BrowserDaemonRegistry.removeBrowserStateListener(this);
   }

   public final int getCacheGeneration() {
      return this._cookieCacheGeneration;
   }

   public final void commit() {
      PersistentObject store = RIMPersistentStore.getPersistentObject(6375174330656707005L);
      synchronized (store) {
         store.setContents(this._cookies, 51);
         store.commit();
      }
   }

   public final boolean clearCookies() {
      if (this._cookies.size() == 0) {
         return false;
      }

      this._cookies.clear();
      this._cookieCacheGeneration++;
      this.commit();
      return true;
   }

   public final void clearExpiredCookies() {
      boolean modified = false;
      synchronized (this._cookies) {
         LongEnumeration keys = this._cookies.keys();

         while (keys.hasMoreElements()) {
            long key = keys.nextElement();
            Cookie[] cookies = (Cookie[])this._cookies.get(key);
            if (cookies != null) {
               for (int i = cookies.length - 1; i >= 0; i--) {
                  if (cookies[i].isExpired(true)) {
                     Arrays.removeAt(cookies, i);
                     modified = true;
                  }
               }
            }

            if (cookies == null || cookies.length == 0) {
               this._cookies.remove(key);
            }
         }
      }

      if (modified) {
         this._cookieCacheGeneration++;
      }
   }

   public final void remove(Cookie cookie) {
      boolean modified = false;
      synchronized (this._cookies) {
         LongEnumeration keys = this._cookies.keys();

         while (keys.hasMoreElements()) {
            long key = keys.nextElement();
            Cookie[] cookies = (Cookie[])this._cookies.get(key);
            if (cookies != null) {
               for (int i = cookies.length - 1; i >= 0; i--) {
                  if (cookies[i] == cookie) {
                     Arrays.removeAt(cookies, i);
                     modified = true;
                  }
               }
            }

            if (cookies == null || cookies.length == 0) {
               this._cookies.remove(key);
            }
         }
      }

      if (modified) {
         this._cookieCacheGeneration++;
      }
   }

   public final String getCookies(String url) {
      boolean acceptCookies = GeneralProperty.getCurrentPropertyAsBoolean(30);
      if (acceptCookies && url != null) {
         URI absUrl = null;

         try {
            absUrl = new URI(url);
         } finally {
            ;
         }

         boolean isSecure = StringUtilities.strEqualIgnoreCase("https", absUrl.getScheme(), 1701707776);
         Cookie[] matchingCookies = this.getMatchingCookies(DomainUtilities.parseAuthority(absUrl), DomainUtilities.parsePath(absUrl), isSecure);
         StringBuffer cookieBuff = null;

         for (int i = 0; i < matchingCookies.length; i++) {
            Cookie currentCookie = matchingCookies[i];
            if (cookieBuff == null) {
               cookieBuff = new StringBuffer();
            } else {
               cookieBuff.append(';');
               cookieBuff.append(' ');
            }

            cookieBuff.append(currentCookie.encode());
         }

         return cookieBuff != null ? cookieBuff.toString() : null;
      } else {
         return "";
      }
   }

   public final LongHashtable getCookies() {
      return this._cookies;
   }

   public final int getNumUniqueHosts() {
      return this._cookies.size();
   }

   public final int getNumCookies() {
      int count = 0;
      synchronized (this._cookies) {
         Enumeration elements = this._cookies.elements();

         while (elements.hasMoreElements()) {
            count += ((Cookie[])elements.nextElement()).length;
         }

         return count;
      }
   }

   @Override
   public final void persistentContentModeChanged(int generation) {
      boolean modified = false;
      synchronized (this._cookies) {
         LongEnumeration keys = this._cookies.keys();

         while (keys.hasMoreElements()) {
            long key = keys.nextElement();
            Cookie[] cookies = (Cookie[])this._cookies.get(key);
            if (cookies != null) {
               for (int i = cookies.length - 1; i >= 0; i--) {
                  if (!cookies[i].checkCrypt(false, true)) {
                     cookies[i].reCrypt(false, true);
                     modified = true;
                  }
               }
            }
         }
      }

      if (modified) {
         this.commit();
      }
   }

   @Override
   public final void persistentContentStateChanged(int state) {
   }

   public static final CookieCache getInstance() {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      CookieCache instance = (CookieCache)ar.getOrWaitFor(7595927212303924497L);
      if (instance == null) {
         instance = new CookieCache();
         ar.put(7595927212303924497L, instance);
      }

      return instance;
   }

   private final void addCookie(Cookie cookie) {
      String domain = cookie.getDomain();
      String path = cookie.getPath();
      String name = cookie.getName();
      long hashCode = HashCodeCalculator.getDigest64(domain.getBytes());
      synchronized (this._cookies) {
         Cookie[] cookies = (Cookie[])this._cookies.get(hashCode);
         if (cookies == null) {
            if (!cookie.isExpired(false)) {
               cookies = new Cookie[]{cookie};
               this._cookies.put(hashCode, cookies);
               this._cookieCacheGeneration++;
            }
         } else {
            boolean found = false;

            for (int i = cookies.length - 1; i >= 0; i--) {
               Cookie domainCookie = cookies[i];
               if (domainCookie.getDomain().equals(domain) && domainCookie.getPath().equals(path) && StringUtilities.strEqual(domainCookie.getName(), name)) {
                  found = true;
                  if (cookie.isExpired(false)) {
                     Arrays.removeAt(cookies, i);
                  } else {
                     cookies[i] = cookie;
                  }
               }
            }

            if (!found && !cookie.isExpired(false)) {
               Arrays.add(cookies, cookie);
               this._cookieCacheGeneration++;
            } else if (found) {
               this._cookieCacheGeneration++;
            }
         }
      }
   }

   private CookieCache() {
      PersistentObject store = RIMPersistentStore.getPersistentObject(6375174330656707005L);
      if (store.getContents() == null) {
         this._cookies = new LongHashtable();
      } else {
         this._cookies = (LongHashtable)store.getContents();
      }

      BrowserDaemonRegistry.addBrowserStateListener(this);
      PersistentContent.addWeakListener(this);
   }

   private final Cookie[] getMatchingCookies(String requestHost, String requestURI, boolean isSecure) {
      Cookie[] matchingCookies = new Cookie[0];
      long hashCode = HashCodeCalculator.getDigest64(requestHost.getBytes());
      Cookie[] cookies = (Cookie[])this._cookies.get(hashCode);
      if (cookies != null) {
         this.addMatchingCookies(cookies, matchingCookies, requestHost, requestURI, isSecure);
      }

      hashCode = HashCodeCalculator.getDigest64(('.' + requestHost).getBytes());
      cookies = (Cookie[])this._cookies.get(hashCode);
      if (cookies != null) {
         this.addMatchingCookies(cookies, matchingCookies, requestHost, requestURI, isSecure);
      }

      for (int indexOfDot = requestHost.indexOf(46); indexOfDot >= 0; indexOfDot = requestHost.indexOf(46, indexOfDot + 1)) {
         hashCode = HashCodeCalculator.getDigest64(requestHost.substring(indexOfDot).getBytes());
         cookies = (Cookie[])this._cookies.get(hashCode);
         if (cookies != null) {
            this.addMatchingCookies(cookies, matchingCookies, requestHost, requestURI, isSecure);
         }
      }

      if (matchingCookies.length == 0) {
         return matchingCookies;
      }

      Arrays.sort(matchingCookies, new CookieCache$CookieComparator(null));
      return matchingCookies;
   }

   private final void addMatchingCookies(Cookie[] cookies, Cookie[] matchingCookies, String requestHost, String requestURI, boolean isSecureConnection) {
      for (int i = cookies.length - 1; i >= 0; i--) {
         Cookie cookie = cookies[i];
         String domain = cookie.getDomain();
         String path = cookie.getPath();
         if ((!cookie.isSecure() || isSecureConnection) && cookie.domainMatch(requestHost, domain) && requestURI.startsWith(path) && !cookie.isExpired(false)) {
            Arrays.add(matchingCookies, cookie);
         }
      }
   }
}
