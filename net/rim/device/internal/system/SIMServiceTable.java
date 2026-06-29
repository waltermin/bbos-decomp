package net.rim.device.internal.system;

import net.rim.device.api.system.ApplicationRegistry;

public class SIMServiceTable {
   protected byte[] _mappedSST = new byte[11];
   public static final int U_LOCAL_PHONE_BOOK;
   public static final int B_FDN;
   public static final int B_EXT_2;
   public static final int B_SDN;
   public static final int B_EXT_3;
   public static final int B_BDN;
   public static final int B_EXT_4;
   public static final int U_OCI_AND_OCT;
   public static final int U_ICI_AND_ICT;
   public static final int B_SMS;
   public static final int B_SMSR;
   public static final int B_SMS_PARAMS;
   public static final int B_AOC;
   public static final int U_CCP2;
   public static final int B_CELL_BROADCAST_MESSAGE_ID;
   public static final int B_CELL_BROADCAST_MESSAGE_ID_RANGES;
   public static final int B_GROUP_ID_LEVEL_1;
   public static final int B_GROUP_ID_LEVEL_2;
   public static final int B_SERVICE_PROVIDER_NAME;
   public static final int B_USER_CONTROLLED_PLMN_SELECT_WITH_A_T;
   public static final int B_MSISDN;
   public static final int B_IMG;
   public static final int B_SOLSA;
   public static final int B_EMLPP;
   public static final int B_AUTO_ANSWER_EMLPP;
   public static final int B_RFU;
   public static final int U_GSM_ACCESS;
   public static final int B_DATA_DOWNLOAD_VIA_SMS_CB;
   public static final int B_DATA_DOWNLOAD_VIA_SMS_PP;
   public static final int U_CALL_CONTROL_USIM;
   public static final int U_MO_SMS_CONTROL_USIM;
   public static final int B_RUN_AT_COMMAND;
   public static final int U_SHALL_BE_SET_TO_1;
   public static final int U_ENABLED_SERVICES_TABLE;
   public static final int U_APN_CONTROL_LIST;
   public static final int B_DEPERSONALIZATION_CONTROL_KEYS;
   public static final int B_COOP_NETWORK_LIST;
   public static final int U_GSM_SECURITY_CONTEXT;
   public static final int B_CPBCCH_INFO;
   public static final int B_INVESTIGATION_SCAN;
   public static final int B_MEXE;
   public static final int B_OPERATOR_CONTROLLED_PLMN_SELECT_WITH_A_T;
   public static final int B_HPLMN_SELECT_WITH_A_T;
   public static final int U_EXT_5;
   public static final int B_PLMN_NETWORK_NAME;
   public static final int B_OPERATOR_PLMN_LIST;
   public static final int B_MAILBOX_DIALLING_NUMBERS;
   public static final int B_MESSAGE_WAITING_INDICATOR_STATUS;
   public static final int B_CALL_FORWARDING_INDICATION_STATUS;
   public static final int B_SERVICE_PROVIDER_DISPLAY_INFO;
   public static final int B_MMS;
   public static final int B_EXT_8;
   public static final int U_CALL_CONTROL_GPRS_USIM;
   public static final int B_MMS_USER_CONNECT_PARAMS;
   public static final int B_NIA;
   public static final int B_VGCS_GROUP_ID_LIST;
   public static final int B_VBS_GROUP_ID_LIST;
   public static final int U_PSEUDONYM;
   public static final int U_USER_CONTROLLED_PLMN_SELECT_FOR_WLAN;
   public static final int U_OPERATOR_CONTROLLED_PLMN_SELECT_FOR_WLAN;
   public static final int U_USER_CONTROLLED_WSID_LIST;
   public static final int U_OPERATOR_CONTROLLED_WSID_LIST;
   public static final int U_VGCS_SECURITY;
   public static final int U_VBS_SECURITY;
   public static final int U_WLAN_REAUTH_ID;
   public static final int U_MULTIMEDIA_MESSAGES_STORAGE;
   public static final int U_GBA;
   public static final int U_MBMS_SECURITY;
   public static final int U_DATA_DOWNLOAD_VIA_USSD;
   public static final int U_EQUIVALENT_HPLMN;
   public static final int U_ADDITIONAL_TERMINAL_PROFILE_AFTER_UICC;
   public static final int S_CHV1_DISABLE_FUNC;
   public static final int S_ADN;
   public static final int S_CCP;
   public static final int S_PLMN_SELECTOR;
   public static final int S_EXT_1;
   public static final int S_LND;
   public static final int S_MENU_SELECT;
   public static final int S_CALL_CONTROL;
   public static final int S_PROACTIVE_SIM;
   public static final int S_MO_SMS_CONTROL_BY_SIM;
   public static final int S_GPRS;
   public static final int S_USSD_STRING_DATA_OBJECT_SUPPORTED_IN_CALL_CTL;
   public static final int S_EXTENDED_CCP;
   private static final int LAST_ENUM;
   public static final int SERVICE_TABLE_ARRAY_LENGTH;
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
