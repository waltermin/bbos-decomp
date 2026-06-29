package net.rim.device.apps.api.ui;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.math.Fixed32;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Display;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.internal.i18n.CommonResource;
import net.rim.vm.Memory;

public class ZoomImageCropper extends MainScreen {
   private ZoomImageCropper$ZoomBitmapFieldExt _imageField;
   private EncodedImage _image;
   private int _originalWidth;
   private int _originalHeight;
   private int _finalWidth;
   private int _finalHeight;
   private int _originalScaleFP;
   private boolean _cancelled;
   private boolean _initialised;
   private int _cropAspectRatioFP;
   private int _displayAspectRatioFP;
   private MenuItem _soItem = new ZoomImageCropper$1(
      this, ResourceBundle.getBundle(2545338480386147321L, "net.rim.device.apps.internal.resource.Ui"), 111, 131072, 0
   );
   private MenuItem _cropAndSaveItem = new ZoomImageCropper$2(
      this, ResourceBundle.getBundle(2545338480386147321L, "net.rim.device.apps.internal.resource.Ui"), 112, 332288, 0
   );
   private MenuItem _cancelItem = new ZoomImageCropper$3(
      this, ResourceBundle.getBundle(-6812884907508133143L, "net.rim.device.internal.resource.Common"), 19, 268500992, 0
   );
   public static final long ZIC_CROP_RECT = 1L;
   public static final long ZIC_CROP_CIRCLE = 2L;
   public static final int MIN_SIZE_FOR_LOADING_DIALOG = 200000;
   private static final int ZERO_POINT_75_FP = Fixed32.div(Fixed32.toFP(3), Fixed32.toFP(4));

   public ZoomImageCropper(EncodedImage image, int width, int height, long zicStyle) {
      super(2814814191747072L);
      this._image = image;
      this._finalWidth = width;
      this._finalHeight = height;
      this._originalWidth = image.getWidth();
      this._originalHeight = image.getHeight();
      if (this._originalWidth > Display.getWidth() || this._originalHeight > Display.getHeight()) {
         Memory.maximizeContiguousRAM(2 * this._originalWidth * this._originalHeight);
      }

      this.setDefaultClose(false);
      if (zicStyle == 0) {
         zicStyle = 1;
      }

      this._imageField = new ZoomImageCropper$ZoomBitmapFieldExt(this, image, zicStyle, 22517998136852516L);
      this._imageField.setPreferredSize(Display.getWidth(), Display.getHeight());
      this._imageField.setFocusStyle(3);
      this._imageField.setProtected(true);
      if (image.getFrameCount() > 1) {
         this._imageField.setMaximumLoopIterations(1);
      }

      this.initCropBoxAndImage();
      this._imageField.setScrollStepSetX(this._imageField.getScrollStepX() >> 1);
      this._imageField.setScrollStepSetY(this._imageField.getScrollStepY() >> 1);
      this._initialised = true;
   }

   private void initCropBoxAndImage() {
      int displayWidthFP = Fixed32.toFP(Display.getWidth());
      int displayHeightFP = Fixed32.toFP(Display.getHeight());
      int widthFP = Fixed32.toFP(this._finalWidth);
      int heightFP = Fixed32.toFP(this._finalHeight);
      this._cropAspectRatioFP = Fixed32.div(widthFP, heightFP);
      this._displayAspectRatioFP = Fixed32.div(displayWidthFP, displayHeightFP);
      if (this._cropAspectRatioFP < this._displayAspectRatioFP) {
         heightFP = Fixed32.mul(ZERO_POINT_75_FP, displayHeightFP);
         widthFP = Fixed32.mul(heightFP, this._cropAspectRatioFP);
      } else {
         widthFP = Fixed32.mul(ZERO_POINT_75_FP, displayWidthFP);
         heightFP = Fixed32.div(widthFP, this._cropAspectRatioFP);
      }

      this._imageField.layoutHack(Display.getWidth(), Display.getHeight());
      this._imageField
         .setCropRect(
            Fixed32.toInt(displayWidthFP - widthFP) >> 1, Fixed32.toInt(displayHeightFP - heightFP) >> 1, Fixed32.toInt(widthFP), Fixed32.toInt(heightFP)
         );
      if (this._originalWidth <= this._finalWidth && this._originalHeight <= this._finalHeight) {
         this._imageField.zoom1To1();
         this._originalScaleFP = this._imageField.getScale();
         int cropWidth = this._imageField.getCropWidth();
         int cropHeight = this._imageField.getCropHeight();
         if (cropWidth > this._originalWidth && cropHeight > this._originalHeight) {
            int cropWidthRatioFP = Fixed32.div(Fixed32.toFP(cropWidth), Fixed32.toFP(this._originalWidth));
            int cropHeightRatioFP = Fixed32.div(Fixed32.toFP(cropHeight), Fixed32.toFP(this._originalHeight));
            int oldScale = this._originalScaleFP;
            this._originalScaleFP = Fixed32.mul(this._originalScaleFP, cropWidthRatioFP > cropHeightRatioFP ? cropHeightRatioFP : cropWidthRatioFP);
            if (oldScale != this._originalScaleFP) {
               this._imageField.setScale(this._originalScaleFP, 0, 0, 0);
            }
         }
      } else {
         this._imageField.zoomToFit();
         this._originalScaleFP = this._imageField.getScale();
      }

      this._imageField._modified = false;
   }

   public Bitmap getBitmap() {
      int finalWidthFP = Fixed32.toFP(this._finalWidth);
      int finalHeightFP = Fixed32.toFP(this._finalHeight);
      int cropWidthFP = Fixed32.toFP(this._imageField.getCropWidth());
      int cropHeightFP = Fixed32.toFP(this._imageField.getCropHeight());
      int scaleForImageFP;
      if (this._cropAspectRatioFP < this._displayAspectRatioFP) {
         scaleForImageFP = Fixed32.div(finalHeightFP, cropHeightFP);
      } else {
         scaleForImageFP = Fixed32.div(finalWidthFP, cropWidthFP);
      }

      int bitmapWidthFP = Fixed32.mul(scaleForImageFP, Fixed32.toFP(this._imageField.getBitmapWidth()));
      int bitmapHeightFP = Fixed32.mul(scaleForImageFP, Fixed32.toFP(this._imageField.getBitmapHeight()));
      int minFinalWidth = Math.min(this._finalWidth, Fixed32.toInt(bitmapWidthFP));
      int minFinalHeight = Math.min(this._finalHeight, Fixed32.toInt(bitmapHeightFP));
      Bitmap bitmap = new Bitmap(minFinalWidth, minFinalHeight);
      Graphics graphics = new Graphics(bitmap);
      int scaleFP = this._imageField.getScale();
      int finalOverCropFP = Fixed32.div(Fixed32.toFP(this._finalWidth), Fixed32.toFP(this._imageField.getCropWidth()));
      int scaleFinalFP = Fixed32.mul(scaleFP, finalOverCropFP);
      int topXFinal = this._imageField.getFinalOffsetX(finalOverCropFP);
      int topYFinal = this._imageField.getFinalOffsetY(finalOverCropFP);
      this._image.setScale(1);
      ZoomImageCropper$ZoomBitmapFieldExt zoom = new ZoomImageCropper$ZoomBitmapFieldExt(this, this._image, 0, 18014398509482020L);
      zoom.setPreferredSize(minFinalWidth, minFinalHeight);
      zoom.setScale(scaleFinalFP, topXFinal, topYFinal, this._imageField.getRotationValue());
      synchronized (Application.getEventLock()) {
         zoom.layoutHack(minFinalWidth, minFinalHeight);
         graphics.setColor(16777215);
         graphics.fillRect(0, 0, minFinalWidth, minFinalHeight);
         zoom.paintHack(graphics);
         return bitmap;
      }
   }

   private void confirm() {
      if (!this._imageField.isModified()) {
         this.discard();
      } else {
         switch (Dialog.ask(1, CommonResource.getString(10003))) {
            case 0:
               return;
            case 1:
            default:
               this.save();
               return;
            case 2:
               this.discard();
         }
      }
   }

   private void discard() {
      this._cancelled = true;
      this.close();
   }

   @Override
   public void save() {
      this._cancelled = false;
      this.close();
   }

   public boolean isCancelled() {
      return this._cancelled;
   }

   @Override
   protected boolean keyChar(char key, int status, int time) {
      if (key == 27) {
         this.confirm();
         return true;
      } else {
         return key == '\n' ? true : super.keyChar(key, status, time);
      }
   }

   @Override
   protected void makeMenu(Menu menu, int instance) {
      if (instance == 0) {
         if (this._imageField != null && !this._imageField.isAnimated() && this._imageField.isModified()) {
            menu.add(this._soItem);
         }

         menu.add(this._cancelItem);
      }

      menu.add(this._cropAndSaveItem);
      super.makeMenu(menu, instance);
      menu.setDefaultIgnoreContextMenuDefault(this._cropAndSaveItem);
   }

   @Override
   public void onUiEngineAttached(boolean attach) {
      if (attach) {
         if (this._image.getLength() > 200000) {
            ZoomImageCropper$ProgressDialog dialog = new ZoomImageCropper$ProgressDialog();
            dialog.show();
            this.getApplication().invokeLater(new ZoomImageCropper$AddImageRunnable(this, this._imageField));
            this.getApplication().invokeLater(dialog);
            return;
         }

         this.add(this._imageField);
      }
   }

   public void zoomToFill() {
      this._imageField.zoomToFill();
   }

   public void zoomToFit() {
      this._imageField.zoomToFit();
   }
}
