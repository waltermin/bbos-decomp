package net.rim.device.cldc.io.commlink;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Display;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.container.FullScreen;
import net.rim.device.api.ui.theme.Theme;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.api.util.StringTokenizer;
import net.rim.device.api.util.StringUtilities;

final class CommLinkStatusScreen extends FullScreen implements Runnable, GlobalEventListener {
   private boolean _connected;
   private int _left;
   private int _right;
   private int _width = Display.getWidth();
   private int _textY;
   private Bitmap _backgroundWithText = (Bitmap)(new Object(Display.getWidth(), Display.getHeight()));
   private Bitmap _background;
   private Bitmap _leftArrow;
   private Bitmap _rightArrow;
   private XYRect _pagerPos;
   private XYRect _leftPos;
   private XYRect _rightPos;
   private XYRect _arrowPos;
   private Object _lock = new Object();
   private UiApplication _app = UiApplication.getUiApplication();
   private CommLinkStatusScreen$DrawArrow _arrowUpdate = new CommLinkStatusScreen$DrawArrow(this, false, false, null);
   private boolean _die;
   private String _connectingMessage;
   private String _connectedMessage;
   private boolean _pushed;
   private String _statusMessage;
   private String _displayMessage;

   final void push() {
      if (!this._pushed) {
         this._pushed = true;
         this._app.queueStatus(this, -1073741826, true);
         this._app.addGlobalEventListener(this);
      }
   }

   final void pop() {
      if (this._pushed) {
         this._pushed = false;
         this._app.dismissStatus(this);
         this._app.removeGlobalEventListener(this);
      }
   }

   final void drawArrow(boolean left, boolean draw) {
      CommLinkStatusScreen$DrawArrow arrowUpdate = this._arrowUpdate;
      boolean finished;
      synchronized (arrowUpdate) {
         finished = arrowUpdate._finished;
         arrowUpdate.setNewState(left, draw);
      }

      if (finished) {
         this._app.invokeLater(arrowUpdate);
      }
   }

   final void connected() {
      this._left = 0;
      this._right = 0;
      this._connected = true;
      synchronized (Application.getEventLock()) {
         this.invalidate();
      }
   }

   final void advanceAnimation(boolean deviceToPC) {
      synchronized (this._lock) {
         if (deviceToPC) {
            this._right++;
            if (this._right == 1) {
               this.drawArrow(false, true);
            }
         } else {
            this._left++;
            if (this._left == 1) {
               this.drawArrow(true, true);
            }
         }

         this._lock.notify();
      }
   }

   final void stop() {
      synchronized (this._lock) {
         this._die = true;
         this._lock.notify();
      }

      this.pop();
   }

   @Override
   public final void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == -6549094840388549801L && object0 instanceof Object) {
         this._statusMessage = (String)object0;
         synchronized (Application.getEventLock()) {
            this.invalidate();
         }
      }
   }

   @Override
   public final void run() {
      synchronized (this._lock) {
         while (!this._die) {
            if (this._left <= 0 && this._right <= 0) {
               try {
                  this._lock.wait();
               } finally {
                  continue;
               }
            } else {
               int oldleft = this._left;
               int oldright = this._right;

               label80:
               try {
                  this._lock.wait(200);
               } finally {
                  break label80;
               }

               if (this._left == oldleft && this._right == oldright) {
                  this._left = 0;
                  this._right = 0;
                  this.drawArrow(false, false);
               }
            }
         }
      }
   }

   CommLinkStatusScreen() {
      super(0);
      this._connected = false;
      Theme theme = ThemeManager.getActiveTheme();
      EncodedImage image = theme.getImage("net_rim_comm_background");
      this._background = image.getBitmap();
      image = theme.getImage("net_rim_comm_receive");
      this._leftArrow = image.getBitmap();
      image = theme.getImage("net_rim_comm_send");
      this._rightArrow = image.getBitmap();
      image = theme.getImage("net_rim_comm_text_kludge");
      int height = Display.getHeight();
      XYRect clamp = (XYRect)(new Object(0, 0, this._width, height));
      this._pagerPos = (XYRect)(new Object(
         this._width - this._background.getWidth() >> 1, height - this._background.getHeight() >> 1, this._background.getWidth(), this._background.getHeight()
      ));
      this._pagerPos.intersect(clamp);
      this._leftPos = (XYRect)(new Object(this._pagerPos.x, this._pagerPos.y, this._leftArrow.getWidth(), this._leftArrow.getHeight()));
      this._leftPos.intersect(clamp);
      this._rightPos = (XYRect)(new Object(this._pagerPos.x, this._pagerPos.y, this._rightArrow.getWidth(), this._rightArrow.getHeight()));
      this._rightPos.intersect(clamp);
      this._arrowPos = (XYRect)(new Object(this._leftPos));
      this._arrowPos.union(this._rightPos);
      this._textY = this._pagerPos.y + (image.getHeight() > 1 ? image.getHeight() : -image.getWidth());
      ResourceBundle rb = ResourceBundle.getBundle(-7779803456443759697L, "net.rim.device.internal.resource.CommLink");
      this._connectingMessage = rb.getString(0);
      this._connectedMessage = rb.getString(1);
      this.push();
      ((Thread)(new Object(this))).start();
   }

   @Override
   protected final void paint(Graphics graphics) {
      try {
         graphics.clear();
         String message = this._connectedMessage;
         if (this._statusMessage != null) {
            message = this._statusMessage;
         }

         if (!this._connected) {
            graphics.drawText(this._connectingMessage, 0, Display.getHeight() >> 1, 44, this._width);
            return;
         }

         if (!StringUtilities.strEqual(message, this._displayMessage)) {
            this._displayMessage = message;
            Graphics bufferGraphics = (Graphics)(new Object(this._backgroundWithText));
            bufferGraphics.drawBitmap(this._pagerPos, this._background, 0, 0);
            if (message != null) {
               Font defaultFont = bufferGraphics.getFont();
               int availableWidth = this.getWidth();
               int xpos = 0;
               int ypos = this._textY;
               int stringWidth = 0;
               int spaceWidth = defaultFont.getAdvance(' ');
               StringTokenizer st = (StringTokenizer)(new Object(message));
               StringBuffer buffer = (StringBuffer)(new Object());

               String nextWord;
               do {
                  nextWord = st.hasMoreElements() ? st.nextToken() : null;
                  stringWidth = nextWord != null ? defaultFont.getAdvance(nextWord) : 0;
                  if (nextWord == null || xpos + stringWidth > availableWidth && xpos != 0) {
                     if (xpos > 0) {
                        String stringToDraw = buffer.toString().trim();
                        bufferGraphics.drawText(stringToDraw, 0, ypos, 4, this._width);
                        buffer.setLength(0);
                        if (nextWord != null) {
                           buffer.append(nextWord);
                           buffer.append(' ');
                           xpos = stringWidth + spaceWidth;
                        }

                        ypos = ypos + defaultFont.getHeight() + 5;
                     }
                  } else {
                     buffer.append(nextWord);
                     buffer.append(' ');
                     xpos = xpos + stringWidth + spaceWidth;
                  }
               } while (nextWord != null);
            }
         }

         graphics.drawBitmap(0, 0, this._backgroundWithText.getWidth(), this._backgroundWithText.getHeight(), this._backgroundWithText, 0, 0);
         this._arrowUpdate.paintArrows(graphics);
      } finally {
         return;
      }
   }
}
