package net.rim.wica.versioning;

public class DeviceFeatureVersion$StringMinMaxVersionHandler extends DeviceFeatureVersion$HandlerBase {
   private String _minVersion;
   private String _maxVersion;

   public String getMinVersion() {
      return this._minVersion;
   }

   public String getMaxVersion() {
      return this._maxVersion;
   }

   @Override
   public String serialize() {
      return ((StringBuffer)(new Object())).append(super.serialize()).append(this._minVersion).append(":").append(this._maxVersion).toString();
   }

   @Override
   public void deserialize(String name, String val) {
      this.setFeatureName(name);
      int ind = val.indexOf(":");
      if (ind > 0 && ind < val.length()) {
         this._minVersion = val.substring(0, ind);
         this._maxVersion = val.substring(ind + 1);
      }
   }
}
