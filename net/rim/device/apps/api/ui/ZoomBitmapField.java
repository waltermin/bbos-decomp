package net.rim.device.apps.api.ui;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.math.Fixed32;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Display;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.ui.ContextMenu;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.Trackball;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.theme.ThemeAttributeSet;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.MathUtilities;
import net.rim.device.internal.ui.Image;
import net.rim.device.internal.ui.UiInternal;

public class ZoomBitmapField extends MIMEContentAnimatedBitmapField implements ImageOperation {
   private int MIN_ZOOM_FP;
   private float MIN_ZOOM_LOG;
   private int _preferredWidth;
   private int _preferredHeight;
   private final Bitmap _cropBitmapImage;
   private final Graphics _graphicsCropImage;
   private final XYRect _rectImage;
   private int _bitmapScale;
   private boolean _tileUpdateNeeded;
   private FocusPaintManager _focusManager;
   private int _scale;
   private int _rotation;
   private int _topX;
   private int _topY;
   private int _scrollStepX;
   private int _scrollStepY;
   private int _scrollStepSetX;
   private int _scrollStepSetY;
   private final int[] _tempx;
   private final int[] _tempy;
   private boolean _isZooming;
   private int _reflection;
   private boolean _hotKeyEnabled;
   private int _zoomTo;
   private MenuItem _zItem;
   private MenuItem _z1to1Item;
   private MenuItem _zoomToFitItem;
   private MenuItem _zoomToFillItem;
   private MenuItem _rItem;
   private int _zoomingStartTopX;
   private int _zoomingStartTopY;
   private int _zoomingStartScale;
   private int _rotationStart;
   private boolean _zoomToFitMinScale;
   private int[] _zoomBoundaries;
   private int _fitFactor;
   private int _fillFactor;
   private static final int ZOOMSTEP_FP = Fixed32.div(Fixed32.toFP(5), Fixed32.toFP(4));
   private static final float ZOOMSTEP_LOG = (float)MathUtilities.log((double)4608308318706860032L);
   private static final int MAX_ZOOM_FP = Fixed32.toFP(8);
   private static final float MAX_ZOOM_LOG = (float)MathUtilities.log((double)4620693217682128896L) / ZOOMSTEP_LOG;
   private static final int MIN_ZOOM_FP_STD = Fixed32.div(65536, Fixed32.toFP(8));
   private static final float MIN_ZOOM_LOG_STD = (float)MathUtilities.log((double)4593671619917905920L) / ZOOMSTEP_LOG;
   private static final int LEGACY_ALT_TRACKWHEEL = 1073741825;
   public static final int REFLECTION_HORIZONTAL = 2;
   public static final int REFLECTION_VERTICAL = 1;
   private static Image _zoomImage = ThemeManager.getThemeAwareImage("net_rim_zoom");
   private static Image _zoomElevatorImage = ThemeManager.getThemeAwareImage("net_rim_zoom_elevator");
   private static final int ZOOM_TO_NONE = 0;
   private static final int ZOOM_TO_FILL = 1;
   private static final int ZOOM_TO_FIT = 2;

   public void zoomOriginal() {
      if (!this.isInOriginalState()) {
         this.zoom(65536, 65536, true, true);
         boolean scrolled = false;
         if (this._topY > 0 && this.getTransformedHeight() > this.getPreferredHeight()) {
            this._topY = 0;
            scrolled = true;
         }

         if (this._topX > 0 && this.getTransformedWidth() > this.getPreferredWidth()) {
            this._topX = 0;
            scrolled = true;
         }

         if (scrolled) {
            this.checkCropRectVsTopPoints();
         }

         this.invalidate();
      }
   }

   public int getFlipValue() {
      return this._reflection;
   }

   public int getFocusStyle() {
      return this._focusManager.getFocusStyle();
   }

   public boolean getHotKeyHandleStatus() {
      return this._hotKeyEnabled;
   }

   public int getImageX() {
      return this.getTopX();
   }

   public int getImageY() {
      return this.getTopY();
   }

   public int getRotationValue() {
      return this._rotation;
   }

   public int getScale() {
      return this._scale;
   }

   public int getScaledHeight() {
      EncodedImage image = this.getImage();
      return image != null ? image.getScaledHeight() : 0;
   }

   public int getScaledWidth() {
      EncodedImage image = this.getImage();
      return image != null ? image.getScaledWidth() : 0;
   }

   public final int getScrollStepSetX() {
      return this._scrollStepSetX > 0 ? this._scrollStepSetX : this._scrollStepX;
   }

   public final int getScrollStepSetY() {
      return this._scrollStepSetY > 0 ? this._scrollStepSetY : this._scrollStepY;
   }

   int getScrollStepX() {
      return this._scrollStepX;
   }

   int getScrollStepY() {
      return this._scrollStepY;
   }

   protected int getTopX() {
      return this._topX;
   }

   protected int getTopY() {
      return this._topY;
   }

   public int getTransformedHeight() {
      EncodedImage image = this.getImage();
      int transformedHeight = 0;
      if (image != null) {
         transformedHeight = this._rotation != 0 && this._rotation != 180 ? image.getScaledWidth() : image.getScaledHeight();
      }

      return transformedHeight;
   }

   public int getTransformedWidth() {
      EncodedImage image = this.getImage();
      int transformedWidth = 0;
      if (image != null) {
         transformedWidth = this._rotation != 0 && this._rotation != 180 ? image.getScaledHeight() : image.getScaledWidth();
      }

      return transformedWidth;
   }

   public boolean isInOriginalState() {
      if (!this.isAnimated()) {
         if (this._scale != 65536) {
            return false;
         }

         if (this._rotation != 0) {
            return false;
         }

         if (this._topX > 0 && this.getTransformedWidth() > this.getPreferredWidth()
            || this._topY > 0 && this.getTransformedHeight() > this.getPreferredHeight()) {
            return false;
         }
      }

      return true;
   }

   public boolean isZooming() {
      return this._isZooming;
   }

   void paintZoom(Graphics graphics) {
      if (this._isZooming) {
         int x = 10;
         int y = 10;
         int zoomWidth = _zoomImage.getWidth(100, 200);
         int zoomHeight = _zoomImage.getHeight(100, 200);
         _zoomImage.paint(graphics, x, y, zoomWidth, zoomHeight);
         int zoomElevatorWidth = _zoomElevatorImage.getWidth(100, 100);
         int zoomElevatorHeight = _zoomElevatorImage.getHeight(100, 100);
         float zoom = (float)MathUtilities.log(Fixed32.toIntTenThou(this._scale) / 1176256512) / ZOOMSTEP_LOG;
         zoom = (zoom - this.MIN_ZOOM_LOG) / (MAX_ZOOM_LOG - this.MIN_ZOOM_LOG) * (zoomHeight - zoomElevatorHeight);
         _zoomElevatorImage.paint(graphics, x, y + zoomHeight - (int)zoom - zoomElevatorHeight, zoomElevatorWidth, zoomElevatorHeight);
      }
   }

   public void rotate(int angle) {
      switch (angle) {
         case 0:
         case 360:
            return;
         case 90:
         case 180:
         case 270:
            if (!this.isAnimated()) {
               this._rotation += angle;
               if (this._rotation >= 360) {
                  this._rotation -= 360;
               }

               if (angle == 90 || angle == 270) {
                  int tempValue = this._topX;
                  this._topX = this._topY;
                  this._topY = tempValue;
               }

               this.checkTopPoints(false, false);
               this.checkCropRectVsTopPoints();
               this.calculateMinZoomValues(this.getPreferredWidth(), this.getPreferredHeight());
               this.setTrackballSensitivity(this.isFocus());
               this.setZoomBoundaries(this.getPreferredWidth(), this.getPreferredHeight());
               this.invalidate();
            }

            return;
         default:
            throw new Object();
      }
   }

   public void zoomToFill() {
      this.zoomToInternal(this.getPreferredWidth(), this.getPreferredHeight(), 1, true, true);
   }

   public boolean rotateStepWithoutInvalidate() {
      if (this.isAnimated()) {
         return false;
      }

      this._rotation += 90;
      if (this._rotation == 360) {
         this._rotation = 0;
      }

      int tempValue = this._topX;
      this._topX = this._topY;
      this._topY = tempValue;
      this.checkTopPoints(false, false);
      this.checkCropRectVsTopPoints();
      this.calculateMinZoomValues(this.getPreferredWidth(), this.getPreferredHeight());
      this.setZoomBoundaries(this.getPreferredWidth(), this.getPreferredHeight());
      return true;
   }

   void zoom(int crtZoomValue, int crtZoomValueInverse, boolean doDefaultXCenter, boolean doDefaultYCenter) {
      this.zoomImpl(crtZoomValue, crtZoomValueInverse, doDefaultXCenter, doDefaultYCenter, this._rotation);
   }

   public boolean scroll(int dx, int dy) {
      boolean result = false;
      if (this.isAnimated()) {
         return false;
      }

      int transformedWidth = this.getTransformedWidth();
      int transformedHeight = this.getTransformedHeight();
      int fieldAvailableWidth = this.getPreferredWidth();
      int fieldAvailableHeight = this.getPreferredHeight();
      if (dx != 0 && transformedWidth > fieldAvailableWidth && (dx > 0 && this._topX < transformedWidth - fieldAvailableWidth || dx < 0 && this._topX > 0)) {
         this._topX = this._topX + dx * this.getScrollStepSetX();
         if (this._topX < 0) {
            this._topX = 0;
         }

         if (this._topX >= transformedWidth - fieldAvailableWidth) {
            this._topX = transformedWidth - fieldAvailableWidth;
         }

         result = true;
      }

      if (dy != 0 && transformedHeight > fieldAvailableHeight && (dy > 0 && this._topY < transformedHeight - fieldAvailableHeight || dy < 0 && this._topY > 0)) {
         this._topY = this._topY + dy * this.getScrollStepSetY();
         if (this._topY < 0) {
            this._topY = 0;
         }

         if (this._topY >= transformedHeight - fieldAvailableHeight) {
            this._topY = transformedHeight - fieldAvailableHeight;
         }

         result = true;
      }

      if (result) {
         this.checkCropRectVsTopPoints();
         this.invalidate();
      }

      return result;
   }

   public void scrollToBottom() {
      if (!this.isAnimated() && this.getTransformedHeight() > this.getPreferredHeight()) {
         this._topY = this.getTransformedHeight() - this.getPreferredHeight();
         this.checkCropRectVsTopPoints();
         this.invalidate();
      }
   }

   public void scrollToLeft() {
      if (!this.isAnimated() && this._topX > 0 && this.getTransformedWidth() > this.getPreferredWidth()) {
         this._topX = 0;
         this.checkCropRectVsTopPoints();
         this.invalidate();
      }
   }

   public void scrollToRight() {
      if (!this.isAnimated() && this.getTransformedWidth() > this.getPreferredWidth()) {
         this._topX = this.getTransformedWidth() - this.getPreferredWidth();
         this.checkCropRectVsTopPoints();
         this.invalidate();
      }
   }

   public void scrollToTop() {
      if (!this.isAnimated() && this._topY > 0 && this.getTransformedHeight() > this.getPreferredHeight()) {
         this._topY = 0;
         this.checkCropRectVsTopPoints();
         this.invalidate();
      }
   }

   public boolean setFlipValue(int reflection) {
      if (this.isAnimated()) {
         return false;
      }

      if (this._reflection == reflection) {
         return true;
      }

      this._reflection = reflection;
      if (this.getManager() != null) {
         this.checkCropRectVsTopPoints();
         this.invalidate();
      }

      return true;
   }

   public boolean setFocusStyle(int newStyle) {
      if (this._focusManager.setFocusStyle(newStyle)) {
         if (this.isFocus()) {
            this.invalidate();
         }

         return true;
      } else {
         return false;
      }
   }

   public void setHotKeyHandleStatus(boolean enable) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public void setMinimumZoomFactorFlag(boolean minZoomFlag) {
      if (this._zoomToFitMinScale != minZoomFlag) {
         this._zoomToFitMinScale = minZoomFlag;
         Manager manager = this.getManager();
         if (manager != null && manager.isValidLayout()) {
            this.calculateMinZoomValues(this.getPreferredWidth(), this.getPreferredHeight());
         }

         this.setZoomBoundaries(this.getPreferredWidth(), this.getPreferredHeight());
      }
   }

   public boolean getMinimumZoomFactorFlag() {
      return this._zoomToFitMinScale;
   }

   public void setPreferredSize(int preferredWidth, int preferredHeight) {
      if (preferredWidth >= -1 && preferredHeight >= -1) {
         this._preferredWidth = preferredWidth;
         this._preferredHeight = preferredHeight;
      } else {
         throw new Object();
      }
   }

   public void setScale(int scaleFactor, int topX, int topY, int rotationValue) {
      if (!this.isAnimated()) {
         this.zoomImpl(scaleFactor, Fixed32.div(65536, scaleFactor), false, false, rotationValue);
         this._topX = topX;
         this._topY = topY;
         this.checkTopPoints(false, false);
         this.checkCropRectVsTopPoints();
         this.calculateMinZoomValues(this.getPreferredWidth(), this.getPreferredHeight());
      }
   }

   public void scaleToFit(int width, int height, boolean fitFully, int topX, int topY, int rotationValue) {
      EncodedImage image = this.getImage();
      if (image != null) {
         int imageWidthF32;
         int imageHeightF32;
         if (rotationValue != 90 && rotationValue != 270) {
            imageWidthF32 = Fixed32.toFP(image.getWidth());
            imageHeightF32 = Fixed32.toFP(image.getHeight());
         } else {
            imageWidthF32 = Fixed32.toFP(image.getHeight());
            imageHeightF32 = Fixed32.toFP(image.getWidth());
         }

         int widthF32 = Fixed32.toFP(width);
         int heightF32 = Fixed32.toFP(height);
         int widthFactorF32 = Fixed32.div(widthF32, imageWidthF32);
         int heightFactorF32 = Fixed32.div(heightF32, imageHeightF32);
         if (fitFully ? widthFactorF32 >= heightFactorF32 : widthFactorF32 <= heightFactorF32) {
            this.zoomImpl(heightFactorF32, Fixed32.div(imageHeightF32, heightF32), false, false, rotationValue);
         } else {
            this.zoomImpl(widthFactorF32, Fixed32.div(imageWidthF32, widthF32), false, false, rotationValue);
         }

         this._topX = topX;
         this._topY = topY;
         this.checkTopPoints(false, false);
         this.checkCropRectVsTopPoints();
      }
   }

   public final void setScrollStepSetX(int scrollStepSetX) {
      this._scrollStepSetX = scrollStepSetX > 0 ? scrollStepSetX : 0;
   }

   public final void setScrollStepSetY(int scrollStepSetY) {
      this._scrollStepSetY = scrollStepSetY > 0 ? scrollStepSetY : 0;
   }

   protected void trackballSensitivityChanged() {
   }

   public void setZooming(boolean isZooming) {
      if (this._isZooming != isZooming) {
         this._isZooming = isZooming;
         if (isZooming) {
            this._zoomingStartScale = this._scale;
            this._zoomingStartTopX = this._topX;
            this._zoomingStartTopY = this._topY;
            this._rotationStart = this._rotation;
         } else {
            this._zoomingStartTopX = this._zoomingStartTopY = -1;
            this._zoomingStartScale = -1;
            this._rotationStart = -1;
         }

         this.invalidate();
      }

      this.setTrackballSensitivity(this.isFocus());
   }

   void updateTile() {
      EncodedImage image = this.getImage();
      if (image != null) {
         this._graphicsCropImage.clear(0, 0, this._rectImage.width, this._rectImage.height);
         this._graphicsCropImage.drawImage(0, 0, this._rectImage.width, this._rectImage.height, image, 0, this._rectImage.x, this._rectImage.y);
      }

      this._tileUpdateNeeded = false;
   }

   void zoom(int crtZoomValue, boolean doDefaultXCenter, boolean doDefaultYCenter) {
      this.zoom(crtZoomValue, Fixed32.div(65536, crtZoomValue), doDefaultXCenter, doDefaultYCenter);
   }

   @Override
   public boolean zoom(boolean zoomIn) {
      if (!this.canZoom(zoomIn)) {
         return false;
      }

      int nextZoomValue = this.getNextZoomFactor(zoomIn);
      if (this._zoomBoundaries != null) {
         int smallZoomValue = Math.min(this._scale, nextZoomValue);
         int largeZoomValue = Math.max(this._scale, nextZoomValue);

         for (int i = 0; i < this._zoomBoundaries.length; i++) {
            if (smallZoomValue < this._zoomBoundaries[i] && this._zoomBoundaries[i] < largeZoomValue) {
               nextZoomValue = this._zoomBoundaries[i];
               break;
            }
         }
      }

      if (nextZoomValue == this._fitFactor) {
         this.zoomToFit();
         return true;
      } else if (nextZoomValue == this._fillFactor) {
         this.zoomToInternal(
            this.getPreferredWidth(),
            this.getPreferredHeight(),
            1,
            this.getTransformedWidth() <= this.getPreferredWidth(),
            this.getTransformedHeight() <= this.getPreferredHeight()
         );
         return true;
      } else {
         this.zoom(nextZoomValue, this.getTransformedWidth() <= this.getPreferredWidth(), this.getTransformedHeight() <= this.getPreferredHeight());
         return true;
      }
   }

   @Override
   public boolean scroll(int amount, boolean horz) {
      return horz ? this.scroll(amount, 0) : this.scroll(0, amount);
   }

   @Override
   public void zoom1To1() {
      if (!this.isAnimated()) {
         this.zoom(65536, true, true);
      }
   }

   @Override
   public void zoomToFit() {
      this.zoomToInternal(this.getPreferredWidth(), this.getPreferredHeight(), 2, true, true);
   }

   @Override
   public boolean rotateStep() {
      boolean retVal = this.rotateStepWithoutInvalidate();
      this.invalidate();
      return retVal;
   }

   @Override
   public boolean canZoom(boolean zoomIn) {
      if (this.isAnimated()) {
         return false;
      } else {
         int nextZoomFP = this.getNextZoomFactor(zoomIn);
         if (this.MIN_ZOOM_FP <= nextZoomFP && nextZoomFP <= MAX_ZOOM_FP) {
            int bitmapWidth = this._rotation != 0 && this._rotation != 180 ? super.getBitmapHeight() : super.getBitmapWidth();
            int bitmapHeight = this._rotation != 0 && this._rotation != 180 ? super.getBitmapWidth() : super.getBitmapHeight();
            int newImageWidth = Fixed32.toRoundedInt(Fixed32.mul(Fixed32.toFP(bitmapWidth), nextZoomFP));
            int newImageHeight = Fixed32.toRoundedInt(Fixed32.mul(Fixed32.toFP(bitmapHeight), nextZoomFP));
            return newImageWidth != 0 && newImageHeight != 0;
         } else {
            return false;
         }
      }
   }

   @Override
   protected void paintImage(Graphics graphics, int x, int y, int width, int height, EncodedImage image, int frame, int left, int top) {
      if (this.isAnimated()) {
         super.paintImage(graphics, x, y, width, height, image, frame, left, top);
      } else if (this._rectImage != null) {
         if (this._tileUpdateNeeded) {
            this.updateTile();
         }

         int xOffset;
         int yOffset;
         if (this._rotation == 0) {
            xOffset = this._rectImage.x;
            yOffset = this._rectImage.y;
         } else if (this._rotation == 90) {
            xOffset = image.getScaledHeight() - (this._rectImage.y + this._rectImage.height);
            yOffset = this._rectImage.x;
         } else if (this._rotation == 270) {
            yOffset = image.getScaledWidth() - (this._rectImage.x + this._rectImage.width);
            xOffset = this._rectImage.y;
         } else {
            xOffset = image.getScaledWidth() - (this._rectImage.x + this._rectImage.width);
            yOffset = image.getScaledHeight() - (this._rectImage.y + this._rectImage.height);
         }

         this.paintField(graphics, x, y, this._cropBitmapImage, this._topX - xOffset, this._topY - yOffset);
         return;
      }
   }

   @Override
   public int getPreferredHeight() {
      if (this._preferredHeight != -1) {
         return this._preferredHeight;
      }

      Manager manager = this.getManager();
      int height = 0;
      if (manager != null) {
         height = manager.getContentHeight();
      }

      if (height > Display.getHeight()) {
         height = Display.getHeight();
      }

      return height;
   }

   private void resetView() {
      this._topX = this._topY = 0;
      this._scale = 65536;
      this._rotation = 0;
      this._zoomTo = 0;
      this._zoomingStartTopX = this._zoomingStartTopY = -1;
      this._zoomingStartScale = -1;
      this._rotationStart = -1;
      if (this._rectImage != null) {
         this._rectImage.set(0, 0, 0, 0);
      }
   }

   @Override
   public int getPreferredWidth() {
      if (this._preferredWidth != -1) {
         return this._preferredWidth;
      }

      Manager manager = this.getManager();
      int width = 0;
      if (manager != null) {
         width = manager.getContentWidth();
      }

      if (width > Display.getWidth()) {
         width = Display.getWidth();
      }

      return width;
   }

   @Override
   protected void paintBitmap(Graphics graphics, int x, int y, int width, int height, Bitmap bitmap, int left, int top) {
      throw new RuntimeException("cod2jar: tail call (jumpspecial)");
   }

   private int getRealTopY() {
      int yTop = this._topY;
      switch (this._rotation) {
         case 90:
            return this.getTransformedWidth() - this.getPreferredWidth() - this._topX;
         case 180:
            return this.getTransformedHeight() - this.getPreferredHeight() - this._topY;
         case 270:
            yTop = this._topX;
         default:
            return yTop;
      }
   }

   private int scaleToSource(int value) {
      int retValue = value;
      if (retValue != 0 && this._scale != 65536) {
         retValue = Fixed32.toRoundedInt(Fixed32.div(Fixed32.toFP(retValue), this._scale));
      }

      return retValue;
   }

   @Override
   protected boolean navigationMovement(int dx, int dy, int status, int time) {
      if (!this.isAnimated() && !this.isZooming()) {
         if ((status & 1073741825) != 1073741825) {
            if ((status & 2) != 0) {
               return false;
            }

            return this.scroll(dx, dy);
         }

         if (dy != 0) {
            return this.scroll(dy, true);
         }
      }

      return super.navigationMovement(dx, dy, status, time);
   }

   private void checkCropRectVsTopPoints() {
      EncodedImage image = this.getImage();
      if (image != null) {
         int fieldAvailableWidth = this.getPreferredWidth();
         int fieldAvailableHeight = this.getPreferredHeight();
         if (this._rotation == 90 || this._rotation == 270) {
            int temp = fieldAvailableWidth;
            fieldAvailableWidth = fieldAvailableHeight;
            fieldAvailableHeight = temp;
         }

         int visibleWidth = Math.min(fieldAvailableWidth, this.getScaledWidth());
         int visibleHeight = Math.min(fieldAvailableHeight, this.getScaledHeight());
         int topX = this.getRealTopX();
         int topY = this.getRealTopY();
         boolean noXAdjustment = topX >= this._rectImage.x && this._rectImage.x + this._rectImage.width >= topX + visibleWidth;
         boolean noYAdjustment = topY >= this._rectImage.y && this._rectImage.y + this._rectImage.height >= topY + visibleHeight;
         if (!noXAdjustment || !noYAdjustment || this._bitmapScale != this._scale) {
            if (!noXAdjustment) {
               if (topX < this._rectImage.x) {
                  this._rectImage.x = topX + visibleWidth - this._rectImage.width + this.getScrollStepSetX();
               } else {
                  this._rectImage.x = topX - this.getScrollStepSetX();
               }

               this._rectImage.x = Math.max(this._rectImage.x, 0);
            }

            if (!noYAdjustment) {
               if (topY < this._rectImage.y) {
                  this._rectImage.y = topY + visibleHeight - this._rectImage.height + this.getScrollStepSetY();
               } else {
                  this._rectImage.y = topY - this.getScrollStepSetY();
               }

               this._rectImage.y = Math.max(this._rectImage.y, 0);
            }

            int sourceX = this._rectImage.x;
            int sourceY = this._rectImage.y;
            if (sourceX + this._cropBitmapImage.getWidth() > image.getScaledWidth()) {
               sourceX = Math.max(0, image.getScaledWidth() - this._cropBitmapImage.getWidth());
               this._rectImage.x = sourceX;
            }

            if (sourceY + this._cropBitmapImage.getHeight() > image.getScaledHeight()) {
               sourceY = Math.max(0, image.getScaledHeight() - this._cropBitmapImage.getHeight());
               this._rectImage.y = sourceY;
            }

            this._rectImage.width = Math.min(image.getScaledWidth(), this._cropBitmapImage.getWidth());
            this._rectImage.height = Math.min(image.getScaledHeight(), this._cropBitmapImage.getHeight());
            this._bitmapScale = this._scale;
            this._tileUpdateNeeded = true;
         }
      }
   }

   private void checkTopPoints(boolean doDefaultXCenter, boolean doDefaultYCenter) {
      int fieldAvailableWidth = this.getPreferredWidth();
      int fieldAvailableHeight = this.getPreferredHeight();
      if (fieldAvailableWidth >= this.getTransformedWidth()) {
         this._topX = 0;
      } else if (doDefaultXCenter) {
         this._topX = this.getTransformedWidth() - fieldAvailableWidth >> 1;
      } else {
         if (this._topX < 0) {
            this._topX = 0;
         }

         if (this._topX >= this.getTransformedWidth() - fieldAvailableWidth) {
            this._topX = this.getTransformedWidth() - fieldAvailableWidth;
         }
      }

      if (fieldAvailableHeight >= this.getTransformedHeight()) {
         this._topY = 0;
      } else if (doDefaultYCenter) {
         this._topY = this.getTransformedHeight() - fieldAvailableHeight >> 1;
      } else {
         if (this._topY < 0) {
            this._topY = 0;
         }

         if (this._topY >= this.getTransformedHeight() - fieldAvailableHeight) {
            this._topY = this.getTransformedHeight() - fieldAvailableHeight;
         }
      }
   }

   @Override
   protected void drawFocus(Graphics graphics, boolean on) {
      if (this._focusManager.getFocusStyle() != 3) {
         if (!this.isAnimated()) {
            if (on) {
               XYRect focusRect = (XYRect)(new Object());
               this.getFocusRect(focusRect);
               focusRect.x = Math.max(this.getXPos(), 0) + focusRect.x;
               focusRect.y = Math.max(this.getYPos(), 0) + focusRect.y;
               focusRect.width = Math.min(focusRect.width, this.getTransformedWidth());
               focusRect.height = Math.min(focusRect.height, this.getTransformedHeight());
               this._focusManager.drawFocus(graphics, focusRect, ThemeAttributeSet.getColor(this, 2));
               focusRect = null;
            } else {
               super.drawFocus(graphics, on);
            }
         } else {
            super.drawFocus(graphics, on);
         }
      }
   }

   @Override
   public int getBitmapHeight() {
      return this.getImage() != null && !this.isAnimated() ? this.getTransformedHeight() : super.getBitmapHeight();
   }

   @Override
   public int getBitmapWidth() {
      return this.getImage() != null && !this.isAnimated() ? this.getTransformedWidth() : super.getBitmapWidth();
   }

   @Override
   public void setBitmap(Bitmap bitmap) {
      int oldWidth = this.getPreferredWidth();
      int oldHeight = this.getPreferredHeight();
      super.setBitmap(bitmap);
      this.resetView();
      if (!this.isAnimated()) {
         Manager manager = this.getManager();
         if (manager != null && manager.isValidLayout()) {
            int prefWidth = this.getPreferredWidth();
            int prefHeight = this.getPreferredHeight();
            if (oldWidth == prefWidth && oldHeight == prefHeight) {
               this.checkCropRectVsTopPoints();
               this.calculateMinZoomValues(prefWidth, prefHeight);
            }

            if (this.isFocus()) {
               this.setTrackballSensitivity(true);
            }
         }
      }
   }

   static int getCropBitmapHeight() {
      return Display.getHeight() * 5 / 3;
   }

   static int getCropBitmapWidth() {
      return Display.getWidth() * 5 / 3;
   }

   public ZoomBitmapField(long style) {
      super(null, style);
      this.MIN_ZOOM_FP = MIN_ZOOM_FP_STD;
      this.MIN_ZOOM_LOG = MIN_ZOOM_LOG_STD;
      this._preferredWidth = -1;
      this._preferredHeight = -1;
      this._cropBitmapImage = (Bitmap)(new Object(getCropBitmapWidth(), getCropBitmapHeight()));
      this._graphicsCropImage = (Graphics)(new Object(this._cropBitmapImage));
      this._rectImage = (XYRect)(new Object());
      this._focusManager = new FocusPaintManager();
      this._scale = 65536;
      this._scrollStepX = 4;
      this._scrollStepY = 4;
      this._tempx = new int[4];
      this._tempy = new int[4];
      this._hotKeyEnabled = true;
      this._zoomTo = 0;
      this._zoomingStartTopX = -1;
      this._zoomingStartTopY = -1;
      this._zoomingStartScale = -1;
      this._rotationStart = -1;
      this._zoomToFitMinScale = true;
      this._fitFactor = -1;
      this._fillFactor = -1;
   }

   public ZoomBitmapField(EncodedImage image, int maxLoopIterations, long style) {
      super(image, maxLoopIterations, style);
      this.MIN_ZOOM_FP = MIN_ZOOM_FP_STD;
      this.MIN_ZOOM_LOG = MIN_ZOOM_LOG_STD;
      this._preferredWidth = -1;
      this._preferredHeight = -1;
      this._cropBitmapImage = (Bitmap)(new Object(getCropBitmapWidth(), getCropBitmapHeight()));
      this._graphicsCropImage = (Graphics)(new Object(this._cropBitmapImage));
      this._rectImage = (XYRect)(new Object());
      this._focusManager = new FocusPaintManager();
      this._scale = 65536;
      this._scrollStepX = 4;
      this._scrollStepY = 4;
      this._tempx = new int[4];
      this._tempy = new int[4];
      this._hotKeyEnabled = true;
      this._zoomTo = 0;
      this._zoomingStartTopX = -1;
      this._zoomingStartTopY = -1;
      this._zoomingStartScale = -1;
      this._rotationStart = -1;
      this._zoomToFitMinScale = true;
      this._fitFactor = -1;
      this._fillFactor = -1;
   }

   private void calculateMinZoomValues(int mgrWidth, int mgrHeight) {
      if (this._zoomToFitMinScale && mgrWidth > 0 && mgrHeight > 0 && this.canZoomTo(2)) {
         EncodedImage image = this.getImage();
         if (image != null) {
            int bitmapWidth = this._rotation != 0 && this._rotation != 180 ? image.getHeight() : image.getWidth();
            int bitmapHeight = this._rotation != 0 && this._rotation != 180 ? image.getWidth() : image.getHeight();
            int zoomToFitFactor = Math.min(
               Fixed32.div(Fixed32.toFP(mgrWidth), Fixed32.toFP(bitmapWidth)), Fixed32.div(Fixed32.toFP(mgrHeight), Fixed32.toFP(bitmapHeight))
            );
            this.MIN_ZOOM_FP = Math.min(zoomToFitFactor, 65536);
            if (this.MIN_ZOOM_FP == 65536) {
               this.MIN_ZOOM_LOG = (float)false;
            } else {
               this.MIN_ZOOM_LOG = (float)MathUtilities.log(Fixed32.toIntTenThou(zoomToFitFactor) / 1176256512) / ZOOMSTEP_LOG;
            }

            if (this._scale < this.MIN_ZOOM_FP) {
               this.zoomImpl(this.MIN_ZOOM_FP, Fixed32.div(65536, this.MIN_ZOOM_FP), true, true, this._rotation);
               this._topX = this._topY = 0;
            }

            return;
         }
      }

      this.MIN_ZOOM_FP = MIN_ZOOM_FP_STD;
      this.MIN_ZOOM_LOG = MIN_ZOOM_LOG_STD;
   }

   @Override
   public void setImage(EncodedImage image) {
      int oldWidth = this.getPreferredWidth();
      int oldHeight = this.getPreferredHeight();
      super.setImage(image);
      this.resetView();
      if (!this.isAnimated()) {
         Manager manager = this.getManager();
         if (manager != null && manager.isValidLayout()) {
            int prefWidth = this.getPreferredWidth();
            int prefHeight = this.getPreferredHeight();
            if (oldWidth == prefWidth && oldHeight == prefHeight) {
               this.checkCropRectVsTopPoints();
               this.calculateMinZoomValues(prefWidth, prefHeight);
            }

            if (this.isFocus()) {
               this.setTrackballSensitivity(true);
            }
         }
      }

      this.startAnimation();
   }

   private void setZoomBoundaries(int mgrWidth, int mgrHeight) {
      if (mgrWidth > 0 && mgrHeight > 0) {
         EncodedImage image = this.getImage();
         if (image != null) {
            int bitmapWidth = this._rotation != 0 && this._rotation != 180 ? image.getHeight() : image.getWidth();
            int bitmapHeight = this._rotation != 0 && this._rotation != 180 ? image.getWidth() : image.getHeight();
            int zoomToFactorHorz = Fixed32.div(Fixed32.toFP(mgrWidth), Fixed32.toFP(bitmapWidth));
            int zoomToFactorVert = Fixed32.div(Fixed32.toFP(mgrHeight), Fixed32.toFP(bitmapHeight));
            this._fillFactor = Math.max(zoomToFactorHorz, zoomToFactorVert);
            this._fitFactor = Math.min(zoomToFactorHorz, zoomToFactorVert);
            this._zoomBoundaries = new int[5];
            int index = -1;
            if (!this._zoomToFitMinScale) {
               this.MIN_ZOOM_FP = MIN_ZOOM_FP_STD;
               this._zoomBoundaries[++index] = this.MIN_ZOOM_FP;
            } else {
               this.MIN_ZOOM_FP = Math.min(65536, this._fitFactor);
               Arrays.removeAt(this._zoomBoundaries, 4);
            }

            this._zoomBoundaries[++index] = this._fitFactor;
            this._zoomBoundaries[++index] = this._fillFactor;
            this._zoomBoundaries[++index] = 65536;
            this._zoomBoundaries[++index] = MAX_ZOOM_FP;
            Arrays.sort(this._zoomBoundaries, 0, index + 1);

            for (int i = 1; i < this._zoomBoundaries.length; i++) {
               if (this._zoomBoundaries[i] == this._zoomBoundaries[i - 1]) {
                  Arrays.removeAt(this._zoomBoundaries, i);
                  i--;
               }
            }

            if (this._fitFactor < this.MIN_ZOOM_FP) {
               this._fitFactor = this.MIN_ZOOM_FP;
            } else if (this._fitFactor > MAX_ZOOM_FP) {
               this._fitFactor = MAX_ZOOM_FP;
            }

            if (this._fillFactor < this.MIN_ZOOM_FP) {
               this._fillFactor = this.MIN_ZOOM_FP;
            } else if (this._fillFactor > MAX_ZOOM_FP) {
               this._fillFactor = MAX_ZOOM_FP;
            }

            int minIndex = Arrays.getIndex(this._zoomBoundaries, this.MIN_ZOOM_FP);
            int maxIndex = Arrays.getIndex(this._zoomBoundaries, MAX_ZOOM_FP);
            if (minIndex != 0) {
               for (int i = 0; i < minIndex; i++) {
                  Arrays.removeAt(this._zoomBoundaries, i);
               }
            }

            if (maxIndex != this._zoomBoundaries.length - 1) {
               for (int i = maxIndex + 1; i < this._zoomBoundaries.length; i++) {
                  Arrays.removeAt(this._zoomBoundaries, i);
               }
            }
         }
      }
   }

   private void calculateScrollSteps() {
      this._scrollStepX = 4;
      this._scrollStepY = 4;
   }

   private void paintField(Graphics graphics, int x, int y, Bitmap bitmap, int topX, int topY) {
      if (bitmap != null) {
         int dvy = 0;
         int duy = 0;
         int dvx = 0;
         int dux = 0;
         int textureX = 0;
         int textureY = 0;
         int scaledFP = 65536;
         boolean flipX = (this._reflection & 2) != 0;
         boolean flipY = (this._reflection & 1) != 0;
         switch (this._rotation) {
            case 0:
               dux = flipX ? -scaledFP : scaledFP;
               dvy = flipY ? -scaledFP : scaledFP;
               textureX = 0;
               textureY = 0;
               break;
            case 90:
               dvx = flipY ? scaledFP : -scaledFP;
               duy = flipX ? -scaledFP : scaledFP;
               textureX = this._rectImage.height;
               textureY = 0;
               break;
            case 180:
               dux = flipX ? scaledFP : -scaledFP;
               dvy = flipY ? scaledFP : -scaledFP;
               textureX = this._rectImage.width;
               textureY = this._rectImage.height;
               break;
            case 270:
               dvx = flipY ? -scaledFP : scaledFP;
               duy = flipX ? scaledFP : -scaledFP;
               textureX = 0;
               textureY = this._rectImage.width;
         }

         int realX = this.getTransformedWidth() > this.getPreferredWidth() ? 0 : this.getPreferredWidth() - this.getTransformedWidth() >> 1;
         int realY = this.getTransformedHeight() > this.getPreferredHeight() ? 0 : this.getPreferredHeight() - this.getTransformedHeight() >> 1;
         int width = this._rotation != 0 && this._rotation != 180 ? this._rectImage.height : this._rectImage.width;
         int height = this._rotation != 0 && this._rotation != 180 ? this._rectImage.width : this._rectImage.height;
         this._tempx[0] = this._tempx[3] = realX - topX;
         this._tempx[1] = this._tempx[2] = realX - topX + width;
         this._tempy[0] = this._tempy[1] = realY - topY;
         this._tempy[2] = this._tempy[3] = realY - topY + height;
         graphics.drawTexturedPath(this._tempx, this._tempy, null, null, this._tempx[0] + textureX, this._tempy[0] + textureY, dux, dvx, duy, dvy, bitmap);
      }

      this.paintZoom(graphics);
   }

   private int getNextZoomFactor(boolean zoomIn) {
      if (zoomIn) {
         int nextZoomFactor = Fixed32.mul(this._scale, ZOOMSTEP_FP);
         if (this._scale < MAX_ZOOM_FP && nextZoomFactor > MAX_ZOOM_FP) {
            nextZoomFactor = MAX_ZOOM_FP;
         }

         return nextZoomFactor;
      } else {
         int nextZoomFactor = Fixed32.div(this._scale, ZOOMSTEP_FP);
         if (this._scale > this.MIN_ZOOM_FP && nextZoomFactor < this.MIN_ZOOM_FP) {
            nextZoomFactor = this.MIN_ZOOM_FP;
         }

         return nextZoomFactor;
      }
   }

   @Override
   protected void onDisplay() {
      super.onDisplay();
      if (this._zoomToFitMinScale) {
         this.calculateMinZoomValues(this.getPreferredWidth(), this.getPreferredHeight());
      }
   }

   @Override
   protected boolean keyChar(char character, int status, int time) {
      if (super.keyChar(character, status, time)) {
         return true;
      }

      if (character == 27 && this._isZooming) {
         if (this._zoomingStartTopX != -1 && this._zoomingStartTopY != -1 && this._zoomingStartScale != -1 && this._rotationStart != -1) {
            UiApplication app = UiApplication.getUiApplication();
            if (app != null) {
               app.suspendPainting(true);

               label44:
               try {
                  this.setScale(this._zoomingStartScale, this._zoomingStartTopX, this._zoomingStartTopY, this._rotationStart);
               } finally {
                  break label44;
               }

               app.suspendPainting(false);
            }
         }

         this.setZooming(false);
         return true;
      } else {
         return false;
      }
   }

   @Override
   protected boolean keyDown(int keycode, int time) {
      if (this._hotKeyEnabled && this.isEditable() && !this.isAnimated()) {
         boolean altStatus = (Keypad.status(keycode) & 1) != 0;
         char ch = UiInternal.map(Keypad.key(keycode), Keypad.status(1));
         switch (ch) {
            case '0':
               break;
            case '1':
               if (altStatus) {
                  while (this.canZoom(true)) {
                     this.zoom(true);
                  }
               } else {
                  this.zoomToFit();
               }

               return true;
            case '2':
            default:
               if (this._hotKeyEnabled) {
                  if (altStatus) {
                     this.scrollToTop();
                     return true;
                  }

                  this.moveFocus(-1, 0, time);
                  return true;
               }
               break;
            case '3':
               int i = altStatus ? 3 : 1;

               while (this.canZoom(true)) {
                  if (--i < 0) {
                     break;
                  }

                  this.zoom(true);
               }

               return true;
            case '4':
               if (this._hotKeyEnabled) {
                  if (altStatus) {
                     this.scrollToLeft();
                     return true;
                  }

                  this.moveFocus(-1, Trackball.isSupported() ? 65536 : 1, time);
                  return true;
               }
               break;
            case '5':
               if (!altStatus) {
                  this.checkTopPoints(true, true);
                  this.checkCropRectVsTopPoints();
                  this.invalidate();
                  return true;
               }
               break;
            case '6':
               if (this._hotKeyEnabled) {
                  if (altStatus) {
                     this.scrollToRight();
                     return true;
                  }

                  this.moveFocus(1, Trackball.isSupported() ? 65536 : 1, time);
                  return true;
               }
               break;
            case '7':
               if (altStatus) {
                  while (this.canZoom(false)) {
                     this.zoom(false);
                  }
               } else {
                  this.zoom1To1();
               }

               return true;
            case '8':
               if (this._hotKeyEnabled) {
                  if (altStatus) {
                     this.scrollToBottom();
                     return true;
                  }

                  this.moveFocus(1, 0, time);
                  return true;
               }
               break;
            case '9':
               int i = altStatus ? 3 : 1;

               while (this.canZoom(false)) {
                  if (--i < 0) {
                     break;
                  }

                  this.zoom(false);
               }

               return true;
         }

         ch = UiInternal.map(Keypad.key(keycode), 0);
         switch (ch) {
            case 'L':
            case 'l':
               this.rotate(altStatus ? 270 : 90);
               return true;
         }
      }

      return super.keyDown(keycode, time);
   }

   private void setTrackballSensitivity(boolean focus) {
      Screen screen = this.getScreen();
      if (Trackball.isSupported() && screen != null) {
         if (focus) {
            if (this._isZooming) {
               screen.setTrackballSensitivityXOffset(-100);
               screen.setTrackballSensitivityYOffset(0);
               screen.setTrackballFilter(-1);
            } else if (this.getTransformedHeight() <= this.getHeight() && this.getTransformedWidth() <= this.getWidth()) {
               screen.setTrackballSensitivityXOffset(Integer.MAX_VALUE);
               screen.setTrackballSensitivityYOffset(Integer.MAX_VALUE);
               screen.setTrackballFilter(-1);
            } else {
               screen.setTrackballSensitivityXOffset(100);
               screen.setTrackballSensitivityYOffset(100);
               screen.setTrackballFilter(4);
            }
         } else {
            screen.setTrackballSensitivityXOffset(Integer.MAX_VALUE);
            screen.setTrackballSensitivityYOffset(Integer.MAX_VALUE);
            screen.setTrackballFilter(-1);
         }

         this.trackballSensitivityChanged();
      }
   }

   @Override
   protected void layout(int width, int height) {
      if (!this.isAnimated() && this.getManager() != null) {
         int managerWidth = this.getPreferredWidth();
         int managerHeight = this.getPreferredHeight();
         if (managerWidth <= 0 || managerWidth > Display.getWidth()) {
            managerWidth = Display.getWidth();
         }

         if (managerHeight <= 0 || managerHeight > Display.getHeight()) {
            managerHeight = Display.getHeight();
         }

         super.layout(managerWidth, managerHeight);
         this.setZoomBoundaries(managerWidth, managerHeight);
         if (this._zoomTo == 2) {
            this.zoomToInternal(managerWidth, managerHeight, 2, true, true);
         } else if (this._zoomTo == 1) {
            this.zoomToInternal(managerWidth, managerHeight, 1, true, true);
         } else {
            this.checkTopPoints(false, false);
         }

         this.checkCropRectVsTopPoints();
         this.calculateMinZoomValues(managerWidth, managerHeight);
      } else {
         super.layout(width, height);
      }
   }

   private String toStringFixed32TenThou(int fp) {
      int tenThou = Fixed32.toIntTenThou(fp);
      String decimal = ((StringBuffer)(new Object("0000"))).append(Math.abs(tenThou % 10000)).toString();
      return ((StringBuffer)(new Object())).append(String.valueOf(tenThou / 10000)).append('.').append(decimal.substring(decimal.length() - 4)).toString();
   }

   @Override
   protected boolean trackwheelRoll(int amount, int status, int time) {
      if (!this.isAnimated() && amount != 0 && (status & 2) != 0) {
         int initialAmount = Math.abs(amount);

         for (boolean zoomIn = amount > 0; initialAmount > 0 && this.canZoom(zoomIn); initialAmount--) {
            this.zoom(zoomIn);
         }

         return true;
      } else {
         return super.trackwheelRoll(amount, status, time);
      }
   }

   @Override
   protected void makeContextMenu(ContextMenu contextMenu, int instance) {
      super.makeContextMenu(contextMenu, instance);
      if (this.isEditable() && !this.isAnimated()) {
         if (this._zItem == null) {
            this._zItem = new ZoomBitmapField$1(
               this, ResourceBundle.getBundle(2545338480386147321L, "net.rim.device.apps.internal.resource.Ui"), 110, 131072, 0
            );
         }

         contextMenu.addItem(this._zItem);
         contextMenu.setDefaultItem(this._zItem);
         if (this._rItem == null) {
            this._rItem = new ZoomBitmapField$2(this, ResourceBundle.getBundle(2545338480386147321L, "net.rim.device.apps.internal.resource.Ui"), 3, 131072, 0);
         }

         contextMenu.addItem(this._rItem);
         if (instance == 0) {
            if (this.canZoomTo(2)) {
               if (this._zoomToFitItem == null) {
                  this._zoomToFitItem = new ZoomBitmapField$3(
                     this, ResourceBundle.getBundle(2545338480386147321L, "net.rim.device.apps.internal.resource.Ui"), 113, 131072, 0
                  );
               }

               contextMenu.addItem(this._zoomToFitItem);
            }

            if (this.canZoomTo(1)) {
               if (this._zoomToFillItem == null) {
                  this._zoomToFillItem = new ZoomBitmapField$4(
                     this, ResourceBundle.getBundle(2545338480386147321L, "net.rim.device.apps.internal.resource.Ui"), 114, 131072, 0
                  );
               }

               contextMenu.addItem(this._zoomToFillItem);
            }

            if (this._z1to1Item == null) {
               this._z1to1Item = new ZoomBitmapField$5(
                  this, ResourceBundle.getBundle(2545338480386147321L, "net.rim.device.apps.internal.resource.Ui"), 1, 131072, 0
               );
            }

            contextMenu.addItem(this._z1to1Item);
         }
      }
   }

   @Override
   protected boolean navigationClick(int status, int time) {
      if (this.isZooming()) {
         this.setZooming(false);
         return true;
      } else {
         return super.navigationClick(status, time);
      }
   }

   @Override
   protected int moveFocus(int amount, int status, int time) {
      int result = super.moveFocus(amount, status, time);
      if (this.isZooming()) {
         if (!Trackball.isSupported() || (status & 65536) == 0) {
            this.zoom(amount < 0);
            result = 0;
         }
      } else if (!this.isAnimated() && amount != 0) {
         if (Trackball.isSupported() && (status & 2) != 0) {
            return amount;
         }

         if (!this.scroll(amount, Trackball.isSupported() ? (status & 65536) != 0 : (status & 1) != 0)) {
            return amount;
         }

         return 0;
      }

      return result;
   }

   private boolean canZoomTo(int zoomTo) {
      if (!this.isAnimated()) {
         int mgrWidth = this.getPreferredWidth();
         int mgrHeight = this.getPreferredHeight();
         if (mgrWidth <= 0 || mgrHeight <= 0) {
            return true;
         }

         EncodedImage image = this.getImage();
         if (image != null) {
            int bitmapWidth = this._rotation != 0 && this._rotation != 180 ? image.getHeight() : image.getWidth();
            int bitmapHeight = this._rotation != 0 && this._rotation != 180 ? image.getWidth() : image.getHeight();
            int zoomToFactorHorz = Fixed32.div(Fixed32.toFP(mgrWidth), Fixed32.toFP(bitmapWidth));
            int zoomToFactorVert = Fixed32.div(Fixed32.toFP(mgrHeight), Fixed32.toFP(bitmapHeight));
            int zoomToFactor;
            if (2 == zoomTo) {
               zoomToFactor = Math.min(zoomToFactorHorz, zoomToFactorVert);
            } else {
               if (1 != zoomTo) {
                  return false;
               }

               zoomToFactor = Math.max(zoomToFactorHorz, zoomToFactorVert);
            }

            int newImageWidth = Fixed32.toRoundedInt(Fixed32.mul(Fixed32.toFP(bitmapWidth), zoomToFactor));
            int newImageHeight = Fixed32.toRoundedInt(Fixed32.mul(Fixed32.toFP(bitmapHeight), zoomToFactor));
            if (newImageWidth > 0 && newImageHeight > 0) {
               return true;
            }
         }
      }

      return false;
   }

   @Override
   protected void onFocus(int direction) {
      super.onFocus(direction);
      this.setTrackballSensitivity(true);
   }

   @Override
   protected void onUnfocus() {
      super.onUnfocus();
      this.setTrackballSensitivity(false);
   }

   private int getRealTopX() {
      int xTop = this._topX;
      switch (this._rotation) {
         case 90:
            return this._topY;
         case 180:
            return this.getTransformedWidth() - this.getPreferredWidth() - this._topX;
         case 270:
            xTop = this.getTransformedHeight() - this.getPreferredHeight() - this._topY;
         default:
            return xTop;
      }
   }

   private void zoomToInternal(int mgrWidth, int mgrHeight, int zoomTo, boolean doDefaultXCenter, boolean doDefaultYCenter) {
      if (this.canZoomTo(zoomTo)) {
         if (mgrWidth > 0 && mgrHeight > 0) {
            EncodedImage image = this.getImage();
            int bitmapWidth = this._rotation != 0 && this._rotation != 180 ? image.getHeight() : image.getWidth();
            int bitmapHeight = this._rotation != 0 && this._rotation != 180 ? image.getWidth() : image.getHeight();
            int zoomToFactorHorz = Fixed32.div(Fixed32.toFP(mgrWidth), Fixed32.toFP(bitmapWidth));
            int zoomToFactorVert = Fixed32.div(Fixed32.toFP(mgrHeight), Fixed32.toFP(bitmapHeight));
            int zoomToFactorInverseHorz = Fixed32.div(Fixed32.toFP(bitmapWidth), Fixed32.toFP(mgrWidth));
            int zoomToFactorInverseVert = Fixed32.div(Fixed32.toFP(bitmapHeight), Fixed32.toFP(mgrHeight));
            int fillFactor = Math.max(zoomToFactorHorz, zoomToFactorVert);
            int fitFactor = Math.min(zoomToFactorHorz, zoomToFactorVert);
            int inverseFill;
            if (fillFactor != this._fillFactor && this._fillFactor != -1) {
               fillFactor = this._fillFactor;
               inverseFill = Fixed32.div(65536, fillFactor);
            } else {
               inverseFill = Math.min(zoomToFactorInverseHorz, zoomToFactorInverseVert);
            }

            int inverseFit;
            if (fitFactor != this._fitFactor && this._fitFactor != -1) {
               fitFactor = this._fitFactor;
               inverseFit = Fixed32.div(65536, fitFactor);
            } else {
               inverseFit = Math.max(zoomToFactorInverseHorz, zoomToFactorInverseVert);
            }

            if (zoomTo == 1) {
               this.zoom(fillFactor, inverseFill, doDefaultXCenter, doDefaultYCenter);
            } else if (zoomTo == 2) {
               this.zoom(fitFactor, inverseFit, doDefaultXCenter, doDefaultYCenter);
            }
         }

         if (zoomTo == 1) {
            this._zoomTo = 1;
            return;
         }

         if (zoomTo == 2) {
            this._zoomTo = 2;
         }
      }
   }

   private void zoomImpl(int crtZoomValue, int crtZoomValueInverse, boolean doDefaultXCenter, boolean doDefaultYCenter, int rotationValue) {
      this._zoomTo = 0;
      if (crtZoomValue > 0 && (crtZoomValue != this._scale || rotationValue != this._rotation)) {
         EncodedImage image = this.getImage();
         if (image != null) {
            this._rotation = rotationValue;
            int fieldAvailableWidth = this.getPreferredWidth();
            int fieldAvailableHeight = this.getPreferredHeight();
            if (!doDefaultXCenter && this._topX >= 0) {
               this._topX = Fixed32.toRoundedInt(Fixed32.mul(Fixed32.toFP(this.scaleToSource(this._topX + (fieldAvailableWidth >> 1))), crtZoomValue))
                  - (fieldAvailableWidth >> 1);
            }

            if (!doDefaultYCenter && this._topY >= 0) {
               this._topY = Fixed32.toRoundedInt(Fixed32.mul(Fixed32.toFP(this.scaleToSource(this._topY + (fieldAvailableHeight >> 1))), crtZoomValue))
                  - (fieldAvailableHeight >> 1);
            }

            this._rectImage.x = Fixed32.toRoundedInt(Fixed32.mul(Fixed32.toFP(this.scaleToSource(this._rectImage.x)), crtZoomValue));
            this._rectImage.y = Fixed32.toRoundedInt(Fixed32.mul(Fixed32.toFP(this.scaleToSource(this._rectImage.y)), crtZoomValue));
            System.out.println(((StringBuffer)(new Object("zoom "))).append(this.toStringFixed32TenThou(crtZoomValue)).toString());
            this._scale = crtZoomValue;
            image.setScaleX32(crtZoomValueInverse);
            image.setScaleY32(crtZoomValueInverse);
            this.checkTopPoints(doDefaultXCenter, doDefaultYCenter);
            this.checkCropRectVsTopPoints();
            this.invalidate();
         }
      }

      this.setTrackballSensitivity(this.isFocus());
   }

   public ZoomBitmapField() {
      this(0);
   }
}
