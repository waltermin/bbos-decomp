package net.rim.device.apps.internal.deviceselftest;

import net.rim.device.api.system.Application;
import net.rim.device.api.system.KeyListener;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.theme.ThemeManager;

public class TestTaskBase implements KeyListener {
   DeviceSelfTest app = (DeviceSelfTest)Application.getApplication();
   static Dialog d;

   TestTaskBase() {
   }

   @Override
   public boolean keyChar(char key, int status, int time) {
      return false;
   }

   @Override
   public boolean keyDown(int keycode, int time) {
      return false;
   }

   @Override
   public boolean keyUp(int keycode, int time) {
      return false;
   }

   @Override
   public boolean keyRepeat(int keycode, int time) {
      return false;
   }

   @Override
   public boolean keyStatus(int keycode, int time) {
      return false;
   }

   public static void showMessage(String msg) {
      if (d == null || !d.isVisible()) {
         d = (Dialog)(new Object(0, msg, 0, null, 33554432));
         d.setIcon(ThemeManager.getThemeAwareImage("dialog_exclamation"));
         Application.getApplication().invokeLater(new TestTaskBase$1());
      }
   }
}
