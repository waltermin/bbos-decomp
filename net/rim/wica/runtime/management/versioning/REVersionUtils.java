package net.rim.wica.runtime.management.versioning;

import java.util.Vector;
import net.rim.wica.compatibility.VersionContext;
import net.rim.wica.versioning.DeviceFeatureVersion;
import net.rim.wica.versioning.DeviceFeatureVersion$Handler;
import net.rim.wica.versioning.DeviceVersions;
import net.rim.wica.versioning.ServerFeatureVersion;
import net.rim.wica.versioning.ServerVersions;

public class REVersionUtils {
   public static final String DISCOVERY_VERSION_MIN = "1.1.0";
   public static final String DISCOVERY_VERSION_MAX = "1.2.0";
   public static final String PROVISIONING_VERSION_MIN = "1.1.0";
   public static final String PROVISIONING_VERSION_MAX = "1.1.0";
   public static final String CONTROL_CENTER_VERSION_MIN = "1.1.0";
   public static final String CONTROL_CENTER_VERSION_MAX = "1.1.0";
   public static final int SYSTEM_MSG_VERSION_MIN = 1;
   public static final int SYSTEM_MSG_VERSION_MAX = 4;
   public static final int TRANSPORT_VERSION_MIN = 1;
   public static final int TRANSPORT_VERSION_MAX = 2;
   public static final int SECURITY_VERSION_MIN = 1;
   public static final int SECURITY_VERSION_MAX = 2;

   public static String getVersion(VersionContext versionContext, String version) {
      return (String)versionContext.get(version);
   }

   public static void setVersion(VersionContext versionContext, String versionName, Object versionValue) {
      versionContext.put(versionName, versionValue);
   }

   public static byte[] getDeviceVersions() {
      Vector vec = (Vector)(new Object());
      DeviceFeatureVersion$Handler ver = DeviceFeatureVersion.createHandler("Discovery", "1.1.0", "1.2.0");
      vec.addElement(ver);
      ver = DeviceFeatureVersion.createHandler("Provisioning", "1.1.0", "1.1.0");
      vec.addElement(ver);
      ver = DeviceFeatureVersion.createHandler("ControlCenter", "1.1.0", "1.1.0");
      vec.addElement(ver);
      ver = DeviceFeatureVersion.createHandler("Transport", 1, 2);
      vec.addElement(ver);
      ver = DeviceFeatureVersion.createHandler("System", 1, 4);
      vec.addElement(ver);
      DeviceVersions devVer = DeviceVersions.createFromFeatures(vec);
      return devVer.serialize();
   }

   public static VersionContext getVersionContext(byte[] versions) {
      VersionContext vc = new VersionContext();
      if (versions == null) {
         return null;
      }

      ServerVersions srvVers = ServerVersions.createByDeserializing(versions);
      Vector vers = srvVers.getFeatureVersions();

      for (int i = vers.size() - 1; i >= 0; i--) {
         ServerFeatureVersion sfv = (ServerFeatureVersion)vers.elementAt(i);
         vc.put(sfv.getFeatureName(), sfv.getCurrentVersion());
      }

      return vc;
   }
}
