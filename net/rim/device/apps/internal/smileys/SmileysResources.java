package net.rim.device.apps.internal.smileys;

import net.rim.device.api.i18n.ResourceBundle;

public class SmileysResources {
   private static ResourceBundle _rb = ResourceBundle.getBundle(6018740113250121715L, "net.rim.device.apps.internal.smileys.Smileys");

   public static String[] getStringArray(int id) {
      try {
         return _rb.getStringArray(id);
      } finally {
         ;
      }
   }
}
