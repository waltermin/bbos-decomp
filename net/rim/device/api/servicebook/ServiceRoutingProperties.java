package net.rim.device.api.servicebook;

public final class ServiceRoutingProperties {
   private String _name;
   private int _linkType;
   private int _maxBandwidth;
   private int _monetaryImpact;
   private int _batteryImpact;
   private int _capabilities;
   public static final int LINK_TYPE_RF;
   public static final int LINK_TYPE_SERIAL;
   public static final int LINK_TYPE_WIFI;
   public static final int BANDWIDTH_9600_BPS;
   public static final int BANDWIDTH_57600_BPS;
   public static final int BANDWIDTH_115200_BPS;
   public static final int BANDWIDTH_11000000_BPS;
   public static final int BANDWIDTH_12000000_BPS;
   public static final int IMPACT_EXCELLENT;
   public static final int IMPACT_GOOD;
   public static final int IMPACT_AVERAGE;
   public static final int IMPACT_POOR;
   public static final int IMPACT_BAD;
   public static String MDP = "MDPConnection";
   public static String SRP_RF = "SRPConnection_RF";
   public static String SRP_WI_FI = "SRPConnection_WI_FI";
   public static String STP = "SerialBypass";
   public static String RCP_RF = "RCPConnection_RF";
   public static String RCP_WI_FI = "RCPConnection_WI_FI";
   public static final int INTERNET_CAPABILITY;
   public static final int ENTERPRISE_CAPABILITY;
   public static final int P2P_CAPABILITY;
   public static final int ETP_CAPABILITY;
   public static final int SNTP_CAPABILITY;
   public static final int BIS_CAPABILITY;

   public ServiceRoutingProperties(String name, int linkType, int maxBandwidth, int monetaryImpact, int batteryImpact, int capabilities) {
      this._name = name;
      this._linkType = linkType;
      this._maxBandwidth = maxBandwidth;
      this._monetaryImpact = monetaryImpact;
      this._batteryImpact = batteryImpact;
      this._capabilities = capabilities;
   }

   public final String getName() {
      return this._name;
   }

   public final int getLinkType() {
      return this._linkType;
   }

   public final int getMaxBandwidth() {
      return this._maxBandwidth;
   }

   public final int getMonetaryImpact() {
      return this._monetaryImpact;
   }

   public final int getBatteryImpact() {
      return this._batteryImpact;
   }

   public final int getLinkCapabilities() {
      return this._capabilities;
   }
}
