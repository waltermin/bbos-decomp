package net.rim.device.internal.ui.component;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.XYDimension;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.theme.Tag;
import net.rim.device.api.ui.theme.Theme;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.internal.ui.Image;
import net.rim.device.internal.ui.ImageBitmap;
import net.rim.device.internal.ui.ImageOverlay;
import net.rim.device.internal.ui.ScaleBitmap;

public class ApplicationIconField extends Field {
   private String _appName;
   private String _appState;
   private Image _iconCustom;
   private Image _iconCustomFocus;
   private Image _iconDefault;
   protected Image _bitmap;
   protected Image _bitmapFocus;
   protected Image _bitmapHidden;
   protected Image _bitmapFocusHidden;
   private int _x;
   private int _y;
   private int _width;
   private int _height;
   private static Tag TAG = Tag.create("application-icon");
   private static String THEME_ICON_UNDERLAY = "icon_underlay";
   private static String THEME_ICON_OVERLAY = "icon_overlay";
   private static int _themeGeneration;
   private static Image _underlay;
   private static Image _underlayFocus;
   private static Image _overlay;
   private static Image _overlayFocus;

   public ApplicationIconField(String appName, String appState) {
      super(18014398509481984L);
      this.setTag(TAG);
      this._appName = appName;
      this._appState = appState;
      Theme theme = ThemeManager.getActiveTheme();
      this._width = theme.getRibbonIconWidth();
      this._height = theme.getRibbonIconHeight();
   }

   @Override
   protected void applyTheme() {
      super.applyTheme();
      int themeGeneration = ThemeManager.getGeneration();
      if (_themeGeneration != themeGeneration) {
         _themeGeneration = themeGeneration;
         Theme theme = ThemeManager.getActiveTheme();
         _underlay = theme.getApplicationIcon(THEME_ICON_UNDERLAY, 0, Integer.MAX_VALUE, null, 2);
         _overlay = theme.getApplicationIcon(THEME_ICON_OVERLAY, 0, Integer.MAX_VALUE, null, 2);
         _underlayFocus = theme.getApplicationIcon(THEME_ICON_UNDERLAY, 6, Integer.MAX_VALUE, _underlay, 2);
         _overlayFocus = theme.getApplicationIcon(THEME_ICON_OVERLAY, 6, Integer.MAX_VALUE, _overlay, 2);
      }

      this.setBitmapInternal();
   }

   @Override
   protected void drawFocus(Graphics graphics, boolean on) {
      if (this._bitmapFocus == null && _underlayFocus == _underlay && _overlayFocus == _overlay) {
         super.drawFocus(graphics, on);
      } else {
         this.paint(graphics);
      }
   }

   protected Image convertToImage(Object icon) {
      if (!(icon instanceof Bitmap)) {
         return null;
      }

      Bitmap bmp = (Bitmap)icon;
      int bmpWidth = bmp.getWidth();
      int bmpHeight = bmp.getHeight();
      XYDimension scaledSize = this.getScaledDimensions(bmpWidth, bmpHeight);
      if (scaledSize.width != bmpWidth || scaledSize.height != bmpHeight) {
         bmp = ScaleBitmap.scaleBitmap(0, bmp, scaledSize.width, scaledSize.height);
      }

      return ImageBitmap.create(bmp);
   }

   private XYDimension getScaledDimensions(int width, int height) {
      int desiredWidth = this.getPreferredWidth();
      int desiredHeight = this.getPreferredHeight();
      int dW = desiredWidth - width;
      int dH = desiredHeight - height;
      int max_dW = desiredWidth >> 3;
      int max_dH = desiredHeight >> 3;
      if (dW < 0 || dH < 0 || dW > max_dW && dH > max_dH) {
         if (dW > 0 && dH > 0) {
            int inc_dW = Math.min(dW / max_dW, 3);
            int inc_dH = Math.min(dH / max_dH, 3);
            desiredWidth -= inc_dW * max_dW;
            desiredHeight -= inc_dH * max_dH;
         } else {
            desiredWidth -= max_dW;
            desiredHeight -= max_dH;
         }

         return desiredWidth * height / width > desiredHeight
            ? new XYDimension(desiredHeight * width / height, desiredHeight)
            : new XYDimension(desiredWidth, desiredWidth * height / width);
      } else {
         return new XYDimension(width, height);
      }
   }

   @Override
   public int getPreferredHeight() {
      return this._height + this.getPaddingTop() + this.getPaddingBottom();
   }

   @Override
   public int getPreferredWidth() {
      return this._width + this.getPaddingLeft() + this.getPaddingRight();
   }

   @Override
   protected void layout(int width, int height) {
      this._x = this.getPaddingLeft();
      width = this._width + this.getPaddingLeft() + this.getPaddingRight();
      this._y = this.getPaddingTop();
      height = this._height + this.getPaddingTop() + this.getPaddingBottom();
      this.setExtent(width, height);
   }

   protected void fillPaintExtent(XYRect paintExtent) {
      if (paintExtent != null) {
         paintExtent.set(this._x, this._y, this._width, this._height);
      }
   }

   @Override
   protected void paint(Graphics graphics) {
      boolean isDrawingFocus = graphics.isDrawingStyleSet(8);
      Image underlay = isDrawingFocus ? _underlayFocus : _underlay;
      if (underlay != null) {
         underlay.paint(graphics, this._x, this._y, this._width, this._height);
      }

      if (this._bitmap != null) {
         Image bitmap = this._bitmapFocus != null && isDrawingFocus ? this._bitmapFocus : this._bitmap;
         bitmap.paint(graphics, this._x, this._y, this._width, this._height);
      }

      Image overlay = isDrawingFocus ? _overlayFocus : _overlay;
      if (overlay != null) {
         overlay.paint(graphics, this._x, this._y, this._width, this._height);
      }
   }

   public void setAppState(String appState) {
      this._appState = appState;
   }

   public void setBitmap() {
      this.setBitmapInternal();
   }

   private void setBitmapInternal() {
      Image bitmap = this._iconCustom;
      Image bitmapFocus = this._iconCustomFocus;
      Image bitmapHidden = null;
      Image bitmapFocusHidden = null;
      String appstate = this._appState;
      Theme theme = ThemeManager.getActiveTheme();
      if (bitmap != null) {
         if (appstate != null && appstate.equals("new")) {
            Image overlay = theme.getApplicationIcon("new_overlay", 0, this._width, null, 2);
            if (overlay != null) {
               if (bitmap != null) {
                  bitmap = ImageOverlay.create(bitmap, overlay);
               }

               if (bitmapFocus != null) {
                  bitmapFocus = ImageOverlay.create(bitmapFocus, overlay);
               }
            }
         }
      } else {
         String name = StringUtilities.removeChars(this._appName, " ̲");
         Image rawIcon = theme.getApplicationIcon(name, appstate, 0, this._width, this._iconDefault, 0);
         if (rawIcon != null) {
            int iconWidth = rawIcon.getWidth(Graphics.getScreenWidth(), Graphics.getScreenHeight());
            int iconHeight = rawIcon.getHeight(Graphics.getScreenWidth(), Graphics.getScreenHeight());
            int methodScaleToFit = 0;
            XYDimension scaledSize = this.getScaledDimensions(iconWidth, iconHeight);
            int scaledWidth = scaledSize.width;
            int scaledHeight = scaledSize.height;
            if (scaledWidth != iconWidth || scaledHeight != iconHeight) {
               methodScaleToFit = 8;
            }

            bitmap = theme.getApplicationIcon(name, appstate, 0, scaledWidth, this._iconDefault, methodScaleToFit);
            bitmapHidden = theme.getApplicationIcon(name, appstate, 0, scaledWidth, this._iconDefault, 16 | methodScaleToFit);
         }

         if (bitmap != this._iconDefault) {
            int method = 1;
            rawIcon = theme.getApplicationIcon(name, appstate, 6, this._width, this._iconDefault, 0);
            if (rawIcon != null) {
               int iconWidth = rawIcon.getWidth(Graphics.getScreenWidth(), Graphics.getScreenHeight());
               int iconHeight = rawIcon.getHeight(Graphics.getScreenWidth(), Graphics.getScreenHeight());
               int methodScaleToFit = 0;
               XYDimension scaledSize = this.getScaledDimensions(iconWidth, iconHeight);
               int scaledWidth = scaledSize.width;
               int scaledHeight = scaledSize.height;
               if (scaledWidth != iconWidth || scaledHeight != iconHeight) {
                  methodScaleToFit = 8;
               }

               bitmapFocus = theme.getApplicationIcon(name, appstate, 6, scaledWidth, null, method | methodScaleToFit);
               bitmapFocusHidden = theme.getApplicationIcon(name, appstate, 6, scaledWidth, null, method | 16 | methodScaleToFit);
            }
         }
      }

      if (this._bitmap != bitmap) {
         this._bitmap = bitmap;
         this._bitmapFocus = bitmapFocus;
         if (bitmapHidden == null && this._iconCustom != null) {
            Bitmap bmpHidden = Bitmap.createGreyscaleBitmap(this._iconCustom, this._width, this._height);
            if (bmpHidden != null) {
               bitmapHidden = ImageBitmap.create(bmpHidden);
            }
         }

         if (bitmapFocusHidden == null && this._iconCustomFocus != null) {
            Bitmap bmpFocusHidden = Bitmap.createGreyscaleBitmap(this._iconCustomFocus, this._width, this._height);
            if (bmpFocusHidden != null) {
               bitmapFocusHidden = ImageBitmap.create(bmpFocusHidden);
            }
         }

         this._bitmapHidden = bitmapHidden;
         this._bitmapFocusHidden = bitmapFocusHidden;
         this.layout(this.getWidth(), this.getHeight());
         this.invalidate();
      }
   }

   public void setIconCustom(Object iconCustom, Object iconCustomFocus) {
      if (this._iconCustom != iconCustom) {
         this._iconCustom = this.convertToImage(iconCustom);
      }

      if (this._iconCustomFocus != iconCustomFocus) {
         this._iconCustomFocus = this.convertToImage(iconCustomFocus);
      }
   }

   public void setIconCustom(Image iconCustom, Image iconCustomFocus) {
      this._iconCustom = iconCustom;
      this._iconCustomFocus = iconCustomFocus;
   }

   public void setIconDefault(Object iconDefault) {
      if (this._iconDefault != iconDefault) {
         this._iconDefault = this.convertToImage(iconDefault);
      }
   }

   public void setIconDefault(Image iconDefault) {
      this._iconDefault = iconDefault;
   }

   public void setSize(int width, int height) {
      this._width = width;
      this._height = height;
   }

   public String getAppName() {
      return this._appName;
   }

   @Override
   public int getAccessibleRole() {
      return 5;
   }

   @Override
   public String getAccessibleName() {
      return this.getAppName();
   }
}
