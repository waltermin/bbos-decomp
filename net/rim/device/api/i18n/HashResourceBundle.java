package net.rim.device.api.i18n;

import net.rim.device.api.util.IntHashtable;

public class HashResourceBundle extends ResourceBundle {
   private IntHashtable _contents = new IntHashtable();

   public HashResourceBundle(Locale locale) {
      super(locale);
   }

   @Override
   protected Object handleGetObject(int key) {
      return this._contents.get(key);
   }

   public void put(int key, Object value) {
      if (value == null) {
         this._contents.remove(key);
      } else {
         this._contents.put(key, value);
      }

      ResourceBundleFamily family = this.getFamily();
      if (family != null) {
         family.clearEntry(key);
      }
   }
}
