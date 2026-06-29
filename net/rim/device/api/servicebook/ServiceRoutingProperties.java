package net.rim.device.api.servicebook;

public final class ServiceRoutingProperties {
   private String _name;
   private int _linkType;
   private int _maxBandwidth;
   private int _monetaryImpact;
   private int _batteryImpact;
   private int _capabilities;
   public static final int LINK_TYPE_RF = 1;
   public static final int LINK_TYPE_SERIAL = 2;
   public static final int LINK_TYPE_WIFI = 3;
   public static final int BANDWIDTH_9600_BPS = 1;
   public static final int BANDWIDTH_57600_BPS = 6;
   public static final int BANDWIDTH_115200_BPS = 12;
   public static final int BANDWIDTH_11000000_BPS = 1146;
   public static final int BANDWIDTH_12000000_BPS = 1250;
   public static final int IMPACT_EXCELLENT = 0;
   public static final int IMPACT_GOOD = 1;
   public static final int IMPACT_AVERAGE = 2;
   public static final int IMPACT_POOR = 3;
   public static final int IMPACT_BAD = 4;
   public static String MDP = "MDPConnection";
   public static String SRP_RF = "SRPConnection_RF";
   public static String SRP_WI_FI = "SRPConnection_WI_FI";
   public static String STP = "SerialBypass";
   public static String RCP_RF = "RCPConnection_RF";
   public static String RCP_WI_FI = "RCPConnection_WI_FI";
   public static final int INTERNET_CAPABILITY = 1;
   public static final int ENTERPRISE_CAPABILITY = 2;
   public static final int P2P_CAPABILITY = 4;
   public static final int ETP_CAPABILITY = 8;
   public static final int SNTP_CAPABILITY = 16;
   public static final int BIS_CAPABILITY = 32;

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
