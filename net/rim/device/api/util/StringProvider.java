package net.rim.device.api.util;

import net.rim.device.api.i18n.Locale;
import net.rim.device.api.i18n.ResourceBundleFamily;

public final class StringProvider {
   private boolean _useResource;
   private String _stringSet;
   private ResourceBundleFamily _family;
   private int _id;
   private String _string;
   private Locale _locale;

   public StringProvider(ResourceBundleFamily family, int id) {
      this._family = family;
      this._id = id;
      this._useResource = true;
      this.resetStringBaseline();
   }

   public StringProvider(String string) {
      this.setString(string);
      this.resetStringBaseline();
   }

   public final String getString() {
      return this._string;
   }

   public final boolean isStringDifferent() {
      return this._useResource ? this._locale != Locale.getDefault() : this._string == this._stringSet;
   }

   public final void resetStringBaseline() {
      if (this._useResource) {
         this._string = this._family.getString(this._id);
         this._locale = Locale.getDefault();
      } else {
         this._string = this._stringSet;
      }
   }

   public final void setString(ResourceBundleFamily family, int id) {
      this._family = family;
      this._id = id;
      this._useResource = true;
      this._locale = null;
   }

   public final void setString(String string) {
      this._useResource = false;
      this._stringSet = string;
   }
}
