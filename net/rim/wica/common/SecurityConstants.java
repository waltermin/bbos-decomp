package net.rim.wica.common;

public class SecurityConstants {
   public static final int SECURITY_VERSION = 1;
   public static final int SECURITY_MODE_SIGNED_ONLY = 0;
   public static final int SECURITY_MODE_SIGNED_AND_ENCRYPTED = 1;
   public static final int SECURITY_MODE_UNSECURE = 2;

   public static String securityModeToString(int securityMode) {
      switch (securityMode) {
         case -1: {
            String securityModeAsString = "INVALID";
            return securityModeAsString;
         }
         case 0:
         default: {
            String securityModeAsString = "SECURITY_MODE_SIGNED_ONLY";
            return securityModeAsString;
         }
         case 1: {
            String securityModeAsString = "SECURITY_MODE_SIGNED_AND_ENCRYPTED";
            return securityModeAsString;
         }
         case 2:
            return "SECURITY_MODE_UNSECURE";
      }
   }
}
