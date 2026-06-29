package net.rim.device.apps.internal.deviceselftest;

import net.rim.device.api.system.Application;
import net.rim.device.api.system.Audio;
import net.rim.device.api.system.AudioHeadsetListener;
import net.rim.device.api.ui.Keypad;

final class TestHeadsetDetect extends TestTaskBase implements AudioHeadsetListener {
   TestHeadsetDetectScreen taskScreen;
   static final int INTO;
   static final int OUTOF;
   static final int CLICK;
   static final int UNCLICK;

   TestHeadsetDetect() {
      super.app = (DeviceSelfTest)Application.getApplication();
      super.app.addKeyListener(this);
      Audio.addListener(super.app, this);
      this.onRun();
   }

   final void onRun() {
      this.taskScreen = new TestHeadsetDetectScreen();
      super.app.pushScreen(this.taskScreen);
   }

   @Override
   public final void headsetButtonClick(int button, int time) {
      this.taskScreen.insertReport(3);
   }

   @Override
   public final void headsetButtonUnclick(int button, int time) {
      this.taskScreen.insertReport(4);
   }

   @Override
   public final void headsetInserted(int type) {
      this.taskScreen.insertReport(1);
   }

   @Override
   public final void headsetRemoved() {
      this.taskScreen.insertReport(2);
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
}
