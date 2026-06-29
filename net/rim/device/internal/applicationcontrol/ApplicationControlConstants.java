package net.rim.device.internal.applicationcontrol;

import net.rim.device.api.util.Arrays;

public class ApplicationControlConstants {
   public static final int MODULE_HASH_LENGTH = 20;
   public static final byte[] EMPTY_HASH = new byte[20];
   public static final byte[] FILLED_HASH = new byte[20];
   public static final int REQUIRED_FLAG = 0;
   public static final int EXCLUDED_FLAG = 1;
   public static final int IPC_ALLOWED_FLAG = 2;
   public static final int INTERNAL_ALLOWED_FLAG = 3;
   public static final int INTERNAL_PROMPT_FLAG = 4;
   public static final int EXTERNAL_ALLOWED_FLAG = 5;
   public static final int EXTERNAL_PROMPT_FLAG = 6;
   public static final int LOCAL_ALLOWED_FLAG = 7;
   public static final int PHONE_ALLOWED_FLAG = 8;
   public static final int PHONE_PROMPT_FLAG = 9;
   public static final int EMAIL_ALLOWED_FLAG = 10;
   public static final int PIM_ALLOWED_FLAG = 11;
   public static final int BROWSER_FILTERS_FLAG = 12;
   public static final int EVENT_INJECTOR_FLAG = 13;
   public static final int BLUETOOTH_SERIAL_PROFILE_ALLOWED_FLAG = 14;
   public static final int HANDHELD_KEY_STORE_ALLOWED_FLAG = 15;
   public static final int KEY_STORE_MEDIUM_SECURITY_ALLOWED_FLAG = 16;
   public static final int LAPI_ALLOWED_FLAG = 17;
   public static final int LAPI_PROMPT_FLAG = 18;
   public static final int THEME_DATA_ALLOWED_FLAG = 19;
   public static final int AUTHENTICATOR_API_ALLOWED_FLAG = 20;
   public static final int FILE_API_ALLOWED_FLAG = 21;
   public static final int CMM_API_ALLOWED_FLAG = 22;
   public static final int DEVICE_SETTINGS_ALLOWED_FLAG = 23;
   public static final int DEVICE_SETTINGS_PROMPT_FLAG = 24;
   public static final int SCREEN_CAPTURE_ALLOWED_FLAG = 25;
   public static final int SCREEN_CAPTURE_PROMPT_FLAG = 26;
   public static final int WIFI_ALLOWED_FLAG = 27;
   public static final int WIFI_PROMPT_FLAG = 28;
   public static final int IDLE_TIMER_ALLOWED_FLAG = 29;
   public static final int IDLE_TIMER_PROMPT_FLAG = 30;
   public static final int MEDIA_ALLOWED_FLAG = 31;
   public static final int MEDIA_PROMPT_FLAG = 32;
   public static final byte TAG_DOMAIN_INTERNAL_CONNECTIONS = 1;
   public static final byte TAG_DOMAIN_EXTERNAL_CONNECTIONS = 2;
   public static final byte TAG_DOMAIN_BROWSER_FILTERS = 3;
   static final long FLAG = Long.MIN_VALUE;
   static final int RRI_BYPASS_FLAG = 63;
   static final long ALL_FLAGS_MASK = -2147483648L;
   public static final long ALLOW_FLAGS_MASK = 3873059760727130112L;
   public static final long PAIRED_ALLOW_FLAGS_MASK = 1477252511105548288L;
   public static final long NON_USER_FLAGS_MASK = -4611677222334365695L;
   public static final long RESTRICTIVE_SETTINGS_MASK = 2229815737965346816L;
   public static final long PERMISSIVE_SETTINGS_MASK = 3869672972855803904L;
   static final long INVERTED_SYNC_FLAGS = 7769595838464L;
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
