package net.rim.device.internal.i18n;

import net.rim.device.api.i18n.Locale;

public final class LocaleUtil {
   private static final int ENGLISH_LOCALE_NUMBER = 0;
   private static final int FRENCH_LOCALE_NUMBER = 1;
   private static final int SPANISH_LOCALE_NUMBER = 2;
   private static final int GERMAN_LOCALE_NUMBER = 3;
   private static final int ITALIAN_LOCALE_NUMBER = 4;
   private static final int ALL_LOCALES = -1;

   public static final int convertCppLocaleToJavaLocale(int code) {
      switch (code) {
         case -2:
            return code;
         case -1:
            return 0;
         case 0:
         default:
            return 1701707776;
         case 1:
            return 1718747136;
         case 2:
            return 1702035456;
         case 3:
            return 1684340736;
         case 4:
            return 1769209856;
      }
   }

   public static final int convertJavaLocaleToCppLocale(int code) {
      if (code == 0) {
         return -1;
      }

      Locale locale = Locale.get(code);
      String language = locale.getLanguage();
      Locale languageLocale = Locale.get(language);
      code = languageLocale.getCode();
      switch (code) {
         case 1684340736:
            return 3;
         case 1701707776:
            return 0;
         case 1702035456:
            return 2;
         case 1718747136:
            return 1;
         case 1769209856:
            return 4;
         default:
            return code;
      }
   }
}
