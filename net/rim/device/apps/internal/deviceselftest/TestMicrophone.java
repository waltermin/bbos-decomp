package net.rim.device.apps.internal.deviceselftest;

import javax.microedition.media.Player;
import net.rim.device.api.media.control.AudioPathControl;
import net.rim.device.api.system.KeyListener;
import net.rim.device.api.ui.Keypad;

class TestMicrophone extends TestTaskBase implements KeyListener {
   TestMicrophoneScreen taskScreen;
   int microphoneType;
   AudioPathControl apc;
   Player p;
   static final int RECORDING_TIME_LENGTH;
   static final int STOP;
   static final int RECORDING;
   static final int PLAYING;
   static final int HANDSET_MICROPHONE;
   static final int HEADSET_MICROPHONE;
   static final int BLUETOOTH_MICROPHONE;
   static boolean isOn;
   static boolean isPlayerOn;
   static boolean isSkipped;

   TestMicrophone() {
      super.app.addKeyListener(this);
      isOn = false;
      isSkipped = false;
   }

   void showScreen() {
      this.taskScreen = new TestMicrophoneScreen(this, this.microphoneType);
      super.app.pushScreen(this.taskScreen);
   }

   void recordAndPlay() {
      if (!isOn) {
         new RecordNPlay(this).start();
      }
   }

   @Override
   public boolean keyDown(int keycode, int time) {
      char key = (char)Keypad.key(keycode);
      if (key == 27 && this.taskScreen.isMenuOn) {
         this.taskScreen.isMenuOn = false;
         return false;
      } else {
         this.recordAndPlay();
         return true;
      }
   }

   @Override
   public boolean keyStatus(int keycode, int time) {
      this.recordAndPlay();
      return true;
   }
}
