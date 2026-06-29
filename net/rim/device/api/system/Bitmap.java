package net.rim.device.api.system;

import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.api.util.Arrays;
import net.rim.device.internal.ui.Image;
import net.rim.device.resources.Resource;
import net.rim.device.resources.Resource$Internal;
import net.rim.vm.Array;
import net.rim.vm.TraceBack;

public final class Bitmap {
   private int _type;
   private int _width;
   private int _height;
   private int _stride;
   private byte[] _data;
   private int _numBands;
   private int _axesPerBand;
   private byte[][][] _bands;
   private Bitmap _alpha;
   private int _cacheId;
   private int _transColour;
   private boolean _readonly;
   public static final int INFORMATION = 0;
   public static final int QUESTION = 1;
   public static final int EXCLAMATION = 2;
   public static final int HOURGLASS = 3;
   static final int BITMAP_ORIENTATION_MASK = 128;
   static final int BITMAP_COLOUR_MASK = 96;
   static final int BITMAP_DEPTH_MASK = 7;
   static final int ORIENTATION_COLUMNWISE = 0;
   static final int ORIENTATION_ROWWISE = 128;
   static final int GREYSCALE = 0;
   static final int COLOUR = 64;
   static final int BITDEPTH_MONO = 1;
   static final int BITDEPTH_2BPP = 2;
   static final int BITDEPTH_4BPP = 3;
   static final int BITDEPTH_16BPP = 5;
   public static final int ALPHA_BITDEPTH_MONO = 1;
   public static final int ALPHA_BITDEPTH_4BPP = 3;
   public static final int ALPHA_BITDEPTH_8BPP = 2;
   public static final int COLUMNWISE_MONOCHROME = 1;
   public static final int ROWWISE_16BIT_COLOR = 197;
   public static final int ROWWISE_MONOCHROME = 129;
   public static final int COLUMNWISE_2BIT_GREYSCALE = 2;
   public static final int ROWWISE_4BIT_GREYSCALE = 131;
   private static final int ROWWISE_8BIT_ALPHA = 148;
   private static final int ROWWISE_4BIT_ALPHA = 147;
   private static final int ROWWISE_1BIT_ALPHA = 145;
   private static final int MAX_BAND_SIZE = 8192;
   private static final int BITMAP_BAND_GUARD_SAFETY_SIZE = 4;
   private static final int TRANSPARENCY_COLOR_NONE = -1;
   public static final int TRUE_WHITE = 16777215;
   public static final int TRUE_BLACK = 0;
   public static final int DECODE_ALPHA = 1;
   public static final int DECODE_NATIVE = 2;
   public static final int DECODE_READONLY = 4;
   public static final int DEFAULT_TYPE;

   public static final int getDefaultType() {
      return DEFAULT_TYPE;
   }

   public Bitmap(int width, int height) {
      this(DEFAULT_TYPE, width, height, null, false);
   }

   public Bitmap(int type, int width, int height) {
      this(type, width, height, null, false);
   }

   public Bitmap(int type, int width, int height, byte[] data) {
      this(type, width, height, data, false);
   }

   public Bitmap(int type, int width, int height, byte[] data, boolean readonly) {
      this(type, width, height, data, readonly, true);
   }

   Bitmap(int type, int width, int height, byte[] data, boolean readonly, boolean externalCall) {
      this(type, width, height, data, readonly, externalCall, false);
   }

   Bitmap(int type, int width, int height, byte[] data, boolean readonly, boolean externalCall, boolean alpha) {
      boolean isValidType = false;
      switch (DEFAULT_TYPE) {
         case 1:
            isValidType = type == 1;
            break;
         case 2:
            isValidType = type == 1 || type == 2;
            break;
         case 145:
         case 147:
         case 148:
            isValidType = false;
            break;
         case 197:
            isValidType = type == 1 || type == 197 || type == 129 || type == 148 || type == 147 || type == 145 || !externalCall && type == 131;
      }

      if (!isValidType) {
         throw new IllegalArgumentException("Unsupported.");
      }

      if (width >= 0 && height >= 0) {
         int bytesPerBand = 0;
         byte defValue = 0;
         boolean cwMono2RwMono = false;
         if (type == 1 && (DEFAULT_TYPE & 128) == 128) {
            cwMono2RwMono = true;
            type = 129;
         }

         this._type = type;
         this._width = width;
         this._height = height;
         this._transColour = -1;
         this._readonly = readonly;
         int pixelsPerDword = 32 >> (type & 7) - 1;
         int stride;
         if ((type & 128) == 128) {
            stride = width;
         } else {
            stride = height;
         }

         stride = stride + (pixelsPerDword - 1) & ~(pixelsPerDword - 1);
         if (stride < 0) {
            throw new IllegalArgumentException("Bitmap is too large");
         }

         stride = stride / pixelsPerDword << 2;
         if (stride < 0) {
            throw new IllegalArgumentException("Bitmap is too large");
         }

         int numBytes;
         int reqBytes;
         int srcStride;
         switch (type) {
            case 1:
               srcStride = height + 7 >> 3;
               reqBytes = srcStride * width;
               numBytes = stride * width + 3;
               if (numBytes < 0 || numBytes > 8192) {
                  throw new IllegalArgumentException("Monochrome bitmap is too large");
               }

               this._numBands = 1;
               this._axesPerBand = width;
               break;
            case 2:
               srcStride = stride;
               numBytes = stride * width;
               reqBytes = numBytes;
               if (numBytes < 0 || numBytes > 8192) {
                  throw new IllegalArgumentException("Greyscale bitmap is too large");
               }

               this._numBands = 1;
               this._axesPerBand = width;
               break;
            case 129:
            case 131:
            case 145:
            case 147:
            case 148:
            case 197:
               if (cwMono2RwMono) {
                  srcStride = height + 7 >> 3;
                  reqBytes = srcStride * width;
               } else {
                  srcStride = stride;
                  reqBytes = srcStride * height;
               }

               numBytes = stride * height;
               defValue = -1;
               if (numBytes < 0) {
                  throw new IllegalArgumentException("Bitmap is too large");
               }

               if (numBytes > 8192) {
                  if (stride < 0 || stride > 8192) {
                     throw new IllegalArgumentException("Bitmap is too large");
                  }

                  int bitPos = 0;

                  for (int temp = 8192 / stride; temp != 0; temp >>= 1) {
                     bitPos++;
                  }

                  this._axesPerBand = 1 << --bitPos;
                  this._numBands = (height + this._axesPerBand - 1) / this._axesPerBand;
               } else {
                  this._axesPerBand = height;
                  this._numBands = 1;
               }

               bytesPerBand = this._axesPerBand * stride;
               break;
            default:
               throw new IllegalArgumentException("Invalid type.");
         }

         this._stride = stride;
         if (data != null && data.length < reqBytes) {
            throw new IllegalArgumentException("Invalid bitmap size.");
         }

         if (this._numBands != 1) {
            int offset = 0;
            this._bands = new byte[this._numBands][][];

            for (int i = 0; i < this._numBands; i++) {
               int bandSize = Math.min(numBytes, bytesPerBand);
               this._bands[i] = (byte[][])Array.allocContiguousByteArray(bandSize + 4);
               numBytes -= bandSize;
               if (data != null) {
                  if (cwMono2RwMono) {
                     transposeColumnwiseBitmapData(data, srcStride, (byte[])this._bands[i], stride, i * this._axesPerBand, width, bandSize / stride);
                  } else {
                     System.arraycopy(data, offset, this._bands[i], 0, bandSize);
                     offset += bandSize;
                  }
               } else if (alpha) {
                  Arrays.fill((byte[])this._bands[i], (byte)-1);
               } else if (externalCall && defValue != 0) {
                  Arrays.fill((byte[])this._bands[i], defValue);
               }
            }
         } else {
            this._data = Array.allocContiguousByteArray(numBytes + 4);
            if (data != null) {
               if (cwMono2RwMono) {
                  transposeColumnwiseBitmapData(data, srcStride, this._data, stride, 0, width, height);
                  return;
               }

               if (srcStride == stride) {
                  System.arraycopy(data, 0, this._data, 0, reqBytes);
                  return;
               }

               if (type != 1) {
                  throw new RuntimeException();
               }

               int srcOffset = 0;
               int dstOffset = 0;

               for (int col = 0; col < width; col++) {
                  System.arraycopy(data, srcOffset, this._data, dstOffset, srcStride);
                  srcOffset += srcStride;
                  dstOffset += stride;
               }
            } else {
               if (alpha) {
                  Arrays.fill(this._data, (byte)-1);
                  return;
               }

               if (externalCall && defValue != 0) {
                  Arrays.fill(this._data, defValue);
                  return;
               }
            }
         }
      } else {
         throw new IllegalArgumentException("width/height must be positive");
      }
   }

   public static final Bitmap createGreyscaleBitmap(Object src, int width, int height) {
      if (src instanceof Image) {
         Image img = (Image)src;
         Bitmap bmp = new Bitmap(197, width, height);
         bmp.createAlpha(2);
         Graphics gfx = new Graphics(bmp);
         gfx.setGlobalAlpha(0);
         gfx.clear();
         gfx.setGlobalAlpha(255);
         img.paint(gfx, 0, 0, width, height);
         src = bmp;
      }

      if (src instanceof Bitmap) {
         Bitmap bmpNew = new Bitmap(197, width, height);
         bmpNew.createAlpha(2);
         Graphics gfx = new Graphics(bmpNew);
         gfx.setGlobalAlpha(0);
         gfx.clear();
         gfx.setGlobalAlpha(255);
         gfx.rop(16, 0, 0, width, height, (Bitmap)src, 0, 0);
         return bmpNew;
      } else {
         return null;
      }
   }

   public final Bitmap createScaledBitmap(int width, int height) {
      Bitmap dst = new Bitmap(this._type, width, height);
      if (this._alpha != null) {
         switch (this._alpha.getBitsPerPixel()) {
            case 1:
               dst.createAlpha(1);
               return dst;
            default:
               dst.createAlpha(3);
         }
      }

      return dst;
   }

   public final native void getARGB(int[] var1, int var2, int var3, int var4, int var5, int var6, int var7);

   public final Bitmap setRGB565(byte[] rgbData, int offset, int scanLength, int x, int y, int width, int height) {
      if (this._type == 197) {
         this.setRGB565Native(rgbData, offset, scanLength, x, y, width, height);
      }

      return this;
   }

   final native void setRGB565Native(byte[] var1, int var2, int var3, int var4, int var5, int var6, int var7);

   public final int getBitsPerPixel() {
      return 1 << (this._type & 7) - 1;
   }

   public final int getHeight() {
      return this._height;
   }

   public final native void getScaled(Bitmap var1, int var2);

   public final int getTransColor() {
      return this._transColour;
   }

   public final int getType() {
      return this._type;
   }

   public final int getWidth() {
      return this._width;
   }

   public final boolean isWritable() {
      return !this._readonly;
   }

   public final synchronized void setARGB(int[] data, int offset, int scanLength, int left, int top, int width, int height) {
      int type = 0;
      int data_bpp = 0;
      int data_bppAlpha = 0;
      int bpp = 0;
      int bppAlpha = 0;
      if (data == null) {
         throw new NullPointerException("Data is empty.");
      }

      if (this.isWritable() && (!this.hasAlpha() || this._alpha.isWritable())) {
         if (data.length < scanLength * height + offset
            || left < 0
            || top < 0
            || offset < 0
            || scanLength < 0
            || scanLength < width
            || offset > data.length
            || width < 0
            || height < 0
            || left + width > this._width
            || top + height > this._height) {
            throw new IllegalArgumentException("Invalid ARGB coordinates.");
         }

         if (width != 0 && height != 0) {
            bpp = this.getBitsPerPixel();
            if (this.hasAlpha()) {
               bppAlpha = this._alpha.getBitsPerPixel();
            }

            data_bpp = getBppFromARGB(data, false);
            data_bppAlpha = getBppFromARGB(data, true);
            if (data_bpp > bpp) {
               short var13;
               switch (data_bpp) {
                  case 4:
                  case 8:
                     data_bpp = 2;
                  case 2:
                  case 16:
                     if (Display.isColor()) {
                        var13 = 197;
                        data_bpp = 16;
                        break;
                     } else if (bpp != 2 && Display.getNumColors() >= 4) {
                        var13 = 2;
                        data_bpp = 2;
                        break;
                     }
                  default:
                     var13 = -1;
               }

               if (var13 != -1) {
                  this.promote(new Bitmap(var13, this._width, this._height));
                  bpp = data_bpp;
               }
            }

            if (data_bppAlpha > bppAlpha) {
               if (data_bppAlpha > 1 && !Display.isColor()) {
                  if (!this.hasAlpha()) {
                     data_bppAlpha = 1;
                  } else {
                     data_bppAlpha = 0;
                  }
               }

               short var14;
               switch (data_bppAlpha) {
                  case 0:
                  case 3:
                     var14 = -1;
                     break;
                  case 1:
                  default:
                     if ((DEFAULT_TYPE & 128) == 128) {
                        var14 = 129;
                     } else {
                        var14 = 1;
                     }
                     break;
                  case 2:
                     data_bppAlpha = 4;
                  case 4:
                     var14 = 131;
               }

               if (var14 != -1) {
                  if (!this.hasAlpha()) {
                     this._alpha = new Bitmap(var14, this._width, this._height, null, false, false, true);
                  } else {
                     this._alpha.promote(new Bitmap(var14, this._width, this._height, null, false, false));
                  }

                  bppAlpha = data_bppAlpha;
               }
            }

            this.copyARGB(data, bpp, bppAlpha, offset, scanLength, left, top, width, height);
         }
      } else {
         throw new IllegalArgumentException("Bitmap is read-only.");
      }
   }

   private final native void promote(Bitmap var1);

   private final native void copyARGB(int[] var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9);

   private static final native int getBppFromARGB(int[] var0, boolean var1);

   public static final Bitmap createBitmapFromPNG(byte[] png, int offset, int length) {
      return createBitmapFromBytes(png, offset, length, 1, 1, -1, 0);
   }

   public static final Bitmap createBitmapFromPNG(byte[] png, int offset, int length, boolean decodeAlpha) {
      return createBitmapFromBytes(png, offset, length, decodeAlpha ? 1 : 0, 1, -1, 0);
   }

   public static final Bitmap createBitmapFromBytes(byte[] bytes, int offset, int length, int scale) {
      return createBitmapFromBytes(bytes, offset, length, 1, scale, -1, 0);
   }

   public static final Bitmap createBitmapFromBytes(byte[] bytes, int offset, int length, int decodeMode, int scale) {
      return createBitmapFromBytes(bytes, offset, length, decodeMode, scale, -1, 0);
   }

   public static final Bitmap createBitmapFromBytes(byte[] bytes, int offset, int length, int decodeMode, int scale, int trans) {
      return createBitmapFromBytes(bytes, offset, length, decodeMode, scale, trans, 0);
   }

   public static final Bitmap createBitmapFromBytes(byte[] bytes, int offset, int length, int decodeMode, int scale, int trans, int frameIndex) {
      EncodedImage image = EncodedImage.createEncodedImage(bytes, offset, length);
      image.setDecodeMode(decodeMode);
      image.setScale(scale);
      return image.getBitmap(frameIndex);
   }

   @Override
   public final synchronized boolean equals(Object obj) {
      if (obj instanceof Bitmap) {
         Bitmap otherBitmap = (Bitmap)obj;
         if (this._type == otherBitmap._type
            && this._width == otherBitmap._width
            && this._height == otherBitmap._height
            && this._numBands == otherBitmap._numBands
            && this._axesPerBand == otherBitmap._axesPerBand) {
            return compareBitmapData(this, otherBitmap);
         }
      }

      return false;
   }

   private static final native boolean compareBitmapData(Bitmap var0, Bitmap var1);

   private final Bitmap cloneBitmap() {
      Bitmap clone = new Bitmap(this._type, this._width, this._height);
      clone._transColour = this._transColour;
      if (this._data != null) {
         if (clone._data == null) {
            throw new RuntimeException();
         }

         System.arraycopy(this._data, 0, clone._data, 0, this._data.length);
         return clone;
      } else {
         if (clone._bands == null) {
            throw new RuntimeException();
         }

         if (clone._numBands != this._numBands) {
            throw new RuntimeException();
         }

         int bands = this._numBands;

         for (int i = 0; i < bands; i++) {
            byte[] src = (byte[])this._bands[i];
            System.arraycopy(src, 0, clone._bands[i], 0, src.length);
         }

         return clone;
      }
   }

   public final boolean hasAlpha() {
      return this._alpha != null;
   }

   public final synchronized void setAlpha(Bitmap alpha) {
      if (alpha != null) {
         if (alpha._alpha != null || alpha._width != this._width || alpha._height != this._height) {
            throw new IllegalArgumentException();
         }

         if (Display.isRowwise()) {
            if ((alpha._type & 128) != 128) {
               throw new IllegalArgumentException();
            }
         } else if (alpha._type != 1) {
            throw new IllegalArgumentException();
         }

         alpha = alpha.cloneBitmap();
      }

      this._alpha = alpha;
   }

   final void setAlphaDirect(Bitmap alpha) {
      this._alpha = alpha;
   }

   public final void createAlpha(int bitDepth) {
      switch (bitDepth) {
         case 0:
            throw new IllegalArgumentException();
         case 1:
         default: {
            Bitmap alpha = new Bitmap(145, this._width, this._height);
            this.setAlpha(alpha);
            return;
         }
         case 2: {
            Bitmap alpha = new Bitmap(148, this._width, this._height);
            this.setAlpha(alpha);
            return;
         }
         case 3: {
            Bitmap alpha = new Bitmap(147, this._width, this._height);
            this.setAlpha(alpha);
         }
      }
   }

   public static final Bitmap getBitmapResource(String module, String name) {
      name.length();
      Resource resource = null;
      if (module == null) {
         module = TraceBack.getCallingModuleName(2);
      }

      resource = Resource$Internal.getResourceClass(module);
      if (resource != null) {
         byte[] data = resource.getResource(name);
         if (data != null) {
            return createBitmapFromPNG(data, 0, -1);
         }
      }

      return null;
   }

   public static final Bitmap getBitmapResource(String name) {
      return getBitmapResource(null, name);
   }

   public static final Bitmap getPredefinedBitmap(int predefinedBitmap) {
      return ThemeManager.getPredefinedBitmap(predefinedBitmap);
   }

   private static final native void transposeColumnwiseBitmapData(byte[] var0, int var1, byte[] var2, int var3, int var4, int var5, int var6);

   static {
      switch (Display.getNumColors()) {
         case 2:
            DEFAULT_TYPE = 1;
            return;
         case 4:
            DEFAULT_TYPE = 2;
            return;
         case 65536:
            DEFAULT_TYPE = 197;
            return;
         default:
            DEFAULT_TYPE = 1;
      }
   }
}
