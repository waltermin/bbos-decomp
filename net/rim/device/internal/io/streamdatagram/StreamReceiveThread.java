package net.rim.device.internal.io.streamdatagram;

import net.rim.device.api.io.DatagramConnectionBase;
import net.rim.device.cldc.io.nativebase.NativeConnectionBase;

final class StreamReceiveThread extends Thread {
   StreamDatagramTransportBase _transport;
   NativeConnectionBase _connection;

   StreamReceiveThread(StreamDatagramTransportBase transport, DatagramConnectionBase subConnection) {
      this._transport = transport;
      if (subConnection instanceof NativeConnectionBase) {
         this._connection = (NativeConnectionBase)subConnection;
      } else {
         throw new RuntimeException();
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
      // 01: getfield net/rim/device/internal/io/streamdatagram/StreamReceiveThread._transport Lnet/rim/device/internal/io/streamdatagram/StreamDatagramTransportBase;
      // 04: aload 0
      // 05: getfield net/rim/device/internal/io/streamdatagram/StreamReceiveThread._connection Lnet/rim/device/cldc/io/nativebase/NativeConnectionBase;
      // 08: invokevirtual net/rim/device/cldc/io/nativebase/NativeConnectionBase.receiveDatagramBase ()Lnet/rim/device/api/io/DatagramBase;
      // 0b: invokevirtual net/rim/device/internal/io/streamdatagram/StreamDatagramTransportBase.processReceivedDatagram (Ljavax/microedition/io/Datagram;)V
      // 0e: goto 00
      // 11: astore 1
      // 12: goto 00
      // 15: astore 1
      // 16: goto 00
      // try (0 -> 6): 7 null
      // try (0 -> 6): 9 null
   }

   public final void halt() {
   }
}
