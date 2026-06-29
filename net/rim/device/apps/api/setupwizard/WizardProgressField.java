package net.rim.device.apps.api.setupwizard;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Display;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.theme.Tag;
import net.rim.device.api.ui.theme.Theme;
import net.rim.device.api.ui.theme.ThemeAttributeSet;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.api.util.StringUtilities;

class WizardProgressField extends Field {
   private int _min;
   private int _max;
   private int _current;
   private String _label;
   private int _barStart;
   private int _barEnd;
   private int _barCurrent;
   private Bitmap _footerBitmap;
   private Bitmap _progressBitmap;
   private boolean _isHidden;
   private static final Tag TAG = Tag.create("browser-footer");
   private static final String BROWSER_FOOTER_PROGRESS_BITMAP;
   private static final String BROWSER_FOOTER_BITMAP;
   private static final int SEPARATOR_HEIGHT;

   public WizardProgressField() {
      this.setTag(TAG);
   }

   @Override
   protected void applyTheme() {
      Theme newTheme = ThemeManager.getActiveTheme();
      this._footerBitmap = getBitmap(newTheme, "browser-footer");
      this._progressBitmap = getBitmap(newTheme, "browser-footer~progress");
      super.applyTheme();
      this.setFont(Font.getDefault().derive(0, 5, 3));
   }

   @Override
   public int getPreferredWidth() {
      return Display.getWidth();
   }

   @Override
   public int getPreferredHeight() {
      return this._isHidden ? 0 : this.getFont().getHeight() + 0;
   }

   public void setHidden(boolean val) {
      this._isHidden = val;
      this.updateLayout();
   }

   private static Bitmap getBitmap(Theme theme, String imgStr) {
      EncodedImage img = theme.getImage(imgStr, true);
      return img != null ? img.getBitmap() : null;
   }

   @Override
   protected void paint(Graphics graphics) {
      int fieldWidth = this.getWidth();
      int fieldHeight = this.getHeight();
      if (this._footerBitmap != null) {
         graphics.drawBitmap(0, 0, fieldWidth, fieldHeight, this._footerBitmap, 0, 0);
      }

      if (this._label != null) {
         graphics.setColor(ThemeAttributeSet.getColor(this, 1));
         graphics.drawText(this._label, 0, this._label.length(), this._barStart, 0, 116, this._barEnd - this._barStart);
      }

      graphics.pushContext(this._barStart, 0, this._barCurrent, fieldHeight, 0, 0);
      if (this._progressBitmap != null) {
         graphics.drawBitmap(this._barStart, 0, this._barCurrent, fieldHeight, this._progressBitmap, 0, 0);
         graphics.setColor(ThemeAttributeSet.getColor(this, 5));
         graphics.drawText(this._label, 0, this._label.length(), this._barStart, 0, 116, this._barEnd - this._barStart);
      } else {
         graphics.invert(this._barStart, 0, this._barCurrent - this._barStart, fieldHeight - 0 - 0);
      }

      graphics.popContext();
      graphics.setColor(ThemeAttributeSet.getColor(this, 1));
   }

   @Override
   protected void layout(int width, int height) {
      int widthToUse = Math.min(width, this.getPreferredWidth());
      int heightToUse = Math.min(height, this.getPreferredHeight());
      this.setExtent(widthToUse, heightToUse);
      this.progressLayout();
   }

   @Override
   public boolean isFocusable() {
      return false;
   }

   public void reset(String label, int min, int max, int start) {
      boolean changed = false;
      if (!StringUtilities.strEqual(this._label, label) || this._min != min || this._max != max) {
         this._label = label;
         this._min = min;
         this._max = max;
         changed = true;
      }

      this._current = start;
      Manager manager = this.getManager();
      if (manager != null && manager.isValidLayout()) {
         int currentValue = this._barCurrent;
         this.progressLayout();
         if (changed || currentValue != this._barCurrent) {
            this.invalidate();
         }
      }
   }

   private void progressLayout() {
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
}
