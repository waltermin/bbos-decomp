package net.rim.wica.compatibility;

import java.util.Hashtable;
import net.rim.wica.compatibility.handlers.CapabilityHandler;
import net.rim.wica.compatibility.handlers.FeatureVersionCapHandler;
import net.rim.wica.compatibility.handlers.FeatureVersionCapHandler$FeatureVersion;
import net.rim.wica.compatibility.versionranges.IntVersionRange;
import net.rim.wica.compatibility.versionranges.WicletVersionRange;

public class Capabilities {
   public static final String PUSH_RESERVED_WICLETS_FEATURE = "push_reserved_wiclets";
   public static final String LOCALE_FEATURE = "locale";
   public static final String AUTHENTICATE_FEATURE = "authenticate";
   static Hashtable tbl = (Hashtable)(new Object());
   static final FeatureVersionCapHandler$FeatureVersion[] LOCALE_FEATURES = new FeatureVersionCapHandler$FeatureVersion[]{
      new FeatureVersionCapHandler$FeatureVersion("Discovery", new WicletVersionRange("1.2.0"))
   };
   static final FeatureVersionCapHandler$FeatureVersion[] AUTHENTICATE_FEATURES = new FeatureVersionCapHandler$FeatureVersion[]{
      new FeatureVersionCapHandler$FeatureVersion("System", new IntVersionRange(3))
   };
   static final FeatureVersionCapHandler$FeatureVersion[] PUSH_RESERVED_WICLETS_FEATURES = new FeatureVersionCapHandler$FeatureVersion[]{
      new FeatureVersionCapHandler$FeatureVersion("Security", new IntVersionRange(2))
   };
   public static final CapabilityHandler LOCALIZATION_CAPABILITY_Handler = new FeatureVersionCapHandler("locale", LOCALE_FEATURES);
   public static final CapabilityHandler AUTHENTICATION_CAPABILITY_Handler = new FeatureVersionCapHandler("authenticate", AUTHENTICATE_FEATURES);
   public static final CapabilityHandler PUSH_RESERVED_WICLET_CAPABILITY_Handler = new FeatureVersionCapHandler(
      "push_reserved_wiclets", PUSH_RESERVED_WICLETS_FEATURES
   );

   public static CapabilityHandler getCapabilityHandler(String name) {
      return (CapabilityHandler)tbl.get(name);
   }

   static {
      tbl.put(LOCALIZATION_CAPABILITY_Handler.getName(), LOCALIZATION_CAPABILITY_Handler);
      tbl.put(AUTHENTICATION_CAPABILITY_Handler.getName(), AUTHENTICATION_CAPABILITY_Handler);
      tbl.put(PUSH_RESERVED_WICLET_CAPABILITY_Handler.getName(), PUSH_RESERVED_WICLET_CAPABILITY_Handler);
   }
}
