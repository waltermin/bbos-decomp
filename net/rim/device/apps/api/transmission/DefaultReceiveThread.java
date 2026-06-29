package net.rim.device.apps.api.transmission;

import net.rim.device.api.system.EventLogger;

public class DefaultReceiveThread extends BaseConnectionThread {
   private PacketReceiver _packetReceiver;

   public DefaultReceiveThread(PacketReceiver aPacketReceiver, long eventLoggerGUID) {
      super(eventLoggerGUID != 0 ? eventLoggerGUID : 7733987007924995022L);
      this._packetReceiver = aPacketReceiver;
      if (eventLoggerGUID == 0) {
         try {
            EventLogger.register(7733987007924995022L, "net.rim.transmission.ReceiveThread", 2);
         } finally {
            return;
         }
      }
   }

   @Override
   public void run() {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: aconst_null
      // 001: astore 1
      // 002: aconst_null
      // 003: astore 2
      // 004: aconst_null
      // 005: astore 3
      // 006: aconst_null
      // 007: astore 4
      // 009: aconst_null
      // 00a: astore 5
      // 00c: aload 0
      // 00d: getfield net/rim/device/apps/api/transmission/BaseConnectionThread._eventLoggerGUID J
      // 010: ldc_w 1383363182
      // 013: bipush 0
      // 014: invokestatic net/rim/device/api/system/EventLogger.logEvent (JII)Z
      // 017: pop
      // 018: aload 0
      // 019: getfield net/rim/device/apps/api/transmission/BaseConnectionThread._connection Ljavax/microedition/io/DatagramConnection;
      // 01c: sipush 1024
      // 01f: invokeinterface javax/microedition/io/DatagramConnection.newDatagram (I)Ljavax/microedition/io/Datagram; 2
      // 024: checkcast net/rim/device/api/io/DatagramBase
      // 027: astore 1
      // 028: aload 1
      // 029: astore 2
      // 02a: goto 03c
      // 02d: astore 6
      // 02f: aload 0
      // 030: getfield net/rim/device/apps/api/transmission/BaseConnectionThread._eventLoggerGUID J
      // 033: ldc_w 1382313571
      // 036: bipush 1
      // 037: invokestatic net/rim/device/api/system/EventLogger.logEvent (JII)Z
      // 03a: pop
      // 03b: return
      // 03c: aload 0
      // 03d: getfield net/rim/device/apps/api/transmission/BaseConnectionThread._eventLoggerGUID J
      // 040: ldc_w 1383556720
      // 043: bipush 5
      // 045: invokestatic net/rim/device/api/system/EventLogger.logEvent (JII)Z
      // 048: pop
      // 049: aload 0
      // 04a: getfield net/rim/device/apps/api/transmission/BaseConnectionThread._connection Ljavax/microedition/io/DatagramConnection;
      // 04d: aload 1
      // 04e: invokeinterface javax/microedition/io/DatagramConnection.receive (Ljavax/microedition/io/Datagram;)V 2
      // 053: aload 1
      // 054: instanceof net/rim/device/cldc/io/gme/GMEDatagram
      // 057: istore 6
      // 059: aload 0
      // 05a: getfield net/rim/device/apps/api/transmission/BaseConnectionThread._eventLoggerGUID J
      // 05d: ldc_w 1381516132
      // 060: iload 6
      // 062: ifeq 06f
      // 065: aload 1
      // 066: checkcast net/rim/device/cldc/io/gme/GMEDatagram
      // 069: invokevirtual net/rim/device/cldc/io/gme/GMEDatagram.getTransactionId ()I
      // 06c: goto 070
      // 06f: bipush 0
      // 070: bipush 10
      // 072: bipush 0
      // 073: invokestatic net/rim/device/api/system/EventLogger.logEvent (JIIII)Z
      // 076: pop
      // 077: aload 0
      // 078: getfield net/rim/device/apps/api/transmission/BaseConnectionThread._eventLoggerGUID J
      // 07b: ldc_w 1383101040
      // 07e: bipush 5
      // 080: invokestatic net/rim/device/api/system/EventLogger.logEvent (JII)Z
      // 083: pop
      // 084: aload 2
      // 085: invokevirtual net/rim/device/api/util/DataBuffer.getArray ()[B
      // 088: invokestatic net/rim/device/api/crypto/RandomSource.add (Ljava/lang/Object;)V
      // 08b: aconst_null
      // 08c: astore 3
      // 08d: bipush 0
      // 08e: istore 7
      // 090: iload 6
      // 092: ifne 098
      // 095: goto 13c
      // 098: aload 1
      // 099: checkcast net/rim/device/cldc/io/gme/GMEDatagram
      // 09c: astore 8
      // 09e: aload 8
      // 0a0: invokevirtual net/rim/device/cldc/io/gme/GMEDatagram.getBoundServiceRecord ()Lnet/rim/device/api/servicebook/ServiceRecord;
      // 0a3: dup
      // 0a4: astore 5
      // 0a6: ifnull 0c2
      // 0a9: aload 3
      // 0aa: ifnonnull 0b5
      // 0ad: new net/rim/device/apps/api/framework/model/ContextObject
      // 0b0: dup
      // 0b1: invokespecial net/rim/device/apps/api/framework/model/ContextObject.<init> ()V
      // 0b4: astore 3
      // 0b5: aload 3
      // 0b6: ldc2_w -6095803566992128485
      // 0b9: aload 5
      // 0bb: invokestatic net/rim/device/apps/api/framework/model/ContextObject.put (Ljava/lang/Object;JLjava/lang/Object;)Ljava/lang/Object;
      // 0be: pop
      // 0bf: goto 0dd
      // 0c2: aload 8
      // 0c4: invokevirtual net/rim/device/cldc/io/gme/GMEDatagram.wasBoundServiceRecordDisallowed ()Z
      // 0c7: ifeq 0dd
      // 0ca: aload 3
      // 0cb: ifnonnull 0d6
      // 0ce: new net/rim/device/apps/api/framework/model/ContextObject
      // 0d1: dup
      // 0d2: invokespecial net/rim/device/apps/api/framework/model/ContextObject.<init> ()V
      // 0d5: astore 3
      // 0d6: aload 3
      // 0d7: sipush 135
      // 0da: invokevirtual net/rim/device/apps/api/framework/model/ContextObject.setFlag (I)V
      // 0dd: aload 8
      // 0df: sipush 512
      // 0e2: invokevirtual net/rim/device/api/io/DatagramBase.isFlagSet (I)Z
      // 0e5: ifeq 0ee
      // 0e8: aload 1
      // 0e9: invokevirtual net/rim/device/api/io/DatagramBase.getDatagramId ()I
      // 0ec: istore 7
      // 0ee: aload 8
      // 0f0: invokevirtual net/rim/device/cldc/io/gme/GMEDatagram.wasEncrypted ()Z
      // 0f3: ifeq 108
      // 0f6: aload 3
      // 0f7: ifnonnull 102
      // 0fa: new net/rim/device/apps/api/framework/model/ContextObject
      // 0fd: dup
      // 0fe: invokespecial net/rim/device/apps/api/framework/model/ContextObject.<init> ()V
      // 101: astore 3
      // 102: aload 3
      // 103: bipush 110
      // 105: invokevirtual net/rim/device/apps/api/framework/model/ContextObject.setFlag (I)V
      // 108: aload 8
      // 10a: invokevirtual net/rim/device/cldc/io/gme/GMEDatagram.wasDatagramSecure ()Z
      // 10d: ifeq 122
      // 110: aload 3
      // 111: ifnonnull 11c
      // 114: new net/rim/device/apps/api/framework/model/ContextObject
      // 117: dup
      // 118: invokespecial net/rim/device/apps/api/framework/model/ContextObject.<init> ()V
      // 11b: astore 3
      // 11c: aload 3
      // 11d: bipush 126
      // 11f: invokevirtual net/rim/device/apps/api/framework/model/ContextObject.setFlag (I)V
      // 122: aload 8
      // 124: invokevirtual net/rim/device/cldc/io/gme/GMEDatagram.usedGlobalScramblingKey ()Z
      // 127: ifeq 13c
      // 12a: aload 3
      // 12b: ifnonnull 136
      // 12e: new net/rim/device/apps/api/framework/model/ContextObject
      // 131: dup
      // 132: invokespecial net/rim/device/apps/api/framework/model/ContextObject.<init> ()V
      // 135: astore 3
      // 136: aload 3
      // 137: bipush 109
      // 139: invokevirtual net/rim/device/apps/api/framework/model/ContextObject.setFlag (I)V
      // 13c: aload 1
      // 13d: invokevirtual net/rim/device/api/io/DatagramBase.getAddressBase ()Lnet/rim/device/api/io/DatagramAddressBase;
      // 140: dup
      // 141: astore 4
      // 143: ifnull 15c
      // 146: aload 3
      // 147: ifnonnull 152
      // 14a: new net/rim/device/apps/api/framework/model/ContextObject
      // 14d: dup
      // 14e: invokespecial net/rim/device/apps/api/framework/model/ContextObject.<init> ()V
      // 151: astore 3
      // 152: aload 3
      // 153: ldc2_w -7981905408958106750
      // 156: aload 4
      // 158: invokestatic net/rim/device/apps/api/framework/model/ContextObject.put (Ljava/lang/Object;JLjava/lang/Object;)Ljava/lang/Object;
      // 15b: pop
      // 15c: aload 0
      // 15d: getfield net/rim/device/apps/api/transmission/BaseConnectionThread._eventLoggerGUID J
      // 160: ldc_w 1382445680
      // 163: bipush 5
      // 165: invokestatic net/rim/device/api/system/EventLogger.logEvent (JII)Z
      // 168: pop
      // 169: aload 0
      // 16a: getfield net/rim/device/apps/api/transmission/DefaultReceiveThread._packetReceiver Lnet/rim/device/apps/api/transmission/PacketReceiver;
      // 16d: aload 2
      // 16e: aload 3
      // 16f: invokeinterface net/rim/device/apps/api/transmission/PacketReceiver.receivePacket (Lnet/rim/device/api/util/DataBuffer;Ljava/lang/Object;)V 3
      // 174: goto 182
      // 177: astore 8
      // 179: aload 1
      // 17a: bipush 65
      // 17c: ldc_w "Error handling message"
      // 17f: invokestatic net/rim/device/cldc/io/gme/GmeUtil.sendTransactionErrorDatagram (Lnet/rim/device/api/io/DatagramBase;ILjava/lang/String;)V
      // 182: iload 7
      // 184: ifne 18a
      // 187: goto 03c
      // 18a: aload 0
      // 18b: getfield net/rim/device/apps/api/transmission/BaseConnectionThread._connection Ljavax/microedition/io/DatagramConnection;
      // 18e: dup
      // 18f: instanceof net/rim/device/api/io/DatagramConnectionBase
      // 192: ifne 199
      // 195: pop
      // 196: goto 03c
      // 199: checkcast net/rim/device/api/io/DatagramConnectionBase
      // 19c: iload 7
      // 19e: invokevirtual net/rim/device/api/io/DatagramConnectionBase.datagramProcessed (I)V
      // 1a1: goto 03c
      // 1a4: astore 6
      // 1a6: aload 0
      // 1a7: getfield net/rim/device/apps/api/transmission/BaseConnectionThread._eventLoggerGUID J
      // 1aa: ldc_w 1128485443
      // 1ad: bipush 5
      // 1af: invokestatic net/rim/device/api/system/EventLogger.logEvent (JII)Z
      // 1b2: pop
      // 1b3: goto 03c
      // 1b6: astore 6
      // 1b8: goto 03c
      // try (16 -> 24): 25 null
      // try (173 -> 178): 179 null
      // try (33 -> 197): 198 null
      // try (33 -> 197): 206 null
   }
}
