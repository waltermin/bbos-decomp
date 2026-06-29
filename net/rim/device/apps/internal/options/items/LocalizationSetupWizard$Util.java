package net.rim.device.apps.internal.options.items;

import net.rim.device.api.i18n.Locale;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.StringUtilities;
import net.rim.tid.util.Utils;

final class LocalizationSetupWizard$Util {
   public static final DisplayableLocale[] getDisplayableLocales() {
      Locale[] locales = Locale.getAvailableLocales();
      int numLocales = locales.length;
      int localeIndex = 0;
      DisplayableLocale[] displayableLocales = new DisplayableLocale[numLocales - 1];

      for (int i = 0; i < numLocales; i++) {
         if (locales[i].getCode() != 0) {
            displayableLocales[localeIndex++] = new DisplayableLocale(locales[i], getDisplayString(locales[i]));
         }
      }

      return displayableLocales;
   }

   public static final void filterMultitapInputLocales(Locale[] locales) {
      for (int i = 0; i < locales.length; i++) {
         if (locales[i].getVariant().equals("Multitap")) {
            Arrays.removeAt(locales, i);
            i--;
         }
      }
   }

   public static final DisplayableLocale[] getInputLocales() {
      Locale[] locales = Locale.getAvailableInputLocales();
      Utils.filterRootInputLocales(locales);
      filterMultitapInputLocales(locales);
      int numLocales = locales.length;
      int localeIndex = 0;
      DisplayableLocale[] inputLocales = new DisplayableLocale[numLocales];

      for (int i = 0; i < numLocales; i++) {
         inputLocales[localeIndex++] = new DisplayableLocale(locales[i], getDisplayString(locales[i]));
      }

      return inputLocales;
   }

   private static final String getDisplayString(Locale aLocale) {
      if ((aLocale.getCode() & -65536) != 2053636096) {
         return aLocale.getDisplayName();
      }

      String display = aLocale.getDisplayName();
      StringBuffer res = new StringBuffer(display);
      display = StringUtilities.toUpperCase(display, 1701707776);
      int index = display.indexOf("HONG KONG");
      if (index != -1) {
         res.delete(index, index + 9);
         res.insert(index, "Traditional");
      } else {
         index = display.indexOf("TAIWAN");
         if (index != -1) {
            res.delete(index, index + 6);
            res.insert(index, "Traditional");
         } else {
            index = display.indexOf("CHINA");
            if (index != -1) {
               res.delete(index, index + 5);
               res.insert(index, "Simplified");
            }
         }
      }

      return res.toString();
   }

   public static final int getAppropriateChineseInputLocale(int dispLocaleCode, DisplayableLocale[] inputLocales) {
      int matchIndex = -1;
      int candIndex = -1;
      Locale matchLocale = dispLocaleCode == 2053653326 ? Locale.get("zh", "CN", "Pinyin") : Locale.get("zh", "HK", "Jyutping");

      for (int i = 0; i < inputLocales.length; i++) {
         Locale current = inputLocales[i].getLocale();
         if (matchLocale.equals(current)) {
            matchIndex = i;
            break;
         }

         if (candIndex == -1 && dispLocaleCode == current.getCode()) {
            candIndex = i;
         }
      }

      return matchIndex != -1 ? matchIndex : candIndex;
   }

   public static final int findLocale(Locale locale, DisplayableLocale[] set, boolean useLocaleCode) {
      int numLocales = set.length;
      int localeCode = locale.getCode();

      for (int i = 0; i < numLocales; i++) {
         Locale currentLocale = set[i].getLocale();
         if (useLocaleCode) {
            int code = currentLocale.getCode();
            if ((code & localeCode) == localeCode) {
               return i;
            }
         } else if (currentLocale.equals(locale)) {
            return i;
         }
      }

      return -1;
   }
}
