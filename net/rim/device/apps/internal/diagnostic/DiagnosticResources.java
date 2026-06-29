package net.rim.device.apps.internal.diagnostic;

import net.rim.device.api.i18n.ResourceBundle;

public final class DiagnosticResources {
   private static ResourceBundle _strings = ResourceBundle.getBundle(3021852233071414445L, "net.rim.device.apps.internal.diagnostic.Diagnostics");

   private DiagnosticResources() {
   }

   public static final String getString(int id) {
      return _strings.getString(id);
   }
}
