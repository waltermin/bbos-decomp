package net.rim.device.apps.internal.deviceselftest;

import net.rim.device.api.system.Backlight;
import net.rim.device.api.system.KeyListener;
import net.rim.device.api.ui.Keypad;

final class TestBacklight extends TestTaskBase implements KeyListener {
   TestBacklightScreen taskScreen;
   boolean isOn;
   static int processId;

   TestBacklight() {
      processId = super.app.getProcessId();
      super.app.addKeyListener(this);
      this.onRun();
   }

   final void onRun() {
      this.taskScreen = new TestBacklightScreen();
      super.app.pushScreen(this.taskScreen);
      this.isOn = Backlight.isEnabled();
      new EnforceForeground(this).start();
   }

   final void toggleBacklight() {
      if (Backlight.isEnabled()) {
         Backlight.enable(false);
      } else {
         Backlight.enable(true);
      }
   }

   @Override
   public final boolean keyChar(char key, int status, int time) {
      return true;
   }

   @Override
   public final boolean keyDown(int keycode, int time) {
      synchronized (this) {
         char key = (char)Keypad.key(keycode);
         if (key == 27 && this.taskScreen.isMenuOn) {
            this.taskScreen.isMenuOn = false;
            return false;
         }

         if (key != ' ') {
            if (this.isOn) {
               TestTaskBase.showMessage(DeviceSelfTestResources.getString(144));
            }
         } else {
            this.toggleBacklight();
         }

         this.isOn = Backlight.isEnabled();
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
