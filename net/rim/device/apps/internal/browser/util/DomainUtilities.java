package net.rim.device.apps.internal.browser.util;

import java.util.Vector;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.utility.general.URI;

public final class DomainUtilities {
   public static final boolean isPartialDN(String hostname) {
      return hostname.indexOf(46) >= 0 || hostname.lastIndexOf(46) == hostname.length() - 1 ? false : hostname.indexOf(47) < 0;
   }

   public static final boolean isFQDN(String hostname) {
      return hostname.indexOf(46) <= 0 || hostname.lastIndexOf(46) == hostname.length() - 1 ? false : hostname.indexOf(47) < 0;
   }

   public static final boolean isIPAddress(String hostname) {
      Vector tokenizedStrings = tokenizeString(hostname, '.');
      if (tokenizedStrings.size() != 4) {
         return false;
      }

      for (int i = 0; i < 4; i++) {
         try {
            Integer.parseInt((String)tokenizedStrings.elementAt(i));
         } finally {
            ;
         }
      }

      return true;
   }

   private static final Vector tokenizeString(String inputString, char tokenSeparator) {
      Vector tokenizedVector = new Vector();
      int tokenizeIndex = 0;

      while (inputString.length() > 0 && tokenizeIndex >= 0) {
         tokenizeIndex = inputString.lastIndexOf(tokenSeparator);
         if (tokenizeIndex >= 0) {
            String token = inputString.substring(tokenizeIndex + 1);
            inputString = inputString.substring(0, tokenizeIndex);
            tokenizedVector.addElement(token);
         }
      }

      tokenizedVector.addElement(inputString);
      return tokenizedVector;
   }

   public static final String parseAuthority(URI absUrl) {
      String result = absUrl.getAuthority();
      if (result == null) {
         return "";
      }

      int len = result.length();
      int begin = 0;

      while (begin < len && result.charAt(begin) == '/') {
         begin++;
      }

      int end = result.indexOf(58);
      if (end < 0) {
         end = len;
      }

      if (begin != 0 || end != len) {
         result = result.substring(begin, end);
      }

      return result;
   }

   public static final String parsePath(URI absUrl) {
      String result = absUrl.getPath();
      return result != null ? result : "";
   }

   public static final boolean isHostInDomain(String host, String domain) {
      if (host == null || domain == null) {
         return false;
      } else if (StringUtilities.endsWithIgnoreCase(host, domain, 1701707776)) {
         int hostLength = host.length();
         int domainLength = domain.length();
         return hostLength == domainLength ? true : host.charAt(hostLength - domainLength - 1) == '.';
      } else {
         return false;
      }
   }
}
