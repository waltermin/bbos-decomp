package net.rim.device.apps.internal.videorecorder;

final class VideoRecorderScreen$2 implements Runnable {
   private final VideoRecorderScreen this$0;

   VideoRecorderScreen$2(VideoRecorderScreen _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.IndexOutOfBoundsException: Index 2 out of bounds for length 1
      //   at java.base/jdk.internal.util.Preconditions.outOfBounds(Preconditions.java:100)
      //   at java.base/jdk.internal.util.Preconditions.outOfBoundsCheckIndex(Preconditions.java:106)
      //   at java.base/jdk.internal.util.Preconditions.checkIndex(Preconditions.java:302)
      //   at java.base/java.util.Objects.checkIndex(Objects.java:385)
      //   at java.base/java.util.ArrayList.remove(ArrayList.java:551)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.removeExceptionInstructionsEx(FinallyProcessor.java:1052)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.verifyFinallyEx(FinallyProcessor.java:502)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.iterateGraph(FinallyProcessor.java:90)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:185)
      //
      // Bytecode:
      // 00: aconst_null
      // 01: astore 1
      // 02: aload 0
      // 03: getfield net/rim/device/apps/internal/videorecorder/VideoRecorderScreen$2.this$0 Lnet/rim/device/apps/internal/videorecorder/VideoRecorderScreen;
      // 06: getfield net/rim/device/apps/internal/videorecorder/VideoRecorderScreen._vrc Lnet/rim/device/apps/internal/videorecorder/VideoRecordController;
      // 09: bipush 1
      // 0a: invokevirtual net/rim/device/apps/internal/videorecorder/VideoRecordController.getVideoFileName (Z)Ljava/lang/String;
      // 0d: astore 2
      // 0e: aload 2
      // 0f: invokestatic javax/microedition/io/Connector.open (Ljava/lang/String;)Ljavax/microedition/io/Connection;
      // 12: checkcast java/lang/Object
      // 15: astore 1
      // 16: aload 1
      // 17: invokeinterface javax/microedition/io/file/FileConnection.exists ()Z 1
      // 1c: ifeq 32
      // 1f: new java/lang/Object
      // 22: dup
      // 23: bipush 0
      // 24: i2l
      // 25: invokespecial net/rim/device/apps/internal/mediarecorder/RenderScreen.<init> (J)V
      // 28: astore 3
      // 29: aload 3
      // 2a: aload 1
      // 2b: invokevirtual net/rim/device/apps/internal/mediarecorder/RenderScreen.init (Ljavax/microedition/io/InputConnection;)V
      // 2e: aload 3
      // 2f: invokevirtual net/rim/device/apps/internal/mediarecorder/RenderScreen.doModal ()V
      // 32: aload 1
      // 33: ifnull 61
      // 36: aload 1
      // 37: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 3c: return
      // 3d: astore 2
      // 3e: return
      // 3f: astore 2
      // 40: aload 1
      // 41: ifnull 61
      // 44: aload 1
      // 45: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 4a: return
      // 4b: astore 2
      // 4c: return
      // 4d: astore 4
      // 4f: aload 1
      // 50: ifnull 5e
      // 53: aload 1
      // 54: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 59: goto 5e
      // 5c: astore 5
      // 5e: aload 4
      // 60: athrow
      // 61: return
      // try (28 -> 30): 31 null
      // try (2 -> 26): 33 null
      // try (36 -> 38): 39 null
      // try (2 -> 26): 41 null
      // try (33 -> 34): 41 null
      // try (44 -> 46): 47 null
      // try (41 -> 42): 41 null
   }
}
