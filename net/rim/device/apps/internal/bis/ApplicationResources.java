package net.rim.device.apps.internal.bis;

import net.rim.device.api.i18n.Locale;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.i18n.ResourceBundleFamily;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.StringTokenizer;
import net.rim.device.apps.internal.bis.session.ClientSessionState;

public final class ApplicationResources {
   private static final int LANGUAGE_AND_COUNTRY_SEPERATOR = 95;
   private static ResourceBundleFamily _resources = ResourceBundle.getBundle(-4646224451783035820L, "net.rim.device.apps.internal.bis.resource.BISClient");

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public static final String getString(int id) {
      if (_resources == null) {
         throw new Object("No resource strings present on device");
      }

      Locale locale = ClientPersistentState.getInstance().getLocale();
      ResourceBundle bundle = _resources.getBundle(locale);
      String result = null;

      label104:
      try {
         if (bundle == null) {
            result = _resources.getString(id);
         } else {
            boolean var18 = false /* VF: Semaphore variable */;

            label100:
            try {
               var18 = true;
               result = bundle.getString(id);
               var18 = false;
            } finally {
               if (var18) {
                  BISEventLogger.logEvent(
                     ((StringBuffer)(new Object("ApplicationResources: unable to find string with key ")))
                        .append(id)
                        .append(" in locale ")
                        .append(locale.toString())
                        .toString(),
                     0
                  );
                  if (ClientSessionState.getInstance().getBrandingInfo() != null) {
                     Locale defaultLocale = Common.getLocale(ClientSessionState.getInstance().getBrandingInfo().getDefaultLanguage());
                     ResourceBundle defaultBundle = _resources.getBundle(defaultLocale);
                     if (defaultBundle != null) {
                        boolean var14 = false /* VF: Semaphore variable */;

                        label93:
                        try {
                           var14 = true;
                           result = defaultBundle.getString(id);
                           var14 = false;
                        } finally {
                           if (var14) {
                              BISEventLogger.logEvent(
                                 ((StringBuffer)(new Object("ApplicationResources: unable to find string with key ")))
                                    .append(id)
                                    .append(" in default locale ")
                                    .append(locale.toString())
                                    .toString(),
                                 0
                              );
                              break label93;
                           }
                        }
                     }
                  }
                  break label100;
               }
            }
         }
      } finally {
         break label104;
      }

      if (result == null) {
         result = "";
      }

      return result;
   }

   public static final boolean isAppLocalePresent(String languageAndCountry) {
      boolean result = false;
      Locale[] supportedLocales = getSupportedLocales(languageAndCountry);
      if (supportedLocales.length > 0) {
         ResourceBundle queriedLocaleBundle = _resources.getBundle(supportedLocales[0]);
         if (queriedLocaleBundle != null && queriedLocaleBundle.getLocale().equals(supportedLocales[0])) {
            result = true;
         }
      }

      return result;
   }

   public static final Locale[] getSupportedLocales(String localeQueryList) {
      Locale[] result = new Object[0];
      new Object();
      StringTokenizer localeQueryListTokenizer = (StringTokenizer)(new Object(localeQueryList, ';'));

      while (localeQueryListTokenizer.hasMoreTokens()) {
         String languageAndCountry = localeQueryListTokenizer.nextToken();
         ResourceBundle localizedBundle = ResourceBundle.getBundle(languageAndCountry);
         if (localizedBundle != null) {
            Locale locale = null;
            String language = getLanguageCode(languageAndCountry);
            if ("iw".equalsIgnoreCase(language)) {
               language = "he";
            }

            String country = getCountryCode(languageAndCountry);
            if (country != null) {
               locale = Locale.get(language, country);
            } else {
               locale = Locale.get(language);
            }

            if (locale != null) {
               Arrays.add(result, locale);
            }
         }
      }

      return result;
   }

   private static final String getLanguageCode(String languageAndCountry) {
      int seperatorIndex = languageAndCountry.indexOf(95);
      return seperatorIndex != -1 ? languageAndCountry.substring(0, seperatorIndex) : languageAndCountry;
   }

   private static final String getCountryCode(String languageAndCountry) {
      int seperatorIndex = languageAndCountry.indexOf(95);
      return seperatorIndex != -1 ? languageAndCountry.substring(seperatorIndex + 1, languageAndCountry.length()) : null;
   }
}
