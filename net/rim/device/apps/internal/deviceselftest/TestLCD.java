package net.rim.device.apps.internal.deviceselftest;

import net.rim.device.api.system.KeyListener;
import net.rim.device.api.ui.Keypad;

final class TestLCD extends TestTaskBase implements KeyListener {
   TestLCDScreen taskScreen;
   private boolean flag = true;
   SolidScreen solidScreen;

   TestLCD() {
      super.app.addKeyListener(this);
      this.onRun();
   }

   final void onRun() {
      this.taskScreen = new TestLCDScreen();
      this.solidScreen = new SolidScreen();
      super.app.pushScreen(this.taskScreen);
   }

   @Override
   public final boolean keyChar(char key, int status, int time) {
      return false;
   }

   final void showSolidScreen() {
      if (this.flag) {
         super.app.pushScreen(this.solidScreen);
         this.solidScreen.changeColor();
         this.flag = false;
      } else {
         super.app.popScreen(this.solidScreen);
         this.flag = true;
      }
   }

   @Override
   public final boolean keyDown(int keycode, int time) {
      char key = (char)Keypad.key(keycode);
      if (key == 27 && this.taskScreen.isMenuOn) {
         this.taskScreen.isMenuOn = false;
         return false;
      } else {
         this.showSolidScreen();
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
      this.showSolidScreen();
      return true;
   }
}
