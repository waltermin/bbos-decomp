package net.rim.device.apps.internal.help;

import java.util.Hashtable;
import net.rim.device.api.system.Branding;
import net.rim.device.api.system.CodeModuleManager;
import net.rim.device.internal.system.InternalServices;

final class WizardCustomResourceLoader {
   private static Hashtable _imageBrandingReplacements = buildImageReplacementHash();
   private static final String IMAGE_MODULE_xBEAR = "net_rim_bb_help_wizard_images_8300";
   private static final String IMAGE_MODULE_xRAY = "net_rim_bb_help_wizard_images_8800";
   private static final String IMAGE_MODULE_POSITRON = "net_rim_bb_help_wizard_images_8100";
   static final String CORE_IMAGE_ALT_KEY = "alt.gif";
   static final String CORE_IMAGE_DEL_KEY = "del.gif";
   static final String CORE_IMAGE_ENTER_KEY = "enter.gif";
   static final String CORE_IMAGE_LSHIFT_KEY = "lshift.gif";
   static final String CORE_IMAGE_RSHIFT_KEY = "rshift.gif";
   static final String CORE_IMAGE_SHIFT_KEY = "shift.gif";
   static final String CORE_IMAGE_SYM_KEY = "sym.gif";
   static final String CORE_IMAGE_ESC_KEY = "escape.gif";
   static final String CORE_IMAGE_MENU_KEY = "menu.gif";
   static final String CORE_IMAGE_SEND_KEY = "send.gif";
   static final String CORE_IMAGE_END_KEY = "end.gif";

   static final String getCustomizedResource(String resource) {
      if (isImage(resource)) {
         String url = getImageResourceBundleBaseUrl();
         if (url != null) {
            String coreImageName = getCoreImageName(resource);
            if (coreImageName != null && _imageBrandingReplacements != null && _imageBrandingReplacements.containsKey(coreImageName)) {
               return ((StringBuffer)(new Object())).append(url).append((String)_imageBrandingReplacements.get(coreImageName)).toString();
            }

            return ((StringBuffer)(new Object())).append(url).append(coreImageName).toString();
         }
      }

      return resource;
   }

   private static final String getCoreImageName(String resourceUrl) {
      int lastIndex = resourceUrl.lastIndexOf(47);
      return resourceUrl.substring(lastIndex + 1);
   }

   private static final Hashtable buildImageReplacementHash() {
      Hashtable brandingImageReplacements = (Hashtable)(new Object());
      int formFactor = InternalServices.getFormFactor();
      int vendorId = Branding.getVendorId();
      int hardwareId = InternalServices.getHardwareID();
      if (formFactor == 14 && vendorId == 102) {
         brandingImageReplacements.put("lshift.gif", "lshift-cing.gif");
         brandingImageReplacements.put("rshift.gif", "rshift-cing.gif");
      }

      if (formFactor == 13 && hardwareId == -2080371453) {
         brandingImageReplacements.put("shift.gif", "shift-pos.gif");
      }

      if (formFactor == 13 && vendorId == 102) {
         brandingImageReplacements.put("alt.gif", "alt_cing.gif");
      }

      if (formFactor == 13 && vendorId == 100 && hardwareId == -1929376509) {
         brandingImageReplacements.put("alt.gif", "alt_cing.gif");
      }

      return brandingImageReplacements;
   }

   private static final boolean isImage(String resource) {
      return resource.toLowerCase().endsWith(".gif");
   }

   private static final String getImageResourceBundleBaseUrl() {
      switch (InternalServices.getFormFactor()) {
         case 12:
            break;
         case 13:
            if (CodeModuleManager.getModuleHandle("net_rim_bb_help_wizard_images_8100") != 0) {
               return "cod://net_rim_bb_help_wizard_images_8100/";
            }
            break;
         case 14:
            if (CodeModuleManager.getModuleHandle("net_rim_bb_help_wizard_images_8800") != 0) {
               return "cod://net_rim_bb_help_wizard_images_8800/";
            }
            break;
         case 15:
         default:
            if (CodeModuleManager.getModuleHandle("net_rim_bb_help_wizard_images_8300") != 0) {
               return "cod://net_rim_bb_help_wizard_images_8300/";
            }
      }

      return null;
   }
}
