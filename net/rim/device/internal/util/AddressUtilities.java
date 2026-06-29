package net.rim.device.internal.util;

import net.rim.device.api.util.StringUtilities;

public final class AddressUtilities {
   private static final String MAILTO_PREFIX;
   private static final String PINTO_PREFIX;
   private static final String PIN_PREFIX;

   private AddressUtilities() {
   }

   public static final String removePrefixes(String address) {
      if (StringUtilities.startsWithIgnoreCase(address, "mailto:", 1701707776)
         || StringUtilities.startsWithIgnoreCase(address, "pinto:", 1701707776)
         || StringUtilities.startsWithIgnoreCase(address, "pin:", 1701707776)) {
         int beginIndex = address.indexOf(58) + 1;
         int endIndex = address.length();

         while (beginIndex < endIndex && address.charAt(beginIndex) == '/') {
            beginIndex++;
         }

         while (beginIndex < endIndex && address.charAt(endIndex - 1) == '/') {
            endIndex--;
         }

         int queryString = address.indexOf(63, beginIndex);
         if (queryString >= 0 && queryString < endIndex) {
            endIndex = queryString;
         }

         address = address.substring(beginIndex, endIndex);
      }

      return address;
   }
}
