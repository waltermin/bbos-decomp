package net.rim.wica.versioning;

public class DeviceFeatureVersion$HandlerBase implements DeviceFeatureVersion$Handler {
   protected String _name;

   @Override
   public String getFeatureName() {
      return this._name;
   }

   @Override
   public void setFeatureName(String name) {
      this._name = name;
   }

   @Override
   public String serialize() {
      return this._name + "=";
   }

   @Override
   public void deserialize(String _1, String _2) {
      throw null;
   }
}
