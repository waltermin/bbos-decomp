package net.rim.device.apps.api.ribbon;

import net.rim.device.api.collection.ReadableLongMap;
import net.rim.device.api.system.ApplicationRegistry;

public class RibbonNetworkInfo {
   protected static long NETWORK_INFO_SINGLETON_GUID = -1767462308126915807L;
   public static final long NETWORK_PROPS_OPERATOR_NAME;
   public static final long NETWORK_PROPS_SPECIFIED_OPERATOR_NAME;
   public static final long NETWORK_PROPS_PDP_ERROR;
   public static final long NETWORK_PROPS_LOW_BATTERY;
   public static final long NETWORK_PROPS_LAST_PDP_ERROR_CAUSE;
   public static final long NETWORK_PROPS_SIM_STATUS;
   public static final long NETWORK_PROPS_E911_LOCK;
   public static final long NETWORK_PROPS_IDLE_MODE_TEXT;
   public static final long NETWORK_PROPS_NETWORK_STATE;
   public static final long NETWORK_PROPS_IMMEDIATE_DISPLAY_STRING;
   public static final long NETWORK_PROPS_MODEM_MODE_ENABLED;
   public static final long NETWORK_PROPS_WIFI_CONNECTION_TEXT;
   public static final long NETWORK_PROPS_ACTIVATION_REQUIRED;

   public static RibbonNetworkInfo getInstance() {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      return (RibbonNetworkInfo)ar.waitForStartup(NETWORK_INFO_SINGLETON_GUID);
   }

   public ReadableLongMap getNetworkPropsCollection() {
      throw null;
   }

   public String getOperatorName() {
      throw null;
   }

   public String getOperatorName(int _1, int _2, int _3, String _4, int _5) {
      throw null;
   }

   public void setIdleModeText(String _1) {
      throw null;
   }
}
