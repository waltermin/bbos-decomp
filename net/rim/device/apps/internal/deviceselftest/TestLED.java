package net.rim.device.apps.internal.deviceselftest;

import net.rim.device.api.system.KeyListener;
import net.rim.device.api.system.LED;
import net.rim.device.api.ui.Keypad;

final class TestLED extends TestTaskBase implements KeyListener {
   TestLEDScreen taskScreen;
   private int cnt = 0;

   TestLED() {
      super.app.addKeyListener(this);
      this.onRun();
   }

   final void onRun() {
      this.taskScreen = new TestLEDScreen();
      super.app.pushScreen(this.taskScreen);
   }

   final void flipLED() {
      if (this.cnt % 4 == 0) {
         LED.setColorConfiguration(0, 25000, 0, 16711680);
      } else if (this.cnt % 4 == 1) {
         LED.setColorConfiguration(0, 25000, 0, 65280);
      } else if (this.cnt % 4 == 2) {
         LED.setColorConfiguration(0, 25000, 0, 255);
      } else {
         LED.setState(0, 0);
      }

      this.cnt++;
   }

   @Override
   public final boolean keyChar(char key, int status, int time) {
      return false;
   }

   @Override
   public final boolean keyDown(int keycode, int time) {
      char key = (char)Keypad.key(keycode);
      if (key == 27 && this.taskScreen.isMenuOn) {
         this.taskScreen.isMenuOn = false;
         return false;
      } else {
         this.flipLED();
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
      this.flipLED();
      return true;
   }
}
