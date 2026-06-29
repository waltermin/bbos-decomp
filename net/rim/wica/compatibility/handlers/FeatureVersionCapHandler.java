package net.rim.wica.compatibility.handlers;

import net.rim.wica.compatibility.VersionContext;

public class FeatureVersionCapHandler implements CapabilityHandler {
   private FeatureVersionCapHandler$FeatureVersion[] _featureVersions;
   private String _name;

   public FeatureVersionCapHandler$FeatureVersion[] getFeatureVersions() {
      return this._featureVersions;
   }

   @Override
   public boolean isCapable(VersionContext versionContext) {
      return this.areVersionsCompatible(versionContext);
   }

   @Override
   public String getName() {
      return this._name;
   }

   public FeatureVersionCapHandler(String name, FeatureVersionCapHandler$FeatureVersion[] featureVersions) {
      this._name = name;
      this._featureVersions = featureVersions;
   }

   private boolean areVersionsCompatible(VersionContext versionContext) {
      if (versionContext == null) {
         throw new Object();
      }

      boolean result = true;

      for (int i = this._featureVersions.length - 1; i >= 0 && result; i--) {
         String version = (String)versionContext.get(this._featureVersions[i]._feature);
         result = result && this._featureVersions[i]._featureVersion.isInRange(version);
      }

      return result;
   }
}
