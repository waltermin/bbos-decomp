package net.rim.device.internal.system;

import net.rim.device.api.system.ApplicationRegistry;

public class SIMServiceTable {
   protected byte[] _mappedSST = new byte[11];
   public static final int U_LOCAL_PHONE_BOOK = 0;
   public static final int B_FDN = 1;
   public static final int B_EXT_2 = 2;
   public static final int B_SDN = 3;
   public static final int B_EXT_3 = 4;
   public static final int B_BDN = 5;
   public static final int B_EXT_4 = 6;
   public static final int U_OCI_AND_OCT = 7;
   public static final int U_ICI_AND_ICT = 8;
   public static final int B_SMS = 9;
   public static final int B_SMSR = 10;
   public static final int B_SMS_PARAMS = 11;
   public static final int B_AOC = 12;
   public static final int U_CCP2 = 13;
   public static final int B_CELL_BROADCAST_MESSAGE_ID = 14;
   public static final int B_CELL_BROADCAST_MESSAGE_ID_RANGES = 15;
   public static final int B_GROUP_ID_LEVEL_1 = 16;
   public static final int B_GROUP_ID_LEVEL_2 = 17;
   public static final int B_SERVICE_PROVIDER_NAME = 18;
   public static final int B_USER_CONTROLLED_PLMN_SELECT_WITH_A_T = 19;
   public static final int B_MSISDN = 20;
   public static final int B_IMG = 21;
   public static final int B_SOLSA = 22;
   public static final int B_EMLPP = 23;
   public static final int B_AUTO_ANSWER_EMLPP = 24;
   public static final int B_RFU = 25;
   public static final int U_GSM_ACCESS = 26;
   public static final int B_DATA_DOWNLOAD_VIA_SMS_CB = 27;
   public static final int B_DATA_DOWNLOAD_VIA_SMS_PP = 28;
   public static final int U_CALL_CONTROL_USIM = 29;
   public static final int U_MO_SMS_CONTROL_USIM = 30;
   public static final int B_RUN_AT_COMMAND = 31;
   public static final int U_SHALL_BE_SET_TO_1 = 32;
   public static final int U_ENABLED_SERVICES_TABLE = 33;
   public static final int U_APN_CONTROL_LIST = 34;
   public static final int B_DEPERSONALIZATION_CONTROL_KEYS = 35;
   public static final int B_COOP_NETWORK_LIST = 36;
   public static final int U_GSM_SECURITY_CONTEXT = 37;
   public static final int B_CPBCCH_INFO = 38;
   public static final int B_INVESTIGATION_SCAN = 39;
   public static final int B_MEXE = 40;
   public static final int B_OPERATOR_CONTROLLED_PLMN_SELECT_WITH_A_T = 41;
   public static final int B_HPLMN_SELECT_WITH_A_T = 42;
   public static final int U_EXT_5 = 43;
   public static final int B_PLMN_NETWORK_NAME = 44;
   public static final int B_OPERATOR_PLMN_LIST = 45;
   public static final int B_MAILBOX_DIALLING_NUMBERS = 46;
   public static final int B_MESSAGE_WAITING_INDICATOR_STATUS = 47;
   public static final int B_CALL_FORWARDING_INDICATION_STATUS = 48;
   public static final int B_SERVICE_PROVIDER_DISPLAY_INFO = 49;
   public static final int B_MMS = 50;
   public static final int B_EXT_8 = 51;
   public static final int U_CALL_CONTROL_GPRS_USIM = 52;
   public static final int B_MMS_USER_CONNECT_PARAMS = 53;
   public static final int B_NIA = 54;
   public static final int B_VGCS_GROUP_ID_LIST = 55;
   public static final int B_VBS_GROUP_ID_LIST = 56;
   public static final int U_PSEUDONYM = 57;
   public static final int U_USER_CONTROLLED_PLMN_SELECT_FOR_WLAN = 58;
   public static final int U_OPERATOR_CONTROLLED_PLMN_SELECT_FOR_WLAN = 59;
   public static final int U_USER_CONTROLLED_WSID_LIST = 60;
   public static final int U_OPERATOR_CONTROLLED_WSID_LIST = 61;
   public static final int U_VGCS_SECURITY = 62;
   public static final int U_VBS_SECURITY = 63;
   public static final int U_WLAN_REAUTH_ID = 64;
   public static final int U_MULTIMEDIA_MESSAGES_STORAGE = 65;
   public static final int U_GBA = 66;
   public static final int U_MBMS_SECURITY = 67;
   public static final int U_DATA_DOWNLOAD_VIA_USSD = 68;
   public static final int U_EQUIVALENT_HPLMN = 69;
   public static final int U_ADDITIONAL_TERMINAL_PROFILE_AFTER_UICC = 70;
   public static final int S_CHV1_DISABLE_FUNC = 71;
   public static final int S_ADN = 72;
   public static final int S_CCP = 73;
   public static final int S_PLMN_SELECTOR = 74;
   public static final int S_EXT_1 = 75;
   public static final int S_LND = 76;
   public static final int S_MENU_SELECT = 77;
   public static final int S_CALL_CONTROL = 78;
   public static final int S_PROACTIVE_SIM = 79;
   public static final int S_MO_SMS_CONTROL_BY_SIM = 80;
   public static final int S_GPRS = 81;
   public static final int S_USSD_STRING_DATA_OBJECT_SUPPORTED_IN_CALL_CTL = 82;
   public static final int S_EXTENDED_CCP = 83;
   private static final int LAST_ENUM = 83;
   public static final int SERVICE_TABLE_ARRAY_LENGTH = 11;
   protected static long SIM_SERVICE_TABLE_SINGLETON_GUID = -6611160430748382700L;
   protected static SIMServiceTable _instance;

   public static SIMServiceTable getInstance() {
      if (_instance == null) {
         ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
         _instance = (SIMServiceTable)ar.get(SIM_SERVICE_TABLE_SINGLETON_GUID);
      }

      return _instance;
   }

   private boolean isEnabled(int identifier) {
      int mask = 1 << identifier % 8;
      return (this._mappedSST[identifier / 8] & mask) == mask;
   }

   public static boolean isServiceEnabled(int identifier) {
      SIMServiceTable instance = getInstance();
      return instance != null ? instance.isEnabled(identifier) : false;
   }

   public static boolean isPNNEnabled() {
      return isServiceEnabled(44);
   }

   public static boolean isPLMNEnabled() {
      return isServiceEnabled(19) || isServiceEnabled(74);
   }

   public static boolean isOPLEnabled() {
      return isServiceEnabled(45);
   }

   public static boolean isSPNEnabled() {
      return isServiceEnabled(18);
   }
}
