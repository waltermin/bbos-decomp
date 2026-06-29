package javax.microedition.lcdui;

import net.rim.device.api.system.Application;
import net.rim.device.api.ui.UiApplication;

public class Ticker {
   private String _str;
   private net.rim.device.api.ui.Font _font;
   private int _pixelLength;
   private int _pixelOffset;
   private int _width;
   private static final int TICKER_ADVANCE;
   static final int TICKER_DELAY;
   static final int TICKER_IDLE;

   public Ticker(String str) {
      str.length();
      this._str = str;
      this._width = net.rim.device.api.system.Display.getWidth();
   }

   final void setStuff(net.rim.device.api.ui.Font font) {
      this._font = font;
      this._pixelLength = this._font.getAdvance(this._str);
   }

   final int getHeight() {
      return this._font.getHeight();
   }

   final void draw(net.rim.device.api.ui.Graphics graphics, int y) {
      graphics.setFont(this._font);
      graphics.drawText(this._str, this._width - this._pixelOffset, y);
   }

   final void advanceTicker() {
      this._pixelOffset += 4;
      if (this._pixelOffset > this._pixelLength + this._width) {
         this._pixelOffset = 0;
      }
   }

   public void setString(String str) {
      synchronized (Application.getEventLock()) {
         if (this._font == null) {
            str.length();
            this._str = str;
         } else {
            this._pixelLength = this._font.getAdvance(str);
            this._str = str;
            this._pixelOffset = 0;
            MIDPScreen active = (MIDPScreen)UiApplication.getUiApplication().getActiveScreen();
            if (active != null && active.getTicker() == this) {
               active.restartTickerTimer();
            }
         }
      }
   }

   public String getString() {
      return this._str;
   }
}
