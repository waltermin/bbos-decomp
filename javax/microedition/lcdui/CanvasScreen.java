package javax.microedition.lcdui;

import javax.microedition.media.MediaException;
import javax.microedition.media.Player;
import net.rim.device.api.system.Application;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.Trackball;
import net.rim.device.api.ui.XYRect;
import net.rim.device.internal.lcdui.Callbacks;
import net.rim.device.internal.lcdui.Lcdui;
import net.rim.device.internal.lcdui.LcduiPlayerController;
import net.rim.device.internal.media.PlayerRegistry;
import net.rim.device.internal.system.InternalServices;

class CanvasScreen extends MIDPScreen implements Callbacks, LcduiPlayerController {
   private Graphics _graphics;
   private Canvas _canvas;
   private boolean _firstPaint = true;
   private boolean _clearBackground;
   private boolean _playerPausedByObscure = false;
   private boolean _obscured;
   private boolean _dirty = false;
   private Player _player;

   public void setCanvas(Canvas canvas) {
      this._canvas = canvas;
   }

   public void setDirty() {
      this._dirty = true;
   }

   @Override
   public void keyCallback(int type, int keycode) {
      boolean suppressKeyEvents = Lcdui.getSuppressKeyEvents();
      int gameAction = this._canvas.getGameAction(keycode);
      switch (type) {
         case 1:
            break;
         case 2:
         default:
            if (!suppressKeyEvents) {
               synchronized (Lcdui.getEventDeliveryLock()) {
                  this._canvas.keyPressed(keycode);
               }
            }

            int currentKeyStates = Lcdui.getCurrentKeyStates();
            int keyDownHistory = Lcdui.getKeyDownHistory();
            Lcdui.setCurrentKeyStates(currentKeyStates | 1 << gameAction);
            Lcdui.setKeyDownHistory(keyDownHistory | 1 << gameAction);
            return;
         case 3:
            if (!suppressKeyEvents) {
               synchronized (Lcdui.getEventDeliveryLock()) {
                  this._canvas.keyRepeated(keycode);
                  return;
               }
            }
            break;
         case 4:
            if (!suppressKeyEvents) {
               synchronized (Lcdui.getEventDeliveryLock()) {
                  this._canvas.keyReleased(keycode);
               }
            }

            int currentKeyStates = Lcdui.getCurrentKeyStates();
            Lcdui.setCurrentKeyStates(currentKeyStates & ~(1 << gameAction));
            return;
         case 5:
            if (!suppressKeyEvents) {
               synchronized (Lcdui.getEventDeliveryLock()) {
                  this._canvas.keyPressed(keycode);
                  this._canvas.keyReleased(keycode);
                  return;
               }
            }
      }
   }

   @Override
   public void paintCallback(net.rim.device.api.ui.Graphics graphics) {
      if (this._clearBackground) {
         graphics.clear();
         this._clearBackground = false;
      }

      this._graphics.setGraphics(graphics, false);
      synchronized (Application.getEventLock()) {
         super.paint(graphics);
      }

      XYRect areaExtent = this.getDisplayableAreaExtent();
      this._graphics.translate(areaExtent.x, areaExtent.y);
      Lcdui.setMIDPGraphics(this._graphics);
      synchronized (Lcdui.getEventDeliveryLock()) {
         this._canvas.paint(this._graphics);
      }

      this._dirty = false;
      this._graphics.translate(-areaExtent.x, -areaExtent.y);
   }

   @Override
   public void setPlayer(Player player) {
      this._player = player;
   }

   @Override
   public void resizeComponent(int width, int height) {
   }

   @Override
   public Object getComponent() {
      return this._canvas;
   }

   @Override
   public void setComponent(Object component) {
   }

   @Override
   public void refresh() {
      if (!this._obscured) {
         this.invalidate();
      }
   }

   @Override
   protected void onObscured() {
      super.onObscured();
      this.toggleVideoVisibility(false);
   }

   private void toggleVideoVisibility(boolean visible) {
      this._obscured = !visible;
      if (visible) {
         if (this._player != null && this._player.getState() == 300 && this._playerPausedByObscure) {
            try {
               this._player.start();
               this._playerPausedByObscure = false;
            } catch (MediaException var4) {
            }
         }
      } else if (this._player != null && this._player.getState() == 400) {
         try {
            this._player.stop();
            this._playerPausedByObscure = true;
         } catch (MediaException var3) {
         }
      }

      XYRect absoluteRect = this.getContentRect();
      this.transformToScreen(absoluteRect);
      PlayerRegistry.getMMAPIConnector().notifyPlayerOffsetChange(this._player, absoluteRect);
   }

   @Override
   public void init() {
      this._firstPaint = true;
   }

   @Override
   protected boolean navigationMovement(int dx, int dy, int status, int time) {
      if (dx > 0) {
         Lcdui.setKeyCallback(5, this, 5);
      } else if (dx < 0) {
         Lcdui.setKeyCallback(5, this, 2);
      }

      if (dy < 0) {
         Lcdui.setKeyCallback(5, this, 1);
         return true;
      }

      if (dy > 0) {
         Lcdui.setKeyCallback(5, this, 6);
      }

      return true;
   }

   @Override
   protected boolean navigationClick(int status, int time) {
      if (Trackball.isSupported()) {
         Lcdui.setKeyCallback(5, this, -8);
         return true;
      } else {
         return super.navigationClick(status, time);
      }
   }

   @Override
   protected boolean trackwheelRoll(int amount, int status, int time) {
      if (amount < 0) {
         if ((status & 1) > 0) {
            Lcdui.setKeyCallback(5, this, 2);
            return true;
         } else {
            Lcdui.setKeyCallback(5, this, 1);
            return true;
         }
      } else {
         if (amount > 0) {
            if ((status & 1) > 0) {
               Lcdui.setKeyCallback(5, this, 5);
               return true;
            }

            Lcdui.setKeyCallback(5, this, 6);
         }

         return true;
      }
   }

   @Override
   public boolean dispatchKeyEvent(int event, char key, int status, int time) {
      switch (key) {
         case '\u0095':
            return super.dispatchKeyEvent(event, key, status, time);
         case '\u0096':
         case '\u0097':
         default:
            this.restartTickerTimer();
            Lcdui.setKeyCallback(5, this, key == 150 ? -150 : -151);
            return true;
      }
   }

   @Override
   protected boolean keyDown(int keycode, int time) {
      if (Keypad.hasSendEndKeys()) {
         switch (Keypad.key(keycode)) {
            case 17:
            case 18:
            case 4098:
               return false;
            case 19:
               Lcdui.setKeyCallback(2, this, -19);
               return true;
            case 21:
               Lcdui.setKeyCallback(2, this, -21);
               return true;
         }
      }

      char c = Keypad.map(keycode);
      if (c == 27) {
         return false;
      }

      if (0 != c) {
         if (InternalServices.isReducedFormFactor()) {
            c = this.getCharmRemappedCharacter(c);
         }

         Lcdui.setKeyCallback(2, this, c);
      }

      return true;
   }

   @Override
   protected boolean keyUp(int keycode, int time) {
      if (Keypad.hasSendEndKeys()) {
         switch (Keypad.key(keycode)) {
            case 16:
            case 20:
               break;
            case 17:
            case 18:
            default:
               return false;
            case 19:
               Lcdui.setKeyCallback(4, this, -19);
               return true;
            case 21:
               Lcdui.setKeyCallback(4, this, -21);
               return true;
         }
      }

      char c = Keypad.map(keycode);
      if (0 != c) {
         if (InternalServices.isReducedFormFactor()) {
            c = this.getCharmRemappedCharacter(c);
         }

         Lcdui.setKeyCallback(4, this, c);
      }

      return true;
   }

   private char getCharmRemappedCharacter(char c) {
      if (c != '*' && c != '#' && (c < '0' || c > '9')) {
         char altedChar = Keypad.getAltedChar(c);
         return altedChar != '*' && altedChar != '#' && (altedChar < '0' || altedChar > '9') ? c : altedChar;
      } else {
         return Keypad.getUnaltedChar(c);
      }
   }

   public CanvasScreen() {
      this._graphics = new Graphics();
   }

   @Override
   public boolean isDirty() {
      return this._dirty;
   }

   @Override
   Displayable getDisplayable() {
      return this._canvas;
   }

   @Override
   public void paint(net.rim.device.api.ui.Graphics graphics) {
      if (this._firstPaint) {
         graphics.clear();
         this.paintCallback(graphics);
         this._firstPaint = false;
      } else {
         Lcdui.setPaintCallback(graphics, this, this);
      }
   }

   @Override
   public void paintBackground(net.rim.device.api.ui.Graphics graphics) {
   }

   @Override
   protected void onVisibilityChange(boolean visible) {
      super.onVisibilityChange(visible);
      this.toggleVideoVisibility(visible);
   }

   @Override
   protected void onExposed() {
      this._clearBackground = true;
      this._canvas.repaint();
      super.onExposed();
      this.toggleVideoVisibility(true);
   }
}
