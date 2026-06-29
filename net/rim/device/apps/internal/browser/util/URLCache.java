package net.rim.device.apps.internal.browser.util;

import java.util.Hashtable;
import net.rim.device.api.browser.field.RenderingOptions;
import net.rim.device.api.util.StringMatch;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.utility.general.URI;
import net.rim.device.cldc.io.utility.URIEncoder;

public final class URLCache {
   private Hashtable _relative = (Hashtable)(new Object());
   private Hashtable _absolute = (Hashtable)(new Object());
   private String _lastAbsoluteLookup;
   private String _lastAbsoluteResult;
   private URI _base;
   private RenderingOptions _renderingOptions;
   private static String DEVICE_HOME = "device:home";
   private static StringMatch _stringMatch = (StringMatch)(new Object("\r \n \t", false, true, false));

   public URLCache(String url, RenderingOptions renderingOptions) {
      this.setBase(url);
      this._renderingOptions = renderingOptions;
   }

   public final void clearCache() {
      this.clear(false);
   }

   private final void clear(boolean clearBaseAsWell) {
      this._relative.clear();
      this._absolute.clear();
      if (clearBaseAsWell) {
         this._base = null;
      }
   }

   public final URI setBase(String url) {
      this.clear(true);
      if (url == null) {
         return this._base;
      }

      url = this.getNormalizedString(url);

      label30:
      try {
         this._base = (URI)(new Object(url));
      } finally {
         break label30;
      }

      if (!this._base.isAbsolute()) {
         this._base = null;
      }

      return this._base;
   }

   public final URI getBase() {
      return this._base;
   }

   public final URI get(String url) {
      return this.get(url, true);
   }

   public final void put(String url, URI resolved) {
      this._absolute.put(resolved.getAbsoluteURL(), resolved);
      if (url != null) {
         this._relative.put(url, resolved);
      }
   }

   public final URI get(String url, boolean allowRelativeRef) {
      if (url == null) {
         return null;
      }

      if (url.length() == 0) {
         return allowRelativeRef && this._base != null ? this._base : null;
      }

      url = this.getNormalizedString(url);
      if (url.startsWith(DEVICE_HOME) && this._renderingOptions != null) {
         String homePage = this._renderingOptions.getPropertyWithStringValue(4550690918222697397L, 15, RenderingOptions.HOME_PAGE_URL_DEFAULT);
         if (homePage != null) {
            url = ((StringBuffer)(new Object())).append(homePage).append(url.substring(DEVICE_HOME.length())).toString();
         }
      }

      URI result = (URI)this._relative.get(url);
      if (result != null) {
         return result;
      }

      result = (URI)this._absolute.get(url);
      if (result != null) {
         return result;
      }

      try {
         result = (URI)(new Object(url, this.getBase(), allowRelativeRef));
         if (result.saveInCache()) {
            this.put(allowRelativeRef ? url : null, result);
            return result;
         }
      } finally {
         return result;
      }

      return result;
   }

   public final String getAbsoluteURL(String url, boolean allowRelativeRef) {
      if (url == null) {
         return null;
      }

      if (url.length() == 0) {
         return allowRelativeRef && this._base != null ? this._base.getAbsoluteURL() : null;
      }

      String originalUrl = url;
      synchronized (this) {
         if (url.equals(this._lastAbsoluteLookup)) {
            return this._lastAbsoluteResult;
         }
      }

      url = this.getNormalizedString(url);
      String result = null;
      if (url.startsWith(DEVICE_HOME)) {
         if (this._renderingOptions != null) {
            String homePage = this._renderingOptions.getPropertyWithStringValue(4550690918222697397L, 15, RenderingOptions.HOME_PAGE_URL_DEFAULT);
            if (homePage != null) {
               result = ((StringBuffer)(new Object())).append(homePage).append(url.substring(DEVICE_HOME.length())).toString();
            }
         }
      } else if (!url.startsWith("http://") && !url.startsWith("https://")) {
         URI tempURI = this.get(url, allowRelativeRef);
         if (tempURI != null) {
            result = tempURI.getAbsoluteURL();
         }
      } else {
         if (url.lastIndexOf(47) <= 7 && url.length() > 8) {
            int pathStart = url.indexOf(63);
            if (pathStart == -1) {
               pathStart = url.indexOf(35);
            }

            if (pathStart == -1) {
               url = ((StringBuffer)(new Object())).append(url).append('/').toString();
            } else {
               url = ((StringBuffer)(new Object())).append(url.substring(0, pathStart)).append('/').append(url.substring(pathStart)).toString();
            }
         }

         result = URIEncoder.encodeNonUSASCII(url, true);
      }

      synchronized (this) {
         if (result != null) {
            this._lastAbsoluteLookup = originalUrl;
            this._lastAbsoluteResult = result;
         }

         return result;
      }
   }

   private final String getNormalizedString(String str) {
      if (str == null) {
         return null;
      }

      str = lowercaseSchemeAndHostname(str, false);
      synchronized (_stringMatch) {
         int index = _stringMatch.indexOf(str);
         if (index == -1) {
            return str;
         }

         StringBuffer buffer = (StringBuffer)(new Object());
         int start = 0;
         int length = str.length();

         while (index != -1) {
            buffer.append(str.substring(start, index));
            start = index + 1;
            if (start >= length) {
               break;
            }

            index = _stringMatch.indexOf(str, start);
         }

         if (start < length) {
            buffer.append(str.substring(start));
         }

         return buffer.toString();
      }
   }

   public static final String lowercaseSchemeAndHostname(String uriReference, boolean stripSlash) {
      int colonIndex;
      if (StringUtilities.regionMatches(uriReference, true, 0, "http://", 0, 7, 1701707776)) {
         colonIndex = 4;
      } else {
         if (!StringUtilities.regionMatches(uriReference, true, 0, "https://", 0, 8, 1701707776)) {
            return uriReference;
         }

         colonIndex = 5;
      }

      int nextSlash = uriReference.indexOf(47, colonIndex + 3);
      int endOfHost = nextSlash;
      if (endOfHost == -1) {
         endOfHost = uriReference.length();
      }

      for (int i = 0; i < endOfHost; i++) {
         char aChar = uriReference.charAt(i);
         if (aChar >= 'A' && aChar <= 'Z') {
            uriReference = ((StringBuffer)(new Object()))
               .append(StringUtilities.toLowerCase(uriReference.substring(0, endOfHost), 1701707776))
               .append(uriReference.substring(endOfHost))
               .toString();
            break;
         }
      }

      if (stripSlash && nextSlash + 1 == uriReference.length()) {
         uriReference = uriReference.substring(0, nextSlash);
      }

      return uriReference;
   }
}
