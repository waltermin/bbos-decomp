package net.rim.device.apps.internal.deviceselftest;

import javax.microedition.media.Player;
import javax.microedition.media.PlayerListener;

final class RecordNPlay extends Thread implements PlayerListener {
   TestMicrophone handler;

   RecordNPlay(TestMicrophone _handler) {
      this.handler = _handler;
   }

   @Override
   public final void run() {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: aload 0
      // 001: getfield net/rim/device/apps/internal/deviceselftest/RecordNPlay.handler Lnet/rim/device/apps/internal/deviceselftest/TestMicrophone;
      // 004: ldc_w "capture://audio"
      // 007: invokestatic javax/microedition/media/Manager.createPlayer (Ljava/lang/String;)Ljavax/microedition/media/Player;
      // 00a: putfield net/rim/device/apps/internal/deviceselftest/TestMicrophone.p Ljavax/microedition/media/Player;
      // 00d: aload 0
      // 00e: getfield net/rim/device/apps/internal/deviceselftest/RecordNPlay.handler Lnet/rim/device/apps/internal/deviceselftest/TestMicrophone;
      // 011: getfield net/rim/device/apps/internal/deviceselftest/TestMicrophone.p Ljavax/microedition/media/Player;
      // 014: aload 0
      // 015: invokeinterface javax/microedition/media/Player.addPlayerListener (Ljavax/microedition/media/PlayerListener;)V 2
      // 01a: aload 0
      // 01b: getfield net/rim/device/apps/internal/deviceselftest/RecordNPlay.handler Lnet/rim/device/apps/internal/deviceselftest/TestMicrophone;
      // 01e: getfield net/rim/device/apps/internal/deviceselftest/TestMicrophone.p Ljavax/microedition/media/Player;
      // 021: invokeinterface javax/microedition/media/Player.realize ()V 1
      // 026: aload 0
      // 027: getfield net/rim/device/apps/internal/deviceselftest/RecordNPlay.handler Lnet/rim/device/apps/internal/deviceselftest/TestMicrophone;
      // 02a: aload 0
      // 02b: getfield net/rim/device/apps/internal/deviceselftest/RecordNPlay.handler Lnet/rim/device/apps/internal/deviceselftest/TestMicrophone;
      // 02e: getfield net/rim/device/apps/internal/deviceselftest/TestMicrophone.p Ljavax/microedition/media/Player;
      // 031: ldc_w "net.rim.device.api.media.control.AudioPathControl"
      // 034: invokeinterface javax/microedition/media/Controllable.getControl (Ljava/lang/String;)Ljavax/microedition/media/Control; 2
      // 039: checkcast net/rim/device/api/media/control/AudioPathControl
      // 03c: putfield net/rim/device/apps/internal/deviceselftest/TestMicrophone.apc Lnet/rim/device/api/media/control/AudioPathControl;
      // 03f: aload 0
      // 040: getfield net/rim/device/apps/internal/deviceselftest/RecordNPlay.handler Lnet/rim/device/apps/internal/deviceselftest/TestMicrophone;
      // 043: getfield net/rim/device/apps/internal/deviceselftest/TestMicrophone.microphoneType I
      // 046: bipush 2
      // 048: if_icmpne 07f
      // 04b: invokestatic net/rim/device/internal/bluetooth/BluetoothME.isAnyDeviceConnected ()Z
      // 04e: ifne 060
      // 051: aload 0
      // 052: getfield net/rim/device/apps/internal/deviceselftest/RecordNPlay.handler Lnet/rim/device/apps/internal/deviceselftest/TestMicrophone;
      // 055: pop
      // 056: sipush 128
      // 059: invokestatic net/rim/device/apps/internal/deviceselftest/DeviceSelfTestResources.getString (I)Ljava/lang/String;
      // 05c: invokestatic net/rim/device/apps/internal/deviceselftest/TestTaskBase.showMessage (Ljava/lang/String;)V
      // 05f: return
      // 060: aload 0
      // 061: getfield net/rim/device/apps/internal/deviceselftest/RecordNPlay.handler Lnet/rim/device/apps/internal/deviceselftest/TestMicrophone;
      // 064: getfield net/rim/device/apps/internal/deviceselftest/TestMicrophone.apc Lnet/rim/device/api/media/control/AudioPathControl;
      // 067: invokeinterface net/rim/device/api/media/control/AudioPathControl.getAudioPath ()I 1
      // 06c: bipush 2
      // 06e: if_icmpeq 07f
      // 071: aload 0
      // 072: getfield net/rim/device/apps/internal/deviceselftest/RecordNPlay.handler Lnet/rim/device/apps/internal/deviceselftest/TestMicrophone;
      // 075: getfield net/rim/device/apps/internal/deviceselftest/TestMicrophone.apc Lnet/rim/device/api/media/control/AudioPathControl;
      // 078: bipush 2
      // 07a: invokeinterface net/rim/device/api/media/control/AudioPathControl.setAudioPath (I)V 2
      // 07f: aload 0
      // 080: getfield net/rim/device/apps/internal/deviceselftest/RecordNPlay.handler Lnet/rim/device/apps/internal/deviceselftest/TestMicrophone;
      // 083: getfield net/rim/device/apps/internal/deviceselftest/TestMicrophone.microphoneType I
      // 086: bipush 1
      // 087: if_icmpne 0c8
      // 08a: aload 0
      // 08b: getfield net/rim/device/apps/internal/deviceselftest/RecordNPlay.handler Lnet/rim/device/apps/internal/deviceselftest/TestMicrophone;
      // 08e: getfield net/rim/device/apps/internal/deviceselftest/TestMicrophone.apc Lnet/rim/device/api/media/control/AudioPathControl;
      // 091: invokeinterface net/rim/device/api/media/control/AudioPathControl.getAudioPath ()I 1
      // 096: bipush 3
      // 098: if_icmpeq 0c8
      // 09b: aload 0
      // 09c: getfield net/rim/device/apps/internal/deviceselftest/RecordNPlay.handler Lnet/rim/device/apps/internal/deviceselftest/TestMicrophone;
      // 09f: getfield net/rim/device/apps/internal/deviceselftest/TestMicrophone.apc Lnet/rim/device/api/media/control/AudioPathControl;
      // 0a2: bipush 3
      // 0a4: invokeinterface net/rim/device/api/media/control/AudioPathControl.canSwitchToPath (I)Z 2
      // 0a9: ifne 0ba
      // 0ac: aload 0
      // 0ad: getfield net/rim/device/apps/internal/deviceselftest/RecordNPlay.handler Lnet/rim/device/apps/internal/deviceselftest/TestMicrophone;
      // 0b0: pop
      // 0b1: bipush 127
      // 0b3: invokestatic net/rim/device/apps/internal/deviceselftest/DeviceSelfTestResources.getString (I)Ljava/lang/String;
      // 0b6: invokestatic net/rim/device/apps/internal/deviceselftest/TestTaskBase.showMessage (Ljava/lang/String;)V
      // 0b9: return
      // 0ba: aload 0
      // 0bb: getfield net/rim/device/apps/internal/deviceselftest/RecordNPlay.handler Lnet/rim/device/apps/internal/deviceselftest/TestMicrophone;
      // 0be: getfield net/rim/device/apps/internal/deviceselftest/TestMicrophone.apc Lnet/rim/device/api/media/control/AudioPathControl;
      // 0c1: bipush 3
      // 0c3: invokeinterface net/rim/device/api/media/control/AudioPathControl.setAudioPath (I)V 2
      // 0c8: aload 0
      // 0c9: getfield net/rim/device/apps/internal/deviceselftest/RecordNPlay.handler Lnet/rim/device/apps/internal/deviceselftest/TestMicrophone;
      // 0cc: getfield net/rim/device/apps/internal/deviceselftest/TestMicrophone.microphoneType I
      // 0cf: ifne 0fe
      // 0d2: aload 0
      // 0d3: getfield net/rim/device/apps/internal/deviceselftest/RecordNPlay.handler Lnet/rim/device/apps/internal/deviceselftest/TestMicrophone;
      // 0d6: getfield net/rim/device/apps/internal/deviceselftest/TestMicrophone.apc Lnet/rim/device/api/media/control/AudioPathControl;
      // 0d9: bipush 0
      // 0da: invokeinterface net/rim/device/api/media/control/AudioPathControl.canSwitchToPath (I)Z 2
      // 0df: ifne 0f1
      // 0e2: aload 0
      // 0e3: getfield net/rim/device/apps/internal/deviceselftest/RecordNPlay.handler Lnet/rim/device/apps/internal/deviceselftest/TestMicrophone;
      // 0e6: pop
      // 0e7: sipush 129
      // 0ea: invokestatic net/rim/device/apps/internal/deviceselftest/DeviceSelfTestResources.getString (I)Ljava/lang/String;
      // 0ed: invokestatic net/rim/device/apps/internal/deviceselftest/TestTaskBase.showMessage (Ljava/lang/String;)V
      // 0f0: return
      // 0f1: aload 0
      // 0f2: getfield net/rim/device/apps/internal/deviceselftest/RecordNPlay.handler Lnet/rim/device/apps/internal/deviceselftest/TestMicrophone;
      // 0f5: getfield net/rim/device/apps/internal/deviceselftest/TestMicrophone.apc Lnet/rim/device/api/media/control/AudioPathControl;
      // 0f8: bipush 1
      // 0f9: invokeinterface net/rim/device/api/media/control/AudioPathControl.setAudioPath (I)V 2
      // 0fe: aload 0
      // 0ff: getfield net/rim/device/apps/internal/deviceselftest/RecordNPlay.handler Lnet/rim/device/apps/internal/deviceselftest/TestMicrophone;
      // 102: pop
      // 103: bipush 1
      // 104: putstatic net/rim/device/apps/internal/deviceselftest/TestMicrophone.isOn Z
      // 107: aload 0
      // 108: getfield net/rim/device/apps/internal/deviceselftest/RecordNPlay.handler Lnet/rim/device/apps/internal/deviceselftest/TestMicrophone;
      // 10b: pop
      // 10c: bipush 0
      // 10d: putstatic net/rim/device/apps/internal/deviceselftest/TestMicrophone.isPlayerOn Z
      // 110: aload 0
      // 111: getfield net/rim/device/apps/internal/deviceselftest/RecordNPlay.handler Lnet/rim/device/apps/internal/deviceselftest/TestMicrophone;
      // 114: getfield net/rim/device/apps/internal/deviceselftest/TestMicrophone.p Ljavax/microedition/media/Player;
      // 117: ldc_w "RecordControl"
      // 11a: invokeinterface javax/microedition/media/Controllable.getControl (Ljava/lang/String;)Ljavax/microedition/media/Control; 2
      // 11f: checkcast javax/microedition/media/control/RecordControl
      // 122: astore 1
      // 123: aload 1
      // 124: ifnonnull 12a
      // 127: goto 20b
      // 12a: new java/io/ByteArrayOutputStream
      // 12d: dup
      // 12e: invokespecial java/io/ByteArrayOutputStream.<init> ()V
      // 131: astore 2
      // 132: aload 1
      // 133: aload 2
      // 134: invokeinterface javax/microedition/media/control/RecordControl.setRecordStream (Ljava/io/OutputStream;)V 2
      // 139: aload 1
      // 13a: invokeinterface javax/microedition/media/control/RecordControl.startRecord ()V 1
      // 13f: aload 0
      // 140: getfield net/rim/device/apps/internal/deviceselftest/RecordNPlay.handler Lnet/rim/device/apps/internal/deviceselftest/TestMicrophone;
      // 143: getfield net/rim/device/apps/internal/deviceselftest/TestMicrophone.p Ljavax/microedition/media/Player;
      // 146: invokeinterface javax/microedition/media/Player.start ()V 1
      // 14b: invokestatic java/lang/Thread.currentThread ()Ljava/lang/Thread;
      // 14e: pop
      // 14f: sipush 5000
      // 152: i2l
      // 153: invokestatic java/lang/Thread.sleep (J)V
      // 156: aload 0
      // 157: getfield net/rim/device/apps/internal/deviceselftest/RecordNPlay.handler Lnet/rim/device/apps/internal/deviceselftest/TestMicrophone;
      // 15a: getfield net/rim/device/apps/internal/deviceselftest/TestMicrophone.p Ljavax/microedition/media/Player;
      // 15d: aload 0
      // 15e: invokeinterface javax/microedition/media/Player.removePlayerListener (Ljavax/microedition/media/PlayerListener;)V 2
      // 163: aload 1
      // 164: invokeinterface javax/microedition/media/control/RecordControl.commit ()V 1
      // 169: aload 0
      // 16a: getfield net/rim/device/apps/internal/deviceselftest/RecordNPlay.handler Lnet/rim/device/apps/internal/deviceselftest/TestMicrophone;
      // 16d: getfield net/rim/device/apps/internal/deviceselftest/TestMicrophone.p Ljavax/microedition/media/Player;
      // 170: invokeinterface javax/microedition/media/Player.close ()V 1
      // 175: aload 0
      // 176: getfield net/rim/device/apps/internal/deviceselftest/RecordNPlay.handler Lnet/rim/device/apps/internal/deviceselftest/TestMicrophone;
      // 179: pop
      // 17a: getstatic net/rim/device/apps/internal/deviceselftest/TestMicrophone.isSkipped Z
      // 17d: ifeq 181
      // 180: return
      // 181: aload 2
      // 182: invokevirtual java/io/ByteArrayOutputStream.toByteArray ()[B
      // 185: astore 3
      // 186: new java/io/ByteArrayInputStream
      // 189: dup
      // 18a: aload 3
      // 18b: bipush 0
      // 18c: aload 3
      // 18d: arraylength
      // 18e: invokespecial java/io/ByteArrayInputStream.<init> ([BII)V
      // 191: astore 4
      // 193: bipush 1
      // 194: putstatic net/rim/device/apps/internal/deviceselftest/TestMicrophone.isPlayerOn Z
      // 197: aload 0
      // 198: getfield net/rim/device/apps/internal/deviceselftest/RecordNPlay.handler Lnet/rim/device/apps/internal/deviceselftest/TestMicrophone;
      // 19b: aload 4
      // 19d: ldc_w "audio/x-wav"
      // 1a0: invokestatic javax/microedition/media/Manager.createPlayer (Ljava/io/InputStream;Ljava/lang/String;)Ljavax/microedition/media/Player;
      // 1a3: putfield net/rim/device/apps/internal/deviceselftest/TestMicrophone.p Ljavax/microedition/media/Player;
      // 1a6: aload 0
      // 1a7: getfield net/rim/device/apps/internal/deviceselftest/RecordNPlay.handler Lnet/rim/device/apps/internal/deviceselftest/TestMicrophone;
      // 1aa: getfield net/rim/device/apps/internal/deviceselftest/TestMicrophone.p Ljavax/microedition/media/Player;
      // 1ad: aload 0
      // 1ae: invokeinterface javax/microedition/media/Player.addPlayerListener (Ljavax/microedition/media/PlayerListener;)V 2
      // 1b3: aload 0
      // 1b4: getfield net/rim/device/apps/internal/deviceselftest/RecordNPlay.handler Lnet/rim/device/apps/internal/deviceselftest/TestMicrophone;
      // 1b7: getfield net/rim/device/apps/internal/deviceselftest/TestMicrophone.p Ljavax/microedition/media/Player;
      // 1ba: invokeinterface javax/microedition/media/Player.realize ()V 1
      // 1bf: aload 0
      // 1c0: getfield net/rim/device/apps/internal/deviceselftest/RecordNPlay.handler Lnet/rim/device/apps/internal/deviceselftest/TestMicrophone;
      // 1c3: getfield net/rim/device/apps/internal/deviceselftest/TestMicrophone.microphoneType I
      // 1c6: bipush 2
      // 1c8: if_icmpne 1fc
      // 1cb: aload 0
      // 1cc: getfield net/rim/device/apps/internal/deviceselftest/RecordNPlay.handler Lnet/rim/device/apps/internal/deviceselftest/TestMicrophone;
      // 1cf: aload 0
      // 1d0: getfield net/rim/device/apps/internal/deviceselftest/RecordNPlay.handler Lnet/rim/device/apps/internal/deviceselftest/TestMicrophone;
      // 1d3: getfield net/rim/device/apps/internal/deviceselftest/TestMicrophone.p Ljavax/microedition/media/Player;
      // 1d6: ldc_w "net.rim.device.api.media.control.AudioPathControl"
      // 1d9: invokeinterface javax/microedition/media/Controllable.getControl (Ljava/lang/String;)Ljavax/microedition/media/Control; 2
      // 1de: checkcast net/rim/device/api/media/control/AudioPathControl
      // 1e1: putfield net/rim/device/apps/internal/deviceselftest/TestMicrophone.apc Lnet/rim/device/api/media/control/AudioPathControl;
      // 1e4: aload 0
      // 1e5: getfield net/rim/device/apps/internal/deviceselftest/RecordNPlay.handler Lnet/rim/device/apps/internal/deviceselftest/TestMicrophone;
      // 1e8: getfield net/rim/device/apps/internal/deviceselftest/TestMicrophone.apc Lnet/rim/device/api/media/control/AudioPathControl;
      // 1eb: ifnull 1fc
      // 1ee: aload 0
      // 1ef: getfield net/rim/device/apps/internal/deviceselftest/RecordNPlay.handler Lnet/rim/device/apps/internal/deviceselftest/TestMicrophone;
      // 1f2: getfield net/rim/device/apps/internal/deviceselftest/TestMicrophone.apc Lnet/rim/device/api/media/control/AudioPathControl;
      // 1f5: bipush 2
      // 1f7: invokeinterface net/rim/device/api/media/control/AudioPathControl.setAudioPath (I)V 2
      // 1fc: aload 0
      // 1fd: getfield net/rim/device/apps/internal/deviceselftest/RecordNPlay.handler Lnet/rim/device/apps/internal/deviceselftest/TestMicrophone;
      // 200: getfield net/rim/device/apps/internal/deviceselftest/TestMicrophone.p Ljavax/microedition/media/Player;
      // 203: invokeinterface javax/microedition/media/Player.start ()V 1
      // 208: goto 24b
      // 20b: aload 0
      // 20c: getfield net/rim/device/apps/internal/deviceselftest/RecordNPlay.handler Lnet/rim/device/apps/internal/deviceselftest/TestMicrophone;
      // 20f: getfield net/rim/device/apps/internal/deviceselftest/TestMicrophone.p Ljavax/microedition/media/Player;
      // 212: aload 0
      // 213: invokeinterface javax/microedition/media/Player.removePlayerListener (Ljavax/microedition/media/PlayerListener;)V 2
      // 218: aload 0
      // 219: getfield net/rim/device/apps/internal/deviceselftest/RecordNPlay.handler Lnet/rim/device/apps/internal/deviceselftest/TestMicrophone;
      // 21c: getfield net/rim/device/apps/internal/deviceselftest/TestMicrophone.p Ljavax/microedition/media/Player;
      // 21f: invokeinterface javax/microedition/media/Player.close ()V 1
      // 224: return
      // 225: astore 1
      // 226: getstatic java/lang/System.out Ljava/io/PrintStream;
      // 229: aload 1
      // 22a: invokevirtual java/io/PrintStream.println (Ljava/lang/Object;)V
      // 22d: aload 1
      // 22e: invokevirtual java/lang/Throwable.printStackTrace ()V
      // 231: return
      // 232: astore 1
      // 233: getstatic java/lang/System.out Ljava/io/PrintStream;
      // 236: aload 1
      // 237: invokevirtual java/io/PrintStream.println (Ljava/lang/Object;)V
      // 23a: aload 1
      // 23b: invokevirtual java/lang/Throwable.printStackTrace ()V
      // 23e: return
      // 23f: astore 1
      // 240: getstatic java/lang/System.out Ljava/io/PrintStream;
      // 243: aload 1
      // 244: invokevirtual java/io/PrintStream.println (Ljava/lang/Object;)V
      // 247: aload 1
      // 248: invokevirtual java/lang/Throwable.printStackTrace ()V
      // 24b: return
      // try (0 -> 36): 220 null
      // try (37 -> 71): 220 null
      // try (72 -> 93): 220 null
      // try (94 -> 153): 220 null
      // try (154 -> 219): 220 null
      // try (0 -> 36): 227 null
      // try (37 -> 71): 227 null
      // try (72 -> 93): 227 null
      // try (94 -> 153): 227 null
      // try (154 -> 219): 227 null
      // try (0 -> 36): 234 null
      // try (37 -> 71): 234 null
      // try (72 -> 93): 234 null
      // try (94 -> 153): 234 null
      // try (154 -> 219): 234 null
   }

   @Override
   public final void playerUpdate(Player player, String event, Object eventData) {
      if (player == this.handler.p) {
         if (event.equals("recordStarted")) {
            this.handler.taskScreen.updateState(DeviceSelfTestResources.getString(135));
            return;
         }

         if (event.equals("started")) {
            if (TestMicrophone.isPlayerOn) {
               this.handler.taskScreen.updateState(DeviceSelfTestResources.getString(136));
               return;
            }
         } else {
            if (event.equals("recordError")) {
               this.handler.taskScreen.updateState(DeviceSelfTestResources.getString(137));
               return;
            }

            if (event.equals("endOfMedia")) {
               this.handler.taskScreen.updateState(DeviceSelfTestResources.getString(134));
               TestMicrophone.isOn = false;
            }
         }
      }
   }
}
