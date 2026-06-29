package net.rim.device.cldc.impl.gcmp;

import javax.microedition.io.DatagramConnection;
import net.rim.device.api.io.DatagramBase;
import net.rim.device.api.system.DeviceInfo;

final class GcmpReceiveThread extends Thread implements GcmpEvents {
   private DatagramConnection _conn;
   private DatagramBase _dgram;
   private boolean _parseHeader;
   private int _deviceId;

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   GcmpReceiveThread(DatagramConnection conn, boolean parseHeader) {
      this._conn = conn;
      this._parseHeader = parseHeader;
      this._deviceId = DeviceInfo.getDeviceId();

      try {
         this._dgram = (DatagramBase)this._conn.newDatagram(0);
      } catch (Throwable var5) {
         throw new Object(e.getMessage());
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
      // 00: aconst_null
      // 01: astore 1
      // 02: aconst_null
      // 03: astore 2
      // 04: aload 0
      // 05: getfield net/rim/device/cldc/impl/gcmp/GcmpReceiveThread._dgram Lnet/rim/device/api/io/DatagramBase;
      // 08: invokevirtual net/rim/device/api/io/DatagramBase.reset ()V
      // 0b: aload 0
      // 0c: getfield net/rim/device/cldc/impl/gcmp/GcmpReceiveThread._conn Ljavax/microedition/io/DatagramConnection;
      // 0f: aload 0
      // 10: getfield net/rim/device/cldc/impl/gcmp/GcmpReceiveThread._dgram Lnet/rim/device/api/io/DatagramBase;
      // 13: invokeinterface javax/microedition/io/DatagramConnection.receive (Ljavax/microedition/io/Datagram;)V 2
      // 18: aload 0
      // 19: getfield net/rim/device/cldc/impl/gcmp/GcmpReceiveThread._parseHeader Z
      // 1c: ifeq 61
      // 1f: aload 0
      // 20: getfield net/rim/device/cldc/impl/gcmp/GcmpReceiveThread._dgram Lnet/rim/device/api/io/DatagramBase;
      // 23: invokevirtual net/rim/device/api/util/DataBuffer.available ()I
      // 26: bipush 10
      // 28: if_icmpge 3a
      // 2b: ldc2_w -1673931206114386243
      // 2e: ldc_w 1195594293
      // 31: bipush 3
      // 33: invokestatic net/rim/device/api/system/EventLogger.logEvent (JII)Z
      // 36: pop
      // 37: goto 00
      // 3a: aload 0
      // 3b: getfield net/rim/device/cldc/impl/gcmp/GcmpReceiveThread._dgram Lnet/rim/device/api/io/DatagramBase;
      // 3e: bipush 6
      // 40: invokevirtual net/rim/device/api/util/DataBuffer.skipBytes (I)I
      // 43: pop
      // 44: aload 0
      // 45: getfield net/rim/device/cldc/impl/gcmp/GcmpReceiveThread._dgram Lnet/rim/device/api/io/DatagramBase;
      // 48: invokevirtual net/rim/device/api/util/DataBuffer.readInt ()I
      // 4b: aload 0
      // 4c: getfield net/rim/device/cldc/impl/gcmp/GcmpReceiveThread._deviceId I
      // 4f: if_icmpeq 61
      // 52: ldc2_w -1673931206114386243
      // 55: ldc_w 1195594294
      // 58: bipush 3
      // 5a: invokestatic net/rim/device/api/system/EventLogger.logEvent (JII)Z
      // 5d: pop
      // 5e: goto 00
      // 61: aload 0
      // 62: getfield net/rim/device/cldc/impl/gcmp/GcmpReceiveThread._dgram Lnet/rim/device/api/io/DatagramBase;
      // 65: invokevirtual net/rim/device/api/io/DatagramBase.getAddressBase ()Lnet/rim/device/api/io/DatagramAddressBase;
      // 68: astore 1
      // 69: aload 1
      // 6a: dup
      // 6b: instanceof java/lang/Object
      // 6e: ifne 75
      // 71: pop
      // 72: goto 8a
      // 75: checkcast java/lang/Object
      // 78: invokevirtual net/rim/device/api/io/UdpAddress.getApn ()Ljava/lang/String;
      // 7b: astore 2
      // 7c: aload 2
      // 7d: invokestatic net/rim/device/api/system/RadioInfo.getAccessPointNumber (Ljava/lang/String;)I
      // 80: istore 3
      // 81: iload 3
      // 82: bipush 1
      // 83: invokestatic net/rim/device/internal/system/RadioInternal.setFastDormancy (IZ)V
      // 86: goto 8a
      // 89: astore 3
      // 8a: aload 1
      // 8b: aload 0
      // 8c: getfield net/rim/device/cldc/impl/gcmp/GcmpReceiveThread._dgram Lnet/rim/device/api/io/DatagramBase;
      // 8f: invokestatic net/rim/device/cldc/impl/gcmp/GcmpSession.processReceivedPacket (Lnet/rim/device/api/io/DatagramAddressBase;Lnet/rim/device/api/util/DataBuffer;)V
      // 92: goto 00
      // 95: astore 3
      // 96: ldc2_w -1673931206114386243
      // 99: ldc_w 1195594291
      // 9c: bipush 3
      // 9e: invokestatic net/rim/device/api/system/EventLogger.logEvent (JII)Z
      // a1: pop
      // a2: goto 00
      // a5: astore 3
      // a6: goto 00
      // try (56 -> 62): 63 null
      // try (0 -> 25): 69 null
      // try (26 -> 42): 69 null
      // try (43 -> 68): 69 null
      // try (0 -> 25): 76 null
      // try (26 -> 42): 76 null
      // try (43 -> 68): 76 null
   }
}
