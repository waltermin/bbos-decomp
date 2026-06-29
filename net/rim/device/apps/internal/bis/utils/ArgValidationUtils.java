package net.rim.device.apps.internal.bis.utils;

public final class ArgValidationUtils {
   public static final void notNull(Object obj) {
      if (obj == null) {
         throw new Object("null argument");
      }
   }

   public static final boolean hasStringValueChanged(String original, String newer) {
      return newer != null && !newer.equals(original) && (!"".equals(newer) || original != null);
   }

   public static final boolean hasBooleanValueChanged(Boolean original, String newer) {
      if (newer != null) {
         Boolean newerBool = (Boolean)(new Object("true".equals(newer)));
         return !newerBool.equals(original);
      } else {
         return false;
      }
   }
}
