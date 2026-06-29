package net.rim.device.api.itpolicy;

import net.rim.device.api.system.ControlledAccess;
import net.rim.device.internal.system.ITPolicyInternal;
import net.rim.device.internal.system.InternalServices;
import net.rim.vm.TraceBack;

public final class ITPolicy {
   public static final long GUID_ITADMIN_DEBUG_MODE = 6058454246005266633L;
   public static final long GUID_IT_POLICY_CHANGED = 8508406279413621091L;
   public static final long GUID_IT_POLICY_CHANGED_LOCKED_HANDHELD = -594020114676189989L;
   public static final long GUID_OWNER_INFO_CHANGED = -8392006003204551101L;
   public static final long GUID_P2P_CHANGED = -2475029172703491550L;
   public static final long DURESS_NOTIFICATION = 4681343386835470834L;
   public static final long OTA_FLUSH_NOTIFICATION = 978519096100388739L;
   public static final long GUID_WIPEABLE_IT_POLICY_CHANGED = -467871197336216166L;
   public static final long GUID_PASSWORD_SET_BY_ITADMIN = 1309561383038111736L;
   public static final int ENABLE_PHONE = 1;
   public static final int ENABLE_BROWSER = 2;
   public static final int SERVICE_BOOK_POLICY_1 = 3;
   public static final int SERVICE_BOOK_POLICY = 4;
   public static final int ITPOLICY_NAME = 5;
   public static final int PASSWORD_REQUIRED = 6;
   public static final int ALLOW_PEER_TO_PEER = 7;
   public static final int PASSWORD_MIN_LENGTH = 8;
   public static final int PASSWORD_MAX_TIMEOUT = 10;
   public static final int PASSWORD_MAX_AGE = 11;
   public static final int ALLOW_USER_CHANGE_PASSWORD_TIMEOUT = 12;
   public static final int PASSWORD_PATTERN_CHECKS = 13;
   public static final int ENABLE_LONG_TERM_TIMEOUT = 14;
   public static final int ENABLE_SMS = 15;
   public static final int ALLOW_BCC_RECIPIENTS = 16;
   public static final int HOMEPAGE_ADDRESS = 17;
   public static final int HOMEPAGE_ADDRESS_READONLY = 18;
   public static final int ENABLE_WAP_CONFIG = 19;
   public static final int DEFAULT_BROWSER_CONFIG_UID = 20;
   public static final int COMMON_POLICY = 21;
   public static final int PASSWORD_POLICY = 22;
   public static final int CMIME_APP_POLICY = 23;
   public static final int SECURITY_POLICY = 24;
   public static final int SMIME_APP_POLICY = 25;
   public static final int PGP_APP_POLICY = 26;
   public static final int MEMORY_CLEANER_APP_POLICY = 27;
   public static final int TLS_POLICY = 28;
   public static final int WTLS_POLICY = 29;
   public static final int BROWSER_POLICY = 30;
   public static final int STK_POLICY = 31;
   public static final int TCP_POLICY = 32;
   public static final int SYNC_POLICY = 33;
   public static final int BLUETOOTH_POLICY = 34;
   public static final int VOIP_POLICY = 36;
   public static final int SMART_DIALING_POLICY = 37;
   public static final int VPN_POLICY = 38;
   public static final int WLAN_POLICY = 40;
   public static final int HELP_POLICY = 42;
   public static final int BLUETOOTH_SMART_CARD_READER_POLICY = 43;
   public static final int MDS_POLICY = 44;
   public static final int SECURE_EMAIL_POLICY = 45;
   public static final int CAMERA_POLICY = 47;
   public static final int AUTO_SIGNATURE = 94;
   public static final int ENTERPRISE_VOICE_CLIENT_POLICY = 102;
   public static final int FIREWALL_POLICY = 103;
   public static final int OTA_UPGRADE_POLICY = 120;
   public static final int CONTENT_PROTECTION_PUBLIC_KEY = 248;
   public static final int WLAN_AGGREGATED_PROFILES_POLICY = 249;
   public static final int ITPOLICY_VERIFICATION_KEYS = 250;
   public static final int ITPOLICY_SIGNATURES = 251;
   public static final int ITPOLICY_VERIFICATION_KEY = 252;
   public static final int ITPOLICY_SIGNATURE = 253;
   public static final int RESERVED_POLICY = 254;
   public static final int USER_POLICY = 255;
   public static final boolean ENABLE_PHONE_DEFAULT = true;
   public static final boolean ENABLE_BROWSER_DEFAULT = true;
   public static final boolean HOMEPAGE_ADDRESS_READONLY_DEFAULT = false;
   public static final boolean ENABLE_WAP_CONFIG_DEFAULT = true;
   public static final boolean ALLOW_PEER_TO_PEER_DEFAULT = true;
   public static final boolean ENABLE_LONG_TERM_TIMEOUT_DEFAULT = false;
   public static final boolean ENABLE_SMS_DEFAULT = true;
   public static final boolean ALLOW_BCC_RECIPIENTS_DEFAULT = true;
   public static final boolean ALLOW_USER_CHANGE_PASSWORD_TIMEOUT_DEFAULT = true;
   public static final int PASSWORD_MAX_AGE_DEFAULT = 0;
   public static final int PASSWORD_MAX_TIMEOUT_DEFAULT = 60;
   public static final int PASSWORD_MIN_LENGTH_DEFAULT = 4;
   public static final int PASSWORD_PATTERN_CHECKS_DEFAULT = 0;
   public static final boolean PASSWORD_REQUIRED_DEFAULT = false;
   public static final int TERNARY_TRUE = 0;
   public static final int TERNARY_FALSE = 1;
   public static final int TERNARY_PROMPT = 2;
   public static final int PUBLIC_KEY_TAG = 1;
   public static final int SIGNATURE_KEY_TAG = 1;

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
