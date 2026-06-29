package net.rim.device.apps.internal.mms.resources;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.i18n.ResourceBundleFamily;
import net.rim.device.api.system.Bitmap;
import net.rim.device.internal.ui.IconCollection;
import net.rim.device.internal.ui.Image;

public class MMSResources {
   private static ResourceBundle _mmsBundle = ResourceBundle.getBundle(8432718016989017157L, "net.rim.device.apps.internal.resource.MMS");
   private static final Image MOVE_IMAGE = IconCollection.get("net_rim_mms_MoveUpDown", 1).getImage(0);

   public static String getString(int id) {
      return _mmsBundle.getString(id);
   }

   public static ResourceBundleFamily getResourceBundle() {
      return _mmsBundle.getFamily();
   }

   public static Image getBlankTemplateImage() {
      return IconCollection.get("BlankTemplate", 1).getImage(0);
   }

   public static Bitmap getCalendarIcon() {
      return Bitmap.getBitmapResource("CalendarIcon.gif");
   }

   public static Bitmap getAddressIcon() {
      return Bitmap.getBitmapResource("AddressIcon.gif");
   }

   public static final Image getMoveImage() {
      return MOVE_IMAGE;
   }

   public static String getBrokenImageURI() {
      return "cod://net_rim_bb_mms/BrokenImage.gif";
   }
}
