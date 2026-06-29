package net.rim.device.apps.internal.deviceselftest;

import java.util.Timer;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.Phone;
import net.rim.device.api.ui.Keypad;

final class TestRFAntenna extends TestTaskBase {
   TestRFAntennaScreen taskScreen;
   Timer timer;
   TimeoutAction timeoutAction;
   TestRFAntenna$MyPhoneListener phoneListener;
   int processId;
   static final String DEFAULT_PHONE_NO = "15198887465";
   static final int SAMPLING_PERIOD = 5000;

   TestRFAntenna() {
      super.app = (DeviceSelfTest)Application.getApplication();
      this.processId = super.app.getProcessId();
      super.app.addKeyListener(this);
      this.onRun();
   }

   final void onRun() {
      this.taskScreen = new TestRFAntennaScreen(this);
      super.app.pushScreen(this.taskScreen);
      this.phoneListener = new TestRFAntenna$MyPhoneListener(this);
      super.app.addRadioListener(this.phoneListener);
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   final void doCall() {
      try {
         Phone.getInstance().startCall(this.taskScreen._phoneInput.getText(), 41782781);
      } catch (Throwable var3) {
         System.out.println(e);
         return;
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   final void doHangup() {
      if (this.timer != null) {
         this.timer.cancel();
      }

      if (Phone.isPhoneActive()) {
         try {
            Phone.getInstance().stopAllCalls(true);
         } catch (Throwable var3) {
            System.out.println(e);
            return;
         }
      }
   }

   @Override
   public final boolean keyDown(int keycode, int time) {
      char key = (char)Keypad.key(keycode);
      switch (key) {
         case '\u0011':
            if (ApplicationManager.getApplicationManager().getForegroundProcessId() != this.processId) {
               return false;
            } else if (this.taskScreen.isMenuOn) {
               return true;
            } else {
               if (Phone.isPhoneActive()) {
                  return true;
               }

               this.doCall();
               return true;
            }
         case '\u0012':
            if (ApplicationManager.getApplicationManager().getForegroundProcessId() != this.processId) {
               return false;
            } else {
               if (Phone.isPhoneActive()) {
                  return false;
               }

               return true;
            }
         case '\u001b':
            if (ApplicationManager.getApplicationManager().getForegroundProcessId() != this.processId) {
               return false;
            } else {
               if (this.taskScreen.isMenuOn) {
                  this.taskScreen.isMenuOn = false;
                  return false;
               }

               return true;
            }
         case 'ဂ':
            if (ApplicationManager.getApplicationManager().getForegroundProcessId() != this.processId) {
               return false;
            }

            return true;
         default:
            return false;
      }
   }

   @Override
   public final boolean keyChar(char key, int status, int time) {
      return false;
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
