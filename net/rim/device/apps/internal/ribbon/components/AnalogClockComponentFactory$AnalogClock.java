package net.rim.device.apps.internal.ribbon.components;

import java.util.Hashtable;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.FontFamily;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.apps.api.ribbon.RibbonComponent;
import net.rim.device.apps.api.ribbon.RibbonComponent$RibbonComponentChangeListener;
import net.rim.device.apps.api.ribbon.RibbonComponentInitializer;
import net.rim.device.apps.api.ribbon.TextRibbonComponent;

final class AnalogClockComponentFactory$AnalogClock
   extends TextRibbonComponent
   implements RibbonComponent$RibbonComponentChangeListener,
   RibbonComponentInitializer {
   private AnalogClockComponentFactory _factory;
   private RibbonComponent$RibbonComponentChangeListener _listener;
   Font _hourFont;
   Font _minuteFont;
   Font _secondFont;
   EncodedImage _face;
   int _hourXOfs;
   int _minuteXOfs;
   int _secondXOfs;
   int _hourYOfs;
   int _minuteYOfs;
   int _secondYOfs;

   AnalogClockComponentFactory$AnalogClock(AnalogClockComponentFactory factory) {
      this._factory = factory;
   }

   @Override
   public final int getComponentWidth() {
      return this._face.getWidth();
   }

   @Override
   public final int getComponentHeight() {
      return this._face.getHeight();
   }

   @Override
   public final void applyTheme() {
   }

   @Override
   public final synchronized void setChangeListener(RibbonComponent$RibbonComponentChangeListener listener) {
      this._listener = listener;
   }

   @Override
   public final synchronized void ribbonComponentChanged(RibbonComponent component) {
      RibbonComponent$RibbonComponentChangeListener listener = this._listener;
      if (listener != null) {
         listener.ribbonComponentChanged(this);
      }
   }

   final Font parseHandFont(String name) {
      if (name == null) {
         return null;
      }

      try {
         return FontFamily.forName(name).getFont(0, 320);
      } finally {
         ;
      }
   }

   @Override
   public final void initialize(Hashtable params, Object context) {
      super.initialize(params, context);
      this._hourFont = this.parseHandFont((String)params.get("hour-font"));
      if (this._hourFont == null) {
         throw new Object("Cannot find hour font");
      }

      this._minuteFont = this.parseHandFont((String)params.get("minute-font"));
      if (this._minuteFont == null) {
         throw new Object("Cannot find minute font");
      }

      this._secondFont = this.parseHandFont((String)params.get("second-font"));
      if (this._secondFont == null) {
         throw new Object("Cannot find second font");
      }

      this._face = ThemeManager.getActiveTheme().getImage((String)params.get("face"));
      if (this._face == null) {
         throw new Object("Cannot find face image");
      }

      this._hourXOfs = (this._face.getHeight() - this._hourFont.getAdvance('0')) / 2;
      this._hourYOfs = (this._face.getHeight() - this._hourFont.getHeight()) / 2;
      this._minuteXOfs = (this._face.getHeight() - this._minuteFont.getAdvance('0')) / 2;
      this._minuteYOfs = (this._face.getHeight() - this._minuteFont.getHeight()) / 2;
      if (this._secondFont != null) {
         this._secondXOfs = (this._face.getHeight() - this._secondFont.getAdvance('0')) / 2;
         this._secondYOfs = (this._face.getHeight() - this._secondFont.getHeight()) / 2;
         this._factory.trackSeconds(true);
      } else {
         this._factory.trackSeconds(false);
      }

      this.ribbonComponentChanged(null);
   }

   @Override
   public final void uninitialize() {
   }

   @Override
   public final int paintComponent(Graphics graphics, int x, int y, int width, int height, Object context) {
      synchronized (this._factory) {
         if (this._face != null) {
            graphics.drawImage(x, y, width, height, this._face, 0, 0, 0);
         }

         graphics.setFont(this._hourFont);
         graphics.drawText(this._factory._hour, x + this._hourXOfs, y + this._hourYOfs, 0, width);
         graphics.setFont(this._minuteFont);
         graphics.drawText(this._factory._minute, x + this._minuteXOfs, y + this._minuteYOfs, 0, width);
         if (this._secondFont != null) {
            graphics.setFont(this._secondFont);
            graphics.drawText(this._factory._second, x + this._secondXOfs, y + this._secondYOfs, 0, width);
         }

         return 0;
      }
   }
}
