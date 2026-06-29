package net.rim.device.api.ui;

import net.rim.device.api.i18n.Locale;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.i18n.ResourceBundleFamily;
import net.rim.vm.Persistable;

final class Keypad$KeypadData implements Persistable {
   public int _localeCode;
   private int _osLocale;
   private int _osLocaleVariant;
   private int _keypadID;

   public Keypad$KeypadData() {
      this.setLocale(Locale.getDefaultForKeyboard());
   }

   private final Locale getLocale() {
      return Locale.get(this._localeCode, Locale.convertKeyboardIDToString(0), this._keypadID);
   }

   private final Locale getOSLocale() {
      return Locale.get(this._osLocale, Locale.convertKeyboardIDToString(this._osLocaleVariant));
   }

   public final void setLocale(Locale locale) {
      ResourceBundleFamily family = ResourceBundle.getBundle(-4248492586227566823L, "net.rim.device.internal.resource.Keypad");
      ResourceBundle bundle = family.getBundle(locale);
      this.setKeymap(locale, bundle);
      this._localeCode = locale.getCode();
   }

   private final void setKeymap(Locale locale, ResourceBundle bundle) {
      Locale osLocale = Locale.getDefaultForKeyboard();
      this._osLocale = osLocale.getCode();
      this._osLocaleVariant = Locale.convertStringToKeyboardID(osLocale.getVariant());
      this._keypadID = locale.isKeyboardIDSet() ? locale.getKeyboardID() : Keypad.getHardwareLayout();
   }
}
