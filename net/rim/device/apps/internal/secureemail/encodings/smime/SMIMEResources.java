package net.rim.device.apps.internal.secureemail.encodings.smime;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.i18n.ResourceBundleFamily;

public final class SMIMEResources {
   private static ResourceBundleFamily _rb = ResourceBundle.getBundle(103984685412143679L, "net.rim.device.apps.internal.resource.secureemail.SMIME");

   public static final ResourceBundleFamily getBundle() {
      return _rb;
   }

   public static final String getString(int id) {
      return _rb.getString(id);
   }

   public static final String[] getStringArray(int id) {
      return _rb.getStringArray(id);
   }
}
