package net.rim.device.api.system;

import java.util.Enumeration;
import javax.microedition.media.control.MetaDataControl;
import net.rim.device.api.math.Fixed32;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.api.util.ToIntHashtable;
import net.rim.device.internal.media.metadata.MetadataHandlerFactory;
import net.rim.device.resources.Resource;
import net.rim.device.resources.Resource$Internal;
import net.rim.vm.TraceBack;

public class EncodedImage {
   EncodedImage$ImageInfo _info;
   EncodedImage$FrameInfo[] _frameInfo;
   int _decodeMode;
   int _scaleX;
   int _scaleY;
   byte[] _data;
   int _offset;
   int _length;
   String _filename;
   Bitmap _cacheBitmap;
   int _cacheDecodeMode;
   int _cacheScaleX;
   int _cacheScaleY;
   int _decodeSteps = Integer.MAX_VALUE;
   private static ToIntHashtable _mimeTypes;
   public static final int DECODE_ALPHA = 1;
   public static final int DECODE_NATIVE = 2;
   public static final int DECODE_READONLY = 4;
   public static final int DECODE_NO_DITHER = 8;
   private static final int IMAGE_TYPE_UNKNOWN = 0;
   public static final int IMAGE_TYPE_GIF = 1;
   public static final int IMAGE_TYPE_PNG = 2;
   public static final int IMAGE_TYPE_JPEG = 3;
   public static final int IMAGE_TYPE_WBMP = 4;
   public static final int IMAGE_TYPE_BMP = 5;
   public static final int IMAGE_TYPE_TIFF = 6;
   public static final int IMAGE_TYPE_RWI = 7;
   public static final int IMAGE_TYPE_RGI = 8;

   EncodedImage() {
      this._decodeMode = 1;
      this._scaleY = this._scaleX = Fixed32.toFP(1);
   }

   public static EncodedImage createEncodedImage(byte[] data, int offset, int length) {
      return createEncodedImage(data, offset, length, null);
   }

   public static EncodedImage createEncodedImage(byte[] data, int offset, int length, String mimeType) {
      int imageType;
      if (mimeType == null) {
         imageType = getImageType(data, offset, length);
      } else {
         imageType = getMIMETypes().get(StringUtilities.toLowerCase(mimeType, 1701707776));
      }

      switch (imageType) {
         case 0:
            throw new IllegalArgumentException();
         case 1:
         default:
            return new GIFEncodedImage(data, offset, length);
         case 2:
            return new PNGEncodedImage(data, offset, length);
         case 3:
            if (JPEGEncodedImage.isJPEGSupported()) {
               return new JPEGEncodedImage(data, offset, length);
            }

            throw new IllegalArgumentException();
         case 4:
            return new WBMPEncodedImage(data, offset, length);
         case 5:
            if (BMPEncodedImage.isBMPSupported()) {
               return new BMPEncodedImage(data, offset, length);
            }

            throw new IllegalArgumentException();
         case 6:
            if (TIFFEncodedImage.isTIFFSupported()) {
               return new TIFFEncodedImage(data, offset, length);
            }

            throw new IllegalArgumentException();
         case 7:
         case 8:
            if (ProgressiveImage.isProgressiveSupported()) {
               return new ProgressiveImage(data, offset, length, true);
            } else {
               throw new IllegalArgumentException();
            }
      }
   }

   public static EncodedImage createEncodedImage(String filename, String mimeType) {
      int imageType;
      if (mimeType == null) {
         imageType = 1;
      } else {
         imageType = getMIMETypes().get(StringUtilities.toLowerCase(mimeType, 1701707776));
      }

      switch (imageType) {
         case 0:
            throw new IllegalArgumentException();
         case 1:
         default:
            return new GIFEncodedImage(filename);
         case 2:
            return new PNGEncodedImage(filename);
         case 3:
            if (JPEGEncodedImage.isJPEGSupported()) {
               return new JPEGEncodedImage(filename);
            }

            throw new IllegalArgumentException();
         case 4:
            return new WBMPEncodedImage(filename);
         case 5:
            if (BMPEncodedImage.isBMPSupported()) {
               return new BMPEncodedImage(filename);
            }

            throw new IllegalArgumentException();
         case 6:
            if (TIFFEncodedImage.isTIFFSupported()) {
               return new TIFFEncodedImage(filename);
            } else {
               throw new IllegalArgumentException();
            }
      }
   }

   public static EncodedImage getEncodedImageResource(String name) {
      return getEncodedImageResource(null, name);
   }

   public static EncodedImage getEncodedImageResource(String module, String name) {
      name.length();
      Resource resource = null;
      if (module == null) {
         module = TraceBack.getCallingModuleName(2);
      }

      resource = Resource$Internal.getResourceClass(module);
      if (resource != null) {
         byte[] data = resource.getResource(name);
         if (data != null) {
            return createEncodedImage(data, 0, data.length);
         }
      }

      return null;
   }

   public void setDecodeMode(int decodeMode) {
      this._decodeMode = decodeMode;
   }

   public int getDecodeMode() {
      return this._decodeMode;
   }

   private static ToIntHashtable getMIMETypes() {
      if (_mimeTypes == null) {
         _mimeTypes = new ToIntHashtable();
         _mimeTypes.put("image/gif", 1);
         if (isCFISupported()) {
            _mimeTypes.put("image/x-rpi", 1);
         }

         _mimeTypes.put("image/png", 2);
         _mimeTypes.put("image/vnd.rim.png", 2);
         _mimeTypes.put("image/vnd.wap.wbmp", 4);
         if (JPEGEncodedImage.isJPEGSupported()) {
            _mimeTypes.put("image/jpeg", 3);
            _mimeTypes.put("image/jpg", 3);
            _mimeTypes.put("image/pjpeg", 3);
            if (isCFISupported()) {
               _mimeTypes.put("image/x-rdi", 3);
            }
         }

         if (BMPEncodedImage.isBMPSupported()) {
            _mimeTypes.put("image/bmp", 5);
         }

         if (TIFFEncodedImage.isTIFFSupported()) {
            _mimeTypes.put("image/tiff", 6);
         }

         if (ProgressiveImage.isProgressiveSupported()) {
            _mimeTypes.put("image/x-rwi", 7);
            _mimeTypes.put("image/x-rgi", 8);
         }
      }

      return _mimeTypes;
   }

   public static Enumeration getSupportedMIMETypes() {
      return getMIMETypes().keys();
   }

   public void setScale(int scale) {
      if (scale < 1) {
         throw new IllegalArgumentException();
      }

      this._scaleY = this._scaleX = Fixed32.toFP(scale);
   }

   public int getScale() {
      return Fixed32.toInt(this._scaleX);
   }

   public void setScaleX32(int scale) {
      if (scale <= 0) {
         throw new IllegalArgumentException();
      }

      this._scaleX = scale;
   }

   public void setScaleY32(int scale) {
      if (scale <= 0) {
         throw new IllegalArgumentException();
      }

      this._scaleY = scale;
   }

   public int getScaleX32() {
      return this._scaleX;
   }

   public int getScaleY32() {
      return this._scaleY;
   }

   public EncodedImage scaleImage32(int scaleX, int scaleY) {
      EncodedImage image = createEncodedImage(this._data, this._offset, this._length);
      image.setScaleX32(scaleX);
      image.setScaleY32(scaleY);
      return image;
   }

   public int getImageType() {
      return this._info.imageType;
   }

   public MetaDataControl getMetaData() {
      return MetadataHandlerFactory.extract(this);
   }

   public int getWidth() {
      return this._info.width;
   }

   public final int getScaledWidth() {
      return Fixed32.divtoInt(Fixed32.toFP(this._info.width), this._scaleX);
   }

   public int getHeight() {
      return this._info.height;
   }

   public final int getScaledHeight() {
      return Fixed32.divtoInt(Fixed32.toFP(this._info.height), this._scaleY);
   }

   public int getFrameCount() {
      return this._info.frameCount;
   }

   private boolean isCacheable(int frameIndex) {
      if ((this._decodeMode & 4) == 0) {
         return false;
      } else {
         return (this.getBitmapType(frameIndex) & 7) > 1 ? false : (this.getAlphaType(frameIndex) & 7) <= 1;
      }
   }

   public static native boolean isCFISupported();

   public static boolean isMIMETypeSupported(String mimeType) {
      return getMIMETypes().get(StringUtilities.toLowerCase(mimeType, 1701707776)) != -1;
   }

   public boolean isMonochrome() {
      return this._info.isMonochrome;
   }

   public static boolean isImageValid(byte[] data, int offset, int length) {
      int imageType = getImageType(data, offset, length);
      switch (imageType) {
         case 0:
            return false;
         case 1:
         case 2:
         case 4:
         default:
            return true;
         case 3:
            return JPEGEncodedImage.isJPEGSupported();
         case 5:
            return BMPEncodedImage.isBMPSupported();
         case 6:
            return TIFFEncodedImage.isTIFFSupported();
         case 7:
         case 8:
            return ProgressiveImage.isProgressiveSupported();
      }
   }

   public boolean hasTransparency() {
      return this._info.hasTransparency;
   }

   public int getFrameWidth(int frameIndex) {
      if (frameIndex >= 0 && frameIndex <= this._info.frameCount) {
         return this._frameInfo[frameIndex].width;
      } else {
         throw new IllegalArgumentException();
      }
   }

   public int getFrameHeight(int frameIndex) {
      if (frameIndex >= 0 && frameIndex <= this._info.frameCount) {
         return this._frameInfo[frameIndex].height;
      } else {
         throw new IllegalArgumentException();
      }
   }

   public int getScaledFrameWidth(int frameIndex) {
      if (frameIndex >= 0 && frameIndex <= this._info.frameCount) {
         return Fixed32.toRoundedInt(Fixed32.div(Fixed32.toFP(this._frameInfo[frameIndex].width), this._scaleX));
      } else {
         throw new IllegalArgumentException();
      }
   }

   public int getScaledFrameHeight(int frameIndex) {
      if (frameIndex >= 0 && frameIndex <= this._info.frameCount) {
         return Fixed32.toRoundedInt(Fixed32.div(Fixed32.toFP(this._frameInfo[frameIndex].height), this._scaleY));
      } else {
         throw new IllegalArgumentException();
      }
   }

   public boolean getFrameMonochrome(int frameIndex) {
      if (frameIndex >= 0 && frameIndex <= this._info.frameCount) {
         return this._frameInfo[frameIndex].isMonochrome;
      } else {
         throw new IllegalArgumentException();
      }
   }

   public boolean getFrameTransparency(int frameIndex) {
      if (frameIndex >= 0 && frameIndex <= this._info.frameCount) {
         return this._frameInfo[frameIndex].hasTransparency;
      } else {
         throw new IllegalArgumentException();
      }
   }

   public Bitmap getBitmap() {
      return this.getBitmap(0);
   }

   public Bitmap getBitmap(int frameIndex) {
      if (this._cacheBitmap != null && this._decodeMode == this._cacheDecodeMode && this._scaleX == this._cacheScaleX && this._scaleY == this._cacheScaleY) {
         return this._cacheBitmap;
      }

      Bitmap bitmap = this.getBitmapImpl(frameIndex);
      if (this.isCacheable(frameIndex)) {
         this._cacheBitmap = bitmap;
         this._cacheDecodeMode = this._decodeMode;
         this._cacheScaleX = this._scaleX;
         this._cacheScaleY = this._scaleY;
      }

      return bitmap;
   }

   public String getMIMEType() {
      throw null;
   }

   Bitmap getBitmapImpl(int _1) {
      throw null;
   }

   public final byte[] getData() {
      return this._data;
   }

   public final int getOffset() {
      return this._offset;
   }

   public final int getLength() {
      return this._length;
   }

   public int getBitmapType(int _1) {
      throw null;
   }

   public int getAlphaType(int _1) {
      throw null;
   }

   private static final native int getImageType(byte[] var0, int var1, int var2);

   public EncodedImage getStandardsCompliantEncodedImage() {
      return this;
   }

   public EncodedImage getReplacementImage(int width, int height) {
      return this;
   }

   public final void setDecodeSteps(int steps) {
      ControlledAccess.assertRRISignature(TraceBack.getCallingModule(0));
      this._decodeSteps = steps;
   }
}
