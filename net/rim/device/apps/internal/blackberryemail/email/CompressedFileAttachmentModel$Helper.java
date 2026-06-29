package net.rim.device.apps.internal.blackberryemail.email;

import net.rim.device.api.io.MIMETypeAssociations;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.system.JPEGEncodedImage;
import net.rim.device.apps.internal.blackberryemail.resources.EmailResources;
import net.rim.device.internal.io.file.FileUtilities;

public class CompressedFileAttachmentModel$Helper {
   private CompressedFileAttachmentModel$Helper() {
   }

   public static CompressedFileAttachmentModel createModelForImage(String selectedFilePath) {
      EncodedImage encodedImage = processImageToFitSize(47616, selectedFilePath);
      if (encodedImage == null) {
         return null;
      }

      CompressedFileAttachmentModel model = new CompressedFileAttachmentModel();
      model.setContentType(encodedImage.getMIMEType());
      model.setData(encodedImage.getData());
      model.setDisplayName(FileUtilities.getName(selectedFilePath));
      model.setFile(selectedFilePath);
      model.setFileSize(encodedImage.getLength());
      return model;
   }

   public static CompressedFileAttachmentModel createModelForImage(byte[] imageData, String file) {
      EncodedImage encodedImage = processImageToFitSize(47616, imageData);
      if (encodedImage == null) {
         return null;
      }

      CompressedFileAttachmentModel model = new CompressedFileAttachmentModel();
      model.setContentType(encodedImage.getMIMEType());
      model.setData(encodedImage.getData());
      model.setDisplayName(FileUtilities.getName(file != null ? file : EmailResources.getString(73) + encodedImage.getMIMEType()));
      model.setFile(file);
      model.setFileSize(encodedImage.getLength());
      return model;
   }

   public static EncodedImage processImageToFitSize(int maxSize, byte[] imageData) {
      EncodedImage image = EncodedImage.createEncodedImage(imageData, 0, imageData.length);
      if (image == null) {
         return null;
      } else {
         return image.getLength() <= maxSize ? image : scaleAndCompressImage(image, maxSize);
      }
   }

   public static EncodedImage processImageToFitSize(int maxSize, String file) {
      if (MIMETypeAssociations.getMediaType(file) != 1) {
         return null;
      } else {
         EncodedImage image = FileUtilities.getEncodedImage(FileUtilities.makeFileURL(file));
         if (image == null) {
            return null;
         } else {
            return image.getLength() <= maxSize ? image : scaleAndCompressImage(image, maxSize);
         }
      }
   }

   public static EncodedImage scaleAndCompressImage(EncodedImage image, int maxSize) {
      Bitmap bitmap = null;
      int scaleFactor = 2;
      int maxScaleFactor = 10;
      int scaleFactorStep = 1;
      byte[] imageData = image.getData();
      boolean success = false;
      if (resize(maxSize, image.getLength())) {
         bitmap = Bitmap.createBitmapFromBytes(image.getData(), 0, imageData.length, scaleFactor);
      } else {
         bitmap = image.getBitmap();
      }

      EncodedImage finalImage = null;

      while (!success) {
         finalImage = compress(bitmap, 45, 5, 0, maxSize);
         if (finalImage != null) {
            success = true;
            break;
         }

         if (scaleFactor >= maxScaleFactor) {
            return null;
         }

         scaleFactor += scaleFactorStep;
         bitmap = Bitmap.createBitmapFromBytes(imageData, 0, imageData.length, scaleFactor);
         success = false;
      }

      return success && finalImage.getLength() <= maxSize ? finalImage : null;
   }

   private static EncodedImage compress(Bitmap bitmap, int initCompressionValue, int step, int stopCompressionValue, int maxSize) {
      int quality = initCompressionValue;
      int stopValue = stopCompressionValue;
      if (stopValue < 0 || stopValue > initCompressionValue) {
         stopValue = 0;
      }

      JPEGEncodedImage encodedImg;
      for (encodedImg = JPEGEncodedImage.encode(bitmap, quality);
         encodedImg.getLength() >= maxSize && quality >= stopValue;
         encodedImg = JPEGEncodedImage.encode(bitmap, quality)
      ) {
         quality -= step;
      }

      return encodedImg.getLength() > maxSize ? null : encodedImg;
   }

   private static boolean resize(int maxSize, int imageSize) {
      int delta = maxSize - imageSize;
      return delta <= 50;
   }
}
