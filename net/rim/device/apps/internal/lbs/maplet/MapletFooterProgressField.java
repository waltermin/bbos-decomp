package net.rim.device.apps.internal.lbs.maplet;

import net.rim.device.api.system.Application;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Display;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.FontFamily;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.theme.Tag;
import net.rim.device.api.ui.theme.Theme;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.apps.internal.lbs.LBSApplication;
import net.rim.device.apps.internal.lbs.MapField;

public final class MapletFooterProgressField {
   private int _themeGeneration;
   private Bitmap _footerBitmap;
   private Bitmap _progressBitmap;
   private int _min;
   private int _max;
   private int _current;
   private boolean _showingFooter;
   private String _label;
   private int _barStart;
   private int _barEnd;
   private int _barCurrent;
   private Font _font;
   private long _lastPaintTime;
   private Runnable _repaintRunnable;
   private int _repaintRunnableId = -1;
   private Application _app;
   private MapField _parent;
   private int _width = 0;
   private int _height = 0;
   private MapletFooterProgressField$EmptyField _emptyField;
   private static final Tag TAG = Tag.create("browser-footer");
   private static final String BROWSER_FOOTER_PROGRESS_BITMAP;
   private static final String BROWSER_FOOTER_BITMAP;
   private static final int SEPARATOR_HEIGHT;
   private static final int MIN_PAINT_DELAY;

   public MapletFooterProgressField(MapField parent) {
      this.setupBitmaps();
      this._min = 0;
      this._app = Application.getApplication();
      this._repaintRunnable = new MapletFooterProgressField$RepaintMe(this);
      this._parent = parent;
      this._emptyField = new MapletFooterProgressField$EmptyField(this);
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public final void reset(String label, int min, int value, int max, boolean immediate) {
      boolean changed = false;
      if (this._label != label || this._min != min || this._max != max) {
         this._label = label;
         this._min = min;
         this._max = max;
         changed = true;
      }

      int currentValue = this._barCurrent;
      this._current = value;
      this.progressLayout();
      if (changed || currentValue != this._barCurrent) {
         label54:
         try {
            if (!immediate && this._lastPaintTime + 100 > System.currentTimeMillis()) {
               if (this._repaintRunnableId == -1) {
                  this._repaintRunnableId = this._app.invokeLater(this._repaintRunnable, 100, false);
               }
            } else if (this._repaintRunnableId != -1) {
               this._app.cancelInvokeLater(this._repaintRunnableId);
               this._repaintRunnableId = -1;
            }
         } catch (Throwable var10) {
            EventLogger.logEvent(
               LBSApplication.UID, ((StringBuffer)(new Object("MapletFooterProgressField.reset"))).append(e.getMessage()).toString().getBytes(), 2
            );
            break label54;
         }

         this._parent.invalidateProgressField();
      }
   }

   private final void setupBitmaps() {
      int currentGeneration = ThemeManager.getGeneration();
      if (this._themeGeneration != currentGeneration) {
         this._themeGeneration = currentGeneration;
         Theme theme = ThemeManager.getActiveTheme();
         this._footerBitmap = getBitmap(theme, "browser-footer");
         this._progressBitmap = getBitmap(theme, "browser-footer~progress");
      }
   }

   private static final Bitmap getBitmap(Theme theme, String imgStr) {
      EncodedImage img = theme.getImage(imgStr, true);
      return img != null ? img.getBitmap() : null;
   }

   protected final void setFooterVisible(boolean visible) {
      if (visible != this._showingFooter) {
         this._showingFooter = visible;
      }
   }

   public final boolean isFooterVisible() {
      return this._showingFooter;
   }

   private final void drawLabel(Graphics graphics, int color) {
      graphics.setColor(color);
      if (this._label != null) {
         int length = this._label.length();
         graphics.drawText(this._label, 0, length, this._barStart, 1, 116, this._barEnd - this._barStart);
      }
   }

   private final void progressLayout() {
      this._barStart = 0;
      this._barEnd = this.getWidth();
      if (this._current == this._max && this._max > 0) {
         this._barCurrent = this._barEnd;
      } else if (this._current == this._min) {
         this._barCurrent = this._barStart;
      } else {
         long barWidth = this._barEnd - this._barStart;
         long rangeWidth = this._max - this._min;
         this._barCurrent = (int)((barWidth << 32) / rangeWidth * (this._current - this._min) >> 32) + this._barStart;
      }
   }

   public final int getWidth() {
      return this._width;
   }

   public final int getHeight() {
      return this._height;
   }

   protected final void applyTheme() {
      int size = Ui.convertSize(6, 3, 0);

      label20:
      try {
         this._font = FontFamily.forName("BBMillbank").getFont(0, size);
      } finally {
         break label20;
      }

      this.setupBitmaps();
   }

   public final int getPreferredWidth() {
      return Display.getWidth();
   }

   public final int getPreferredHeight() {
      if (this._font == null) {
         this.applyTheme();
      }

      return this._font.getHeight() + 1 + 1;
   }

   public final void layout(int width, int height) {
      this.applyTheme();
      this._width = width;
      this._height = height;
      this.progressLayout();
   }

   public final void paint(Graphics graphics) {
      this._lastPaintTime = System.currentTimeMillis();
      if (this._showingFooter) {
         int fieldWidth = this.getWidth();
         int fieldHeight = this.getHeight();
         if (this._footerBitmap != null) {
            graphics.drawBitmap(0, 0, fieldWidth, fieldHeight, this._footerBitmap, 0, 0);
         }

         if (this._font != null) {
            graphics.setFont(this._font);
         }

         if (this._label != null) {
            int foregroundColor1 = this._emptyField.getColor(1);
            int foregroundColor2 = this._emptyField.getColor(5);
            if (this._progressBitmap != null) {
               graphics.drawBitmap(this._barStart, 0, this._barCurrent, fieldHeight, this._progressBitmap, 0, 0);
               if (foregroundColor2 == foregroundColor1) {
                  this.drawLabel(graphics, foregroundColor1);
               } else {
                  this.drawLabel(graphics, foregroundColor1);
                  graphics.pushContext(this._barStart, 0, this._barCurrent, fieldHeight, 0, 0);
                  this.drawLabel(graphics, foregroundColor2);
                  graphics.popContext();
               }
            } else {
               this.drawLabel(graphics, foregroundColor1);
               graphics.invert(this._barStart, 1, this._barCurrent - this._barStart, fieldHeight - 1 - 1);
            }
         }

         graphics.setColor(this._emptyField.getColor(1));
         graphics.drawLine(0, 0, fieldWidth, 0);
      }
   }
}
