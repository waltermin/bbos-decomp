package net.rim.device.apps.internal.ribbon.launcher;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.XYRect;
import net.rim.device.internal.ui.Image;
import net.rim.device.internal.ui.ScaleBitmap;
import net.rim.device.internal.ui.component.ApplicationIconField;

public final class RibbonIconField extends ApplicationIconField {
   private ApplicationEntry _applicationEntry;
   private int _bitmapHeight;
   private Object _bitmapCustom;
   private Object _bitmapCustomFocus;
   private Object _bitmapDefault;
   private boolean _forceVisible;
   private static Bitmap _movingFocusBitmap;
   private static boolean _useMovingFocus;
   private static boolean _isMovingFocusAFolder;
   private static XYRect _rect = (XYRect)(new Object());

   public RibbonIconField(ApplicationEntry applicationEntry) {
      super(applicationEntry.getUniqueName(), applicationEntry.getState());
      this._applicationEntry = applicationEntry;
   }

   @Override
   protected final void applyTheme() {
      this.setBitmapInternal();
      super.applyTheme();
   }

   private final void setBitmapInternal() {
      Object bitmapApp = this._applicationEntry.getBitmap();
      Object bitmapFocus = this._applicationEntry.getBitmapFocus();
      Object bitmapDefault = this._applicationEntry.getBitmapDefault();
      int bitmapHeight = this.getPreferredHeight();
      if (this._bitmapCustom != bitmapApp || this._bitmapCustomFocus != bitmapFocus || this._bitmapHeight != bitmapHeight) {
         this.setIconCustom(bitmapApp, bitmapFocus);
      }

      if (this._bitmapDefault != bitmapDefault || this._bitmapHeight != bitmapHeight) {
         this.setIconDefault(bitmapDefault);
      }

      this._bitmapCustom = bitmapApp;
      this._bitmapCustomFocus = bitmapFocus;
      this._bitmapDefault = bitmapDefault;
      this._bitmapHeight = bitmapHeight;
   }

   @Override
   protected final void drawFocus(Graphics graphics, boolean on) {
      if (!_useMovingFocus) {
         super.drawFocus(graphics, on);
      } else {
         if (!on) {
            int color = graphics.getColor();
            graphics.setColor(16777215);
            graphics.drawRoundRect(1, 1, this.getWidth() - 2, this.getHeight() - 2, 5, 5);
            graphics.setColor(color);
         } else {
            graphics.drawRoundRect(1, 1, this.getWidth() - 2, this.getHeight() - 2, 5, 5);
            if (!_isMovingFocusAFolder && this._applicationEntry.getDescriptor() instanceof FolderEntryPointDescriptor
               || _isMovingFocusAFolder && FolderEntryPointDescriptor.isUpFolder(this._applicationEntry)) {
               int width = this.getWidth();
               int height = this.getHeight();
               if (width >= 0 && height >= 0 && _movingFocusBitmap != null) {
                  boolean focus = graphics.isDrawingStyleSet(8);
                  graphics.setDrawingStyle(8, false);
                  graphics.drawBitmap(0, 0, this.getWidth(), this.getHeight(), _movingFocusBitmap, 0, 0);
                  graphics.setDrawingStyle(8, focus);
                  return;
               }
            }
         }
      }
   }

   @Override
   public final void setBitmap() {
      this.setAppState(this._applicationEntry.getState());
      this.setBitmapInternal();
      super.setBitmap();
   }

   public static final void setFocusIconField(RibbonIconField field) {
      if (field != null) {
         int w = field.getPreferredWidth();
         int h = field.getPreferredHeight();
         Bitmap topBitmap = (Bitmap)(new Object(w, h));
         topBitmap.createAlpha(2);
         Graphics g = (Graphics)(new Object(topBitmap));
         g.setGlobalAlpha(0);
         g.clear();
         g.setGlobalAlpha(255);
         field.paint(g, true);
         _movingFocusBitmap = ScaleBitmap.scaleBitmap(1, topBitmap, 3 * w >> 2, 3 * h >> 2);
         _isMovingFocusAFolder = ((ApplicationEntry)field.getCookie()).getDescriptor() instanceof FolderEntryPointDescriptor;
      } else {
         _movingFocusBitmap = null;
      }
   }

   public final void setUseMovingFocus(boolean useMovingFocus) {
      this.focusRemove();
      _useMovingFocus = useMovingFocus;
      this.focusAdd(true);
   }

   public final void setForcePaintVisible(boolean forced) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   @Override
   public final void invalidate() {
      throw new RuntimeException("cod2jar: tail call (jumpspecial)");
   }

   @Override
   public final void paint(Graphics graphics) {
      this.paint(graphics, false);
   }

   public final void paintIconOnly(Graphics graphics) {
      this.paint(graphics, true);
   }

   private final void paint(Graphics graphics, boolean bIconOnly) {
      Manager manager = this.getManager();
      Bitmap bitmap = null;
      int currentAlpha = graphics.getGlobalAlpha();
      if (!bIconOnly && manager instanceof GridApplicationLauncherField) {
         bitmap = ((GridApplicationLauncherField)manager).getBackgroundBitmap();
      }

      if (bitmap != null) {
         graphics.getAbsoluteClippingRect(_rect);
         int x = _rect.x;
         int offset = Display.getHeight()
            - manager.getScreen().getHeight()
            + manager.getExtent().y
            - (graphics.getBackgroundOffsetY() + manager.getVerticalScroll());
         int y = _rect.y + offset;
         _rect.set(graphics.getClippingRect());
         int width = this.getWidth();
         int height = this.getHeight();
         if (width >= 0 && height >= 0 && x >= 0 && x < bitmap.getWidth() && y >= 0 && y < bitmap.getHeight()) {
            graphics.drawBitmap(_rect.x, _rect.y, this.getWidth(), this.getHeight(), bitmap, x, y);
         }
      }

      if (!this._applicationEntry.isVisible() && !this._forceVisible) {
         Image bitmapNormal = super._bitmap;
         Image bitmapFocus = super._bitmapFocus;
         int opacity = graphics.getGlobalAlpha();
         super._bitmap = super._bitmapHidden;
         super._bitmapFocus = super._bitmapFocusHidden;
         graphics.setGlobalAlpha(opacity >> 1);
         if (bIconOnly) {
            this.paintBitmap(graphics);
         } else {
            super.paint(graphics);
         }

         graphics.setGlobalAlpha(opacity);
         super._bitmap = bitmapNormal;
         super._bitmapFocus = bitmapFocus;
      } else if (bIconOnly) {
         this.paintBitmap(graphics);
      } else {
         super.paint(graphics);
      }

      graphics.setGlobalAlpha(currentAlpha);
   }

   private final void paintBitmap(Graphics graphics) {
      boolean isDrawingFocus = graphics.isDrawingStyleSet(8);
      this.fillPaintExtent(_rect);
      if (super._bitmap != null && _rect.width > 0 && _rect.height > 0) {
         Image bitmap = super._bitmapFocus != null && isDrawingFocus ? super._bitmapFocus : super._bitmap;
         bitmap.paint(graphics, _rect.x, _rect.y, _rect.width, _rect.height);
      }
   }

   @Override
   public final String getAccessibleName() {
      return this._applicationEntry.getDescription(true);
   }
}
