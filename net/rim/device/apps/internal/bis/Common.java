package net.rim.device.apps.internal.bis;

import net.rim.device.api.i18n.Locale;

public final class Common {
   public static final String getLocaleCode(Locale locale) {
      String localeCode = locale.getLanguage();
      String country = locale.getCountry();
      if (country != null && !"".equals(country.trim())) {
         localeCode = localeCode + "_" + country;
      }

      return localeCode;
   }

   public static final Locale getLocale(String localeCode) {
      String language = localeCode.substring(0, 2);
      String country = null;
      if (localeCode.length() == 5) {
         country = localeCode.substring(3);
      }

      return Locale.get(language, country);
   }
}
