package net.rim.device.internal.io.streamdatagram;

import java.util.Vector;
import javax.microedition.io.Datagram;

final class StreamSendThread extends Thread {
   Vector _requests = new Vector();

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
      // 03: getfield net/rim/device/internal/io/streamdatagram/StreamSendThread._requests Ljava/util/Vector;
      // 06: dup
      // 07: astore 2
      // 08: monitorenter
      // 09: aload 0
      // 0a: getfield net/rim/device/internal/io/streamdatagram/StreamSendThread._requests Ljava/util/Vector;
      // 0d: invokevirtual java/util/Vector.size ()I
      // 10: ifne 23
      // 13: aload 0
      // 14: getfield net/rim/device/internal/io/streamdatagram/StreamSendThread._requests Ljava/util/Vector;
      // 17: invokevirtual java/lang/Object.wait ()V
      // 1a: goto 1e
      // 1d: astore 3
      // 1e: aload 2
      // 1f: monitorexit
      // 20: goto 02
      // 23: aload 0
      // 24: getfield net/rim/device/internal/io/streamdatagram/StreamSendThread._requests Ljava/util/Vector;
      // 27: bipush 0
      // 28: invokevirtual java/util/Vector.elementAt (I)Ljava/lang/Object;
      // 2b: checkcast net/rim/device/internal/io/streamdatagram/StreamSendThread$STRequest
      // 2e: astore 1
      // 2f: aload 0
      // 30: getfield net/rim/device/internal/io/streamdatagram/StreamSendThread._requests Ljava/util/Vector;
      // 33: bipush 0
      // 34: invokevirtual java/util/Vector.removeElementAt (I)V
      // 37: aload 2
      // 38: monitorexit
      // 39: goto 43
      // 3c: astore 4
      // 3e: aload 2
      // 3f: monitorexit
      // 40: aload 4
      // 42: athrow
      // 43: aload 1
      // 44: getfield net/rim/device/internal/io/streamdatagram/StreamSendThread$STRequest._sendObj Ljava/lang/Object;
      // 47: dup
      // 48: instanceof net/rim/device/internal/io/streamdatagram/StreamDatagramTransportBase
      // 4b: ifne 52
      // 4e: pop
      // 4f: goto 62
      // 52: checkcast net/rim/device/internal/io/streamdatagram/StreamDatagramTransportBase
      // 55: checkcast net/rim/device/internal/io/streamdatagram/StreamDatagramTransportBase
      // 58: aload 1
      // 59: getfield net/rim/device/internal/io/streamdatagram/StreamSendThread$STRequest._datagram Ljavax/microedition/io/Datagram;
      // 5c: invokevirtual net/rim/device/internal/io/streamdatagram/StreamDatagramTransportBase.sendDatagram (Ljavax/microedition/io/Datagram;)V
      // 5f: goto 80
      // 62: aload 1
      // 63: getfield net/rim/device/internal/io/streamdatagram/StreamSendThread$STRequest._sendObj Ljava/lang/Object;
      // 66: dup
      // 67: instanceof javax/microedition/io/DatagramConnection
      // 6a: ifne 71
      // 6d: pop
      // 6e: goto 80
      // 71: checkcast javax/microedition/io/DatagramConnection
      // 74: checkcast javax/microedition/io/DatagramConnection
      // 77: aload 1
      // 78: getfield net/rim/device/internal/io/streamdatagram/StreamSendThread$STRequest._datagram Ljavax/microedition/io/Datagram;
      // 7b: invokeinterface javax/microedition/io/DatagramConnection.send (Ljavax/microedition/io/Datagram;)V 2
      // 80: aconst_null
      // 81: astore 1
      // 82: goto 02
      // 85: astore 2
      // 86: aconst_null
      // 87: astore 1
      // 88: goto 02
      // 8b: astore 2
      // 8c: aconst_null
      // 8d: astore 1
      // 8e: goto 02
      // 91: astore 5
      // 93: aconst_null
      // 94: astore 1
      // 95: aload 5
      // 97: athrow
      // try (11 -> 14): 15 null
      // try (7 -> 18): 32 null
      // try (19 -> 31): 32 null
      // try (32 -> 35): 32 null
      // try (37 -> 62): 65 null
      // try (37 -> 62): 69 null
      // try (37 -> 62): 73 null
      // try (65 -> 66): 73 null
      // try (69 -> 70): 73 null
      // try (73 -> 74): 73 null
   }

   public final void addRequest(Object sendObj, Datagram datagram) {
      synchronized (this._requests) {
         for (int index = this._requests.size() - 1; index >= 0; index--) {
            StreamSendThread$STRequest request = (StreamSendThread$STRequest)this._requests.elementAt(index);
            if (request._sendObj instanceof StreamDatagramTransportBase
               && datagram.getAddress().equals(request._datagram.getAddress())
               && !((StreamDatagramTransportBase)request._sendObj).isEssential(request._datagram)) {
               this._requests.removeElementAt(index);
               break;
            }
         }

         this._requests.addElement(new StreamSendThread$STRequest(sendObj, datagram));
         this._requests.notify();
      }
   }

   public final void halt() {
   }
}
