package javax.microedition.lcdui;

import java.util.Hashtable;
import java.util.Timer;
import java.util.Vector;
import javax.microedition.midlet.MIDlet;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.Backlight;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.menu.MenuScreen;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.internal.lcdui.Lcdui;
import net.rim.device.internal.system.InternalServices;
import net.rim.device.internal.ui.MIDletApplication;

public class Display {
   private MIDlet _midlet;
   private Displayable _current;
   private Displayable _lastDisplayable;
   private Display$SwitchDisplayablesRunnable _switchDisplayablesRunnable;
   private Display$CallSeriallyQueue _callSeriallyRunnables;
   private static Hashtable _midletMap = new Hashtable();
   private static Vector _displayOrder = new Vector();
   public static final int LIST_ELEMENT = 1;
   public static final int CHOICE_GROUP_ELEMENT = 2;
   public static final int ALERT = 3;
   public static final int COLOR_BACKGROUND = 0;
   public static final int COLOR_FOREGROUND = 1;
   public static final int COLOR_HIGHLIGHTED_BACKGROUND = 2;
   public static final int COLOR_HIGHLIGHTED_FOREGROUND = 3;
   public static final int COLOR_BORDER = 4;
   public static final int COLOR_HIGHLIGHTED_BORDER = 5;
   static int _bestAlertImageHeight;
   static int _bestAlertImageWidth;
   static int _bestElementImageHeight;
   static int _bestElementImageWidth;
   static Display$FlashBacklightTimerTask _flashBacklightTask;

   private Display(MIDlet m) {
      this._midlet = m;
      this._switchDisplayablesRunnable = null;
      this._callSeriallyRunnables = new Display$CallSeriallyQueue(null);
   }

   MIDlet getMIDlet() {
      return this._midlet;
   }

   private static void moveDisplayToFront(Display d) {
      _displayOrder.removeElement(d);
      _displayOrder.insertElementAt(d, 0);
   }

   private static void moveDisplayToBack(Display d) {
      _displayOrder.removeElement(d);
      _displayOrder.addElement(d);
   }

   static Displayable getCurrentDisplayable() {
      return _displayOrder.size() > 0 ? ((Display)_displayOrder.elementAt(0))._current : null;
   }

   private void switchDisplayables(Displayable oldDisplayable, Displayable newDisplayable) {
      UiApplication app = UiApplication.getUiApplication();
      if (oldDisplayable != newDisplayable) {
         net.rim.device.api.ui.Screen screen = app.getActiveScreen();
         if (screen instanceof MenuScreen) {
            synchronized (app.getAppEventLock()) {
               app.popScreen(screen);
            }
         }

         if (oldDisplayable != null) {
            MIDPScreen oldMIDPScreen = oldDisplayable.getPeer();
            synchronized (app.getAppEventLock()) {
               app.popScreen(oldMIDPScreen);
            }

            oldDisplayable.getPeer().setDisplay(null);
            if (oldDisplayable instanceof Canvas) {
               synchronized (Lcdui.getEventDeliveryLock()) {
                  ((Canvas)oldDisplayable).hideNotify();
               }
            }
         }

         if (newDisplayable instanceof Canvas) {
            synchronized (Lcdui.getEventDeliveryLock()) {
               ((Canvas)newDisplayable).showNotify();
            }
         }

         MIDPScreen midpScreen = newDisplayable.getPeer();
         midpScreen.setDisplay(this);
         synchronized (app.getAppEventLock()) {
            app.pushScreen(midpScreen);
         }
      }
   }

   public static Display getDisplay(MIDlet m) {
      synchronized (_midletMap) {
         String prop = m.getAppProperty("MIDlet-Name");
         String midletID;
         if (prop != null) {
            midletID = prop;
         } else {
            midletID = ApplicationDescriptor.currentApplicationDescriptor().getModuleName();
         }

         prop = MIDletApplication.getAppProperty(midletID, "MIDlet-Vendor", true);
         if (prop != null) {
            midletID = midletID + ":" + prop;
         }

         Display display = (Display)_midletMap.get(midletID);
         if (display == null) {
            display = new Display(m);
            _midletMap.put(midletID, display);
         } else {
            display._midlet = m;
         }

         return display;
      }
   }

   public int getColor(int colorSpecifier) {
      switch (colorSpecifier) {
         case -1:
            throw new IllegalArgumentException();
         case 0:
         case 1:
         case 2:
         case 3:
         case 4:
         case 5:
         default:
            return 0;
      }
   }

   public int getBorderStyle(boolean highlighted) {
      return 0;
   }

   public boolean isColor() {
      return net.rim.device.api.ui.Graphics.isColor();
   }

   public int numColors() {
      return this.isColor() ? net.rim.device.api.ui.Graphics.getNumColors() : 2;
   }

   public int numAlphaLevels() {
      return this.isColor() ? 16 : 2;
   }

   public Displayable getCurrent() {
      return this._current;
   }

   public void setCurrent(Displayable nextDisplayable) {
      Application app = Application.getApplication();
      synchronized (Application.getEventLock()) {
         Displayable old = getCurrentDisplayable();
         if (nextDisplayable == null) {
            moveDisplayToBack(this);
            app.requestBackground();
         } else if (!(nextDisplayable instanceof Alert)) {
            if (nextDisplayable instanceof Canvas) {
               nextDisplayable.getPeer().init();
            }

            moveDisplayToFront(this);
            app.requestForeground();
            this._current = nextDisplayable;
            this._lastDisplayable = nextDisplayable;
         } else {
            Alert alert = (Alert)nextDisplayable;
            moveDisplayToFront(this);
            app.requestForeground();
            this._current = alert;
            alert.show(this, this._lastDisplayable);
         }

         this.startDisplaySwitch(app, old, getCurrentDisplayable());
      }
   }

   private void startDisplaySwitch(Application app, Displayable oldDisplayable, Displayable newDisplayable) {
      if (this._switchDisplayablesRunnable != null) {
         this._switchDisplayablesRunnable.setNewDisplayable(newDisplayable);
      } else {
         this._switchDisplayablesRunnable = new Display$SwitchDisplayablesRunnable(this, oldDisplayable, newDisplayable);
         app.invokeLater(this._switchDisplayablesRunnable);
      }
   }

   public void setCurrent(Alert alert, Displayable nextDisplayable) {
      synchronized (Application.getEventLock()) {
         alert.getPeer();
         nextDisplayable.getPeer();
         if (nextDisplayable instanceof Alert) {
            throw new IllegalArgumentException();
         }

         Displayable old = getCurrentDisplayable();
         this._lastDisplayable = nextDisplayable;
         moveDisplayToFront(this);
         UiApplication.getUiApplication().requestForeground();
         this._current = alert;
         alert.show(this, nextDisplayable);
         this.startDisplaySwitch(Application.getApplication(), old, alert);
      }
   }

   public void setCurrentItem(Item item) {
      synchronized (Application.getEventLock()) {
         Screen nextDisplayable = item.getOwner();
         if (nextDisplayable != null && !(nextDisplayable instanceof Alert)) {
            Form nextForm = (Form)nextDisplayable;
            if (nextForm != null) {
               this.setCurrent(nextDisplayable);
               Field field = item.addToForm(null);
               item.addToForm(nextForm._formChangeListener);
               field.setFocus();
            }
         } else {
            throw new IllegalStateException();
         }
      }
   }

   public void callSerially(Runnable r) {
      this._callSeriallyRunnables.addElement(r);
   }

   public boolean flashBacklight(int duration) {
      if (duration < 0) {
         throw new IllegalArgumentException();
      }

      if (this.getCurrent().isShown()) {
         if (duration == 0) {
            if (_flashBacklightTask != null) {
               _flashBacklightTask.cancel();
               _flashBacklightTask = null;
               return true;
            }
         } else {
            int remainder = duration % 1000;
            int seconds = duration / 1000;
            if (remainder > 0) {
               seconds++;
            }

            if (_flashBacklightTask != null) {
               _flashBacklightTask.cancel();
            }

            int numBacklightOnOffIterations;
            if (seconds % 2 == 0) {
               numBacklightOnOffIterations = seconds;
            } else {
               numBacklightOnOffIterations = seconds + 1;
            }

            if (Backlight.isEnabled()) {
               Backlight.enable(false);
            }

            _flashBacklightTask = new Display$FlashBacklightTimerTask(numBacklightOnOffIterations);
            Timer flashingTimer = new Timer();
            flashingTimer.schedule(_flashBacklightTask, 0, 1000);
         }

         return true;
      } else {
         return false;
      }
   }

   public boolean vibrate(int duration) {
      if (duration < 0) {
         throw new IllegalArgumentException();
      }

      Displayable current = this.getCurrent();
      if (current != null && current.isShown()) {
         net.rim.device.api.system.Alert.stopVibrate();
         if (duration != 0) {
            net.rim.device.api.system.Alert.startVibrate(duration);
         }

         return true;
      } else {
         return false;
      }
   }

   public int getBestImageWidth(int imageType) {
      switch (imageType) {
         case 0:
            throw new IllegalArgumentException();
         case 1:
         case 2:
         default:
            return _bestElementImageWidth;
         case 3:
            return _bestAlertImageWidth;
      }
   }

   public int getBestImageHeight(int imageType) {
      switch (imageType) {
         case 0:
            throw new IllegalArgumentException();
         case 1:
         case 2:
         default:
            return _bestElementImageHeight;
         case 3:
            return _bestAlertImageHeight;
      }
   }

   static int getGameAction(int keyCode) {
      switch (keyCode) {
         case -8:
            return 8;
         case 1:
            return 1;
         case 2:
            return 2;
         case 5:
            return 5;
         case 6:
            return 6;
         default:
            int id = Keypad.getHardwareLayout();
            int hwid = InternalServices.getHardwareID();
            if (InternalServices.isReducedFormFactor()) {
               switch ((char)keyCode) {
                  case '0':
                     return 8;
                  case '2':
                     return 1;
                  case '4':
                     return 2;
                  case '6':
                     return 5;
                  case '8':
                     return 6;
                  case 'A':
                  case 'a':
                     return 10;
                  case 'L':
                  case 'l':
                     return 12;
                  case 'O':
                  case 'o':
                     return 11;
                  case 'Q':
                  case 'q':
                     return 9;
               }
            } else if (id != 1364669234 && hwid != -1677720317 && hwid != 469763332 && hwid != 469763334) {
               switch ((char)keyCode) {
                  case '\b':
                  case 'G':
                  case 'K':
                  case 'S':
                  case 'g':
                  case 'k':
                  case 's':
                     return 5;
                  case ' ':
                     return 8;
                  case 'A':
                  case 'D':
                  case 'H':
                  case 'L':
                  case 'a':
                  case 'd':
                  case 'h':
                  case 'l':
                     return 2;
                  case 'C':
                  case 'F':
                  case 'J':
                  case 'N':
                  case 'c':
                  case 'f':
                  case 'j':
                  case 'n':
                     return 6;
                  case 'O':
                  case 'o':
                     return 11;
                  case 'P':
                  case 'p':
                     return 12;
                  case 'Q':
                  case 'q':
                     return 9;
                  case 'R':
                  case 'U':
                  case 'r':
                  case 'u':
                     return 1;
                  case 'W':
                  case 'w':
                     return 10;
               }
            } else {
               switch ((char)keyCode) {
                  case ' ':
                     return 8;
                  case '2':
                  case 'E':
                  case 'e':
                     return 1;
                  case '4':
                  case 'S':
                  case 's':
                     return 2;
                  case '6':
                  case 'F':
                  case 'f':
                     return 5;
                  case '8':
                  case 'X':
                  case 'x':
                     return 6;
                  case 'A':
                  case 'a':
                     return 10;
                  case 'L':
                  case 'l':
                     return 12;
                  case 'O':
                  case 'o':
                     return 11;
                  case 'Q':
                  case 'q':
                     return 9;
               }
            }

            if (keyCode == 0) {
               throw new IllegalArgumentException();
            } else {
               return 0;
            }
      }
   }

   static {
      net.rim.device.internal.ui.Image dialogImage = ThemeManager.getThemeAwareImage("dialog_exclamation");
      if (dialogImage != null) {
         _bestAlertImageHeight = dialogImage.getHeight(net.rim.device.api.system.Display.getWidth(), net.rim.device.api.system.Display.getHeight());
         _bestAlertImageWidth = dialogImage.getWidth(net.rim.device.api.system.Display.getWidth(), net.rim.device.api.system.Display.getHeight());
      }

      _bestElementImageHeight = Font.getDefaultFont().getHeight();
      _bestElementImageWidth = _bestElementImageHeight;
   }
}
