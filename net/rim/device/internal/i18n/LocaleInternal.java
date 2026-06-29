package net.rim.device.internal.i18n;

import net.rim.device.api.i18n.Locale;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.i18n.ResourceBundleFamily;
import net.rim.device.api.system.ApplicationDescriptor;

public final class LocaleInternal {
   public static final String LOCALE_SEPARATOR;
   public static final char LOCALE_SEPARATOR_CHAR;
   public static final String LOCALE_SEPARATOR_STRING;
   public static final String CRB_EXTENSION;
   private static String RUNTIME_RESOURCES = "net_rim_resouce";
   private static ResourceBundleFamily _resourceFamily;
   private static ResourceBundle _resources;

   public static final void addLocale() {
      String module = ApplicationDescriptor.currentApplicationDescriptor().getModuleName();
      if (module.startsWith(RUNTIME_RESOURCES)) {
         throw new IllegalStateException();
      }

      Locale.addLocaleInternal(getLocaleFromModule());
   }

   public static final Locale getLocaleFromModule() {
      String module = ApplicationDescriptor.currentApplicationDescriptor().getModuleName();
      return getLocaleFromModuleName(module, "__");
   }

   public static final Locale getLocaleFromModuleName(String module) {
      return getLocaleFromModuleName(module, "£");
   }

   private static final Locale getLocaleFromModuleName(String module, String separator) {
      String language = "";
      String country = "";
      String variant = "";
      int languageMarker = module.indexOf(separator);
      if (languageMarker != -1 && languageMarker + 2 < module.length()) {
         languageMarker += separator.length();
         language = module.substring(languageMarker, languageMarker + 2);
         int countryMarker = module.indexOf(95, languageMarker);
         if (countryMarker != -1) {
            int variantMarker = module.indexOf(95, ++countryMarker);
            if (variantMarker == -1) {
               country = module.substring(countryMarker);
            } else {
               country = module.substring(countryMarker, variantMarker);
               variant = module.substring(variantMarker + 1);
            }
         }
      }

      return Locale.get(language, country, variant);
   }

   public static final ResourceBundleFamily getBundle() {
      if (_resourceFamily == null) {
         _resourceFamily = ResourceBundle.getBundle(8736789735327653723L, "net.rim.device.internal.resource.Locale");
      }

      return _resourceFamily;
   }

   public static final String getString(int id) {
      if (_resources == null) {
         _resources = ResourceBundle.getBundle(8736789735327653723L, "net.rim.device.internal.resource.Locale").getBundle(Locale.get(0));
      }

      return _resources.getString(id);
   }

   static {
      Locale.get(0);
   }
}
