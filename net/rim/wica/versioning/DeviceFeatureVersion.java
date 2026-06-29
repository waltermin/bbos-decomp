package net.rim.wica.versioning;

import java.util.Hashtable;

public class DeviceFeatureVersion {
   static Hashtable deviceFeatureVersionHandlers = (Hashtable)(new Object());

   public static String getHandlerName(String featureName) {
      return (String)deviceFeatureVersionHandlers.get(featureName);
   }

   public static DeviceFeatureVersion$MinMaxVersionHandler createHandler(String name, int min, int max) {
      DeviceFeatureVersion$MinMaxVersionHandler devVer = new DeviceFeatureVersion$MinMaxVersionHandler();
      devVer.setFeatureName(name);
      devVer._minVersion = min;
      devVer._maxVersion = max;
      return devVer;
   }

   public static DeviceFeatureVersion$StringMinMaxVersionHandler createHandler(String name, String min, String max) {
      DeviceFeatureVersion$StringMinMaxVersionHandler devVer = new DeviceFeatureVersion$StringMinMaxVersionHandler();
      devVer.setFeatureName(name);
      devVer._minVersion = min;
      devVer._maxVersion = max;
      return devVer;
   }

   public static DeviceFeatureVersion$Handler getHandlerForFeature(String name) {
      String handlerName = getHandlerName(name);
      DeviceFeatureVersion$Handler devVer = null;
      if (handlerName != null) {
         try {
            Class cls = Class.forName(handlerName);
            return (DeviceFeatureVersion$Handler)cls.newInstance();
         } finally {
            return devVer;
         }
      } else {
         return devVer;
      }
   }

   static {
      deviceFeatureVersionHandlers.put("Discovery", "net.rim.wica.versioning.DeviceFeatureVersion$StringMinMaxVersionHandler");
      deviceFeatureVersionHandlers.put("Provisioning", "net.rim.wica.versioning.DeviceFeatureVersion$StringMinMaxVersionHandler");
      deviceFeatureVersionHandlers.put("ControlCenter", "net.rim.wica.versioning.DeviceFeatureVersion$StringMinMaxVersionHandler");
      deviceFeatureVersionHandlers.put("System", "net.rim.wica.versioning.DeviceFeatureVersion$MinMaxVersionHandler");
      deviceFeatureVersionHandlers.put("Transport", "net.rim.wica.versioning.DeviceFeatureVersion$MinMaxVersionHandler");
      deviceFeatureVersionHandlers.put("Security", "net.rim.wica.versioning.DeviceFeatureVersion$MinMaxVersionHandler");
   }
}
