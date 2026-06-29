package net.rim.wica.runtime.resources;

import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.i18n.ResourceBundleFamily;
import net.rim.device.api.system.Bitmap;
import net.rim.device.resources.Resource;

public final class RuntimeResources {
   private static ResourceBundleFamily _resources = ResourceBundle.getBundle(1044214262601816921L, "net.rim.wica.runtime.resources.Runtime");

   public static final ResourceBundleFamily getResourceBundleFamily() {
      return _resources;
   }

   public static final String getString(int id) {
      return _resources.getString(id);
   }

   public static final String getString(int patternResource, String argument) {
      return getString(patternResource, new Object[]{argument});
   }

   public static final String getString(int patternResource, Object[] arguments) {
      String s = _resources.getString(patternResource);
      return MessageFormat.format(s, arguments);
   }

   public static final String[] getStringArray(int id) {
      return _resources.getStringArray(id);
   }

   public static final byte[] getBinaryResource(String name) {
      return Resource.getResourceClass().getResource(name);
   }

   public static final Bitmap getBitmapResource(String name) {
      return Bitmap.getBitmapResource(name);
   }
}
