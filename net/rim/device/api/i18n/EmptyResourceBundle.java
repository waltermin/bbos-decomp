package net.rim.device.api.i18n;

class EmptyResourceBundle extends ResourceBundle {
   EmptyResourceBundle(Locale locale) {
      super(locale);
   }

   @Override
   protected Object handleGetObject(int key) {
      return null;
   }
}
