package net.rim.wica.runtime.diagnostics.impl;

import net.rim.vm.DebugSupport;

final class SimulatorDebugServer implements Runnable {
   private DebugHandler _handler;
   private int _port = 5555;
   private static final boolean DEBUG = false;
   private static final String DEBUG_ENV_VAR = "MdsDebug";
   private static final String PORT_ENV_VAR = "MdsDebugPort";
   private static final String ACTIVATION_ENV_VAR = "MdsActivationUrl";
   private static final String DEBUG_ENV_VAR_ON = "on";
   private static final boolean USE_DIRECT_TCP = true;
   private static final int DEFAULT_TARGET_PORT = 5555;
   private static final int DEFAULT_DEBUGGER_PORT = 45654;

   SimulatorDebugServer(DebugHandler handler) {
      this._handler = handler;
   }

   final boolean start() {
      boolean result = false;
      String debug = DebugSupport.getenv("MdsDebug");
      if (debug != null && debug.equals("on")) {
         String portString = DebugSupport.getenv("MdsDebugPort");
         if (portString != null) {
            label26:
            try {
               this._port = Integer.parseInt(portString);
            } finally {
               break label26;
            }
         }

         Thread serverThread = new Thread(this, "MDSDebugServer");
         serverThread.start();
         Thread startupThread = new Thread(new SimulatorDebugServer$StartupRunnable(), "MDSDebugBootstrap");
         startupThread.start();
         result = true;
      }

      return result;
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
      // 02: new java/lang/StringBuffer
      // 05: dup
      // 06: ldc_w "socket://:"
      // 09: invokespecial java/lang/StringBuffer.<init> (Ljava/lang/String;)V
      // 0c: aload 0
      // 0d: getfield net/rim/wica/runtime/diagnostics/impl/SimulatorDebugServer._port I
      // 10: invokevirtual java/lang/StringBuffer.append (I)Ljava/lang/StringBuffer;
      // 13: ldc_w ";deviceside=true"
      // 16: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 19: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 1c: invokestatic javax/microedition/io/Connector.open (Ljava/lang/String;)Ljavax/microedition/io/Connection;
      // 1f: checkcast javax/microedition/io/ServerSocketConnection
      // 22: astore 1
      // 23: aload 1
      // 24: invokeinterface javax/microedition/io/StreamConnectionNotifier.acceptAndOpen ()Ljavax/microedition/io/StreamConnection; 1
      // 29: checkcast javax/microedition/io/SocketConnection
      // 2c: astore 2
      // 2d: aload 2
      // 2e: bipush 0
      // 2f: bipush 0
      // 30: invokeinterface javax/microedition/io/SocketConnection.setSocketOption (BI)V 3
      // 35: aload 2
      // 36: bipush 2
      // 38: bipush 1
      // 39: invokeinterface javax/microedition/io/SocketConnection.setSocketOption (BI)V 3
      // 3e: new net/rim/wica/common/debug/session/TargetSessionManager
      // 41: dup
      // 42: invokespecial net/rim/wica/common/debug/session/TargetSessionManager.<init> ()V
      // 45: astore 3
      // 46: aload 3
      // 47: new net/rim/wica/runtime/diagnostics/impl/StreamInputByteStreamAdapter
      // 4a: dup
      // 4b: aload 2
      // 4c: invokespecial net/rim/wica/runtime/diagnostics/impl/StreamInputByteStreamAdapter.<init> (Ljavax/microedition/io/StreamConnection;)V
      // 4f: new net/rim/wica/runtime/diagnostics/impl/StreamOutputByteStreamAdapter
      // 52: dup
      // 53: aload 2
      // 54: invokespecial net/rim/wica/runtime/diagnostics/impl/StreamOutputByteStreamAdapter.<init> (Ljavax/microedition/io/StreamConnection;)V
      // 57: invokevirtual net/rim/wica/common/debug/session/TargetSessionManager.init (Lnet/rim/wica/common/debug/io/IInputByteStreamAdapter;Lnet/rim/wica/common/debug/io/IOutputByteStreamAdapter;)V
      // 5a: aload 3
      // 5b: bipush 0
      // 5c: invokevirtual net/rim/wica/common/debug/session/TargetSessionManager.start (I)Z
      // 5f: ifeq 8c
      // 62: aload 3
      // 63: bipush 0
      // 64: invokevirtual net/rim/wica/common/debug/session/TargetSessionManager.waitForNewSession (I)Z
      // 67: pop
      // 68: aload 0
      // 69: getfield net/rim/wica/runtime/diagnostics/impl/SimulatorDebugServer._handler Lnet/rim/wica/runtime/diagnostics/impl/DebugHandler;
      // 6c: bipush 1
      // 6d: aload 3
      // 6e: invokevirtual net/rim/wica/runtime/diagnostics/impl/DebugHandler.setAttached (ZLnet/rim/wica/common/debug/session/TargetSessionManager;)V
      // 71: aload 3
      // 72: aload 0
      // 73: getfield net/rim/wica/runtime/diagnostics/impl/SimulatorDebugServer._handler Lnet/rim/wica/runtime/diagnostics/impl/DebugHandler;
      // 76: invokevirtual net/rim/wica/common/debug/session/TargetSessionManager.registerRequestMessageHandler (Lnet/rim/wica/common/debug/session/IRequestMessageHandler;)V
      // 79: aload 3
      // 7a: bipush 0
      // 7b: invokevirtual net/rim/wica/common/debug/session/TargetSessionManager.waitForDetach (I)V
      // 7e: aload 3
      // 7f: aconst_null
      // 80: invokevirtual net/rim/wica/common/debug/session/TargetSessionManager.registerRequestMessageHandler (Lnet/rim/wica/common/debug/session/IRequestMessageHandler;)V
      // 83: aload 0
      // 84: getfield net/rim/wica/runtime/diagnostics/impl/SimulatorDebugServer._handler Lnet/rim/wica/runtime/diagnostics/impl/DebugHandler;
      // 87: bipush 0
      // 88: aload 3
      // 89: invokevirtual net/rim/wica/runtime/diagnostics/impl/DebugHandler.setAttached (ZLnet/rim/wica/common/debug/session/TargetSessionManager;)V
      // 8c: aload 2
      // 8d: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 92: aload 3
      // 93: invokevirtual net/rim/wica/common/debug/session/TargetSessionManager.stop ()V
      // 96: goto 23
      // 99: astore 4
      // 9b: goto 23
      // 9e: astore 4
      // a0: aload 2
      // a1: invokeinterface javax/microedition/io/Connection.close ()V 1
      // a6: aload 3
      // a7: invokevirtual net/rim/wica/common/debug/session/TargetSessionManager.stop ()V
      // aa: goto 23
      // ad: astore 4
      // af: goto 23
      // b2: astore 5
      // b4: aload 2
      // b5: invokeinterface javax/microedition/io/Connection.close ()V 1
      // ba: aload 3
      // bb: invokevirtual net/rim/wica/common/debug/session/TargetSessionManager.stop ()V
      // be: goto c3
      // c1: astore 6
      // c3: aload 5
      // c5: athrow
      // c6: astore 2
      // c7: aload 1
      // c8: invokeinterface javax/microedition/io/Connection.close ()V 1
      // cd: return
      // ce: astore 2
      // cf: return
      // d0: astore 7
      // d2: aload 1
      // d3: invokeinterface javax/microedition/io/Connection.close ()V 1
      // d8: goto dd
      // db: astore 8
      // dd: aload 7
      // df: athrow
      // try (69 -> 73): 74 null
      // try (31 -> 69): 76 null
      // try (77 -> 81): 82 null
      // try (31 -> 69): 84 null
      // try (76 -> 77): 84 null
      // try (85 -> 89): 90 null
      // try (84 -> 85): 84 null
      // try (2 -> 93): 93 null
      // try (94 -> 96): 97 null
      // try (2 -> 94): 99 null
      // try (100 -> 102): 103 null
      // try (99 -> 100): 99 null
   }
}
