package net.rim.wica.common;

public class ReservedWicletConstants {
   public static final long RESERVED_WICLET_ID_RANGE = 16L;
   public static final String RESERVED_WICLET_URI_PREFIX = "rim.net/mds";
   public static final String RIM_WICA_URI = "http://www.rim.net/wica/";
   public static final String BUILT_IN_CMP_URI = "http://www.rim.net/wica/builtins";
   public static final int SYSTEMMESSAGES_WICLET_ID = 0;
   public static final int PROVISIONING_WICLET_ID = 1;
   public static final int DISCOVERY_WICLET_ID = 2;
   public static final int CONTROLCENTRE_WICLET_ID = 3;
   public static final int SYSTEMMESSAGES_WICLET_ID_2 = 4;
   public static final String SYSTEMMESSAGES_WICLET_URI = "rim.net/mds/systemmessages";
   public static final String SYSTEMMESSAGES_WICLET_URI_2 = "rim.net/mds/systemmessages1.2.0";
   public static final String PROVISIONNG_WICLET_URI = "rim.net/mds/provisioning";
   public static final String DISCOVERY_WICLET_URI = "rim.net/mds/discovery";
   public static final String CONTROLCENTRE_WICLET_URI = "rim.net/mds/controlcentre";

   public static boolean isSystemWiclet(String uri) {
      return uri.startsWith("rim.net/mds/systemmessages");
   }

   public static boolean isReservedWiclet(String uri) {
      return uri.startsWith("rim.net/mds");
   }
}
