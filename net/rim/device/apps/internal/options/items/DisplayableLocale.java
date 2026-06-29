package net.rim.device.apps.internal.options.items;

import net.rim.device.api.i18n.Locale;

final class DisplayableLocale {
   private Locale _locale;
   private String _displayString;

   public DisplayableLocale(Locale locale) {
      this._locale = locale;
   }

   public DisplayableLocale(Locale locale, String displayString) {
      this(locale);
      this._displayString = displayString;
   }

   public final Locale getLocale() {
      return this._locale;
   }

   @Override
   public final String toString() {
      if (this._displayString != null) {
         return this._displayString;
      }

      StringBuffer sb = new StringBuffer(this._locale.getDisplayName());
      if (sb.length() > 0) {
         char c = sb.charAt(0);
         sb.setCharAt(0, Character.toUpperCase(c));
      }

      this._displayString = sb.toString();
      return this._displayString;
   }
}
