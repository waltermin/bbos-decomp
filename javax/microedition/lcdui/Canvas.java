package javax.microedition.lcdui;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.system.Application;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.Trackball;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.XYRect;
import net.rim.device.internal.system.InternalServices;

public class Canvas extends Displayable {
   public static final int UP = 1;
   public static final int DOWN = 6;
   public static final int LEFT = 2;
   public static final int RIGHT = 5;
   public static final int FIRE = 8;
   public static final int GAME_A = 9;
   public static final int GAME_B = 10;
   public static final int GAME_C = 11;
   public static final int GAME_D = 12;
   public static final int KEY_NUM0 = 48;
   public static final int KEY_NUM1 = 49;
   public static final int KEY_NUM2 = 50;
   public static final int KEY_NUM3 = 51;
   public static final int KEY_NUM4 = 52;
   public static final int KEY_NUM5 = 53;
   public static final int KEY_NUM6 = 54;
   public static final int KEY_NUM7 = 55;
   public static final int KEY_NUM8 = 56;
   public static final int KEY_NUM9 = 57;
   public static final int KEY_STAR = 42;
   public static final int KEY_POUND = 35;
   static final int KEYCODE_UP = 1;
   static final int KEYCODE_RIGHT = 5;
   static final int KEYCODE_DOWN = 6;
   static final int KEYCODE_LEFT = 2;
   static final int KEYCODE_TB = -8;
   static final int KEYCODE_VOLUME_UP = -150;
   static final int KEYCODE_VOLUME_DOWN = -151;
   static final int KEYCODE_CONVENIENCE_KEY_1 = -19;
   static final int KEYCODE_CONVENIENCE_KEY_2 = -21;

   protected Canvas() {
      super(new CanvasScreen());
      ((CanvasScreen)this.getPeer()).setCanvas(this);
      synchronized (Application.getEventLock()) {
         this.getPeer().doLayout();
      }
   }

   public boolean isDoubleBuffered() {
      return true;
   }

   public boolean hasPointerEvents() {
      return false;
   }

   public boolean hasPointerMotionEvents() {
      return false;
   }

   public boolean hasRepeatEvents() {
      return false;
   }

   public int getKeyCode(int gameAction) {
      switch (gameAction) {
         case 0:
         case 3:
         case 4:
         case 7:
            int id = Keypad.getHardwareLayout();
            int hwid = InternalServices.getHardwareID();
            if (!InternalServices.isReducedFormFactor() && id != 1364669234 && hwid != -1677720317 && hwid != 469763332 && hwid != 469763334) {
               switch (gameAction) {
                  case 8:
                     break;
                  case 9:
                  default:
                     return 113;
                  case 10:
                     return 119;
                  case 11:
                     return 111;
                  case 12:
                     return 112;
               }
            } else {
               switch (gameAction) {
                  case 8:
                     break;
                  case 9:
                  default:
                     return 113;
                  case 10:
                     return 97;
                  case 11:
                     return 111;
                  case 12:
                     return 108;
               }
            }

            throw new IllegalArgumentException();
         case 1:
         default:
            return 1;
         case 2:
            return 2;
         case 5:
            return 5;
         case 6:
            return 6;
         case 8:
            if (!InternalServices.isReducedFormFactor()) {
               return 32;
            } else {
               return Trackball.isSupported() ? -8 : 48;
            }
      }
   }

   public String getKeyName(int keyCode) {
      int id;
      switch (keyCode) {
         case -151:
            id = 36;
            break;
         case -150:
            id = 35;
            break;
         case -21:
            id = 38;
            break;
         case -19:
            id = 37;
            break;
         case -8:
            id = 34;
            break;
         case 1:
            id = 9;
            break;
         case 2:
            id = 12;
            break;
         case 5:
            id = 10;
            break;
         case 6:
            id = 11;
            break;
         default:
            switch ((char)keyCode) {
               case '\u0000':
                  throw new IllegalArgumentException();
               case '\b':
                  id = 3;
                  break;
               case '\n':
                  id = 4;
                  break;
               case '\u001b':
                  id = 6;
                  break;
               case ' ':
                  id = 2;
                  break;
               case '\u007f':
                  id = 5;
                  break;
               default:
                  char[] str = new char[]{(char)keyCode};
                  return new String(str);
            }
      }

      return ResourceBundle.getBundle(3711053710409943671L, "net.rim.device.internal.resource.UI").getString(id);
   }

   public int getGameAction(int keyCode) {
      return Display.getGameAction(keyCode);
   }

   public void setFullScreenMode(boolean mode) {
      synchronized (Application.getEventLock()) {
         int oldHeight = this.getPeer().getDisplayableAreaExtent().height;
         this.getPeer().setFullScreenMode(mode);
         int newHeight = this.getPeer().getDisplayableAreaExtent().height;
         if (oldHeight != newHeight) {
            this.sizeChanged(newHeight, this.getWidth());
         }
      }
   }

   protected void keyPressed(int keyCode) {
   }

   protected void keyRepeated(int keyCode) {
   }

   protected void keyReleased(int keyCode) {
   }

   protected void pointerPressed(int x, int y) {
   }

   protected void pointerReleased(int x, int y) {
   }

   protected void pointerDragged(int x, int y) {
   }

   public final void repaint(int x, int y, int width, int height) {
      synchronized (Application.getEventLock()) {
         this.getPeer().invalidate(x, y + this.getPeer().getDisplayableAreaExtent().y, width, height);
         ((CanvasScreen)this.getPeer()).setDirty();
      }
   }

   public final void repaint() {
      synchronized (Application.getEventLock()) {
         XYRect areaExtent = this.getPeer().getDisplayableAreaExtent();
         this.getPeer().invalidate(areaExtent.x, areaExtent.y, areaExtent.width, areaExtent.height);
         ((CanvasScreen)this.getPeer()).setDirty();
      }
   }

   public final void serviceRepaints() {
      boolean displayed = false;
      net.rim.device.api.ui.Screen scr;
      synchronized (Application.getEventLock()) {
         scr = UiApplication.getUiApplication().getActiveScreen();
         if (this.getPeer() == scr) {
            displayed = true;
         }
      }

      if (displayed) {
         scr.updateDisplay();
         if (((CanvasScreen)scr).isDirty()) {
            Application.getApplication().invokeAndWait(new Canvas$1(this));
         }
      }
   }

   protected void showNotify() {
   }

   protected void hideNotify() {
   }

   protected void paint(Graphics _1) {
      throw null;
   }

   @Override
   protected void sizeChanged(int w, int h) {
   }
}
