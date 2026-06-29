package net.rim.device.cldc.io.srp;

import net.rim.device.api.io.DatagramTransportBase;

final class SrpReceiveThread extends Thread {
   private boolean _isTransport;
   private Object _connectionObj;
   private SrpBridgeConnection _connection;

   SrpReceiveThread(Object connection, SrpBridgeConnection subConnection) {
      this._connectionObj = connection;
      this._isTransport = connection instanceof DatagramTransportBase;
      this._connection = subConnection;
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
      // 03: getfield net/rim/device/cldc/io/srp/SrpReceiveThread._connection Lnet/rim/device/cldc/io/srp/SrpBridgeConnection;
      // 06: bipush 0
      // 07: invokevirtual net/rim/device/cldc/io/srp/SrpBridgeConnection.newDatagram (I)Ljavax/microedition/io/Datagram;
      // 0a: astore 1
      // 0b: aload 0
      // 0c: getfield net/rim/device/cldc/io/srp/SrpReceiveThread._connection Lnet/rim/device/cldc/io/srp/SrpBridgeConnection;
      // 0f: aload 1
      // 10: invokevirtual net/rim/device/cldc/io/srp/SrpBridgeConnection.receive (Ljavax/microedition/io/Datagram;)V
      // 13: aload 0
      // 14: getfield net/rim/device/cldc/io/srp/SrpReceiveThread._isTransport Z
      // 17: ifeq 28
      // 1a: aload 0
      // 1b: getfield net/rim/device/cldc/io/srp/SrpReceiveThread._connectionObj Ljava/lang/Object;
      // 1e: checkcast net/rim/device/api/io/DatagramTransportBase
      // 21: aload 1
      // 22: invokevirtual net/rim/device/api/io/DatagramTransportBase.superProcessReceivedDatagram (Ljavax/microedition/io/Datagram;)V
      // 25: goto 33
      // 28: aload 0
      // 29: getfield net/rim/device/cldc/io/srp/SrpReceiveThread._connectionObj Ljava/lang/Object;
      // 2c: checkcast net/rim/device/api/io/DatagramConnectionBase
      // 2f: aload 1
      // 30: invokevirtual net/rim/device/api/io/DatagramConnectionBase.processReceivedDatagram (Ljavax/microedition/io/Datagram;)V
      // 33: aconst_null
      // 34: astore 1
      // 35: aload 0
      // 36: getfield net/rim/device/cldc/io/srp/SrpReceiveThread._connection Lnet/rim/device/cldc/io/srp/SrpBridgeConnection;
      // 39: invokevirtual net/rim/device/cldc/io/srp/SrpBridgeConnection.getConnectionState ()I
      // 3c: ifne 02
      // 3f: return
      // 40: astore 2
      // 41: aconst_null
      // 42: astore 1
      // 43: aload 0
      // 44: getfield net/rim/device/cldc/io/srp/SrpReceiveThread._connection Lnet/rim/device/cldc/io/srp/SrpBridgeConnection;
      // 47: invokevirtual net/rim/device/cldc/io/srp/SrpBridgeConnection.getConnectionState ()I
      // 4a: ifne 02
      // 4d: return
      // 4e: astore 2
      // 4f: aconst_null
      // 50: astore 1
      // 51: aload 0
      // 52: getfield net/rim/device/cldc/io/srp/SrpReceiveThread._connection Lnet/rim/device/cldc/io/srp/SrpBridgeConnection;
      // 55: invokevirtual net/rim/device/cldc/io/srp/SrpBridgeConnection.getConnectionState ()I
      // 58: ifne 02
      // 5b: return
      // 5c: astore 3
      // 5d: aconst_null
      // 5e: astore 1
      // 5f: aload 0
      // 60: getfield net/rim/device/cldc/io/srp/SrpReceiveThread._connection Lnet/rim/device/cldc/io/srp/SrpBridgeConnection;
      // 63: invokevirtual net/rim/device/cldc/io/srp/SrpBridgeConnection.getConnectionState ()I
      // 66: ifne 6a
      // 69: return
      // 6a: aload 3
      // 6b: athrow
      // try (2 -> 25): 32 null
      // try (2 -> 25): 40 null
      // try (2 -> 25): 48 null
      // try (32 -> 33): 48 null
      // try (40 -> 41): 48 null
      // try (48 -> 49): 48 null
   }
}
