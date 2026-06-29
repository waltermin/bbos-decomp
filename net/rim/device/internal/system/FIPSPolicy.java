package net.rim.device.internal.system;

import net.rim.device.api.itpolicy.ITPolicy;

public final class FIPSPolicy {
   public static final boolean PASSWORD_REQUIRED_FIPS_DEFAULT;
   public static final int PASSWORD_MIN_LENGTH_FIPS_DEFAULT;
   public static final boolean SUPPRESS_PASSWORD_ECHO_FIPS_DEFAULT;
   public static final boolean TLS_RESTRICT_FIPS_CIPHERS_FIPS_DEFAULT;
   public static final boolean WTLS_RESTRICT_FIPS_CIPHERS_FIPS_DEFAULT;

   public static final boolean getBoolean(int id, boolean defaultValue, boolean fipsDefaultValue) {
      return getFIPSLevel() >= 2 ? fipsDefaultValue : ITPolicy.getBoolean(id, defaultValue);
   }

   public static final boolean getBoolean(int group, int id, boolean defaultValue, boolean fipsDefaultValue) {
      return getFIPSLevel() >= 2 ? fipsDefaultValue : ITPolicy.getBoolean(group, id, defaultValue);
   }

   public static final int getMaxInteger(int id, int defaultValue, int fipsDefaultValue) {
      return getFIPSLevel() >= 2 ? Math.max(ITPolicy.getInteger(id, defaultValue), fipsDefaultValue) : ITPolicy.getInteger(id, defaultValue);
   }

   public static final int getInteger(int id, int group, int defaultValue, int fipsDefaultValue) {
      return getFIPSLevel() >= 2 ? fipsDefaultValue : ITPolicy.getInteger(id, group, defaultValue);
   }

   public static final boolean isDevicePasswordRequired() {
      return getBoolean(6, false, true);
   }

   public static final boolean disallowThirdPartyAppDownload() {
      return getFIPSLevel() >= 2 ? true : ITPolicy.getBoolean(24, 11, false);
   }

   public static final int getFIPSLevel() {
      return ITPolicy.getByte(24, 39, (byte)1);
   }
}
