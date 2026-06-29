package net.rim.device.apps.internal.api.crypto;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.i18n.ResourceBundleFamily;

public final class CryptoCommonResources {
   private static ResourceBundleFamily _rb = ResourceBundle.getBundle(6923097978240753508L, "net.rim.device.apps.internal.resource.crypto.CryptoCommon");

   public static final String getString(int id) {
      return _rb.getString(id);
   }

   public static final String[] getStringArray(int id) {
      return _rb.getStringArray(id);
   }

   public static final ResourceBundleFamily getBundle() {
      return _rb;
   }

   public static final String getCertificateContainerString(boolean startWithUpperCase, boolean plural) {
      int resourceId;
      if (startWithUpperCase) {
         if (plural) {
            resourceId = 11;
         } else {
            resourceId = 12;
         }
      } else if (plural) {
         resourceId = 9;
      } else {
         resourceId = 10;
      }

      return getString(resourceId);
   }

   public static final String getPGPContainerString(boolean startWithUpperCase, boolean plural) {
      if (startWithUpperCase) {
         return plural ? getString(15) : getString(16);
      } else {
         return plural ? getString(13) : getString(14);
      }
   }
}
