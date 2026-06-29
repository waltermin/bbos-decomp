package net.rim.device.apps.internal.deviceselftest;

import net.rim.device.api.system.Backlight;
import net.rim.device.api.system.KeyListener;
import net.rim.device.api.ui.Keypad;

final class TestKeypadBacklight extends TestTaskBase implements KeyListener {
   TestKeypadBacklightScreen taskScreen;

   TestKeypadBacklight() {
      super.app.addKeyListener(this);
      this.onRun();
   }

   final void onRun() {
      this.taskScreen = new TestKeypadBacklightScreen();
      super.app.pushScreen(this.taskScreen);
   }

   final void turnOnKeypadBacklight() {
      Backlight.setBrightness(100);
      Backlight.enable(2, true);
   }

   @Override
   public final boolean keyChar(char key, int status, int time) {
      return true;
   }

   @Override
   public final boolean keyDown(int keycode, int time) {
      char key = (char)Keypad.key(keycode);
      if (key == 27 && this.taskScreen.isMenuOn) {
         this.taskScreen.isMenuOn = false;
         return false;
      } else {
         this.turnOnKeypadBacklight();
         return true;
      }
   }

   @Override
   public final boolean keyUp(int keycode, int time) {
      return true;
   }

   @Override
   public final boolean keyRepeat(int keycode, int time) {
      return true;
   }

   @Override
   public final boolean keyStatus(int keycode, int time) {
      this.turnOnKeypadBacklight();
      return true;
   }
}
