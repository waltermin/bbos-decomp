package net.rim.device.apps.internal.options.items;

import net.rim.device.api.i18n.Locale;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.i18n.ResourceBundleFamily;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.component.ChoiceField;
import net.rim.device.api.util.Arrays;

final class ScreenKeyboardOptionsItem$KeypadChoiceField extends ChoiceField {
   private Locale[] _locales = Keypad.getAvailableLocales();
   private ResourceBundleFamily _rbFamily = ResourceBundle.getBundle(-4248492586227566823L, "net.rim.device.internal.resource.Keypad");

   ScreenKeyboardOptionsItem$KeypadChoiceField(String label) {
      super(label, Keypad.getAvailableLocales().length, 0);
      Locale locale = Keypad.getLocale();
      int index = this._locales.length - 1;

      while (index > 0 && (!this._locales[index].equals(locale) || this._locales[index].getKeyboardID() != locale.getKeyboardID())) {
         index--;
      }

      this.setSelectedIndex(index);
   }

   @Override
   public final Object getChoice(int index) {
      Locale locale = this._locales[index];
      ResourceBundle bundle = this._rbFamily.getBundle(locale);
      String id = Locale.convertKeyboardIDToString(locale.getKeyboardID());
      String[] iIDs = bundle.getStringArray(102);
      int ind = Arrays.getIndex(iIDs, id);
      String[] names = bundle.getStringArray(1);
      if (ind != -1) {
         return names[ind];
      }

      System.err.println("Warning: Error in Keypad resource bundle");
      return "";
   }
}
