package net.rim.device.apps.internal.deviceselftest;

import java.io.InputStream;
import javax.microedition.media.Player;
import net.rim.device.api.media.control.AudioPathControl;
import net.rim.device.api.system.KeyListener;
import net.rim.device.api.ui.Keypad;

class TestSpeaker extends TestTaskBase implements KeyListener {
   Class clazz;
   TestSpeakerScreen taskScreen;
   int speakerType;
   int cnt;
   InputStream is;
   Player p;
   AudioPathControl apc;
   int lastStatusKeyTS;
   String[] amrArray = new String[]{"/400.amr", "/630.amr", "/1000.amr", "/1600.amr", "/3000.amr"};
   static final int HANDSET;
   static final int HANDSFREE;
   static final int HEADSET;
   static final int BLUETOOTH;

   TestSpeaker() {
      super.app.addKeyListener(this);
      this.cnt = 0;
      this.lastStatusKeyTS = 0;

      try {
         this.clazz = Class.forName("net.rim.device.apps.internal.deviceselftest.DeviceSelfTest");
      } finally {
         return;
      }
   }

   void showScreen() {
      this.taskScreen = new TestSpeakerScreen(this.speakerType);
      super.app.pushScreen(this.taskScreen);
   }

   synchronized void playTone() {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: aload 0
      // 001: aload 0
      // 002: getfield net/rim/device/apps/internal/deviceselftest/TestSpeaker.clazz Ljava/lang/Class;
      // 005: aload 0
      // 006: getfield net/rim/device/apps/internal/deviceselftest/TestSpeaker.amrArray [Ljava/lang/String;
      // 009: aload 0
      // 00a: getfield net/rim/device/apps/internal/deviceselftest/TestSpeaker.cnt I
      // 00d: aload 0
      // 00e: getfield net/rim/device/apps/internal/deviceselftest/TestSpeaker.amrArray [Ljava/lang/String;
      // 011: arraylength
      // 012: irem
      // 013: aaload
      // 014: invokevirtual java/lang/Class.getResourceAsStream (Ljava/lang/String;)Ljava/io/InputStream;
      // 017: putfield net/rim/device/apps/internal/deviceselftest/TestSpeaker.is Ljava/io/InputStream;
      // 01a: aload 0
      // 01b: aload 0
      // 01c: getfield net/rim/device/apps/internal/deviceselftest/TestSpeaker.is Ljava/io/InputStream;
      // 01f: ldc_w "audio/amr"
      // 022: invokestatic javax/microedition/media/Manager.createPlayer (Ljava/io/InputStream;Ljava/lang/String;)Ljavax/microedition/media/Player;
      // 025: putfield net/rim/device/apps/internal/deviceselftest/TestSpeaker.p Ljavax/microedition/media/Player;
      // 028: aload 0
      // 029: getfield net/rim/device/apps/internal/deviceselftest/TestSpeaker.p Ljavax/microedition/media/Player;
      // 02c: invokeinterface javax/microedition/media/Player.realize ()V 1
      // 031: aload 0
      // 032: aload 0
      // 033: getfield net/rim/device/apps/internal/deviceselftest/TestSpeaker.p Ljavax/microedition/media/Player;
      // 036: ldc_w "net.rim.device.api.media.control.AudioPathControl"
      // 039: invokeinterface javax/microedition/media/Controllable.getControl (Ljava/lang/String;)Ljavax/microedition/media/Control; 2
      // 03e: checkcast java/lang/Object
      // 041: putfield net/rim/device/apps/internal/deviceselftest/TestSpeaker.apc Lnet/rim/device/api/media/control/AudioPathControl;
      // 044: aload 0
      // 045: getfield net/rim/device/apps/internal/deviceselftest/TestSpeaker.speakerType I
      // 048: ifne 067
      // 04b: aload 0
      // 04c: getfield net/rim/device/apps/internal/deviceselftest/TestSpeaker.apc Lnet/rim/device/api/media/control/AudioPathControl;
      // 04f: invokeinterface net/rim/device/api/media/control/AudioPathControl.getAudioPath ()I 1
      // 054: ifne 05a
      // 057: goto 0f7
      // 05a: aload 0
      // 05b: getfield net/rim/device/apps/internal/deviceselftest/TestSpeaker.apc Lnet/rim/device/api/media/control/AudioPathControl;
      // 05e: bipush 0
      // 05f: invokeinterface net/rim/device/api/media/control/AudioPathControl.setAudioPath (I)V 2
      // 064: goto 0f7
      // 067: aload 0
      // 068: getfield net/rim/device/apps/internal/deviceselftest/TestSpeaker.speakerType I
      // 06b: bipush 1
      // 06c: if_icmpne 089
      // 06f: aload 0
      // 070: getfield net/rim/device/apps/internal/deviceselftest/TestSpeaker.apc Lnet/rim/device/api/media/control/AudioPathControl;
      // 073: invokeinterface net/rim/device/api/media/control/AudioPathControl.getAudioPath ()I 1
      // 078: bipush 1
      // 079: if_icmpeq 0f7
      // 07c: aload 0
      // 07d: getfield net/rim/device/apps/internal/deviceselftest/TestSpeaker.apc Lnet/rim/device/api/media/control/AudioPathControl;
      // 080: bipush 1
      // 081: invokeinterface net/rim/device/api/media/control/AudioPathControl.setAudioPath (I)V 2
      // 086: goto 0f7
      // 089: aload 0
      // 08a: getfield net/rim/device/apps/internal/deviceselftest/TestSpeaker.speakerType I
      // 08d: bipush 2
      // 08f: if_icmpne 0c5
      // 092: aload 0
      // 093: getfield net/rim/device/apps/internal/deviceselftest/TestSpeaker.apc Lnet/rim/device/api/media/control/AudioPathControl;
      // 096: invokeinterface net/rim/device/api/media/control/AudioPathControl.getAudioPath ()I 1
      // 09b: bipush 3
      // 09d: if_icmpeq 0f7
      // 0a0: aload 0
      // 0a1: getfield net/rim/device/apps/internal/deviceselftest/TestSpeaker.apc Lnet/rim/device/api/media/control/AudioPathControl;
      // 0a4: bipush 3
      // 0a6: invokeinterface net/rim/device/api/media/control/AudioPathControl.canSwitchToPath (I)Z 2
      // 0ab: ifne 0b7
      // 0ae: bipush 127
      // 0b0: invokestatic net/rim/device/apps/internal/deviceselftest/DeviceSelfTestResources.getString (I)Ljava/lang/String;
      // 0b3: invokestatic net/rim/device/apps/internal/deviceselftest/TestTaskBase.showMessage (Ljava/lang/String;)V
      // 0b6: return
      // 0b7: aload 0
      // 0b8: getfield net/rim/device/apps/internal/deviceselftest/TestSpeaker.apc Lnet/rim/device/api/media/control/AudioPathControl;
      // 0bb: bipush 3
      // 0bd: invokeinterface net/rim/device/api/media/control/AudioPathControl.setAudioPath (I)V 2
      // 0c2: goto 0f7
      // 0c5: aload 0
      // 0c6: getfield net/rim/device/apps/internal/deviceselftest/TestSpeaker.speakerType I
      // 0c9: bipush 3
      // 0cb: if_icmpne 0f7
      // 0ce: invokestatic net/rim/device/internal/bluetooth/BluetoothME.isAnyDeviceConnected ()Z
      // 0d1: ifne 0de
      // 0d4: sipush 128
      // 0d7: invokestatic net/rim/device/apps/internal/deviceselftest/DeviceSelfTestResources.getString (I)Ljava/lang/String;
      // 0da: invokestatic net/rim/device/apps/internal/deviceselftest/TestTaskBase.showMessage (Ljava/lang/String;)V
      // 0dd: return
      // 0de: aload 0
      // 0df: getfield net/rim/device/apps/internal/deviceselftest/TestSpeaker.apc Lnet/rim/device/api/media/control/AudioPathControl;
      // 0e2: invokeinterface net/rim/device/api/media/control/AudioPathControl.getAudioPath ()I 1
      // 0e7: bipush 2
      // 0e9: if_icmpeq 0f7
      // 0ec: aload 0
      // 0ed: getfield net/rim/device/apps/internal/deviceselftest/TestSpeaker.apc Lnet/rim/device/api/media/control/AudioPathControl;
      // 0f0: bipush 2
      // 0f2: invokeinterface net/rim/device/api/media/control/AudioPathControl.setAudioPath (I)V 2
      // 0f7: aload 0
      // 0f8: getfield net/rim/device/apps/internal/deviceselftest/TestSpeaker.p Ljavax/microedition/media/Player;
      // 0fb: invokeinterface javax/microedition/media/Player.start ()V 1
      // 100: aload 0
      // 101: aload 0
      // 102: getfield net/rim/device/apps/internal/deviceselftest/TestSpeaker.cnt I
      // 105: bipush 1
      // 106: iadd
      // 107: putfield net/rim/device/apps/internal/deviceselftest/TestSpeaker.cnt I
      // 10a: return
      // 10b: astore 1
      // 10c: getstatic java/lang/System.out Ljava/io/PrintStream;
      // 10f: aload 1
      // 110: invokevirtual java/io/PrintStream.println (Ljava/lang/Object;)V
      // 113: aload 1
      // 114: invokevirtual java/lang/Throwable.printStackTrace ()V
      // 117: return
      // 118: astore 1
      // 119: getstatic java/lang/System.out Ljava/io/PrintStream;
      // 11c: aload 1
      // 11d: invokevirtual java/io/PrintStream.println (Ljava/lang/Object;)V
      // 120: aload 1
      // 121: invokevirtual java/lang/Throwable.printStackTrace ()V
      // 124: return
      // 125: astore 1
      // 126: getstatic java/lang/System.out Ljava/io/PrintStream;
      // 129: aload 1
      // 12a: invokevirtual java/io/PrintStream.println (Ljava/lang/Object;)V
      // 12d: aload 1
      // 12e: invokevirtual java/lang/Throwable.printStackTrace ()V
      // 131: return
      // try (0 -> 74): 109 null
      // try (75 -> 89): 109 null
      // try (90 -> 108): 109 null
      // try (0 -> 74): 116 null
      // try (75 -> 89): 116 null
      // try (90 -> 108): 116 null
      // try (0 -> 74): 123 null
      // try (75 -> 89): 123 null
      // try (90 -> 108): 123 null
   }

   @Override
   public boolean keyDown(int keycode, int time) {
      char key = (char)Keypad.key(keycode);
      if (key == 27 && this.taskScreen.isMenuOn) {
         this.taskScreen.isMenuOn = false;
         return false;
      } else {
         this.playTone();
         return true;
      }
   }

   @Override
   public boolean keyStatus(int keycode, int time) {
      if (time - this.lastStatusKeyTS > 250) {
         this.playTone();
      }

      this.lastStatusKeyTS = time;
      return true;
   }
}
