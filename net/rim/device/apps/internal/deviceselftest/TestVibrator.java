package net.rim.device.apps.internal.deviceselftest;

import net.rim.device.api.system.Alert;
import net.rim.device.api.system.KeyListener;
import net.rim.device.api.ui.Keypad;

final class TestVibrator extends TestTaskBase implements KeyListener {
   TestVibratorScreen taskScreen;
   private boolean onFlag = false;

   TestVibrator() {
      super.app.addKeyListener(this);
      this.onRun();
   }

   final void onRun() {
      this.taskScreen = new TestVibratorScreen();
      super.app.pushScreen(this.taskScreen);
   }

   final void flipVibrator() {
      if (this.onFlag) {
         Alert.stopVibrate();
         this.onFlag = false;
      } else {
         Alert.startVibrate(25500);
         this.onFlag = true;
      }
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
         this.flipVibrator();
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
      this.flipVibrator();
      return true;
   }
}
