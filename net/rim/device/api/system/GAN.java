package net.rim.device.api.system;

public final class GAN {
   public static final int GAN_EVENT_OCCURRED = 1596;
   public static final int GAN_EVENT_DIAGNOSTICS_CHANGED = 0;
   public static final int GAN_EVENT_REGISTRATION_EVENT = 1;
   public static final int GAN_EVENT_HANDOVER_TO_GAN_FAILED = 2;
   public static final int GAN_EVENT_ROVE_IN_FAILED = 3;
   public static final int GAN_EVENT_UTC_REQ_FAILED = 4;
   public static final int GAN_EVENT_NEW_SERVICE_ZONE = 5;
   public static final int STATE_OFF = 0;
   public static final int STATE_NO_COVERAGE = 1;
   public static final int STATE_TRYING = 2;
   public static final int STATE_SUCCESS = 3;
   public static final int STATE_ERROR = 4;
   public static final int STATE_UNKNOWN = 5;
   public static final int STATUS_UNKNOWN = 0;
   public static final int STATUS_OK = 1;
   public static final int STATUS_COVERAGE_LOST = 2;
   public static final int STATUS_NO_DNS_SERVER = 3;
   public static final int STATUS_PROVISIONING_SEGW_DNS_FAILED = 4;
   public static final int STATUS_DEFAULT_SEGW_DNS_FAILED = 5;
   public static final int STATUS_SERVING_SEGW_DNS_FAILED = 6;
   public static final int STATUS_PROVISIONING_SEGW_VPN_TIMEOUT = 7;
   public static final int STATUS_DEFAULT_SEGW_VPN_TIMEOUT = 8;
   public static final int STATUS_SERVING_SEGW_VPN_TIMEOUT = 9;
   public static final int STATUS_PROVISIONING_SEGW_VPN_AUTH_FAILED = 10;
   public static final int STATUS_DEFAULT_SEGW_VPN_AUTH_FAILED = 11;
   public static final int STATUS_SERVING_SEGW_VPN_AUTH_FAILED = 12;
   public static final int STATUS_PROVISIONING_SEGW_BAD_CERTIFICATE = 13;
   public static final int STATUS_DEFAULT_SEGW_BAD_CERTIFICATE = 14;
   public static final int STATUS_SERVING_SEGW_BAD_CERTIFICATE = 15;
   public static final int STATUS_PROVISIONING_GANC_DNS_FAILED = 16;
   public static final int STATUS_DEFAULT_GANC_DNS_FAILED = 17;
   public static final int STATUS_SERVING_GANC_DNS_FAILED = 18;
   public static final int STATUS_PROVISIONING_GANC_TCP_REFUSED = 19;
   public static final int STATUS_DEFAULT_GANC_TCP_REFUSED = 20;
   public static final int STATUS_SERVING_GANC_TCP_REFUSED = 21;
   public static final int STATUS_PROVISIONING_GANC_TCP_FAILED = 22;
   public static final int STATUS_DEFAULT_GANC_TCP_FAILED = 23;
   public static final int STATUS_SERVING_GANC_TCP_FAILED = 24;
   public static final int STATUS_PROVISIONING_GANC_TCP_RESET = 25;
   public static final int STATUS_DEFAULT_GANC_TCP_RESET = 26;
   public static final int STATUS_SERVING_GANC_TCP_RESET = 27;
   public static final int STATUS_DISCOVERY_REJECT = 28;
   public static final int STATUS_DEFAULT_REGISTER_REJECT = 29;
   public static final int STATUS_SERVING_REGISTER_REJECT = 30;
   public static final int STATUS_INTERNAL_ERROR = 31;
   public static final int DRC_NETWORK_CONGESTION = 0;
   public static final int DRC_REJECT_UNSPECIFIED = 1;
   public static final int DRC_REJECT_IMSI_NOT_ALLOWED = 2;
   public static final int RRC_NETWORK_CONJESTION = 0;
   public static final int RRC_AP_NOT_ALLOWED = 1;
   public static final int RRC_LOCATION_NOT_ALLOWED = 2;
   public static final int RRC_INVALID_GANC = 3;
   public static final int RRC_GEO_LOCATION_NOT_KNOWN = 4;
   public static final int RRC_IMSI_NOT_ALLOWED = 5;
   public static final int RRC_UNSPECIFIED = 6;
   public static final int RRC_GANC_SEGW_CERTIFICATE_NOT_VALID = 7;
   public static final int RRC_EAP_SIM_AUTHENTICATION_FAILED = 8;
   public static final int RRC_TCP_ESTABLISHMENT_FAILED = 9;
   public static final int RRC_REDIRECTION = 10;
   public static final int RRC_EAP_AKA_AUTHENTICATION_FAILED = 11;
   public static final int UTC_TIMEOUT = -1;
   public static final int UTC_SUCCESS = 0;
   public static final int UTC_NO_RESOURCES = 2;
   public static final int UTC_GANC_FAILURE = 3;
   public static final int UTC_SERVICE_NOT_AUTHORIZED = 4;
   public static final int UTC_MESSAGE_NOT_SUPPORTED = 5;
   public static final int UTC_MESSAGE_WRONG_PROTOCOL_STATE = 6;
   public static final int UTC_MANDATORY_INFO_INVALID = 7;
   public static final int UTC_MESSAGE_SYNTAX_INCORRECT = 8;
   public static final int UTC_GPRS_SUSPENDED = 9;
   public static final int UTC_NORMAL_DEACTIVATION = 10;
   public static final int UTC_CONDITIONAL_IE_ERROR = 12;
   public static final int UTC_MESSAGE_SEMANTICS_INCORRECT = 13;
   public static final int UTC_PS_HANDOVER_COMMAND_INCORRECT = 14;
   public static final int UTC_PS_HANDOVER_RAT_ACCESS_FAILURE = 15;
   public static final int UTC_PS_HANDOVER_MISSING_SI_INFO = 16;
   public static final int UTC_PS_HANDOVER_NO_UPLINK_TBF_ALLOC = 17;
   public static final int ROVE_THRESHOLD_LOW = 0;
   public static final int ROVE_THRESHOLD_MEDIUM = 1;
   public static final int ROVE_THRESHOLD_HIGH = 2;
   public static final int MIN_SIGNAL_STRENGTH_THRESHOLD = 0;
   public static final int MAX_SIGNAL_STRENGTH_THRESHOLD = 63;
   public static final int DEFAULT_SIGNAL_STRENGTH_THRESHOLD = 255;
   public static final int MIN_SIGNAL_QUALITY_THRESHOLD = 0;
   public static final int MAX_SIGNAL_QUALITY_THRESHOLD = 7;
   public static final int DEFAULT_SIGNAL_QUALITY_THRESHOLD = 255;
   public static final int GAN_PROTOCOL_VERSION_UMA_1_0_0 = 0;
   public static final int GAN_PROTOCOL_VERSION_UMA_1_0_1 = 1;
   public static final int GAN_PROTOCOL_VERSION_UMA_1_0_2 = 2;
   public static final int GAN_PROTOCOL_VERSION_UMA_1_0_3 = 3;
   public static final int GAN_PROTOCOL_VERSION_UMA_1_0_4 = 4;
   public static final int GAN_PROTOCOL_VERSION_3GPP_R6 = 5;
   public static final int SERVICE_ZONE_INFO_NONE = 0;
   public static final int SERVICE_ZONE_INFO_UNLIMITED_CALLS = 1;
   public static final long GUID_GAN_CONFIGURATION_CHANGED = -5567093064078848383L;
   private static final long ID_GAN_SYSTEM = 4566345875717469622L;
   private static GANSystem _system;

   private GAN() {
   }

   public static final void setGANSystem(GANSystem system) {
      ApplicationRegistry appReg = ApplicationRegistry.getApplicationRegistry();
      if (appReg != null) {
         if (appReg.replace(4566345875717469622L, system) != null) {
            throw new IllegalStateException("GAN system already present");
         }

         _system = system;
      }
   }

   public static final GANSystem getGANSystem() {
      if (_system == null) {
         ApplicationRegistry appReg = ApplicationRegistry.getApplicationRegistry();
         _system = appReg != null ? (GANSystem)appReg.get(4566345875717469622L) : null;
      }

      return _system;
   }

   public static final boolean isGANAllowed() {
      GANSystem gan = getGANSystem();
      return gan != null ? gan.isGANAllowed() : false;
   }

   public static final void setGANOverride(boolean ganOverride) {
      GANSystem system = getGANSystem();
      if (system != null) {
         system.setGANOverride(ganOverride);
      }
   }

   public static final native boolean setGANProvisioningAddresses(String var0, int var1, String var2, int var3);

   public static final native boolean setSEGWSecurityCertificate(byte[] var0, boolean var1);

   public static final native GANExtendedNetInfo getExtendedGANNetworkInfo();

   public static final native GANServiceZoneInfo getGANServiceZoneInfo();

   public static final native boolean setGANThresholdValues(int var0, int var1, int var2);

   public static final native boolean setGANProtocolVersion(int var0);
}
