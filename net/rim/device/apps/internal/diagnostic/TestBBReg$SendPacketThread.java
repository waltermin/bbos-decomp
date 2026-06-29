package net.rim.device.apps.internal.diagnostic;

import javax.microedition.io.Datagram;
import net.rim.device.api.util.CyclicQueue;

final class TestBBReg$SendPacketThread extends Thread {
   private CyclicQueue _requests = new CyclicQueue(8);

   @Override
   public final void run() {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 0
      // 01: getfield net/rim/device/apps/internal/diagnostic/TestBBReg$SendPacketThread._requests Lnet/rim/device/api/util/CyclicQueue;
      // 04: dup
      // 05: astore 2
      // 06: monitorenter
      // 07: aload 0
      // 08: getfield net/rim/device/apps/internal/diagnostic/TestBBReg$SendPacketThread._requests Lnet/rim/device/api/util/CyclicQueue;
      // 0b: invokevirtual net/rim/device/api/util/CyclicQueue.isEmpty ()Z
      // 0e: ifeq 18
      // 11: aload 0
      // 12: getfield net/rim/device/apps/internal/diagnostic/TestBBReg$SendPacketThread._requests Lnet/rim/device/api/util/CyclicQueue;
      // 15: invokevirtual java/lang/Object.wait ()V
      // 18: aload 0
      // 19: getfield net/rim/device/apps/internal/diagnostic/TestBBReg$SendPacketThread._requests Lnet/rim/device/api/util/CyclicQueue;
      // 1c: invokevirtual net/rim/device/api/util/CyclicQueue.dequeue ()Ljava/lang/Object;
      // 1f: checkcast net/rim/device/apps/internal/diagnostic/TestBBReg$SendPacketThread$SPTRequest
      // 22: astore 1
      // 23: aload 2
      // 24: monitorexit
      // 25: goto 2d
      // 28: astore 3
      // 29: aload 2
      // 2a: monitorexit
      // 2b: aload 3
      // 2c: athrow
      // 2d: aload 1
      // 2e: getfield net/rim/device/apps/internal/diagnostic/TestBBReg$SendPacketThread$SPTRequest._sendObj Ljava/lang/Object;
      // 31: dup
      // 32: instanceof net/rim/device/api/io/DatagramTransportBase
      // 35: ifne 3c
      // 38: pop
      // 39: goto 4c
      // 3c: checkcast net/rim/device/api/io/DatagramTransportBase
      // 3f: checkcast net/rim/device/api/io/DatagramTransportBase
      // 42: aload 1
      // 43: getfield net/rim/device/apps/internal/diagnostic/TestBBReg$SendPacketThread$SPTRequest._datagram Ljavax/microedition/io/Datagram;
      // 46: invokevirtual net/rim/device/api/io/DatagramTransportBase.send (Ljavax/microedition/io/Datagram;)V
      // 49: goto 6a
      // 4c: aload 1
      // 4d: getfield net/rim/device/apps/internal/diagnostic/TestBBReg$SendPacketThread$SPTRequest._sendObj Ljava/lang/Object;
      // 50: dup
      // 51: instanceof javax/microedition/io/DatagramConnection
      // 54: ifne 5b
      // 57: pop
      // 58: goto 6a
      // 5b: checkcast javax/microedition/io/DatagramConnection
      // 5e: checkcast javax/microedition/io/DatagramConnection
      // 61: aload 1
      // 62: getfield net/rim/device/apps/internal/diagnostic/TestBBReg$SendPacketThread$SPTRequest._datagram Ljavax/microedition/io/Datagram;
      // 65: invokeinterface javax/microedition/io/DatagramConnection.send (Ljavax/microedition/io/Datagram;)V 2
      // 6a: aconst_null
      // 6b: astore 1
      // 6c: goto 00
      // 6f: astore 2
      // 70: aconst_null
      // 71: astore 1
      // 72: goto 00
      // 75: astore 2
      // 76: aconst_null
      // 77: astore 1
      // 78: goto 00
      // 7b: astore 4
      // 7d: aconst_null
      // 7e: astore 1
      // 7f: aload 4
      // 81: athrow
      // try (5 -> 19): 20 null
      // try (20 -> 23): 20 null
      // try (0 -> 50): 53 null
      // try (0 -> 50): 57 null
      // try (0 -> 50): 61 null
      // try (53 -> 54): 61 null
      // try (57 -> 58): 61 null
      // try (61 -> 62): 61 null
   }

   public final void addRequest(Object sendObj, Datagram datagram) {
      synchronized (this._requests) {
         this._requests.enqueue(new TestBBReg$SendPacketThread$SPTRequest(this, sendObj, datagram));
         this._requests.notify();
      }
   }
}
