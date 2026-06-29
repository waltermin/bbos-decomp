package net.rim.wica.runtime.util.internal;

import net.rim.device.api.system.CodeModuleManager;
import net.rim.wica.runtime.provisioning.DeploymentDescriptor;
import net.rim.wica.runtime.provisioning.DeploymentDescriptor$Dependency;
import net.rim.wica.runtime.util.Util;

public final class RuntimeUtilities {
   private static final String DEFAULT_RUNTIME_VERSION = "4.3.0";
   private static final String DEFAULT_RUNTIME_COMPATIBILITY_VERSION = "4.1.4";
   static Class class$net$rim$wica$runtime$util$internal$RuntimeUtilities;

   private RuntimeUtilities() {
   }

   public static final boolean validateRuntimeCompatibility(String version) {
      boolean supported = false;
      String[] reqVersion = Util.split(version, '.');
      String[] comVersion = Util.split("4.1.4", '.');
      if (reqVersion.length >= comVersion.length) {
         try {
            int reqNumber = Integer.parseInt(reqVersion[0]) * 100 + Integer.parseInt(reqVersion[1]) * 10 + Integer.parseInt(reqVersion[2]);
            int comNumber = Integer.parseInt(comVersion[0]) * 100 + Integer.parseInt(comVersion[1]) * 10 + Integer.parseInt(comVersion[2]);
            return comNumber >= reqNumber;
         } finally {
            ;
         }
      } else {
         return supported;
      }
   }

   public static final boolean isRuntimeCompatible(DeploymentDescriptor descriptor) {
      DeploymentDescriptor$Dependency[] dependencies = descriptor.getDependencies();
      DeploymentDescriptor$Dependency currentDep = null;
      boolean compatible = true;

      for (int i = 0; i < dependencies.length; i++) {
         currentDep = dependencies[i];
         if (currentDep.getType() == 1) {
            compatible = validateRuntimeCompatibility(currentDep.getVersion());
         }
      }

      return compatible;
   }

   public static final String getRuntimeVersion(boolean fullVersion) {
      int handle = CodeModuleManager.getModuleHandleForClass(
         class$net$rim$wica$runtime$util$internal$RuntimeUtilities == null
            ? (class$net$rim$wica$runtime$util$internal$RuntimeUtilities = class$("net.rim.wica.runtime.util.internal.RuntimeUtilities"))
            : class$net$rim$wica$runtime$util$internal$RuntimeUtilities
      );
      if (handle <= 0) {
         return "4.3.0";
      }

      String version = CodeModuleManager.getModuleVersion(handle);
      int versionLength = version.length();
      int state = 0;
      int i = 0;
      int thirdPeriodIndex = -1;

      while (state != -1 && i < versionLength) {
         char c = version.charAt(i);
         switch (state) {
            case -1:
               break;
            case 0:
            default:
               if (c == '.') {
                  state = 1;
               } else if (c < '0' || c > '9') {
                  state = -1;
               }
               break;
            case 1:
               if (c == '.') {
                  state = 2;
               } else if (c < '0' || c > '9') {
                  state = -1;
               }
               break;
            case 2:
               if (c == '.') {
                  state = 3;
                  thirdPeriodIndex = i;
               } else if (c < '0' || c > '9') {
                  state = -1;
               }
               break;
            case 3:
               if (c < '0' || c > '9') {
                  state = -1;
               }
         }

         i++;
      }

      if (thirdPeriodIndex < 0 || i != versionLength || state != 3) {
         return "4.3.0";
      } else {
         return fullVersion ? version : version.substring(0, thirdPeriodIndex);
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   static final Class class$(String x0) {
      try {
         return Class.forName(x0);
      } catch (Throwable var3) {
         throw new Object(x1.getMessage());
      }
   }
}
