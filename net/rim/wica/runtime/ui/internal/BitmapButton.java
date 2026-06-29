package net.rim.wica.runtime.ui.internal;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.component.BitmapField;

public class BitmapButton extends BitmapField {
   private Bitmap _focusBitmap;
   private EncodedImage _focusImage;
   public static final long CONSUME_CLICK = 65536L;

   public BitmapButton(Bitmap bitmap, Bitmap focusBitmap) {
      super(bitmap, 22518049676460032L);
      this._focusBitmap = focusBitmap;
   }

   public BitmapButton(EncodedImage image, EncodedImage focusImage) {
      super(null, 22518049676460032L);
      this.setImage(image, focusImage);
   }

   public void setBitmap(Bitmap bitmap, Bitmap focusBitmap) {
      this._focusImage = null;
      this._focusBitmap = focusBitmap;
      this.setBitmap(bitmap);
   }

   public void setImage(EncodedImage image, EncodedImage focusImage) {
      this._focusBitmap = null;
      this._focusImage = focusImage;
      this.setImage(image);
   }

   @Override
   protected void applyThemeOnStateChange() {
      this.invalidate();
   }

   @Override
   protected void drawFocus(Graphics graphics, boolean on) {
      this.paint(graphics);
   }

   @Override
   protected void paint(Graphics graphics) {
      if (this.getState() == 6) {
         if (this._focusImage != null) {
            this.paintImage(
               graphics, this.getXPos(), this.getYPos(), this._focusImage.getScaledWidth(), this._focusImage.getScaledHeight(), this._focusImage, 0, 0, 0
            );
            return;
         }

         if (this._focusBitmap != null) {
            this.paintBitmap(graphics, this.getXPos(), this.getYPos(), this._focusBitmap.getWidth(), this._focusBitmap.getHeight(), this._focusBitmap, 0, 0);
            return;
         }
      } else {
         super.paint(graphics);
      }
   }

   @Override
   protected boolean trackwheelClick(int status, int time) {
      if (this.isEditable()) {
         this.fieldChangeNotify(0);
         return this.isStyle(65536);
      } else {
         return false;
      }
   }

   @Override
   protected boolean keyChar(char key, int status, int time) {
      if (this.isEditable() && key == '\n') {
         this.fieldChangeNotify(0);
         return true;
      } else {
         return false;
      }
   }
}
