package net.rim.device.cldc.io.gme;

import java.util.Hashtable;
import javax.microedition.io.Connection;
import net.rim.device.api.io.ConnectionListener;
import net.rim.device.api.io.DatagramConnectionBase;
import net.rim.device.api.util.CyclicQueue;

final class GmeReceiveThread extends Thread implements ConnectionListener {
   private Transport _transport;
   private Hashtable _connections = new Hashtable();
   private CyclicQueue _queue = new CyclicQueue(32);

   public GmeReceiveThread(Transport transport) {
      this._transport = transport;
   }

   public final void addConnection(DatagramConnectionBase connection, GmeRouterConnection routerConnection) {
      this._connections.put(connection, routerConnection);
      connection.setConnectionListener(this);
   }

   @Override
   public final void dataAvailable(Connection connection) {
      synchronized (this._queue) {
         this._queue.enqueue(connection);
         this._queue.notify();
      }
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
      // 00: aload 0
      // 01: getfield net/rim/device/cldc/io/gme/GmeReceiveThread._queue Lnet/rim/device/api/util/CyclicQueue;
      // 04: dup
      // 05: astore 3
      // 06: monitorenter
      // 07: aload 0
      // 08: getfield net/rim/device/cldc/io/gme/GmeReceiveThread._queue Lnet/rim/device/api/util/CyclicQueue;
      // 0b: invokevirtual net/rim/device/api/util/CyclicQueue.isEmpty ()Z
      // 0e: ifeq 18
      // 11: aload 0
      // 12: getfield net/rim/device/cldc/io/gme/GmeReceiveThread._queue Lnet/rim/device/api/util/CyclicQueue;
      // 15: invokevirtual java/lang/Object.wait ()V
      // 18: aload 0
      // 19: getfield net/rim/device/cldc/io/gme/GmeReceiveThread._queue Lnet/rim/device/api/util/CyclicQueue;
      // 1c: invokevirtual net/rim/device/api/util/CyclicQueue.dequeue ()Ljava/lang/Object;
      // 1f: checkcast net/rim/device/api/io/DatagramConnectionBase
      // 22: astore 1
      // 23: aload 3
      // 24: monitorexit
      // 25: goto 2f
      // 28: astore 4
      // 2a: aload 3
      // 2b: monitorexit
      // 2c: aload 4
      // 2e: athrow
      // 2f: aload 1
      // 30: bipush 0
      // 31: invokevirtual net/rim/device/api/io/DatagramConnectionBase.newDatagram (I)Ljavax/microedition/io/Datagram;
      // 34: astore 2
      // 35: aload 1
      // 36: aload 2
      // 37: invokevirtual net/rim/device/api/io/DatagramConnectionBase.receive (Ljavax/microedition/io/Datagram;)V
      // 3a: aload 0
      // 3b: getfield net/rim/device/cldc/io/gme/GmeReceiveThread._transport Lnet/rim/device/cldc/io/gme/Transport;
      // 3e: aload 0
      // 3f: getfield net/rim/device/cldc/io/gme/GmeReceiveThread._connections Ljava/util/Hashtable;
      // 42: aload 1
      // 43: invokevirtual java/util/Hashtable.get (Ljava/lang/Object;)Ljava/lang/Object;
      // 46: checkcast net/rim/device/cldc/io/gme/GmeRouterConnection
      // 49: aload 2
      // 4a: invokevirtual net/rim/device/cldc/io/gme/Transport.superProcessReceivedDatagram (Lnet/rim/device/cldc/io/gme/GmeRouterConnection;Ljavax/microedition/io/Datagram;)V
      // 4d: aconst_null
      // 4e: astore 1
      // 4f: aconst_null
      // 50: astore 2
      // 51: goto 00
      // 54: astore 3
      // 55: aconst_null
      // 56: astore 1
      // 57: aconst_null
      // 58: astore 2
      // 59: goto 00
      // 5c: astore 3
      // 5d: aconst_null
      // 5e: astore 1
      // 5f: aconst_null
      // 60: astore 2
      // 61: goto 00
      // 64: astore 5
      // 66: aconst_null
      // 67: astore 1
      // 68: aconst_null
      // 69: astore 2
      // 6a: aload 5
      // 6c: athrow
      // try (5 -> 19): 20 null
      // try (20 -> 23): 20 null
      // try (0 -> 41): 46 null
      // try (0 -> 41): 52 null
      // try (0 -> 41): 58 null
      // try (46 -> 47): 58 null
      // try (52 -> 53): 58 null
      // try (58 -> 59): 58 null
   }
}
