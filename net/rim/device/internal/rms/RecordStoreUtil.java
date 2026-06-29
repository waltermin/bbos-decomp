package net.rim.device.internal.rms;

import net.rim.device.api.system.CodeModuleManager;
import net.rim.device.resources.Resource;
import net.rim.device.resources.Resource$Internal;

public class RecordStoreUtil {
   private RecordStoreUtil() {
   }

   public static boolean isOnDevice(String midletSuiteName) {
      boolean isPresent = CodeModuleManager.getModuleHandle(midletSuiteName) != 0;
      if (!isPresent) {
         int[] moduleHandles = CodeModuleManager.getModuleHandles();

         for (int i = 0; i < moduleHandles.length; i++) {
            if (CodeModuleManager.isMidlet(moduleHandles[i])) {
               Resource resource = Resource$Internal.getResourceClass(CodeModuleManager.getModuleName(moduleHandles[i]));
               if (resource != null) {
                  byte[] data = resource.getProperty("MIDlet-Name");
                  if (data != null) {
                     String midletNameAttributeValue = new String(data, 2, data.length - 2);
                     data = resource.getProperty("MIDlet-Vendor");
                     String midletSuiteNameExtractedFromProperties;
                     if (data != null) {
                        String midletSuiteVendor = new String(data, 2, data.length - 2);
                        midletSuiteNameExtractedFromProperties = midletNameAttributeValue + midletSuiteVendor;
                     } else {
                        midletSuiteNameExtractedFromProperties = midletNameAttributeValue;
                     }

                     if (midletSuiteName.equals(midletSuiteNameExtractedFromProperties)) {
                        return true;
                     }
                  }
               }
            }
         }
      }

      return isPresent;
   }
}
