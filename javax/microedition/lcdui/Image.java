package javax.microedition.lcdui;

import java.io.IOException;
import java.io.InputStream;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.resources.Resource$Internal;
import net.rim.vm.TraceBack;

public class Image {
   private Bitmap _bitmap;
   private boolean _mutable;

   final Bitmap getBitmap() {
      return this._bitmap;
   }

   private Image() {
   }

   public static Image createImage(int width, int height) {
      if (width > 0 && height > 0) {
         Image image = new Image();
         image._bitmap = new Bitmap(width, height);
         image._mutable = true;
         return image;
      } else {
         throw new IllegalArgumentException();
      }
   }

   public static Image createImage(Image source) {
      if (source._mutable) {
         int width = source.getWidth();
         int height = source.getHeight();
         Image copy = createImage(width, height);
         copy.getGraphics().getPeer().drawBitmap(0, 0, width, height, source._bitmap, 0, 0);
         copy._mutable = false;
         return copy;
      } else {
         return source;
      }
   }

   public static Image createImage(String name) throws IOException {
      if (name.length() > 0 && name.charAt(0) == '/') {
         name = name.substring(1);
      }

      try {
         byte[] png = Resource$Internal.getResourceClass(TraceBack.getCallingModuleName(2)).getResource(name);
         return createImage(png, 0, png.length);
      } catch (NullPointerException npe) {
         throw new IOException();
      } catch (IllegalArgumentException iae) {
         throw new IOException();
      }
   }

   public static Image createImage(byte[] imageData, int imageOffset, int imageLength) {
      if (imageOffset >= 0 && imageLength >= 0 && imageOffset + imageLength <= imageData.length) {
         Image img = new Image();
         img._bitmap = Bitmap.createBitmapFromPNG(imageData, imageOffset, imageLength);
         img._mutable = false;
         return img;
      } else {
         throw new ArrayIndexOutOfBoundsException();
      }
   }

   private static void getNextScanLine(
      int[] rgbDestData, int offset, int[] rgbSourceData, int rgbSourceScanLength, int rgbDestScanLength, int xPosition, int yPosition, int transform
   ) {
      rgbDestData[offset] = rgbSourceData[xPosition + yPosition * rgbSourceScanLength];
      if (rgbDestScanLength > 1) {
         for (int i = 1; i < rgbDestScanLength; i++) {
            if (Graphics.DUX[transform] == 0) {
               rgbDestData[offset + i] = rgbSourceData[xPosition + (yPosition + i * Graphics.DUY[transform]) * rgbSourceScanLength];
            } else {
               rgbDestData[offset + i] = rgbSourceData[xPosition + Graphics.DUX[transform] * i + rgbSourceScanLength * yPosition];
            }
         }
      }
   }

   public static Image createImage(Image image, int x, int y, int width, int height, int transform) {
      image.isMutable();
      if (x < 0 || y < 0 || x + width > image.getWidth() || y + height > image.getHeight() || width <= 0 || height <= 0 || transform < 0 || transform > 7) {
         throw new IllegalArgumentException();
      }

      if (x == 0 && y == 0 && width == image.getWidth() && height == image.getHeight() && transform == 0) {
         return image;
      }

      int[] rgbData = null;
      int[] rgbDestData = null;
      rgbData = new int[width * height];
      image.getRGB(rgbData, 0, width, x, y, width, height);
      Image resultImage;
      if (transform == 0) {
         resultImage = new Image();
         resultImage._bitmap = new Bitmap(width, height);
         resultImage._bitmap.setARGB(rgbData, 0, width, 0, 0, width, height);
      } else {
         int startPosX = 0;
         int startPosY = 0;
         int newWidth = 0;
         int newHeight = 0;
         switch (transform) {
            case 0:
               break;
            case 1:
               startPosX = 0;
               startPosY = height - 1;
               newWidth = width;
               newHeight = height;
               break;
            case 2:
            default:
               startPosX = width - 1;
               startPosY = 0;
               newWidth = width;
               newHeight = height;
               break;
            case 3:
               startPosX = width - 1;
               startPosY = height - 1;
               newWidth = width;
               newHeight = height;
               break;
            case 4:
               startPosX = 0;
               startPosY = 0;
               newWidth = height;
               newHeight = width;
               break;
            case 5:
               startPosX = 0;
               startPosY = height - 1;
               transform = 6;
               newWidth = height;
               newHeight = width;
               break;
            case 6:
               startPosX = width - 1;
               startPosY = 0;
               transform = 5;
               newWidth = height;
               newHeight = width;
               break;
            case 7:
               startPosX = width - 1;
               startPosY = height - 1;
               newWidth = height;
               newHeight = width;
         }

         rgbDestData = new int[newWidth * newHeight];
         int offset = 0;

         for (int i = 0; i < newHeight; i++) {
            getNextScanLine(rgbDestData, offset, rgbData, width, newWidth, startPosX, startPosY, transform);
            offset += newWidth;
            if (Graphics.DUX[transform] == 0) {
               startPosX += Graphics.DVX[transform];
            } else {
               startPosY += Graphics.DVY[transform];
            }
         }

         resultImage = new Image();
         resultImage._bitmap = new Bitmap(newWidth, newHeight);
         resultImage._bitmap.setARGB(rgbDestData, 0, newWidth, 0, 0, newWidth, newHeight);
      }

      return resultImage;
   }

   public static Image createImage(InputStream stream) throws IOException {
      int blocksize = 4096;
      if (stream == null) {
         throw new NullPointerException();
      }

      int l = stream.available();
      byte[] buffer = new byte[l + 1];
      int length = 0;

      while ((l = stream.read(buffer, length, buffer.length - length)) != -1) {
         length += l;
         if (length == buffer.length) {
            byte[] b = new byte[buffer.length + blocksize];
            System.arraycopy(buffer, 0, b, 0, length);
            buffer = b;
         }
      }

      Image image = new Image();
      EncodedImage encodedImage = null;

      try {
         encodedImage = EncodedImage.createEncodedImage(buffer, 0, length);
      } catch (IllegalArgumentException iae) {
         throw new IOException(iae.getMessage());
      }

      image._bitmap = encodedImage.getBitmap();
      image._mutable = false;
      stream.close();
      return image;
   }

   public Graphics getGraphics() {
      if (!this._mutable) {
         throw new IllegalStateException();
      }

      Graphics graphics = new Graphics(this);
      net.rim.device.api.ui.Graphics rimGraphics = new net.rim.device.api.ui.Graphics(this._bitmap);
      rimGraphics.pushRegion(0, 0, this.getWidth(), this.getHeight(), 0, 0);
      graphics.setGraphics(rimGraphics, false);
      return graphics;
   }

   public int getWidth() {
      return this._bitmap.getWidth();
   }

   public int getHeight() {
      return this._bitmap.getHeight();
   }

   public boolean isMutable() {
      return this._mutable;
   }

   public static Image createRGBImage(int[] rgb, int width, int height, boolean processAlpha) {
      if (width > 0 && height > 0) {
         if (rgb.length < width * height) {
            throw new ArrayIndexOutOfBoundsException();
         }

         Image image = new Image();
         image._bitmap = new Bitmap(width, height);
         image._bitmap.setARGB(rgb, 0, image.getWidth(), 0, 0, width, height);
         if (!processAlpha) {
            image._bitmap.setAlpha(null);
         }

         image._mutable = false;
         return image;
      } else {
         throw new IllegalArgumentException();
      }
   }

   public void getRGB(int[] rgbData, int offset, int scanlength, int x, int y, int width, int height) {
      int rgbDataLength = rgbData.length;
      boolean exceedsBounds = x < 0 || y < 0 || x + width > this._bitmap.getWidth() || y + height > this._bitmap.getHeight();
      if (!exceedsBounds && Math.abs(scanlength) >= width) {
         if (width > 0 && height > 0) {
            if (offset < 0 || offset + (height - 1) * scanlength + width > rgbDataLength || offset + (height - 1) * scanlength < 0) {
               throw new ArrayIndexOutOfBoundsException();
            }

            if (scanlength < 0) {
               int positiveScanLength = 0 - scanlength;

               for (int i = height - 1; i >= 0; i--) {
                  this._bitmap.getARGB(rgbData, offset, positiveScanLength, x, y + i, width, 1);
                  offset += width;
               }
            } else {
               this._bitmap.getARGB(rgbData, offset, scanlength, x, y, width, height);
            }
         }
      } else {
         throw new IllegalArgumentException();
      }
   }
}
