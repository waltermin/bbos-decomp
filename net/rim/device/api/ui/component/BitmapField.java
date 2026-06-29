package net.rim.device.api.ui.component;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.ui.DrawStyle;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.theme.Tag;

public class BitmapField extends Field implements DrawStyle {
   private EncodedImage _image;
   private EncodedImage _replacementForCorruptEncodedImage;
   private Bitmap _bitmap;
   private int _x;
   private int _y;
   private int _hSpace;
   private int _vSpace;
   private static Tag TAG = Tag.create("bitmap");
   public static final int STAMP_MONOCHROME;

   public BitmapField() {
      this(null, 0);
   }

   public BitmapField(Bitmap bitmap) {
      this(bitmap, 0);
   }

   public BitmapField(Bitmap bitmap, long style) {
      super(style);
      this.setTag(TAG);
      this._bitmap = bitmap;
   }

   @Override
   protected void drawFocus(Graphics graphics, boolean on) {
      this.drawHighlightRegion(graphics, 1, on, 0, 0, this.getWidth(), this.getHeight());
   }

   public int getBitmapHeight() {
      if (this._bitmap != null) {
         return this._bitmap.getHeight();
      } else {
         return this._image != null ? this._image.getScaledHeight() : 0;
      }
   }

   public int getBitmapWidth() {
      if (this._bitmap != null) {
         return this._bitmap.getWidth();
      } else {
         return this._image != null ? this._image.getScaledWidth() : 0;
      }
   }

   @Override
   public int getPreferredHeight() {
      return this.getBitmapHeight() + 2 * this._vSpace;
   }

   @Override
   public int getPreferredWidth() {
      return this.getBitmapWidth() + 2 * this._hSpace;
   }

   protected int getXPos() {
      return this._x;
   }

   protected int getYPos() {
      return this._y;
   }

   @Override
   protected void layout(int width, int height) {
      switch ((int)(this.getStyle() & 56)) {
         case 32:
            this._y = height - this.getPreferredHeight() >> 1;
            break;
         case 40:
            this._y = height - this.getPreferredHeight();
            break;
         case 48:
            this._y = 0;
            break;
         default:
            this._y = 0;
            height = Math.min(height, this.getPreferredHeight());
      }

      this._y = this._y + this._vSpace;
      switch ((int)(this.getStyle() & 7)) {
         case 3:
            this._x = 0;
            width = Math.min(width, this.getPreferredWidth());
            break;
         case 4:
         default:
            this._x = width - this.getPreferredWidth() >> 1;
            break;
         case 5:
            this._x = width - this.getPreferredWidth();
            break;
         case 6:
            this._x = 0;
      }

      this._x = this._x + this._hSpace;
      this.setExtent(width, height);
   }

   protected void paintImage(Graphics graphics, int x, int y, int width, int height, EncodedImage image, int frame, int left, int top) {
      try {
         graphics.drawImage(x, y, width, height, image, frame, left, top);
      } catch (IllegalArgumentException e) {
         if (this._replacementForCorruptEncodedImage == null) {
            throw e;
         }

         graphics.drawImage(x, y, width, height, this._replacementForCorruptEncodedImage, frame, left, top);
      }
   }

   protected void paintBitmap(Graphics graphics, int x, int y, int width, int height, Bitmap bitmap, int left, int top) {
      graphics.drawBitmap(x, y, width, height, bitmap, left, top);
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   protected void paint(Graphics graphics) {
      int oldDecodeMode = 0;
      boolean var7 = false /* VF: Semaphore variable */;

      label102: {
         try {
            var7 = true;
            Bitmap bitmap = null;
            if (this._image != null) {
               oldDecodeMode = this._image.getDecodeMode();
               int bitmapType = this._image.getBitmapType(0);
               if (bitmapType != 129 && bitmapType != 1) {
                  this.paintImage(graphics, this._x, this._y, this.getBitmapWidth(), this.getBitmapHeight(), this._image, 0, 0, 0);
                  var7 = false;
                  break label102;
               }

               this._image.setDecodeMode(oldDecodeMode | 4);
               bitmap = this._image.getBitmap(0);
            }

            if (this._bitmap != null) {
               bitmap = this._bitmap;
            }

            if (bitmap != null) {
               if (this.isStyle(65536) && Graphics.isColor() && bitmap.getType() == 129 && bitmap.hasAlpha()) {
                  graphics.rop(-96, this._x, this._y, this.getBitmapWidth(), this.getBitmapHeight(), bitmap, 0, 0);
                  var7 = false;
               } else {
                  this.paintBitmap(graphics, this._x, this._y, this.getBitmapWidth(), this.getBitmapHeight(), bitmap, 0, 0);
                  var7 = false;
               }
            } else {
               var7 = false;
            }
         } finally {
            if (var7) {
               if (this._image != null) {
                  this._image.setDecodeMode(oldDecodeMode);
               }
            }
         }

         if (this._image != null) {
            this._image.setDecodeMode(oldDecodeMode);
            return;
         }

         return;
      }

      if (this._image != null) {
         this._image.setDecodeMode(oldDecodeMode);
      }
   }

   public void setBitmap(Bitmap bitmap) {
      int oldWidth = this.getPreferredWidth();
      int oldHeight = this.getPreferredHeight();
      this._bitmap = bitmap;
      this._image = null;
      this.fieldChangeNotify(Integer.MIN_VALUE);
      Manager manager = this.getManager();
      if (manager != null && manager.isValidLayout()) {
         if (oldWidth != this.getPreferredWidth() || oldHeight != this.getPreferredHeight()) {
            this.updateLayout();
         }

         this.invalidate();
      }
   }

   public void setImage(EncodedImage image) {
      int oldWidth = this.getPreferredWidth();
      int oldHeight = this.getPreferredHeight();
      this._bitmap = null;
      this._image = image;
      this.fieldChangeNotify(Integer.MIN_VALUE);
      Manager manager = this.getManager();
      if (manager != null && manager.isValidLayout()) {
         if (oldWidth != this.getPreferredWidth() || oldHeight != this.getPreferredHeight()) {
            this.updateLayout();
         }

         this.invalidate();
      }
   }

   public void setReplacementForCorruptImage(EncodedImage image) {
      this._replacementForCorruptEncodedImage = image;
   }

   public void setSpace(int hSpace, int vSpace) {
      this._hSpace = hSpace;
      this._vSpace = vSpace;
      this.updateLayout();
   }

   protected Bitmap getBitmap() {
      return this._bitmap;
   }

   protected EncodedImage getImage() {
      return this._image;
   }
}
