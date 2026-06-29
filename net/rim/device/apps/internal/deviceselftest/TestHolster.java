package net.rim.device.apps.internal.deviceselftest;

import net.rim.device.api.system.Application;
import net.rim.device.api.system.HolsterListener;
import net.rim.device.api.system.LED;
import net.rim.device.api.ui.Keypad;

final class TestHolster extends TestTaskBase implements HolsterListener {
   TestHolsterScreen taskScreen;
   final long FILTER_THRESHOLD = 3000;
   static final int INTO_HOLSTER = 1;
   static final int OUTOF_HOLSTER = 2;
   static String clipName;

   TestHolster() {
      super.app = (DeviceSelfTest)Application.getApplication();
      super.app.addHolsterListener(this);
      super.app.addKeyListener(this);
      this.onRun();
   }

   final void onRun() {
      this.taskScreen = new TestHolsterScreen();
      super.app.pushScreen(this.taskScreen);
   }

   @Override
   public final void inHolster() {
      this.taskScreen.insertReport(1);
      LED.setColorConfiguration(0, 25000, 0, 16711680);
   }

   @Override
   public final void outOfHolster() {
      this.taskScreen.insertReport(2);
      LED.setColorConfiguration(0, 25000, 0, 65280);
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
