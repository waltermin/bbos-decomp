package net.rim.device.apps.api.ribbon;

import net.rim.device.api.collection.ReadableLongMap;
import net.rim.device.api.system.ApplicationRegistry;

public class RibbonNetworkInfo {
   protected static long NETWORK_INFO_SINGLETON_GUID = -1767462308126915807L;
   public static final long NETWORK_PROPS_OPERATOR_NAME = -7219683504990287771L;
   public static final long NETWORK_PROPS_SPECIFIED_OPERATOR_NAME = -8817962046913284182L;
   public static final long NETWORK_PROPS_PDP_ERROR = 1040431808191919625L;
   public static final long NETWORK_PROPS_LOW_BATTERY = -1986748551626928033L;
   public static final long NETWORK_PROPS_LAST_PDP_ERROR_CAUSE = -7072296818759564103L;
   public static final long NETWORK_PROPS_SIM_STATUS = -8960794396193289546L;
   public static final long NETWORK_PROPS_E911_LOCK = 1812321293200299507L;
   public static final long NETWORK_PROPS_IDLE_MODE_TEXT = -7608742199570488450L;
   public static final long NETWORK_PROPS_NETWORK_STATE = 4822241500382547294L;
   public static final long NETWORK_PROPS_IMMEDIATE_DISPLAY_STRING = 6665563664396523075L;
   public static final long NETWORK_PROPS_MODEM_MODE_ENABLED = 2594167647522947521L;
   public static final long NETWORK_PROPS_WIFI_CONNECTION_TEXT = -1839664706744174700L;
   public static final long NETWORK_PROPS_ACTIVATION_REQUIRED = 3170113883629495887L;

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
