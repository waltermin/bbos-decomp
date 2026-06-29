package simulationservicebook;

import javax.microedition.io.DatagramConnection;

final class InsertInternetServiceBook$SimulatorReceiveRunnable implements Runnable {
   DatagramConnection _connection;
   boolean _run;

   InsertInternetServiceBook$SimulatorReceiveRunnable(DatagramConnection connection) {
      this._connection = connection;
      this._run = this._connection != null;
   }

   final void closeConnection() {
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
      // 00: aload 0
      // 01: getfield simulationservicebook/InsertInternetServiceBook$SimulatorReceiveRunnable._connection Ljavax/microedition/io/DatagramConnection;
      // 04: ifnull 15
      // 07: aload 0
      // 08: bipush 0
      // 09: putfield simulationservicebook/InsertInternetServiceBook$SimulatorReceiveRunnable._run Z
      // 0c: aload 0
      // 0d: getfield simulationservicebook/InsertInternetServiceBook$SimulatorReceiveRunnable._connection Ljavax/microedition/io/DatagramConnection;
      // 10: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 15: aload 0
      // 16: aconst_null
      // 17: putfield simulationservicebook/InsertInternetServiceBook$SimulatorReceiveRunnable._connection Ljavax/microedition/io/DatagramConnection;
      // 1a: return
      // 1b: astore 1
      // 1c: aload 0
      // 1d: aconst_null
      // 1e: putfield simulationservicebook/InsertInternetServiceBook$SimulatorReceiveRunnable._connection Ljavax/microedition/io/DatagramConnection;
      // 21: return
      // 22: astore 2
      // 23: aload 0
      // 24: aconst_null
      // 25: putfield simulationservicebook/InsertInternetServiceBook$SimulatorReceiveRunnable._connection Ljavax/microedition/io/DatagramConnection;
      // 28: aload 2
      // 29: athrow
      // try (0 -> 9): 13 null
      // try (0 -> 9): 18 null
      // try (13 -> 14): 18 null
      // try (18 -> 19): 18 null
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
      // 00: aconst_null
      // 01: astore 1
      // 02: aload 0
      // 03: getfield simulationservicebook/InsertInternetServiceBook$SimulatorReceiveRunnable._connection Ljavax/microedition/io/DatagramConnection;
      // 06: ifnonnull 0a
      // 09: return
      // 0a: aload 0
      // 0b: getfield simulationservicebook/InsertInternetServiceBook$SimulatorReceiveRunnable._connection Ljavax/microedition/io/DatagramConnection;
      // 0e: bipush 0
      // 0f: invokeinterface javax/microedition/io/DatagramConnection.newDatagram (I)Ljavax/microedition/io/Datagram; 2
      // 14: astore 1
      // 15: goto 1a
      // 18: astore 2
      // 19: return
      // 1a: aload 0
      // 1b: getfield simulationservicebook/InsertInternetServiceBook$SimulatorReceiveRunnable._run Z
      // 1e: ifeq 66
      // 21: aload 0
      // 22: getfield simulationservicebook/InsertInternetServiceBook$SimulatorReceiveRunnable._connection Ljavax/microedition/io/DatagramConnection;
      // 25: aload 1
      // 26: invokeinterface javax/microedition/io/DatagramConnection.receive (Ljavax/microedition/io/Datagram;)V 2
      // 2b: aload 1
      // 2c: invokeinterface javax/microedition/io/Datagram.reset ()V 1
      // 31: goto 1a
      // 34: astore 2
      // 35: getstatic java/lang/System.err Ljava/io/PrintStream;
      // 38: aload 2
      // 39: invokevirtual java/io/PrintStream.println (Ljava/lang/Object;)V
      // 3c: aload 1
      // 3d: invokeinterface javax/microedition/io/Datagram.reset ()V 1
      // 42: goto 1a
      // 45: astore 2
      // 46: getstatic java/lang/System.err Ljava/io/PrintStream;
      // 49: aload 2
      // 4a: invokevirtual java/lang/Throwable.getMessage ()Ljava/lang/String;
      // 4d: invokevirtual java/io/PrintStream.println (Ljava/lang/String;)V
      // 50: aload 2
      // 51: invokevirtual java/lang/Throwable.printStackTrace ()V
      // 54: aload 1
      // 55: invokeinterface javax/microedition/io/Datagram.reset ()V 1
      // 5a: goto 1a
      // 5d: astore 3
      // 5e: aload 1
      // 5f: invokeinterface javax/microedition/io/Datagram.reset ()V 1
      // 64: aload 3
      // 65: athrow
      // 66: aconst_null
      // 67: astore 1
      // 68: return
      // try (6 -> 11): 12 null
      // try (17 -> 21): 24 null
      // try (17 -> 21): 31 null
      // try (17 -> 21): 41 null
      // try (24 -> 28): 41 null
      // try (31 -> 38): 41 null
      // try (41 -> 42): 41 null
   }
}
