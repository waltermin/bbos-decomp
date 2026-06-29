package net.rim.device.apps.internal.browser.util;

import java.io.InputStream;
import net.rim.device.api.math.Fixed32;
import net.rim.device.api.system.Display;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.resources.Resource;

public final class ImageConverter {
   public static final EncodedImage convert(InputStream in, String contentType) {
      byte[] data = RendererControl.readBytesFromInputStream(in);
      return data == null ? null : convert(data, 0, data.length, contentType);
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public static final EncodedImage convert(byte[] data, int offset, int length, String contentType) {
      if (data == null) {
         return null;
      }

      if (contentType != null && contentType.equals("image/vnd.rim.png")) {
         contentType = "image/png";
      }

      try {
         EncodedImage image = null;
         boolean var10 = false /* VF: Semaphore variable */;

         try {
            var10 = true;
            image = EncodedImage.createEncodedImage(data, offset, length, contentType);
            var10 = false;
         } finally {
            if (var10) {
               return EncodedImage.createEncodedImage(data, offset, length, null);
            }
         }

         return image;
      } finally {
         ;
      }
   }

   public static final EncodedImage convertAndScale(InputStream in, String contentType, int desiredWidth, int desiredHeight, int maxWidth) {
      byte[] data = RendererControl.readBytesFromInputStream(in);
      return data == null ? null : convertAndScale(data, 0, data.length, contentType, desiredWidth, desiredHeight, maxWidth);
   }

   public static final EncodedImage convertAndScale(byte[] data, int offset, int length, String contentType, int desiredWidth, int desiredHeight, int maxWidth) {
      try {
         EncodedImage image = convert(data, offset, length, contentType);
         return image == null ? null : scaleImage(image, desiredWidth, desiredHeight, maxWidth, -1);
      } finally {
         ;
      }
   }

   public static final EncodedImage scaleImage(EncodedImage image, int desiredWidth, int desiredHeight, int maxWidth, int maxHeight) {
      try {
         int imageWidth = image.getWidth();
         int imageHeight = image.getHeight();
         if (maxWidth == -1) {
            maxWidth = Display.getWidth();
         }

         if (maxHeight == -1) {
            maxHeight = Display.getHeight();
         }

         if (desiredWidth == -1) {
            int reduceToFitFactorX = 0;
            if (imageWidth > maxWidth) {
               reduceToFitFactorX = Fixed32.div(Fixed32.toFP(imageWidth), Fixed32.toFP(maxWidth));
            }

            int reduceToFitFactorY = 0;
            if (imageHeight > maxHeight) {
               reduceToFitFactorY = Fixed32.div(Fixed32.toFP(imageHeight), Fixed32.toFP(maxHeight));
            }

            if (reduceToFitFactorX >= reduceToFitFactorY) {
               if (reduceToFitFactorX != 0) {
                  image.setScaleX32(reduceToFitFactorX);
                  image.setScaleY32(reduceToFitFactorX);
                  return image;
               }
            } else if (reduceToFitFactorY != 0) {
               image.setScaleX32(reduceToFitFactorY);
               image.setScaleY32(reduceToFitFactorY);
               return image;
            }
         }

         if (desiredWidth > 0 || desiredHeight > 0) {
            int scaleY = Integer.MAX_VALUE;
            if (desiredHeight > 0) {
               scaleY = Fixed32.div(Fixed32.toFP(imageHeight), Fixed32.toFP(desiredHeight));
            }

            int scaleX = Integer.MAX_VALUE;
            if (desiredWidth > 0) {
               int scaleWidth = desiredWidth;
               if (desiredWidth > maxWidth) {
                  scaleWidth = maxWidth;
                  if (desiredHeight > 0) {
                     int desiredAspectRatio = Fixed32.div(Fixed32.toFP(desiredWidth), Fixed32.toFP(desiredHeight));
                     if (desiredAspectRatio != 0) {
                        int scaleHeight = Fixed32.div(Fixed32.toFP(scaleWidth), desiredAspectRatio);
                        if (scaleHeight > 0) {
                           scaleY = Fixed32.div(Fixed32.toFP(image.getHeight()), scaleHeight);
                        }
                     }
                  }
               }

               scaleX = Fixed32.div(Fixed32.toFP(imageWidth), Fixed32.toFP(scaleWidth));
            }

            if (scaleX == Integer.MAX_VALUE) {
               scaleX = scaleY;
            } else if (scaleY == Integer.MAX_VALUE) {
               scaleY = scaleX;
            }

            image.setScaleX32(scaleX);
            image.setScaleY32(scaleY);
         }

         return image;
      } finally {
         ;
      }
   }

   public static final EncodedImage getEncodedImageResource(String name) {
      if (name == null) {
         return null;
      }

      Resource resource = Resource.getResourceClass();
      if (resource != null) {
         byte[] data = resource.getResource(name);
         if (data != null) {
            EncodedImage image = EncodedImage.createEncodedImage(data, 0, -1);
            image.setDecodeMode(1);
            return image;
         }
      }

      return null;
   }
}
