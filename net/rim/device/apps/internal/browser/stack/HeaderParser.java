package net.rim.device.apps.internal.browser.stack;

import net.rim.device.api.io.http.HttpHeaders;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.internal.browser.common.RIMHeaderConstants;

public final class HeaderParser implements RIMHeaderConstants {
   public static String CACHE_CONTROL = "cache-control";
   public static String REFRESH = "refresh";
   public static String REFERER = "referer";
   public static String EXPIRES = "expires";
   public static String DEFAULT_STYLE = "default-style";
   public static String PROFILE = "profile";
   public static String PROFILE_DIFF = "profile-diff";
   public static String X_WAP_PROFILE = "x-wap-profile";
   public static String X_WAP_PROFILE_DIFF = "x-wap-profile-diff";
   public static String CACHE_DIRECTIVE_PUBLIC = "public";
   public static String CACHE_DIRECTIVE_PRIVATE = "private";
   public static String CACHE_DIRECTIVE_NO_CACHE = "no-cache";
   public static String CACHE_DIRECTIVE_NO_STORE = "no-store";
   public static String CACHE_DIRECTIVE_NO_TRANSFORM = "no-transform";
   public static String CACHE_DIRECTIVE_MUST_REVALIDATE = "must-revalidate";
   public static String CACHE_DIRECTIVE_PROXY_REVALIDATE = "proxy-revalidate";
   public static String CACHE_DIRECTIVE_MAX_AGE = "max-age";
   public static String CACHE_DIRECTIVE_S_MAXAGE = "s-maxage";
   public static final String TRUE;
   public static final String FALSE;

   public static final boolean containsDirective(HttpHeaders responseHeaders, String directive) {
      if (responseHeaders == null) {
         return false;
      }

      String cacheControlValue = responseHeaders.getPropertyValue(CACHE_CONTROL);
      if (cacheControlValue == null) {
         return false;
      }

      cacheControlValue = StringUtilities.toLowerCase(cacheControlValue, 1701707776);
      return cacheControlValue.indexOf(directive) >= 0;
   }

   public static final void appendDirective(HttpHeaders responseHeaders, String directive) {
      if (responseHeaders != null && !containsDirective(responseHeaders, directive)) {
         String cacheControlValue = responseHeaders.getPropertyValue(CACHE_CONTROL);
         if (cacheControlValue == null) {
            cacheControlValue = directive;
         } else {
            cacheControlValue = ((StringBuffer)(new Object())).append(cacheControlValue).append(',').append(directive).toString();
         }

         responseHeaders.setProperty(CACHE_CONTROL, cacheControlValue);
      }
   }

   public static final String getPreferredConfigUID(HttpHeaders responseHeaders, String defaultValue) {
      if (responseHeaders == null) {
         return defaultValue;
      }

      String value = responseHeaders.getPropertyValue("x-rim-fetch-bearer");
      if (value != null) {
         value = StringUtilities.toLowerCase(value, 1701707776);
         int index = value.indexOf("configuid=");
         if (index != -1) {
            int nextParm = value.indexOf(44, index);
            if (nextParm == -1) {
               nextParm = value.length();
            }

            return value.substring(index + 10, nextParm);
         }
      }

      return defaultValue;
   }

   public static final String getPreferredTransportCID(HttpHeaders responseHeaders, String defaultValue) {
      if (responseHeaders == null) {
         return defaultValue;
      }

      String value = responseHeaders.getPropertyValue("x-rim-fetch-bearer");
      if (value != null) {
         value = StringUtilities.toLowerCase(value, 1701707776);
         int index = value.indexOf("bearer=");
         if (index != -1) {
            int nextParm = value.indexOf(44, index);
            if (nextParm == -1) {
               nextParm = value.length();
            }

            return value.substring(index + 7, nextParm);
         }
      }

      return defaultValue;
   }

   public static final int getPreferredConfigType(HttpHeaders responseHeaders, int defaultValue) {
      if (responseHeaders == null) {
         return defaultValue;
      }

      String value = responseHeaders.getPropertyValue("x-rim-fetch-bearer");
      if (value != null) {
         value = StringUtilities.toLowerCase(value, 1701707776);
         int index = value.indexOf("configtype=");
         if (index != -1) {
            int nextParm = value.indexOf(44, index);
            if (nextParm == -1) {
               nextParm = value.length();
            }

            try {
               return Integer.parseInt(value.substring(index + 11, nextParm));
            } finally {
               return defaultValue;
            }
         }
      }

      return defaultValue;
   }
}
