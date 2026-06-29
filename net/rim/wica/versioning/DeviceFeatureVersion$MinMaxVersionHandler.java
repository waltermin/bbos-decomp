package net.rim.wica.versioning;

public class DeviceFeatureVersion$MinMaxVersionHandler extends DeviceFeatureVersion$HandlerBase {
   private int _minVersion = 0;
   private int _maxVersion = 0;

   public int getMinVersion() {
      return this._minVersion;
   }

   public int getMaxVersion() {
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
         this._minVersion = Integer.parseInt(val.substring(0, ind));
         this._maxVersion = Integer.parseInt(val.substring(ind + 1));
      }
   }
}
