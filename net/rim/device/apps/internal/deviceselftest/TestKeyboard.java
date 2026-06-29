package net.rim.device.apps.internal.deviceselftest;

import net.rim.device.api.system.KeyListener;
import net.rim.device.api.ui.Keypad;

final class TestKeyboard extends TestTaskBase implements KeyListener {
   TestKeyboardScreen taskScreen;
   StringBuffer sb;
   int lastStatusKeyTS;

   TestKeyboard() {
      super.app.addKeyListener(this);
      this.sb = new StringBuffer();
      this.onRun();
      this.lastStatusKeyTS = 0;
   }

   final void onRun() {
      this.taskScreen = new TestKeyboardScreen();
      super.app.pushScreen(this.taskScreen);
   }

   final void displayKeyString(char key, boolean isCallByKeyStatus) {
      if (key == 256 || key == 258 || key == 257 || !isCallByKeyStatus) {
         String keyString;
         switch (key) {
            case '\b':
               keyString = "{backspace}";
               break;
            case '\n':
               keyString = "{cr}";
               break;
            case '\u0011':
               keyString = "{send}";
               break;
            case '\u0012':
               keyString = "{end}";
               break;
            case '\u0013':
               keyString = "{convenience_1}";
               break;
            case '\u0015':
               keyString = "{application}";
               break;
            case '\u001b':
               keyString = "{esc}";
               break;
            case ' ':
               keyString = "{space}";
               break;
            case '\u007f':
               keyString = "{sym}";
               break;
            case 'Ā':
               keyString = "{r-shift}";
               break;
            case 'ā':
               keyString = "{alt}";
               break;
            case 'Ă':
               keyString = "{l-shift}";
               break;
            case 'ă':
               keyString = "{backlight}";
               break;
            case 'ą':
               keyString = "0";
               break;
            case 'đ':
               keyString = "{speaker phone}";
               break;
            case 'က':
               keyString = "{volume up}";
               break;
            case 'ခ':
               keyString = "{volume down}";
               break;
            case 'ဂ':
               keyString = "{menu}";
               break;
            default:
               keyString = String.valueOf(Character.toLowerCase(key));
         }

         this.sb.append(keyString);
         this.taskScreen.updateKeyString(this.sb);
      }
   }

   @Override
   public final boolean keyChar(char key, int status, int time) {
      return false;
   }

   @Override
   public final boolean keyDown(int keycode, int time) {
      char key = (char)Keypad.key(keycode);
      if (this.taskScreen.isMenuOn) {
         if (key == 27) {
            this.taskScreen.isMenuOn = false;
            return false;
         } else {
            return true;
         }
      } else {
         this.displayKeyString(key, false);
         return true;
      }
   }

   @Override
   public final boolean keyUp(int keycode, int time) {
      return false;
   }

   @Override
   public final boolean keyRepeat(int keycode, int time) {
      return false;
   }

   @Override
   public final boolean keyStatus(int keycode, int time) {
      if (time - this.lastStatusKeyTS > 250) {
         this.displayKeyString((char)Keypad.key(keycode), true);
      }

      this.lastStatusKeyTS = time;
      return true;
   }
}
