package javax.microedition.content;

import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.CodeModuleManager;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.internal.system.CodeModuleGroupProperties;
import net.rim.device.internal.system.CodeModuleGroupPropertiesCollection;
import net.rim.device.resources.Resource;
import net.rim.device.resources.Resource$Internal;

final class ContentHandlerUtilities {
   private static final String SLASH_SLASH;
   private static final String DEVICE_SIDE;

   private ContentHandlerUtilities() {
   }

   static final boolean containsDuplicates(String[] strings) {
      String[] sortedStrings = new String[strings.length];

      for (int i = 0; i < strings.length; i++) {
         sortedStrings[i] = strings[i];
      }

      Arrays.sort(sortedStrings, new ContentHandlerUtilities$StringComparator());

      for (int i = 0; i < sortedStrings.length - 1; i++) {
         if (sortedStrings[i].equals(sortedStrings[i + 1])) {
            return true;
         }
      }

      return false;
   }

   static final void checkStringArrayValues(String[] strings, boolean throwExceptionIfNull) {
      if (strings != null || throwExceptionIfNull) {
         if (strings == null) {
            throw new NullPointerException("An array parameter is null");
         }

         for (int i = 0; i < strings.length; i++) {
            if (strings[i] == null) {
               throw new NullPointerException("An array contains a null value");
            }

            if (strings[i].length() == 0) {
               throw new IllegalArgumentException("Zero length Strings not permitted");
            }
         }
      }
   }

   static final void checkStringArrayValues(String[] strings) {
      checkStringArrayValues(strings, true);
   }

   static final String getStringValue(String key, int moduleHandle) {
      String value = null;
      Resource resource = Resource$Internal.getResourceClass(CodeModuleManager.getModuleName(moduleHandle));
      if (resource != null) {
         if (CodeModuleManager.isMidlet(moduleHandle) && CodeModuleManager.getModuleTrailer(moduleHandle, 2, 0) == null) {
            String name = getStringValue("MIDlet-Name", resource);
            String vendor = getStringValue("MIDlet-Vendor", resource);
            if (name != null && vendor != null) {
               int uid = CodeModuleGroupPropertiesCollection.getGroupUID(name + ":" + vendor);
               CodeModuleGroupProperties cmgp = (CodeModuleGroupProperties)CodeModuleGroupPropertiesCollection.getInstance().getSyncObject(uid);
               if (cmgp != null) {
                  value = (String)cmgp.get(key);
               }
            }
         }

         if (value == null || value.length() == 0) {
            value = getStringValue(key, resource);
         }
      }

      return value;
   }

   private static final String getStringValue(String key, Resource resource) {
      byte[] data = resource.getProperty(key);
      return data != null ? new String(data, 2, data.length - 2) : null;
   }

   static final String checkURL(String url) {
      int positionOfColon = url.indexOf(58);
      if (positionOfColon == -1) {
         throw new IllegalArgumentException("Invalid URL");
      }

      String scheme = StringUtilities.toLowerCase(url.substring(0, positionOfColon), 1701707776);
      if (scheme != null && scheme.length() != 0) {
         char c = scheme.charAt(0);
         if (c >= 'a' && c <= 'z') {
            for (int i = 1; i < scheme.length(); i++) {
               c = scheme.charAt(i);
               if (!Character.isDigit(c) && (c < 'a' || c > 'z') && c != '+' && c != '-' && c != '.') {
                  throw new IllegalArgumentException("Invalid URL");
               }
            }

            int slashesIndex = url.indexOf("//");
            if (slashesIndex == -1) {
               throw new IllegalArgumentException("Invalid URL");
            } else {
               String hostPart = url.substring(slashesIndex + 2);
               if (hostPart == null || hostPart.length() == 0) {
                  throw new IllegalArgumentException("Invalid URL");
               } else {
                  return scheme.equals("socket") && url.indexOf(";deviceside=") == -1 ? url + ";deviceside=" + "true" : url;
               }
            }
         } else {
            throw new IllegalArgumentException("Invalid URL");
         }
      } else {
         throw new IllegalArgumentException("Invalid URL");
      }
   }

   static final ApplicationDescriptor findApplicationDescriptor(int moduleHandle, String classname) {
      ApplicationDescriptor[] descriptors = CodeModuleManager.getApplicationDescriptors(moduleHandle);
      ApplicationDescriptor application = null;
      if (CodeModuleManager.isMidlet(moduleHandle)) {
         for (int i = 0; i < descriptors.length; i++) {
            String[] args = descriptors[i].getArgs();
            if (args[0].equals(classname)) {
               application = descriptors[i];
               break;
            }
         }

         if (application == null && descriptors[0] != null) {
            return new ApplicationDescriptor(descriptors[0], new String[]{classname});
         }
      } else {
         if (descriptors != null && descriptors.length > 0) {
            return new ApplicationDescriptor(descriptors[0], new String[0]);
         }

         application = new ApplicationDescriptor(ApplicationDescriptor.currentApplicationDescriptor(), new String[0]);
      }

      return application;
   }
}
