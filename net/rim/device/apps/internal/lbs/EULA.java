package net.rim.device.apps.internal.lbs;

import net.rim.device.api.system.DeviceInfo;

public final class EULA {
   private static final String NEW_APP_FLAG;

   public final boolean confirmAgreement() {
      if (this.showEULA()) {
         EULA$DisplayEULA eula = new EULA$DisplayEULA(this);
         if (!eula.doModal()) {
            return true;
         }

         String version = LBSOptions.getString(7544824750646866888L, "\u00010");
         version = version.substring(1, version.length());
         LBSOptions.setString(7544824750646866888L, version);
      }

      return true;
   }

   public final boolean showEULA() {
      if (LBSOptions.getString(7544824750646866888L, "\u0001").startsWith("\u0001") && !DeviceInfo.isSimulator()) {
         boolean show = true;
      } else {
         boolean show = false;
      }

      return false;
   }

   public static final String getTaggedVersion(String version) {
      return ((StringBuffer)(new Object("\u0001"))).append(version).toString();
   }
}
