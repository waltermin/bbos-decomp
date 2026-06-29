package net.rim.device.apps.api.messaging.resources;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.i18n.ResourceBundleFamily;

public final class MessageResources {
   private static ResourceBundleFamily _rb = ResourceBundle.getBundle(1758158344049992104L, "net.rim.device.apps.internal.resource.Message");

   public static final ResourceBundleFamily getBundle() {
      return _rb;
   }

   public static final String getString(int id) {
      return _rb.getString(id);
   }

   public static final String[] getStringArray(int id) {
      return (Object[])_rb.getObject(id);
   }
}
