package net.rim.plazmic.internal.mediaengine.model.smil.v0_0.ui;

import net.rim.device.api.math.Fixed32;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Manager;
import net.rim.plazmic.internal.mediaengine.model.smil.v0_0.Region;

public class Slide extends Manager {
   private int _slideStyle;
   private Field _imageComponent;
   private ActiveRichTextFieldWrapper _textComponent;
   private Region _imageRegion = (Region)(new Object(163624922399113216L));
   private Region _textRegion;
   public static final int DEFAULT_STYLE;
   public static final int MISSING_IMG_STYLE;

   public Slide(long style, int imageRegionBackgroundColor, int textRegionBackgroundColor) {
      super(style | 1125899906842624L | 281474976710656L | 1152921504606846976L | 2305843009213693952L);
      this._imageRegion.setBackgroundColor(imageRegionBackgroundColor);
      this._imageRegion.setFit(4);
      this.add(this._imageRegion);
      this._textRegion = (Region)(new Object(163624922399113216L));
      this._textRegion.setFit(8);
      this._textRegion.setBackgroundColor(textRegionBackgroundColor);
      this.add(this._textRegion);
      this._slideStyle = 0;
   }

   public void setSlideStyle(int slideStyle) {
      if (slideStyle == 1) {
         this._imageRegion.setFit(0);
      }

      this._slideStyle = slideStyle;
   }

   public void setImageComponent(Field imageField) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   Field getImageComponent() {
      return this._imageComponent;
   }

   public void setTextComponent(ActiveRichTextFieldWrapper textField) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public Region getImageRegion() {
      return this._imageRegion;
   }

   public Region getTextRegion() {
      return this._textRegion;
   }

   @Override
   public void sublayout(int availWidth, int availHeight) {
      if (this._imageComponent != null && this._imageComponent.getManager() == null) {
         this._imageRegion.add(this._imageComponent);
      }

      if (this._textComponent != null && this._textComponent.getManager() == null) {
         this._textRegion.add(this._textComponent);
      }

      int widthRemaining = availWidth;
      int heightRemaining = availHeight;
      if (this._textComponent != null) {
         this.layoutChild(this._textRegion, availWidth, availHeight);
         int numLines = this._textComponent.getDisplayLineCount();
         if (numLines <= 3) {
            heightRemaining -= this._textComponent.getHeight();
         } else {
            heightRemaining -= 3 * this._textComponent.getDisplayLineHeight(0);
         }
      }

      int imageWidth = 0;
      int imageHeight = 0;
      if (this._imageComponent != null) {
         if (!(this._imageComponent instanceof Object)) {
            imageWidth = this._imageComponent.getPreferredWidth();
            imageHeight = this._imageComponent.getPreferredHeight();
         } else {
            ScalableBitmapField sbf = (ScalableBitmapField)this._imageComponent;
            imageWidth = sbf.getBitmapWidth();
            imageHeight = sbf.getBitmapHeight();
         }

         int widthScaleFactor = Fixed32.div(widthRemaining << 16, imageWidth << 16);
         int heightScaleFactor = Fixed32.div(heightRemaining << 16, imageHeight << 16);
         int scaleFactor = Math.min(widthScaleFactor, heightScaleFactor);
         if (scaleFactor > 131072) {
            scaleFactor = 131072;
         }

         int origImageWidth = imageWidth;
         int origImageHeight = imageHeight;
         imageWidth = imageWidth * scaleFactor + 32768 >> 16;
         imageHeight = imageHeight * scaleFactor + 32768 >> 16;
         if (this._slideStyle != 1) {
            this.layoutChild(this._imageRegion, imageWidth, imageHeight);
            this.setPositionChild(this._imageRegion, availWidth - imageWidth >> 1, 0);
         } else {
            this.layoutChild(this._imageRegion, origImageWidth, origImageHeight);
            this.setPositionChild(this._imageRegion, availWidth - imageWidth >> 1, heightScaleFactor > 1 ? imageHeight - origImageHeight >> 1 : imageHeight);
         }

         heightRemaining -= imageHeight;
      }

      if (this._textComponent != null) {
         this.layoutChild(this._textRegion, availWidth, availHeight - imageHeight);
         this.setPositionChild(this._textRegion, 0, imageHeight);
      }

      this.setExtent(availWidth, availHeight);
      this.setVirtualExtent(availWidth, availHeight);
   }
}
