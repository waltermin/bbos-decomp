package net.rim.device.api.crypto.certificate;

import net.rim.device.api.i18n.ResourceBundle;

public final class CertificateResources {
   private static ResourceBundle _rb = ResourceBundle.getBundle(-6045559534068186282L, "net.rim.device.internal.resource.crypto.Certificate");

   public static final String getString(int id) {
      return _rb.getString(id);
   }

   public static final String[] getStringArray(int id) {
      return _rb.getStringArray(id);
   }
}
