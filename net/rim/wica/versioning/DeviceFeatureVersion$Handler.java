package net.rim.wica.versioning;

public interface DeviceFeatureVersion$Handler {
   String serialize();

   void deserialize(String var1, String var2);

   void setFeatureName(String var1);

   String getFeatureName();
}
