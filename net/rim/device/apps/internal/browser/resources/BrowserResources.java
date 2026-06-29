package net.rim.device.apps.internal.browser.resources;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.i18n.ResourceBundleFamily;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.apps.internal.resource.BrowserResource;
import net.rim.device.resources.Resource;
import net.rim.device.resources.Resource$Internal;

public final class BrowserResources implements BrowserResource {
   private static ResourceBundleFamily _resourceBundle = ResourceBundle.getBundle(-229261654107783483L, "net.rim.device.apps.internal.resource.Browser");
   private static ResourceBundleFamily _httpResourceBundle = ResourceBundle.getBundle(-6246750274064102835L, "net.rim.device.internal.resource.HTTP");

   public static final ResourceBundleFamily getResourceBundle() {
      return _resourceBundle;
   }

   public static final String getString(int id) {
      return _resourceBundle.getString(id);
   }

   public static final String[] getStringArray(int id) {
      return _resourceBundle.getStringArray(id);
   }

   public static final String getHttpString(int id) {
      return _httpResourceBundle.getString(id);
   }

   public static final EncodedImage getPictogramImage(String id) {
      Resource resource = Resource$Internal.getResourceClass("net_rim_bb_browser_lib");
      if (resource != null) {
         int indexOfSlash = id.indexOf(47, 7);
         if (indexOfSlash >= 0 && indexOfSlash + 1 < id.length()) {
            byte[] data = resource.getResource("pictogram/" + id.substring(indexOfSlash + 1) + ".png");
            if (data != null) {
               return EncodedImage.createEncodedImage(data, 0, -1);
            }
         }
      }

      return null;
   }
}
