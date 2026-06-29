package net.rim.device.apps.api.transmission;

import java.util.Vector;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.EventLogger;
import net.rim.device.cldc.io.daemon.ProtocolDaemon;

final class DefaultSendThread$SendThreadCollector extends Thread {
   private Vector _packetQueue = new Vector();
   private static DefaultSendThread$SendThreadCollector _collector;
   private static final long REGISTRY_NAME = -32340375862732252L;
   private static final long GUID = -5359313744971625388L;

   private DefaultSendThread$SendThreadCollector() {
      EventLogger.register(-5359313744971625388L, "net.rim.transmission.SendThread", 2);
   }

   static final DefaultSendThread$SendThreadCollector getInstance() {
      return _collector;
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
      // 00c: aconst_null
      // 00d: astore 7
      // 00f: aconst_null
      // 010: astore 8
      // 012: aconst_null
      // 013: astore 9
      // 015: invokestatic net/rim/device/internal/crypto/CryptoBlock.areMasterKeysAvailable ()Z
      // 018: ifne 022
      // 01b: invokestatic net/rim/device/api/system/PersistentContent.waitForTicket ()Ljava/lang/Object;
      // 01e: pop
      // 01f: goto 015
      // 022: ldc2_w -5359313744971625388
      // 025: ldc_w 1400140398
      // 028: bipush 0
      // 029: invokestatic net/rim/device/api/system/EventLogger.logEvent (JII)Z
      // 02c: pop
      // 02d: aload 0
      // 02e: bipush 4
      // 030: invokevirtual java/lang/Thread.setPriority (I)V
      // 033: aload 0
      // 034: aload 0
      // 035: astore 10
      // 037: monitorenter
      // 038: aload 8
      // 03a: ifnull 08f
      // 03d: aload 8
      // 03f: getfield net/rim/device/apps/api/transmission/DefaultSendThread._closeConnectionAfterPacketsFlushed Z
      // 042: ifeq 08f
      // 045: bipush 0
      // 046: istore 11
      // 048: bipush 0
      // 049: istore 13
      // 04b: iload 13
      // 04d: aload 0
      // 04e: getfield net/rim/device/apps/api/transmission/DefaultSendThread$SendThreadCollector._packetQueue Ljava/util/Vector;
      // 051: invokevirtual java/util/Vector.size ()I
      // 054: if_icmpge 07b
      // 057: aload 0
      // 058: getfield net/rim/device/apps/api/transmission/DefaultSendThread$SendThreadCollector._packetQueue Ljava/util/Vector;
      // 05b: iload 13
      // 05d: invokevirtual java/util/Vector.elementAt (I)Ljava/lang/Object;
      // 060: checkcast net/rim/device/apps/api/transmission/DefaultSendThread$SendThreadDataContainer
      // 063: astore 12
      // 065: aload 12
      // 067: getfield net/rim/device/apps/api/transmission/DefaultSendThread$SendThreadDataContainer._profile Lnet/rim/device/apps/api/transmission/DefaultSendThread;
      // 06a: aload 8
      // 06c: if_acmpne 075
      // 06f: bipush 1
      // 070: istore 11
      // 072: goto 07b
      // 075: iinc 13 1
      // 078: goto 04b
      // 07b: aconst_null
      // 07c: astore 12
      // 07e: iload 11
      // 080: ifne 08f
      // 083: aload 8
      // 085: bipush 0
      // 086: putfield net/rim/device/apps/api/transmission/DefaultSendThread._closeConnectionAfterPacketsFlushed Z
      // 089: aload 8
      // 08b: bipush 0
      // 08c: invokevirtual net/rim/device/apps/api/transmission/DefaultSendThread.closeConnection (Z)V
      // 08f: aload 0
      // 090: getfield net/rim/device/apps/api/transmission/DefaultSendThread$SendThreadCollector._packetQueue Ljava/util/Vector;
      // 093: invokevirtual java/util/Vector.size ()I
      // 096: ifle 0c2
      // 099: aload 0
      // 09a: getfield net/rim/device/apps/api/transmission/DefaultSendThread$SendThreadCollector._packetQueue Ljava/util/Vector;
      // 09d: bipush 0
      // 09e: invokevirtual java/util/Vector.elementAt (I)Ljava/lang/Object;
      // 0a1: checkcast net/rim/device/apps/api/transmission/DefaultSendThread$SendThreadDataContainer
      // 0a4: astore 9
      // 0a6: aload 0
      // 0a7: getfield net/rim/device/apps/api/transmission/DefaultSendThread$SendThreadCollector._packetQueue Ljava/util/Vector;
      // 0aa: bipush 0
      // 0ab: invokevirtual java/util/Vector.removeElementAt (I)V
      // 0ae: aload 9
      // 0b0: getfield net/rim/device/apps/api/transmission/DefaultSendThread$SendThreadDataContainer._packet Lnet/rim/device/apps/api/transmission/Packet;
      // 0b3: astore 4
      // 0b5: aload 9
      // 0b7: getfield net/rim/device/apps/api/transmission/DefaultSendThread$SendThreadDataContainer._profile Lnet/rim/device/apps/api/transmission/DefaultSendThread;
      // 0ba: astore 8
      // 0bc: aconst_null
      // 0bd: astore 9
      // 0bf: goto 0dd
      // 0c2: aconst_null
      // 0c3: astore 8
      // 0c5: aconst_null
      // 0c6: astore 4
      // 0c8: ldc2_w -5359313744971625388
      // 0cb: ldc_w 1400333936
      // 0ce: bipush 5
      // 0d0: invokestatic net/rim/device/api/system/EventLogger.logEvent (JII)Z
      // 0d3: pop
      // 0d4: aload 0
      // 0d5: invokevirtual java/lang/Object.wait ()V
      // 0d8: goto 0dd
      // 0db: astore 11
      // 0dd: aload 10
      // 0df: monitorexit
      // 0e0: goto 0eb
      // 0e3: astore 14
      // 0e5: aload 10
      // 0e7: monitorexit
      // 0e8: aload 14
      // 0ea: athrow
      // 0eb: aload 4
      // 0ed: ifnonnull 0f3
      // 0f0: goto 033
      // 0f3: aload 8
      // 0f5: getfield net/rim/device/apps/api/transmission/DefaultSendThread._stopAsSoonAsPossible Z
      // 0f8: ifeq 121
      // 0fb: aload 8
      // 0fd: ifnull 11a
      // 100: aload 8
      // 102: dup
      // 103: astore 10
      // 105: monitorenter
      // 106: aload 8
      // 108: aconst_null
      // 109: putfield net/rim/device/apps/api/transmission/DefaultSendThread._currentDatagram Lnet/rim/device/api/io/DatagramBase;
      // 10c: aload 10
      // 10e: monitorexit
      // 10f: goto 11a
      // 112: astore 15
      // 114: aload 10
      // 116: monitorexit
      // 117: aload 15
      // 119: athrow
      // 11a: aconst_null
      // 11b: astore 3
      // 11c: aconst_null
      // 11d: astore 2
      // 11e: goto 033
      // 121: aload 4
      // 123: invokevirtual net/rim/device/apps/api/transmission/Packet.getPayload ()[B
      // 126: astore 5
      // 128: aload 5
      // 12a: invokestatic net/rim/device/api/crypto/RandomSource.add (Ljava/lang/Object;)V
      // 12d: aload 8
      // 12f: getfield net/rim/device/apps/api/transmission/BaseConnectionThread._connection Ljavax/microedition/io/DatagramConnection;
      // 132: bipush 0
      // 133: invokeinterface javax/microedition/io/DatagramConnection.newDatagram (I)Ljavax/microedition/io/Datagram; 2
      // 138: checkcast net/rim/device/api/io/DatagramBase
      // 13b: astore 3
      // 13c: aload 3
      // 13d: aload 5
      // 13f: aload 4
      // 141: invokevirtual net/rim/device/apps/api/transmission/Packet.getPayloadOffset ()I
      // 144: aload 4
      // 146: invokevirtual net/rim/device/apps/api/transmission/Packet.getPayloadLength ()I
      // 149: invokevirtual net/rim/device/api/io/DatagramBase.setData ([BII)V
      // 14c: aconst_null
      // 14d: astore 5
      // 14f: goto 192
      // 152: astore 10
      // 154: aload 8
      // 156: getfield net/rim/device/apps/api/transmission/BaseConnectionThread._eventLoggerGUID J
      // 159: ldc_w 1399090787
      // 15c: bipush 1
      // 15d: invokestatic net/rim/device/api/system/EventLogger.logEvent (JII)Z
      // 160: pop
      // 161: aconst_null
      // 162: astore 5
      // 164: aload 8
      // 166: ifnull 183
      // 169: aload 8
      // 16b: dup
      // 16c: astore 11
      // 16e: monitorenter
      // 16f: aload 8
      // 171: aconst_null
      // 172: putfield net/rim/device/apps/api/transmission/DefaultSendThread._currentDatagram Lnet/rim/device/api/io/DatagramBase;
      // 175: aload 11
      // 177: monitorexit
      // 178: goto 183
      // 17b: astore 16
      // 17d: aload 11
      // 17f: monitorexit
      // 180: aload 16
      // 182: athrow
      // 183: aconst_null
      // 184: astore 3
      // 185: aconst_null
      // 186: astore 2
      // 187: goto 033
      // 18a: astore 17
      // 18c: aconst_null
      // 18d: astore 5
      // 18f: aload 17
      // 191: athrow
      // 192: aload 4
      // 194: invokevirtual net/rim/device/apps/api/transmission/Packet.getContextObject ()Ljava/lang/Object;
      // 197: astore 2
      // 198: aload 2
      // 199: instanceof net/rim/device/apps/api/framework/model/ContextObject
      // 19c: ifne 1a2
      // 19f: goto 20e
      // 1a2: aload 2
      // 1a3: ldc2_w -7981905408958106750
      // 1a6: invokestatic net/rim/device/apps/api/framework/model/ContextObject.get (Ljava/lang/Object;J)Ljava/lang/Object;
      // 1a9: checkcast net/rim/device/api/io/DatagramAddressBase
      // 1ac: astore 10
      // 1ae: aload 10
      // 1b0: ifnonnull 1cd
      // 1b3: aload 2
      // 1b4: ldc2_w -5971550291443523639
      // 1b7: invokestatic net/rim/device/apps/api/framework/model/ContextObject.get (Ljava/lang/Object;J)Ljava/lang/Object;
      // 1ba: checkcast java/lang/String
      // 1bd: astore 11
      // 1bf: aload 11
      // 1c1: ifnull 1d3
      // 1c4: aload 3
      // 1c5: aload 11
      // 1c7: invokevirtual net/rim/device/api/io/DatagramBase.setAddress (Ljava/lang/String;)V
      // 1ca: goto 1d3
      // 1cd: aload 3
      // 1ce: aload 10
      // 1d0: invokevirtual net/rim/device/api/io/DatagramBase.setAddressBase (Lnet/rim/device/api/io/DatagramAddressBase;)V
      // 1d3: aload 3
      // 1d4: dup
      // 1d5: instanceof net/rim/device/cldc/io/gme/GMEDatagram
      // 1d8: ifne 1df
      // 1db: pop
      // 1dc: goto 1f2
      // 1df: checkcast net/rim/device/cldc/io/gme/GMEDatagram
      // 1e2: aload 2
      // 1e3: ldc2_w -7050660451800027507
      // 1e6: invokestatic net/rim/device/apps/api/framework/model/ContextObject.get (Ljava/lang/Object;J)Ljava/lang/Object;
      // 1e9: checkcast [Lnet/rim/device/api/servicebook/ServiceRecord;
      // 1ec: checkcast [Lnet/rim/device/api/servicebook/ServiceRecord;
      // 1ef: invokevirtual net/rim/device/cldc/io/gme/GMEDatagram.setServiceRecordOverride ([Lnet/rim/device/api/servicebook/ServiceRecord;)V
      // 1f2: aload 2
      // 1f3: bipush 75
      // 1f5: invokestatic net/rim/device/apps/api/framework/model/ContextObject.getFlag (Ljava/lang/Object;I)Z
      // 1f8: ifeq 202
      // 1fb: aload 3
      // 1fc: bipush 16
      // 1fe: bipush 1
      // 1ff: invokevirtual net/rim/device/api/io/DatagramBase.setFlag (IZ)V
      // 202: aload 2
      // 203: ldc2_w -6151522474633543992
      // 206: invokestatic net/rim/device/apps/api/framework/model/ContextObject.get (Ljava/lang/Object;J)Ljava/lang/Object;
      // 209: checkcast java/lang/String
      // 20c: astore 7
      // 20e: aload 8
      // 210: getfield net/rim/device/apps/api/transmission/BaseConnectionThread._connection Ljavax/microedition/io/DatagramConnection;
      // 213: dup
      // 214: instanceof net/rim/device/api/io/DatagramConnectionBase
      // 217: ifne 21e
      // 21a: pop
      // 21b: goto 23d
      // 21e: checkcast net/rim/device/api/io/DatagramConnectionBase
      // 221: checkcast net/rim/device/api/io/DatagramConnectionBase
      // 224: aload 3
      // 225: invokevirtual net/rim/device/api/io/DatagramConnectionBase.allocateDatagramId (Ljavax/microedition/io/Datagram;)I
      // 228: istore 6
      // 22a: aload 8
      // 22c: getfield net/rim/device/apps/api/transmission/DefaultSendThread._dgId2ListenerTag Lnet/rim/device/api/util/IntIntHashtable;
      // 22f: iload 6
      // 231: aload 4
      // 233: invokevirtual net/rim/device/apps/api/transmission/Packet.getTag ()I
      // 236: invokevirtual net/rim/device/api/util/IntIntHashtable.put (II)I
      // 239: pop
      // 23a: goto 240
      // 23d: bipush 0
      // 23e: istore 6
      // 240: aload 4
      // 242: invokevirtual net/rim/device/apps/api/transmission/Packet.getListener ()Ljava/lang/Object;
      // 245: dup
      // 246: astore 1
      // 247: ifnull 26d
      // 24a: aload 8
      // 24c: getfield net/rim/device/apps/api/transmission/DefaultSendThread._listenersByPacketTag Lnet/rim/device/api/util/IntHashtable;
      // 24f: dup
      // 250: astore 10
      // 252: monitorenter
      // 253: aload 8
      // 255: getfield net/rim/device/apps/api/transmission/DefaultSendThread._listenersByPacketTag Lnet/rim/device/api/util/IntHashtable;
      // 258: iload 6
      // 25a: aload 1
      // 25b: invokevirtual net/rim/device/api/util/IntHashtable.put (ILjava/lang/Object;)Ljava/lang/Object;
      // 25e: pop
      // 25f: aload 10
      // 261: monitorexit
      // 262: goto 26d
      // 265: astore 18
      // 267: aload 10
      // 269: monitorexit
      // 26a: aload 18
      // 26c: athrow
      // 26d: aload 8
      // 26f: dup
      // 270: astore 10
      // 272: monitorenter
      // 273: aload 8
      // 275: aload 3
      // 276: putfield net/rim/device/apps/api/transmission/DefaultSendThread._currentDatagram Lnet/rim/device/api/io/DatagramBase;
      // 279: aload 10
      // 27b: monitorexit
      // 27c: goto 287
      // 27f: astore 19
      // 281: aload 10
      // 283: monitorexit
      // 284: aload 19
      // 286: athrow
      // 287: aload 8
      // 289: getfield net/rim/device/apps/api/transmission/BaseConnectionThread._eventLoggerGUID J
      // 28c: ldc_w 1415082868
      // 28f: aload 3
      // 290: invokevirtual net/rim/device/api/io/DatagramBase.getDatagramId ()I
      // 293: bipush 10
      // 295: bipush 0
      // 296: invokestatic net/rim/device/api/system/EventLogger.logEvent (JIIII)Z
      // 299: pop
      // 29a: aload 8
      // 29c: getfield net/rim/device/apps/api/transmission/BaseConnectionThread._connection Ljavax/microedition/io/DatagramConnection;
      // 29f: aload 3
      // 2a0: invokeinterface javax/microedition/io/DatagramConnection.send (Ljavax/microedition/io/Datagram;)V 2
      // 2a5: aload 8
      // 2a7: getfield net/rim/device/apps/api/transmission/DefaultSendThread._tLogger Lnet/rim/device/internal/io/TrafficLogger;
      // 2aa: ifnull 2dc
      // 2ad: aload 8
      // 2af: getfield net/rim/device/apps/api/transmission/DefaultSendThread._tLogger Lnet/rim/device/internal/io/TrafficLogger;
      // 2b2: aload 8
      // 2b4: getfield net/rim/device/apps/api/transmission/DefaultSendThread._service Lnet/rim/device/apps/api/transmission/TransmissionService;
      // 2b7: bipush 1
      // 2b8: aload 7
      // 2ba: aload 4
      // 2bc: invokevirtual net/rim/device/apps/api/transmission/Packet.getPayloadLength ()I
      // 2bf: aload 4
      // 2c1: invokevirtual net/rim/device/apps/api/transmission/Packet.getPayload ()[B
      // 2c4: invokeinterface net/rim/device/internal/io/TrafficLogger.bytesTransmitted (Ljava/lang/Object;ILjava/lang/String;I[B)V 6
      // 2c9: goto 2dc
      // 2cc: astore 10
      // 2ce: aload 8
      // 2d0: getfield net/rim/device/apps/api/transmission/BaseConnectionThread._eventLoggerGUID J
      // 2d3: ldc_w 1399418725
      // 2d6: bipush 2
      // 2d8: invokestatic net/rim/device/api/system/EventLogger.logEvent (JII)Z
      // 2db: pop
      // 2dc: aload 8
      // 2de: ifnull 2fb
      // 2e1: aload 8
      // 2e3: dup
      // 2e4: astore 10
      // 2e6: monitorenter
      // 2e7: aload 8
      // 2e9: aconst_null
      // 2ea: putfield net/rim/device/apps/api/transmission/DefaultSendThread._currentDatagram Lnet/rim/device/api/io/DatagramBase;
      // 2ed: aload 10
      // 2ef: monitorexit
      // 2f0: goto 2fb
      // 2f3: astore 20
      // 2f5: aload 10
      // 2f7: monitorexit
      // 2f8: aload 20
      // 2fa: athrow
      // 2fb: aconst_null
      // 2fc: astore 3
      // 2fd: aconst_null
      // 2fe: astore 2
      // 2ff: goto 033
      // 302: astore 10
      // 304: aload 8
      // 306: ifnull 323
      // 309: aload 8
      // 30b: dup
      // 30c: astore 10
      // 30e: monitorenter
      // 30f: aload 8
      // 311: aconst_null
      // 312: putfield net/rim/device/apps/api/transmission/DefaultSendThread._currentDatagram Lnet/rim/device/api/io/DatagramBase;
      // 315: aload 10
      // 317: monitorexit
      // 318: goto 323
      // 31b: astore 21
      // 31d: aload 10
      // 31f: monitorexit
      // 320: aload 21
      // 322: athrow
      // 323: aconst_null
      // 324: astore 3
      // 325: aconst_null
      // 326: astore 2
      // 327: goto 033
      // 32a: astore 22
      // 32c: aload 8
      // 32e: ifnull 34b
      // 331: aload 8
      // 333: dup
      // 334: astore 23
      // 336: monitorenter
      // 337: aload 8
      // 339: aconst_null
      // 33a: putfield net/rim/device/apps/api/transmission/DefaultSendThread._currentDatagram Lnet/rim/device/api/io/DatagramBase;
      // 33d: aload 23
      // 33f: monitorexit
      // 340: goto 34b
      // 343: astore 24
      // 345: aload 23
      // 347: monitorexit
      // 348: aload 24
      // 34a: athrow
      // 34b: aconst_null
      // 34c: astore 3
      // 34d: aconst_null
      // 34e: astore 2
      // 34f: aload 22
      // 351: athrow
      // try (99 -> 106): 107 null
      // try (33 -> 110): 111 null
      // try (111 -> 114): 111 null
      // try (128 -> 133): 134 null
      // try (134 -> 137): 134 null
      // try (144 -> 162): 165 null
      // try (180 -> 185): 186 null
      // try (186 -> 189): 186 null
      // try (144 -> 162): 196 null
      // try (165 -> 172): 196 null
      // try (196 -> 197): 196 null
      // try (287 -> 295): 296 null
      // try (296 -> 299): 296 null
      // try (305 -> 310): 311 null
      // try (311 -> 314): 311 null
      // try (316 -> 343): 344 null
      // try (357 -> 362): 363 null
      // try (363 -> 366): 363 null
      // try (29 -> 122): 373 null
      // try (144 -> 174): 373 null
      // try (196 -> 351): 373 null
      // try (380 -> 385): 386 null
      // try (386 -> 389): 386 null
      // try (29 -> 122): 396 null
      // try (144 -> 174): 396 null
      // try (196 -> 351): 396 null
      // try (373 -> 374): 396 null
      // try (403 -> 408): 409 null
      // try (409 -> 412): 409 null
      // try (396 -> 397): 396 null
   }

   static {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      if (ar != null) {
         _collector = (DefaultSendThread$SendThreadCollector)ar.getOrWaitFor(-32340375862732252L);
         if (_collector == null) {
            _collector = new DefaultSendThread$SendThreadCollector();
            ar.put(-32340375862732252L, _collector);
            ProtocolDaemon.getInstance().startThread(_collector);
            return;
         }
      } else {
         _collector = new DefaultSendThread$SendThreadCollector();
         ProtocolDaemon.getInstance().startThread(_collector);
      }
   }
}
