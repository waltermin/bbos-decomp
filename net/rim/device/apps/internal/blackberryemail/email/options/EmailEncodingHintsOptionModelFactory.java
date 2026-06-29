package net.rim.device.apps.internal.blackberryemail.email.options;

import net.rim.device.api.i18n.Locale;
import net.rim.device.apps.api.framework.registration.RIMModelFactory;

final class EmailEncodingHintsOptionModelFactory extends RIMModelFactory {
   @Override
   public final Object createInstance(Object context) {
      return new EmailEncodingHintsOptionModel(context);
   }

   @Override
   public final int getMaximumCount(Object context) {
      return 1;
   }

   @Override
   public final boolean recognize(Object o) {
      return o instanceof EmailEncodingHintsOptionModel;
   }

   static final boolean isHanScriptInputLocaleAvailable() {
      Locale[] inputLocales = Locale.getAvailableInputLocales();
      int numInputLocales = inputLocales.length;

      for (int i = 0; i < numInputLocales; i++) {
         int localeCode = inputLocales[i].getCode() & -65536;
         switch (localeCode) {
            case 1784741888:
               return true;
            case 1802436608:
               return true;
            case 2053636096:
               return true;
         }
      }

      return false;
   }
}
