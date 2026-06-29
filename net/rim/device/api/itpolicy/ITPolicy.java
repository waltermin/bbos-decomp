package net.rim.device.api.itpolicy;

import net.rim.device.api.system.ControlledAccess;
import net.rim.device.internal.system.ITPolicyInternal;
import net.rim.device.internal.system.InternalServices;
import net.rim.vm.TraceBack;

public final class ITPolicy {
   public static final long GUID_ITADMIN_DEBUG_MODE;
   public static final long GUID_IT_POLICY_CHANGED;
   public static final long GUID_IT_POLICY_CHANGED_LOCKED_HANDHELD;
   public static final long GUID_OWNER_INFO_CHANGED;
   public static final long GUID_P2P_CHANGED;
   public static final long DURESS_NOTIFICATION;
   public static final long OTA_FLUSH_NOTIFICATION;
   public static final long GUID_WIPEABLE_IT_POLICY_CHANGED;
   public static final long GUID_PASSWORD_SET_BY_ITADMIN;
   public static final int ENABLE_PHONE;
   public static final int ENABLE_BROWSER;
   public static final int SERVICE_BOOK_POLICY_1;
   public static final int SERVICE_BOOK_POLICY;
   public static final int ITPOLICY_NAME;
   public static final int PASSWORD_REQUIRED;
   public static final int ALLOW_PEER_TO_PEER;
   public static final int PASSWORD_MIN_LENGTH;
   public static final int PASSWORD_MAX_TIMEOUT;
   public static final int PASSWORD_MAX_AGE;
   public static final int ALLOW_USER_CHANGE_PASSWORD_TIMEOUT;
   public static final int PASSWORD_PATTERN_CHECKS;
   public static final int ENABLE_LONG_TERM_TIMEOUT;
   public static final int ENABLE_SMS;
   public static final int ALLOW_BCC_RECIPIENTS;
   public static final int HOMEPAGE_ADDRESS;
   public static final int HOMEPAGE_ADDRESS_READONLY;
   public static final int ENABLE_WAP_CONFIG;
   public static final int DEFAULT_BROWSER_CONFIG_UID;
   public static final int COMMON_POLICY;
   public static final int PASSWORD_POLICY;
   public static final int CMIME_APP_POLICY;
   public static final int SECURITY_POLICY;
   public static final int SMIME_APP_POLICY;
   public static final int PGP_APP_POLICY;
   public static final int MEMORY_CLEANER_APP_POLICY;
   public static final int TLS_POLICY;
   public static final int WTLS_POLICY;
   public static final int BROWSER_POLICY;
   public static final int STK_POLICY;
   public static final int TCP_POLICY;
   public static final int SYNC_POLICY;
   public static final int BLUETOOTH_POLICY;
   public static final int VOIP_POLICY;
   public static final int SMART_DIALING_POLICY;
   public static final int VPN_POLICY;
   public static final int WLAN_POLICY;
   public static final int HELP_POLICY;
   public static final int BLUETOOTH_SMART_CARD_READER_POLICY;
   public static final int MDS_POLICY;
   public static final int SECURE_EMAIL_POLICY;
   public static final int CAMERA_POLICY;
   public static final int AUTO_SIGNATURE;
   public static final int ENTERPRISE_VOICE_CLIENT_POLICY;
   public static final int FIREWALL_POLICY;
   public static final int OTA_UPGRADE_POLICY;
   public static final int CONTENT_PROTECTION_PUBLIC_KEY;
   public static final int WLAN_AGGREGATED_PROFILES_POLICY;
   public static final int ITPOLICY_VERIFICATION_KEYS;
   public static final int ITPOLICY_SIGNATURES;
   public static final int ITPOLICY_VERIFICATION_KEY;
   public static final int ITPOLICY_SIGNATURE;
   public static final int RESERVED_POLICY;
   public static final int USER_POLICY;
   public static final boolean ENABLE_PHONE_DEFAULT;
   public static final boolean ENABLE_BROWSER_DEFAULT;
   public static final boolean HOMEPAGE_ADDRESS_READONLY_DEFAULT;
   public static final boolean ENABLE_WAP_CONFIG_DEFAULT;
   public static final boolean ALLOW_PEER_TO_PEER_DEFAULT;
   public static final boolean ENABLE_LONG_TERM_TIMEOUT_DEFAULT;
   public static final boolean ENABLE_SMS_DEFAULT;
   public static final boolean ALLOW_BCC_RECIPIENTS_DEFAULT;
   public static final boolean ALLOW_USER_CHANGE_PASSWORD_TIMEOUT_DEFAULT;
   public static final int PASSWORD_MAX_AGE_DEFAULT;
   public static final int PASSWORD_MAX_TIMEOUT_DEFAULT;
   public static final int PASSWORD_MIN_LENGTH_DEFAULT;
   public static final int PASSWORD_PATTERN_CHECKS_DEFAULT;
   public static final boolean PASSWORD_REQUIRED_DEFAULT;
   public static final int TERNARY_TRUE;
   public static final int TERNARY_FALSE;
   public static final int TERNARY_PROMPT;
   public static final int PUBLIC_KEY_TAG;
   public static final int SIGNATURE_KEY_TAG;

   private ITPolicy() {
   }

   public static final String getString(String name) {
      return readString(255, name);
   }

   public static final byte[] getByteArray(String name) {
      return readByteArray(255, name);
   }

   public static final byte getByte(String name) {
      return readByte(255, name, (byte)0);
   }

   public static final boolean getBoolean(String name, boolean defaultValue) {
      return readByte(255, name, (byte)(defaultValue ? 1 : 0)) != 0;
   }

   public static final int getInteger(String name, int defaultValue) {
      return readInt(255, name, defaultValue);
   }

   public static final String getString(int id) {
      return readString(id, null);
   }

   public static final String getString(int group, int id) {
      String result = null;
      if (WipeITPolicyDirectory.isWipeableId(group, id)) {
         if (!ControlledAccess.verifyCodeModuleSignature(TraceBack.getCallingModule(0), 51)) {
            return null;
         }

         result = readStringWipeable(group, id);
      }

      return result == null ? readString(group, id) : result;
   }

   public static final byte[] getByteArray(int id) {
      return readByteArray(id, null);
   }

   public static final byte[] getByteArray(int group, int id) {
      byte[] result = null;
      if (WipeITPolicyDirectory.isWipeableId(group, id)) {
         if (!ControlledAccess.verifyCodeModuleSignature(TraceBack.getCallingModule(0), 51)) {
            return null;
         }

         result = readByteArrayWipeable(group, id);
      }

      return result == null ? readByteArray(group, id) : result;
   }

   public static final boolean getBoolean(int id, boolean defaultValue) {
      if (id != 6 || !getBoolean(24, 2, false) && !getBoolean(24, 63, false) && !getBoolean(24, 1, false)) {
         return id == 7 && !InternalServices.isPINMessagingSupported() ? false : readByte(id, null, (byte)(defaultValue ? 1 : 0)) != 0;
      } else {
         return true;
      }
   }

   public static final boolean getBoolean(int group, int id, boolean defaultValue) {
      if (group != 24 || id != 2 || !getBoolean(24, 63, false) && !getBoolean(24, 1, false)) {
         Byte result = null;
         if (WipeITPolicyDirectory.isWipeableId(group, id)) {
            if (!ControlledAccess.verifyCodeModuleSignature(TraceBack.getCallingModule(0), 51)) {
               return defaultValue;
            }

            result = readByteWipeable(group, id);
         }

         return result == null ? readByte(group, id, (byte)(defaultValue ? 1 : 0)) != 0 : result != 0;
      } else {
         return true;
      }
   }

   public static final int getInteger(int id, int defaultValue) {
      return readInt(id, null, defaultValue);
   }

   public static final int getInteger(int group, int id, int defaultValue) {
      Integer result = null;
      if (WipeITPolicyDirectory.isWipeableId(group, id)) {
         if (!ControlledAccess.verifyCodeModuleSignature(TraceBack.getCallingModule(0), 51)) {
            return defaultValue;
         }

         result = readIntegerWipeable(group, id);
      }

      return result == null ? readInt(group, id, defaultValue) : result;
   }

   public static final byte getByte(int id, byte defaultValue) {
      return readByte(id, null, defaultValue);
   }

   public static final byte getByte(int group, int id, byte defaultValue) {
      Byte result = null;
      if (WipeITPolicyDirectory.isWipeableId(group, id)) {
         if (!ControlledAccess.verifyCodeModuleSignature(TraceBack.getCallingModule(0), 51)) {
            return defaultValue;
         }

         result = readByteWipeable(group, id);
      }

      return result == null ? readByte(group, id, defaultValue) : result;
   }

   private static final native byte readByte(int var0, String var1, byte var2);

   private static final native byte readByte(int var0, int var1, byte var2);

   private static final native int readInt(int var0, String var1, int var2);

   private static final native int readInt(int var0, int var1, int var2);

   private static final native String readString(int var0, String var1);

   private static final native String readString(int var0, int var1);

   private static final native byte[] readByteArray(int var0, String var1);

   private static final native byte[] readByteArray(int var0, int var1);

   private static final Byte readByteWipeable(int group, int id) {
      return ITPolicyInternal.readByteInternal(readWipeablePolicyData(), group, id);
   }

   private static final String readStringWipeable(int group, int id) {
      return ITPolicyInternal.readStringInternal(readWipeablePolicyData(), group, id);
   }

   private static final Integer readIntegerWipeable(int group, int id) {
      return ITPolicyInternal.readIntegerInternal(readWipeablePolicyData(), group, id);
   }

   private static final byte[] readByteArrayWipeable(int group, int id) {
      return ITPolicyInternal.readByteArrayInternal(readWipeablePolicyData(), group, id);
   }

   private static final byte[] readWipeablePolicyData() {
      return ITPolicyInternal.readWipeablePolicyData();
   }
}
