package net.rim.device.apps.internal.qm.peer.common;

import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.component.LabelField;

public final class TickerField extends LabelField implements Runnable {
   private int _offset;
   private String _textTicker;
   private int _tickerLen;
   private boolean _isEnableTicker;
   private boolean _isTickerDone;
   private boolean _isPopupDismissed;
   private int _tickerStyle;
   private Object _callback;
   private static final long DEFAULT_LABEL_STYLE;
   private static final int DEFAULT_TICKER_DRAW_STYLE;

   public TickerField(Object ticker) {
      this(ticker, 6);
   }

   public TickerField(Object ticker, int style) {
      super(ticker, 51539607616L);
      this._textTicker = ticker.toString();
      this._tickerLen = this._textTicker.length();
      this._tickerStyle = style;
   }

   @Override
   public final void run() {
      if (!this._isTickerDone) {
         this.invalidate();
      } else {
         if (this._callback instanceof HintPopup && !this._isPopupDismissed) {
            ((HintPopup)this._callback).dismissPopup();
            this._isPopupDismissed = true;
         }
      }
   }

   @Override
   protected final void layout(int width, int height) {
      super.layout(width, height);
      if (this.getPreferredWidth() > width) {
         this._isEnableTicker = true;
         this._isTickerDone = false;
      } else {
         this._isEnableTicker = false;
         this._isTickerDone = true;
      }
   }

   @Override
   protected final void paint(Graphics graphics) {
      int x = 0;
      int y = 0;
      int width = this.getContentWidth() - 1 - x;
      int preferredTickerWidth = this.getPreferredWidth() - 1 - x;
      if (!this._isEnableTicker) {
         super.paint(graphics);
         this._isTickerDone = true;
      } else {
         int step = this.getPreferredWidth() / this._tickerLen;
         int hidden = preferredTickerWidth - width;
         int hiddenChars = hidden / step;
         graphics.drawText(this._textTicker, this._offset, this._tickerLen - this._offset, x, y, this._tickerStyle, width);
         this._offset++;
         if (this._offset - 3 > hiddenChars) {
            this._offset = 0;
            this._isTickerDone = true;
            this._isEnableTicker = false;
            this.invalidate();
         }
      }
   }
}
