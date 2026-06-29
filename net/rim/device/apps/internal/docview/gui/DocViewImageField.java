package net.rim.device.apps.internal.docview.gui;

import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.Trackball;
import net.rim.device.api.ui.XYRect;
import net.rim.device.apps.api.ui.ZoomBitmapField;

final class DocViewImageField extends ZoomBitmapField {
   private byte[] _imageBytes;
   private final XYRect _originalCropRect;
   private String _contentType;
   private boolean _gotContentType;

   DocViewImageField(byte[] bytes, XYRect cropRect, long style, boolean zoomToFitMinScale) {
      super(style);
      this.setMinimumZoomFactorFlag(zoomToFitMinScale);
      this.setImage(EncodedImage.createEncodedImage(bytes, 0, bytes.length));
      this._imageBytes = bytes;
      this._originalCropRect = cropRect;
      this.setFocusStyle(3);
      if (!Trackball.isSupported()) {
         this.setScrollStepSetX(DocViewGUIInternalConstants.SCROLLSTEP_X);
         this.setScrollStepSetY(DocViewGUIInternalConstants.SCROLLSTEP_Y);
      }
   }

   final byte[] getOriginalImageData() {
      return this._imageBytes;
   }

   final XYRect getCropRect() {
      return this._originalCropRect;
   }

   final EncodedImage getEncodedImage() {
      return this.getImage();
   }

   @Override
   protected final void onDisplay() {
      if (this.getPreferredWidth() != this.getContentWidth() || this.getPreferredHeight() != this.getContentHeight()) {
         this.updateLayout();
      }

      super.onDisplay();
   }

   @Override
   protected final void trackballSensitivityChanged() {
      super.trackballSensitivityChanged();
      if (this.isFocus() && !this.isZooming()) {
         Screen scr = this.getScreen();
         if (scr instanceof Object && this.getTransformedHeight() <= this.getHeight() && this.getTransformedWidth() <= this.getWidth()) {
            scr.setTrackballSensitivityYOffset(38 - Trackball.getSensitivityYForSystem());
            scr.setTrackballFilter(-1);
         }
      }
   }

   private final Manager getDesiredManager() {
      Manager mgr = this.getManager();
      if (mgr instanceof Object) {
         mgr = mgr.getManager();
      }

      return mgr;
   }

   @Override
   public final int getPreferredHeight() {
      Manager mgr = this.getDesiredManager();
      return mgr != null ? mgr.getContentHeight() : this.getContentHeight();
   }

   @Override
   public final int getPreferredWidth() {
      Manager mgr = this.getDesiredManager();
      return mgr != null ? mgr.getContentWidth() : this.getContentWidth();
   }

   final boolean hasRectForDetail() {
      int rotValue = this.getRotationValue();
      int originalWidth = rotValue != 0 && rotValue != 180 ? this._originalCropRect.height : this._originalCropRect.width;
      int originalHeight = rotValue != 0 && rotValue != 180 ? this._originalCropRect.width : this._originalCropRect.height;
      return this.getBitmapWidth() > this.getContentWidth() && originalWidth > this.getContentWidth()
         ? true
         : this.getBitmapHeight() > this.getContentHeight() && originalHeight > this.getContentHeight();
   }

   final String getContentType() {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 0
      // 01: getfield net/rim/device/apps/internal/docview/gui/DocViewImageField._gotContentType Z
      // 04: ifne 31
      // 07: aload 0
      // 08: getfield net/rim/device/apps/internal/docview/gui/DocViewImageField._contentType Ljava/lang/String;
      // 0b: ifnonnull 31
      // 0e: aload 0
      // 0f: getfield net/rim/device/apps/internal/docview/gui/DocViewImageField._imageBytes [B
      // 12: bipush 0
      // 13: aload 0
      // 14: getfield net/rim/device/apps/internal/docview/gui/DocViewImageField._imageBytes [B
      // 17: arraylength
      // 18: invokestatic net/rim/device/api/system/EncodedImage.createEncodedImage ([BII)Lnet/rim/device/api/system/EncodedImage;
      // 1b: astore 1
      // 1c: aload 0
      // 1d: aload 1
      // 1e: invokevirtual net/rim/device/api/system/EncodedImage.getMIMEType ()Ljava/lang/String;
      // 21: putfield net/rim/device/apps/internal/docview/gui/DocViewImageField._contentType Ljava/lang/String;
      // 24: goto 2c
      // 27: astore 1
      // 28: goto 2c
      // 2b: astore 1
      // 2c: aload 0
      // 2d: bipush 1
      // 2e: putfield net/rim/device/apps/internal/docview/gui/DocViewImageField._gotContentType Z
      // 31: aload 0
      // 32: getfield net/rim/device/apps/internal/docview/gui/DocViewImageField._contentType Ljava/lang/String;
      // 35: areturn
      // try (6 -> 18): 19 null
      // try (6 -> 18): 21 null
   }

   final XYRect getRectForDetail() {
      if (!this.hasRectForDetail()) {
         return null;
      }

      int rotValue = this.getRotationValue();
      int x = this.getXPos();
      int y = this.getYPos();
      int topX = this.getTopX();
      int topY = this.getTopY();
      int crtImageWidth = this.getBitmapWidth();
      int crtImageHeight = this.getBitmapHeight();
      int fldWidth = this.getContentWidth();
      int fldHeight = this.getContentHeight();
      if (fldWidth != DocViewGUIInternalConstants.SCREEN_WIDTH && (crtImageWidth <= fldWidth || crtImageWidth >= DocViewGUIInternalConstants.SCREEN_WIDTH)) {
         fldWidth = DocViewGUIInternalConstants.SCREEN_WIDTH;
      }

      if (fldHeight != DocViewGUIInternalConstants.SCREEN_HEIGHT
         && (crtImageHeight <= fldHeight || crtImageHeight >= DocViewGUIInternalConstants.SCREEN_HEIGHT)) {
         fldHeight = DocViewGUIInternalConstants.SCREEN_HEIGHT;
      }

      int xTop = topX;
      if (rotValue != 0) {
         switch (rotValue) {
            case 90:
               if (y > 0) {
                  xTop = 0;
               } else {
                  xTop = topY;
               }
               break;
            case 180:
               if (x > 0) {
                  xTop = 0;
               } else {
                  xTop = crtImageWidth - topX - fldWidth;
               }
               break;
            case 270:
               if (y > 0) {
                  xTop = 0;
               } else {
                  xTop = crtImageHeight - topY - fldHeight;
               }
         }
      }

      int yTop = topY;
      if (rotValue != 0) {
         switch (rotValue) {
            case 90:
               if (x > 0) {
                  yTop = 0;
               } else {
                  yTop = crtImageWidth - topX - fldWidth;
               }
               break;
            case 180:
               if (y > 0) {
                  yTop = 0;
               } else {
                  yTop = crtImageHeight - topY - fldHeight;
               }
               break;
            case 270:
               if (x > 0) {
                  yTop = 0;
               } else {
                  yTop = topX;
               }
         }
      }

      int crtWidth = rotValue != 0 && rotValue != 180 ? crtImageHeight : crtImageWidth;
      int crtHeight = rotValue != 0 && rotValue != 180 ? crtImageWidth : crtImageHeight;
      int screenWidth = rotValue != 0 && rotValue != 180 ? fldHeight : fldWidth;
      int screenHeight = rotValue != 0 && rotValue != 180 ? fldWidth : fldHeight;
      int xPos = xTop * this._originalCropRect.width / crtWidth;
      int yPos = yTop * this._originalCropRect.height / crtHeight;
      int width = Math.min(screenWidth, crtWidth - xTop) * this._originalCropRect.width / crtWidth;
      int height = Math.min(screenHeight, crtHeight - yTop) * this._originalCropRect.height / crtHeight;
      int flipValue = this.getFlipValue();
      boolean flipAroundX = (flipValue & 1) != 0;
      boolean flipAroundY = (flipValue & 2) != 0;
      if (flipAroundX) {
         yPos = this._originalCropRect.height - yPos - height;
      }

      if (flipAroundY) {
         xPos = this._originalCropRect.width - xPos - width;
      }

      xPos += this._originalCropRect.x;
      yPos += this._originalCropRect.y;
      return (XYRect)(new Object(Math.max(0, xPos), Math.max(0, yPos), width, height));
   }
}
