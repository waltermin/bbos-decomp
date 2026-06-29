package net.rim.device.api.system;

public final class GAN {
   public static final int GAN_EVENT_OCCURRED;
   public static final int GAN_EVENT_DIAGNOSTICS_CHANGED;
   public static final int GAN_EVENT_REGISTRATION_EVENT;
   public static final int GAN_EVENT_HANDOVER_TO_GAN_FAILED;
   public static final int GAN_EVENT_ROVE_IN_FAILED;
   public static final int GAN_EVENT_UTC_REQ_FAILED;
   public static final int GAN_EVENT_NEW_SERVICE_ZONE;
   public static final int STATE_OFF;
   public static final int STATE_NO_COVERAGE;
   public static final int STATE_TRYING;
   public static final int STATE_SUCCESS;
   public static final int STATE_ERROR;
   public static final int STATE_UNKNOWN;
   public static final int STATUS_UNKNOWN;
   public static final int STATUS_OK;
   public static final int STATUS_COVERAGE_LOST;
   public static final int STATUS_NO_DNS_SERVER;
   public static final int STATUS_PROVISIONING_SEGW_DNS_FAILED;
   public static final int STATUS_DEFAULT_SEGW_DNS_FAILED;
   public static final int STATUS_SERVING_SEGW_DNS_FAILED;
   public static final int STATUS_PROVISIONING_SEGW_VPN_TIMEOUT;
   public static final int STATUS_DEFAULT_SEGW_VPN_TIMEOUT;
   public static final int STATUS_SERVING_SEGW_VPN_TIMEOUT;
   public static final int STATUS_PROVISIONING_SEGW_VPN_AUTH_FAILED;
   public static final int STATUS_DEFAULT_SEGW_VPN_AUTH_FAILED;
   public static final int STATUS_SERVING_SEGW_VPN_AUTH_FAILED;
   public static final int STATUS_PROVISIONING_SEGW_BAD_CERTIFICATE;
   public static final int STATUS_DEFAULT_SEGW_BAD_CERTIFICATE;
   public static final int STATUS_SERVING_SEGW_BAD_CERTIFICATE;
   public static final int STATUS_PROVISIONING_GANC_DNS_FAILED;
   public static final int STATUS_DEFAULT_GANC_DNS_FAILED;
   public static final int STATUS_SERVING_GANC_DNS_FAILED;
   public static final int STATUS_PROVISIONING_GANC_TCP_REFUSED;
   public static final int STATUS_DEFAULT_GANC_TCP_REFUSED;
   public static final int STATUS_SERVING_GANC_TCP_REFUSED;
   public static final int STATUS_PROVISIONING_GANC_TCP_FAILED;
   public static final int STATUS_DEFAULT_GANC_TCP_FAILED;
   public static final int STATUS_SERVING_GANC_TCP_FAILED;
   public static final int STATUS_PROVISIONING_GANC_TCP_RESET;
   public static final int STATUS_DEFAULT_GANC_TCP_RESET;
   public static final int STATUS_SERVING_GANC_TCP_RESET;
   public static final int STATUS_DISCOVERY_REJECT;
   public static final int STATUS_DEFAULT_REGISTER_REJECT;
   public static final int STATUS_SERVING_REGISTER_REJECT;
   public static final int STATUS_INTERNAL_ERROR;
   public static final int DRC_NETWORK_CONGESTION;
   public static final int DRC_REJECT_UNSPECIFIED;
   public static final int DRC_REJECT_IMSI_NOT_ALLOWED;
   public static final int RRC_NETWORK_CONJESTION;
   public static final int RRC_AP_NOT_ALLOWED;
   public static final int RRC_LOCATION_NOT_ALLOWED;
   public static final int RRC_INVALID_GANC;
   public static final int RRC_GEO_LOCATION_NOT_KNOWN;
   public static final int RRC_IMSI_NOT_ALLOWED;
   public static final int RRC_UNSPECIFIED;
   public static final int RRC_GANC_SEGW_CERTIFICATE_NOT_VALID;
   public static final int RRC_EAP_SIM_AUTHENTICATION_FAILED;
   public static final int RRC_TCP_ESTABLISHMENT_FAILED;
   public static final int RRC_REDIRECTION;
   public static final int RRC_EAP_AKA_AUTHENTICATION_FAILED;
   public static final int UTC_TIMEOUT;
   public static final int UTC_SUCCESS;
   public static final int UTC_NO_RESOURCES;
   public static final int UTC_GANC_FAILURE;
   public static final int UTC_SERVICE_NOT_AUTHORIZED;
   public static final int UTC_MESSAGE_NOT_SUPPORTED;
   public static final int UTC_MESSAGE_WRONG_PROTOCOL_STATE;
   public static final int UTC_MANDATORY_INFO_INVALID;
   public static final int UTC_MESSAGE_SYNTAX_INCORRECT;
   public static final int UTC_GPRS_SUSPENDED;
   public static final int UTC_NORMAL_DEACTIVATION;
   public static final int UTC_CONDITIONAL_IE_ERROR;
   public static final int UTC_MESSAGE_SEMANTICS_INCORRECT;
   public static final int UTC_PS_HANDOVER_COMMAND_INCORRECT;
   public static final int UTC_PS_HANDOVER_RAT_ACCESS_FAILURE;
   public static final int UTC_PS_HANDOVER_MISSING_SI_INFO;
   public static final int UTC_PS_HANDOVER_NO_UPLINK_TBF_ALLOC;
   public static final int ROVE_THRESHOLD_LOW;
   public static final int ROVE_THRESHOLD_MEDIUM;
   public static final int ROVE_THRESHOLD_HIGH;
   public static final int MIN_SIGNAL_STRENGTH_THRESHOLD;
   public static final int MAX_SIGNAL_STRENGTH_THRESHOLD;
   public static final int DEFAULT_SIGNAL_STRENGTH_THRESHOLD;
   public static final int MIN_SIGNAL_QUALITY_THRESHOLD;
   public static final int MAX_SIGNAL_QUALITY_THRESHOLD;
   public static final int DEFAULT_SIGNAL_QUALITY_THRESHOLD;
   public static final int GAN_PROTOCOL_VERSION_UMA_1_0_0;
   public static final int GAN_PROTOCOL_VERSION_UMA_1_0_1;
   public static final int GAN_PROTOCOL_VERSION_UMA_1_0_2;
   public static final int GAN_PROTOCOL_VERSION_UMA_1_0_3;
   public static final int GAN_PROTOCOL_VERSION_UMA_1_0_4;
   public static final int GAN_PROTOCOL_VERSION_3GPP_R6;
   public static final int SERVICE_ZONE_INFO_NONE;
   public static final int SERVICE_ZONE_INFO_UNLIMITED_CALLS;
   public static final long GUID_GAN_CONFIGURATION_CHANGED;
   private static final long ID_GAN_SYSTEM;
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
