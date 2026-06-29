package net.rim.wica.common;

public class ReservedWicletConstants {
   public static final long RESERVED_WICLET_ID_RANGE;
   public static final String RESERVED_WICLET_URI_PREFIX;
   public static final String RIM_WICA_URI;
   public static final String BUILT_IN_CMP_URI;
   public static final int SYSTEMMESSAGES_WICLET_ID;
   public static final int PROVISIONING_WICLET_ID;
   public static final int DISCOVERY_WICLET_ID;
   public static final int CONTROLCENTRE_WICLET_ID;
   public static final int SYSTEMMESSAGES_WICLET_ID_2;
   public static final String SYSTEMMESSAGES_WICLET_URI;
   public static final String SYSTEMMESSAGES_WICLET_URI_2;
   public static final String PROVISIONNG_WICLET_URI;
   public static final String DISCOVERY_WICLET_URI;
   public static final String CONTROLCENTRE_WICLET_URI;

   public static boolean isSystemWiclet(String uri) {
      return uri.startsWith("rim.net/mds/systemmessages");
   }

   public static boolean isReservedWiclet(String uri) {
      return uri.startsWith("rim.net/mds");
   }
}
