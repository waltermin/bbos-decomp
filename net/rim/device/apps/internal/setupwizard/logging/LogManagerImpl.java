package net.rim.device.apps.internal.setupwizard.logging;

import net.rim.device.apps.api.setupwizard.Log;
import net.rim.device.apps.api.setupwizard.LogManager;

public final class LogManagerImpl implements LogManager {
   private StringBuffer _buffer = new StringBuffer();
   private static Object _lock = new Object();
   private static final long KEY_NAME = 6452391534420581228L;

   public final void save() {
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
      // 00: ldc_w "file:///store/home/user/"
      // 03: astore 1
      // 04: ldc_w "SetupWizardUsage.log"
      // 07: astore 2
      // 08: bipush 0
      // 09: istore 3
      // 0a: iload 3
      // 0b: bipush 20
      // 0d: if_icmplt 13
      // 10: goto eb
      // 13: new java/lang/StringBuffer
      // 16: dup
      // 17: invokespecial java/lang/StringBuffer.<init> ()V
      // 1a: aload 1
      // 1b: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 1e: aload 2
      // 1f: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 22: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 25: invokestatic javax/microedition/io/Connector.open (Ljava/lang/String;)Ljavax/microedition/io/Connection;
      // 28: checkcast javax/microedition/io/file/FileConnection
      // 2b: astore 4
      // 2d: aload 4
      // 2f: ifnonnull 33
      // 32: return
      // 33: bipush 0
      // 34: istore 5
      // 36: aload 4
      // 38: invokeinterface javax/microedition/io/file/FileConnection.exists ()Z 1
      // 3d: ifne 47
      // 40: aload 4
      // 42: invokeinterface javax/microedition/io/file/FileConnection.create ()V 1
      // 47: getstatic net/rim/device/apps/internal/setupwizard/logging/LogManagerImpl._lock Ljava/lang/Object;
      // 4a: dup
      // 4b: astore 6
      // 4d: monitorenter
      // 4e: aload 4
      // 50: ldc_w 2147483647
      // 53: i2l
      // 54: invokeinterface javax/microedition/io/file/FileConnection.openOutputStream (J)Ljava/io/OutputStream; 3
      // 59: astore 7
      // 5b: aload 0
      // 5c: getfield net/rim/device/apps/internal/setupwizard/logging/LogManagerImpl._buffer Ljava/lang/StringBuffer;
      // 5f: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 62: ldc_w "UTF-8"
      // 65: invokevirtual java/lang/String.getBytes (Ljava/lang/String;)[B
      // 68: astore 8
      // 6a: aload 7
      // 6c: aload 8
      // 6e: invokevirtual java/io/OutputStream.write ([B)V
      // 71: aload 7
      // 73: invokevirtual java/io/OutputStream.close ()V
      // 76: aload 0
      // 77: getfield net/rim/device/apps/internal/setupwizard/logging/LogManagerImpl._buffer Ljava/lang/StringBuffer;
      // 7a: bipush 0
      // 7b: invokevirtual java/lang/StringBuffer.setLength (I)V
      // 7e: aload 6
      // 80: monitorexit
      // 81: goto 8c
      // 84: astore 9
      // 86: aload 6
      // 88: monitorexit
      // 89: aload 9
      // 8b: athrow
      // 8c: bipush 1
      // 8d: istore 5
      // 8f: aload 4
      // 91: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 96: goto c0
      // 99: astore 6
      // 9b: goto c0
      // 9e: astore 6
      // a0: aload 4
      // a2: invokeinterface javax/microedition/io/Connection.close ()V 1
      // a7: goto c0
      // aa: astore 6
      // ac: goto c0
      // af: astore 10
      // b1: aload 4
      // b3: invokeinterface javax/microedition/io/Connection.close ()V 1
      // b8: goto bd
      // bb: astore 11
      // bd: aload 10
      // bf: athrow
      // c0: iload 5
      // c2: ifeq c6
      // c5: return
      // c6: new java/lang/StringBuffer
      // c9: dup
      // ca: ldc_w "SetupWizardUsage-"
      // cd: invokespecial java/lang/StringBuffer.<init> (Ljava/lang/String;)V
      // d0: iload 3
      // d1: bipush 1
      // d2: iadd
      // d3: invokevirtual java/lang/StringBuffer.append (I)Ljava/lang/StringBuffer;
      // d6: ldc_w ".log"
      // d9: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // dc: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // df: astore 2
      // e0: goto e5
      // e3: astore 4
      // e5: iinc 3 1
      // e8: goto 0a
      // eb: return
      // try (35 -> 57): 58 null
      // try (58 -> 61): 58 null
      // try (65 -> 67): 68 null
      // try (26 -> 65): 70 null
      // try (71 -> 73): 74 null
      // try (26 -> 65): 76 null
      // try (70 -> 71): 76 null
      // try (77 -> 79): 80 null
      // try (76 -> 77): 76 null
      // try (10 -> 23): 99 null
      // try (24 -> 85): 99 null
      // try (86 -> 98): 99 null
   }

   final void append(String text) {
      synchronized (_lock) {
         this._buffer.append(text);
         this._buffer.append("\n");
      }
   }

   @Override
   public final Log getCategory(String categoryName) {
      return new LogImpl(categoryName, this);
   }
}
