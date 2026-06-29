package net.rim.device.apps.internal.docview.gui;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.ui.theme.ThemeAttributeSet;
import net.rim.device.apps.api.ui.FocusPaintManager;

class CustomBitmapField extends BitmapField {
   protected String _domID;
   private FocusPaintManager _focusManager = new FocusPaintManager();

   CustomBitmapField(EncodedImage image, long style) {
      super((Bitmap)null, style);
      this.setImage(image);
   }

   CustomBitmapField(Bitmap bmp, long style) {
      super(bmp, style);
   }

   void setDomID(String domID) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   boolean setFocusStyle(int style) {
      if (this._focusManager.setFocusStyle(style)) {
         if (this.isFocus()) {
            this.invalidate();
         }

         return true;
      } else {
         return false;
      }
   }

   EncodedImage getEncodedImage() {
      return this.getImage();
   }

   int getFocusStyle() {
      return this._focusManager.getFocusStyle();
   }

   @Override
   protected void paint(Graphics graphics) {
      try {
         super.paint(graphics);
      } finally {
         Manager fldManager = this.getManager();
         if (fldManager instanceof DocViewTextDisplayField) {
            ((DocViewTextDisplayField)fldManager).previewBitmapFieldIsInvalid(this._domID);
         }

         return;
      }
   }

   @Override
   protected void drawFocus(Graphics graphics, boolean on) {
      if (on) {
         XYRect focusRect = new XYRect();
         this.getFocusRect(focusRect);
         focusRect.x = focusRect.x + this.getXPos();
         focusRect.y = focusRect.y + this.getYPos();
         focusRect.width = Math.min(focusRect.width, this.getBitmapWidth());
         focusRect.height = Math.min(focusRect.height, this.getBitmapHeight());
         this._focusManager.drawFocus(graphics, focusRect, ThemeAttributeSet.getColor(this, 2));
         Object var6 = null;
      } else {
         try {
            super.drawFocus(graphics, on);
         } finally {
            return;
         }
      }
   }
}
