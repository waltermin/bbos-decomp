package net.rim.device.apps.internal.deviceselftest;

import net.rim.device.api.system.Application;

final class TestTrackball extends TestTaskBase {
   TestTrackballScreen taskScreen = new TestTrackballScreen();
   private boolean isDialogOn;
   static final int MAX_ROW_NUMBER = 5;

   TestTrackball() {
      super.app.pushScreen(this.taskScreen);
      super.app.addKeyListener(this);
      this.isDialogOn = false;
   }

   private final void exitTest() {
      Application.getApplication().invokeLater(new TestTrackball$1(this));
   }

   @Override
   public final boolean keyChar(char key, int status, int time) {
      return false;
   }

   @Override
   public final boolean keyDown(int keycode, int time) {
      this.exitTest();
      return true;
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
      this.exitTest();
      return true;
   }
}
