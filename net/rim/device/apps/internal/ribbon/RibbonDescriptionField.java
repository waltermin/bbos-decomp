package net.rim.device.apps.internal.ribbon;

import net.rim.device.api.memorycleaner.MemoryCleanerDaemon;
import net.rim.device.api.memorycleaner.MemoryCleanerListener;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.FontFamily;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.TextMetrics;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.theme.Tag;
import net.rim.device.api.ui.theme.Theme;
import net.rim.device.api.ui.theme.ThemeAttributeSet;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.internal.ui.Background;
import net.rim.vm.Memory;

final class RibbonDescriptionField extends Field implements MemoryCleanerListener {
   private boolean _compressedBanners;
   private String _text;
   TextMetrics bounds = (TextMetrics)(new Object());
   private Font _font;
   private Tag _backgroundTag = Tag.create("homescreen-description");
   private ThemeAttributeSet _backgroundTas;
   private boolean _backgroundSet;
   private Bitmap _screenBackground;
   private static final int ACCENTED_FONT_ADJUSTMENT = 2;

   RibbonDescriptionField(boolean compressedBanners) {
      super(1152921504606846976L);
      this.setTag(Tag.create("status"));
      this.setId("homescreen");
      this._compressedBanners = compressedBanners;
      if (this._compressedBanners) {
         label19:
         try {
            this.setFont(FontFamily.forName(FontFamily.FAMILY_SYSTEM).getFont(0, 8));
         } finally {
            break label19;
         }
      }

      MemoryCleanerDaemon.addWeakListener(this, false);
   }

   @Override
   public final void applyTheme() {
      if (!this._compressedBanners) {
         ThemeAttributeSet attr = this.getThemeAttributeSet();
         if (attr != null) {
            attr.apply();
            this.setFont(attr.getFont());
         }
      }

      Theme theme = ThemeManager.getActiveTheme();
      this._backgroundTas = theme.getAttributeSet(this._backgroundTag);
      super.applyTheme();
      this._font = this.getFont();
   }

   @Override
   public final int getPreferredHeight() {
      ThemeAttributeSet tas = this.getThemeAttributeSet();
      if (tas != null) {
         XYRect posn = tas.getPosition();
         if (posn != null) {
            return posn.height;
         }
      }

      return this.getFontHeight();
   }

   @Override
   protected final void layout(int width, int height) {
      ThemeAttributeSet tas = this.getThemeAttributeSet();
      XYRect posn = tas != null ? tas.getPosition() : null;
      if (posn == null) {
         this.setExtent(width, this.getFontHeight());
      } else {
         this.setExtent(posn.width, posn.height);
         this.setPosition(posn.x, posn.y);
      }

      this._backgroundSet = false;
   }

   private final int getFontHeight() {
      return this._font.getHeight() + 4;
   }

   @Override
   protected final void paint(Graphics graphics) {
      RibbonScreenManager mgr = (RibbonScreenManager)this.getManager().getManager();
      Bitmap screenBackground = mgr.getBackgroundImage();
      if ((!this._backgroundSet || this._screenBackground != screenBackground) && this._backgroundTas != null) {
         this._backgroundSet = true;
         this._screenBackground = screenBackground;
         int cx = this.getWidth();
         int cy = this.getHeight();
         Bitmap backgroundBitmap = (Bitmap)(new Object(cx, cy));
         Graphics bgGraphics = (Graphics)(new Object(backgroundBitmap));
         int top = this.getTop() + this.getManager().getTop();
         if (screenBackground != null) {
            bgGraphics.drawBitmap(0, 0, cx, cy, screenBackground, 0, top);
         }

         Background background = this._backgroundTas.getBackground();
         background.draw(bgGraphics, (XYRect)(new Object(0, 0, cx, cy)));
         this.getThemeAttributeSet().getWriterInternal().setBackgroundImage(backgroundBitmap);
         this.invalidate();
      }

      int attr = 116;
      graphics.setFont(this._font);
      int deltaY = this.getHeight() - this._font.getHeight() >> 1;
      graphics.drawText(this._text, 0, this._text.length(), 0, deltaY, attr, this.getWidth());
   }

   public final void setText(String text) {
      this._text = text;
      this.invalidate();
   }

   @Override
   public final boolean cleanNow(int event) {
      if (event == 10 && Memory.isPlaintext(this._text)) {
         this.setText("<Content Protection is enabled>");
         return true;
      } else {
         return false;
      }
   }

   @Override
   public final String getDescription() {
      return null;
   }
}
