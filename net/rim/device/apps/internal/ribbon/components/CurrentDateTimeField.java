package net.rim.device.apps.internal.ribbon.components;

import java.util.Hashtable;
import net.rim.device.api.ui.DrawTextParam;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.FontFamily;
import net.rim.device.api.ui.FontLogicHelper;
import net.rim.device.api.ui.FontMetrics;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.TextGraphics;
import net.rim.device.api.ui.TextMetrics;
import net.rim.device.apps.api.ribbon.RibbonComponent;
import net.rim.device.apps.api.ribbon.RibbonComponent$RibbonComponentChangeListener;
import net.rim.device.apps.api.ribbon.RibbonComponentInitializer;
import net.rim.device.apps.api.ribbon.StringBufferRibbonComponent;

final class CurrentDateTimeField extends StringBufferRibbonComponent implements RibbonComponent$RibbonComponentChangeListener, RibbonComponentInitializer {
   private CurrentDateTimeComponentFactory _dateTimeManager;
   private RibbonComponent$RibbonComponentChangeListener _listener;
   private int _clockType = 1;
   private int _ampmSize = Integer.MAX_VALUE;
   private Font _ampmFont;
   private static final int DEFAULT_WIDTH;
   private static final int DEFAULT_SIZE;
   private static FontMetrics _fontMetrics = (FontMetrics)(new Object());
   private static TextGraphics _textGraphics = (TextGraphics)(new Object("BBMillbank", 10));
   private static DrawTextParam _textParams = (DrawTextParam)(new Object());
   private static TextMetrics _textMetrics = (TextMetrics)(new Object());

   private CurrentDateTimeField() {
      super._width = 70;
   }

   CurrentDateTimeField(CurrentDateTimeComponentFactory dateTimeManager) {
      this();
      this._dateTimeManager = dateTimeManager;
   }

   @Override
   public final void applyTheme() {
   }

   @Override
   protected final String getDefaultTag() {
      switch (this._clockType) {
         case 1:
            return "date-time";
         case 2:
         default:
            return "date";
         case 3:
            return "time";
      }
   }

   @Override
   public final int paintComponent(Graphics graphics, int x, int y, int width, int height, Object context) {
      if (this._clockType == 3 && this._ampmSize != Integer.MAX_VALUE) {
         Font currentFont = super._font;
         if (currentFont == null) {
            throw new Object("Undefined font");
         }

         graphics.pushContext(x, y, width, height, 0, 0);
         graphics.setFont(currentFont);
         if (super._fgColorSet) {
            graphics.setColor(super._fgColor);
         }

         StringBuffer lock = this.getText();
         if (lock == null) {
            graphics.popContext();
            return 0;
         }

         synchronized (lock) {
            StringBuffer textNoAmPm = this._dateTimeManager.getTimeNoAmPmBuffer();
            StringBuffer textAmPm = this._dateTimeManager.getAmPmBuffer();
            int hAlign = super._align & 7;
            if (hAlign == 5) {
               int widthRemaining = super._width;
               if (textAmPm.length() != 0) {
                  graphics.setFont(this._ampmFont);
                  currentFont.getMetrics(_fontMetrics);
                  int aboveBaselineMain = _fontMetrics.iAscent + _fontMetrics.iLeadingAbove;
                  this._ampmFont.getMetrics(_fontMetrics);
                  int aboveBaselineAmpm = _fontMetrics.iAscent + _fontMetrics.iLeadingAbove;
                  widthRemaining -= this.drawText(graphics, textAmPm, x, y + aboveBaselineMain - aboveBaselineAmpm, 5, widthRemaining);
               }

               graphics.setFont(currentFont);
               this.drawText(graphics, textNoAmPm, x, y, 5, widthRemaining);
            } else {
               if (hAlign == 4) {
                  _textGraphics.setFontSpec(currentFont);
                  int textWidth;
                  if (textNoAmPm.length() == 0) {
                     textWidth = 0;
                  } else {
                     _textGraphics.measureText(textNoAmPm, 0, textNoAmPm.length(), _textParams, _textMetrics);
                     textWidth = _textMetrics.iBoundsBrX - _textMetrics.iBoundsTlX;
                  }

                  _textGraphics.setFontSpec(this._ampmFont);
                  if (textAmPm.length() != 0) {
                     _textGraphics.measureText(textAmPm, 0, textAmPm.length(), _textParams, _textMetrics);
                     textWidth += _textMetrics.iBoundsBrX - _textMetrics.iBoundsTlX;
                  }

                  x += (super._width - textWidth) / 2;
               }

               x += this.drawText(graphics, textNoAmPm, x, y, 6, super._width);
               if (textAmPm.length() != 0) {
                  graphics.setFont(this._ampmFont);
                  currentFont.getMetrics(_fontMetrics);
                  int aboveBaselineMain = _fontMetrics.iAscent + _fontMetrics.iLeadingAbove;
                  this._ampmFont.getMetrics(_fontMetrics);
                  int aboveBaselineAmpm = _fontMetrics.iAscent + _fontMetrics.iLeadingAbove;
                  this.drawText(graphics, textAmPm, x, y + aboveBaselineMain - aboveBaselineAmpm, 6, super._width);
               }
            }
         }

         graphics.popContext();
         return 0;
      } else {
         return super.paintComponent(graphics, x, y, width, height, context);
      }
   }

   @Override
   protected final void setMainFont() {
      super.setMainFont();
      this._ampmFont = super._font;
   }

   @Override
   protected final void setAltFont(int locale) {
      super.setAltFont(locale);
      if (super._font != null && super._altFont != null) {
         if (this._clockType == 3) {
            this._ampmFont = super._font.derive(super._font.getStyle(), this._ampmSize);
            if (!FontLogicHelper.fontLegible(this._ampmFont, locale)) {
               this._ampmFont = FontLogicHelper.getSuggestedFont(this._ampmFont, locale, false);
            }
         }

         if (!"BBMillbank".equals(super._font.getFontFamily().getName())) {
            try {
               FontFamily family = FontFamily.forName("BBMillbank");
               int[] t = super._font.getTransform();
               super._font = family.getFont(
                  super._font.getStyle(),
                  super._font.getHeight(),
                  0,
                  super._font.getAntialiasMode(),
                  super._font.getEffects(),
                  t[0],
                  t[1],
                  t[2],
                  t[3],
                  t[4],
                  t[5],
                  super._font.getEffectsStrokeColor(),
                  super._font.getEffectsFillColor()
               );
            } finally {
               return;
            }
         }
      }
   }

   @Override
   protected final StringBuffer getText() {
      return this._dateTimeManager.getBuffer(this._clockType);
   }

   @Override
   protected final boolean isTextDescentIncluded() {
      return false;
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

   @Override
   public final void initialize(Hashtable parms, Object context) {
      String str = (String)parms.get("clocktype");
      if (str != null) {
         if (str.equals("date")) {
            this._clockType = 2;
         } else if (str.equals("time")) {
            this._clockType = 3;
         }
      }

      str = (String)parms.get("ampmSize");
      if (str != null) {
         this._ampmSize = Integer.parseInt(str);
      }

      super.initialize(parms, context);
      this.ribbonComponentChanged(null);
   }

   @Override
   public final void uninitialize() {
   }
}
