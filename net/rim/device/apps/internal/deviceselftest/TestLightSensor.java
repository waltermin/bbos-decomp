package net.rim.device.apps.internal.deviceselftest;

import net.rim.device.api.system.KeyListener;
import net.rim.device.api.ui.Keypad;

final class TestLightSensor extends TestTaskBase implements KeyListener {
   TestLightSensorScreen taskScreen;

   TestLightSensor() {
      super.app.addKeyListener(this);
      this.onRun();
   }

   final void onRun() {
      this.taskScreen = new TestLightSensorScreen();
      super.app.pushScreen(this.taskScreen);
      new DataReader(this.taskScreen).start();
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
      return true;
   }
}
