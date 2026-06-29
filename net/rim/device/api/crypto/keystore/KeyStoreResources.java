package net.rim.device.api.crypto.keystore;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.i18n.ResourceBundleFamily;

public final class KeyStoreResources {
   private static ResourceBundleFamily _rb = ResourceBundle.getBundle(60462186577914032L, "net.rim.device.internal.resource.crypto.KeyStore");

   public static final ResourceBundleFamily getResourceBundle() {
      return _rb;
   }

   public static final String getString(int id) {
      return _rb.getString(id);
   }

   public static final String[] getStringArray(int id) {
      return _rb.getStringArray(id);
   }
}
