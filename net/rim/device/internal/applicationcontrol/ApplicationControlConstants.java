package net.rim.device.internal.applicationcontrol;

import net.rim.device.api.util.Arrays;

public class ApplicationControlConstants {
   public static final int MODULE_HASH_LENGTH;
   public static final byte[] EMPTY_HASH = new byte[20];
   public static final byte[] FILLED_HASH = new byte[20];
   public static final int REQUIRED_FLAG;
   public static final int EXCLUDED_FLAG;
   public static final int IPC_ALLOWED_FLAG;
   public static final int INTERNAL_ALLOWED_FLAG;
   public static final int INTERNAL_PROMPT_FLAG;
   public static final int EXTERNAL_ALLOWED_FLAG;
   public static final int EXTERNAL_PROMPT_FLAG;
   public static final int LOCAL_ALLOWED_FLAG;
   public static final int PHONE_ALLOWED_FLAG;
   public static final int PHONE_PROMPT_FLAG;
   public static final int EMAIL_ALLOWED_FLAG;
   public static final int PIM_ALLOWED_FLAG;
   public static final int BROWSER_FILTERS_FLAG;
   public static final int EVENT_INJECTOR_FLAG;
   public static final int BLUETOOTH_SERIAL_PROFILE_ALLOWED_FLAG;
   public static final int HANDHELD_KEY_STORE_ALLOWED_FLAG;
   public static final int KEY_STORE_MEDIUM_SECURITY_ALLOWED_FLAG;
   public static final int LAPI_ALLOWED_FLAG;
   public static final int LAPI_PROMPT_FLAG;
   public static final int THEME_DATA_ALLOWED_FLAG;
   public static final int AUTHENTICATOR_API_ALLOWED_FLAG;
   public static final int FILE_API_ALLOWED_FLAG;
   public static final int CMM_API_ALLOWED_FLAG;
   public static final int DEVICE_SETTINGS_ALLOWED_FLAG;
   public static final int DEVICE_SETTINGS_PROMPT_FLAG;
   public static final int SCREEN_CAPTURE_ALLOWED_FLAG;
   public static final int SCREEN_CAPTURE_PROMPT_FLAG;
   public static final int WIFI_ALLOWED_FLAG;
   public static final int WIFI_PROMPT_FLAG;
   public static final int IDLE_TIMER_ALLOWED_FLAG;
   public static final int IDLE_TIMER_PROMPT_FLAG;
   public static final int MEDIA_ALLOWED_FLAG;
   public static final int MEDIA_PROMPT_FLAG;
   public static final byte TAG_DOMAIN_INTERNAL_CONNECTIONS;
   public static final byte TAG_DOMAIN_EXTERNAL_CONNECTIONS;
   public static final byte TAG_DOMAIN_BROWSER_FILTERS;
   static final long FLAG;
   static final int RRI_BYPASS_FLAG;
   static final long ALL_FLAGS_MASK;
   public static final long ALLOW_FLAGS_MASK;
   public static final long PAIRED_ALLOW_FLAGS_MASK;
   public static final long NON_USER_FLAGS_MASK;
   public static final long RESTRICTIVE_SETTINGS_MASK;
   public static final long PERMISSIVE_SETTINGS_MASK;
   static final long INVERTED_SYNC_FLAGS;
   public static int[] APP_PERM_TO_APP_CONTROL_MAP = new int[]{
      20,
      14,
      12,
      23,
      22,
      10,
      13,
      5,
      21,
      15,
      3,
      2,
      16,
      7,
      17,
      8,
      11,
      25,
      19,
      27,
      29,
      31,
      -804650996,
      31,
      28,
      31,
      30,
      31,
      30,
      31,
      31,
      30,
      31,
      30,
      31,
      -804650996,
      31,
      29,
      31,
      30,
      31,
      30,
      31,
      31,
      30,
      31,
      30,
      31,
      -805044212,
      505355551,
      522133023,
      522067742,
      51,
      4408146,
      4801362,
      5391186,
      5526098,
      -805044096,
      1234518860,
      2049212631,
      1777570406,
      2140414811,
      1298472420,
      -521147027,
      1418454475,
      1438191017,
      1118189862,
      1955083787,
      -1645491194,
      -1786283218,
      759841631,
      1860705112,
      -1994617363,
      -1618316449,
      1309619627,
      1199574529,
      -466156065,
      -21253724,
      460597610,
      1748995976,
      -1486544065,
      -175135758,
      1933597168,
      -790102144,
      -2056848544,
      -476291070,
      -965384476,
      1233135489
   };

   static {
      Arrays.fill(FILLED_HASH, (byte)-1);
   }
}
