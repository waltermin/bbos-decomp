package net.rim.device.apps.internal.deviceselftest;

import net.rim.device.api.system.Application;
import net.rim.device.api.ui.Keypad;

final class TestGPS extends TestTaskBase {
   TestGPSScreen taskScreen;
   private static final String MAIN_SCREEN_NAME;
   private static final String GPS_SCREEN_NAME;
   static final int fixingTimeout;

   TestGPS() {
      super.app = (DeviceSelfTest)Application.getApplication();
      super.app.addKeyListener(this);
      this.onRun();
   }

   final void onRun() {
      this.taskScreen = new TestGPSScreen(this);
      super.app.pushScreen(this.taskScreen);
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
   public final boolean keyChar(char key, int status, int time) {
      return true;
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
