package net.rim.device.apps.internal.bis.launch.resource;

import net.rim.device.api.system.EncodedImage;
import net.rim.device.resources.Resource;

public final class ImageCache {
   private static EncodedImage _helpImage = getEncodedImageResource("help.gif");

   private static final EncodedImage getEncodedImageResource(String name) {
      name.length();
      Resource resource = Resource.getResourceClass();
      if (resource != null) {
         byte[] data = resource.getResource(name);
         if (data != null) {
            return EncodedImage.createEncodedImage(data, 0, data.length);
         }
      }

      return null;
   }
}
