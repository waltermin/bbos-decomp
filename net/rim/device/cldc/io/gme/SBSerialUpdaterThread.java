package net.rim.device.cldc.io.gme;

import javax.microedition.io.Connector;
import javax.microedition.io.DatagramConnection;
import net.rim.device.api.io.DatagramBase;
import net.rim.device.api.servicebook.ServiceBook;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.util.DataBuffer;

public final class SBSerialUpdaterThread extends Thread {
   private ServiceBook _mySB;
   private DatagramConnection _myConn;
   private DatagramBase _dg;
   public static final long GUID = -4609695795537859281L;
   public static final String STR = "net.rim.desktop.sb";
   public static final int ERROR_FIPS_REQUIRMENT_NOT_MET = 1179799117;
   public static final int ERROR_CANNOT_CREATE_SB_CONNECTION = 1128485699;
   public static final int ERROR_UNKNOWN_EXCEPTION_SENDING_RESPONSE = 1430606674;
   public static final int ERROR_UNKNOWN_EXCEPTION_PROCESSING_PACKET = 1430605904;
   public static final int ERROR_UNABLE_TO_OBTAIN_SERVICEBOOK = 1431588691;
   public static final int ERROR_UNABLE_TO_OBTAIN_SERVICEBOOK_BAILING = 1431588691;
   public static final int ERROR_IOEXCEPTION_SENDING_RESPONSE = 1229935442;
   private static final int APP_CANNOT_OPERATE_DUE_TO_LOCK = 40966;

   public SBSerialUpdaterThread() {
      EventLogger.register(-4609695795537859281L, "net.rim.desktop.sb", 2);
      this._mySB = ServiceBook.getSB();
      this._myConn = null;
      this._dg = null;
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
      // 000: invokestatic net/rim/device/internal/system/FIPSPolicy.getFIPSLevel ()I
      // 003: bipush 3
      // 005: if_icmplt 015
      // 008: ldc2_w -4609695795537859281
      // 00b: ldc_w 1179799117
      // 00e: bipush 2
      // 010: invokestatic net/rim/device/api/system/EventLogger.logEvent (JII)Z
      // 013: pop
      // 014: return
      // 015: aload 0
      // 016: getfield net/rim/device/cldc/io/gme/SBSerialUpdaterThread._mySB Lnet/rim/device/api/servicebook/ServiceBook;
      // 019: ifnonnull 058
      // 01c: ldc2_w -4609695795537859281
      // 01f: ldc_w 1431588691
      // 022: bipush 2
      // 024: invokestatic net/rim/device/api/system/EventLogger.logEvent (JII)Z
      // 027: pop
      // 028: bipush 0
      // 029: istore 1
      // 02a: aload 0
      // 02b: getfield net/rim/device/cldc/io/gme/SBSerialUpdaterThread._mySB Lnet/rim/device/api/servicebook/ServiceBook;
      // 02e: ifnonnull 044
      // 031: iload 1
      // 032: bipush 3
      // 034: if_icmpge 044
      // 037: aload 0
      // 038: invokestatic net/rim/device/api/servicebook/ServiceBook.getSB ()Lnet/rim/device/api/servicebook/ServiceBook;
      // 03b: putfield net/rim/device/cldc/io/gme/SBSerialUpdaterThread._mySB Lnet/rim/device/api/servicebook/ServiceBook;
      // 03e: iinc 1 1
      // 041: goto 02a
      // 044: aload 0
      // 045: getfield net/rim/device/cldc/io/gme/SBSerialUpdaterThread._mySB Lnet/rim/device/api/servicebook/ServiceBook;
      // 048: ifnonnull 058
      // 04b: ldc2_w -4609695795537859281
      // 04e: ldc_w 1431588691
      // 051: bipush 2
      // 053: invokestatic net/rim/device/api/system/EventLogger.logEvent (JII)Z
      // 056: pop
      // 057: return
      // 058: aload 0
      // 059: invokespecial net/rim/device/cldc/io/gme/SBSerialUpdaterThread.initConnection ()V
      // 05c: goto 06d
      // 05f: astore 3
      // 060: ldc2_w -4609695795537859281
      // 063: ldc_w 1128485699
      // 066: bipush 2
      // 068: invokestatic net/rim/device/api/system/EventLogger.logEvent (JII)Z
      // 06b: pop
      // 06c: return
      // 06d: aload 0
      // 06e: invokespecial net/rim/device/cldc/io/gme/SBSerialUpdaterThread.getNextPacketData ()Lnet/rim/device/api/util/DataBuffer;
      // 071: astore 1
      // 072: goto 079
      // 075: astore 3
      // 076: goto 06d
      // 079: aload 0
      // 07a: getfield net/rim/device/cldc/io/gme/SBSerialUpdaterThread._mySB Lnet/rim/device/api/servicebook/ServiceBook;
      // 07d: bipush 1
      // 07e: invokevirtual net/rim/device/api/servicebook/ServiceBook.setSerialInjectionInProgress (Z)V
      // 081: bipush 1
      // 082: istore 3
      // 083: invokestatic net/rim/device/internal/provisioning/ActivationService.getInstanceNoWait ()Lnet/rim/device/internal/provisioning/ActivationService;
      // 086: astore 4
      // 088: aload 4
      // 08a: ifnull 09a
      // 08d: aload 4
      // 08f: invokevirtual net/rim/device/internal/provisioning/ActivationService.isAnyTransactionInProgress ()Z
      // 092: ifeq 09a
      // 095: bipush 0
      // 096: istore 3
      // 097: goto 1ae
      // 09a: aload 1
      // 09b: invokevirtual net/rim/device/api/util/DataBuffer.rewind ()V
      // 09e: invokestatic net/rim/device/internal/crypto/CryptoBlock.areMasterKeysAvailable ()Z
      // 0a1: ifne 144
      // 0a4: aload 0
      // 0a5: getfield net/rim/device/cldc/io/gme/SBSerialUpdaterThread._myConn Ljavax/microedition/io/DatagramConnection;
      // 0a8: checkcast net/rim/device/cldc/io/commlink/Protocol
      // 0ab: astore 5
      // 0ad: aload 5
      // 0af: invokevirtual net/rim/device/cldc/io/commlink/Protocol.popStatusScreen ()V
      // 0b2: invokestatic net/rim/device/api/system/Alert.isBuzzerSupported ()Z
      // 0b5: ifeq 113
      // 0b8: bipush 12
      // 0ba: newarray 9
      // 0bc: dup
      // 0bd: bipush 0
      // 0be: sipush 440
      // 0c1: sastore
      // 0c2: dup
      // 0c3: bipush 1
      // 0c4: bipush 100
      // 0c6: sastore
      // 0c7: dup
      // 0c8: bipush 2
      // 0c9: sipush 880
      // 0cc: sastore
      // 0cd: dup
      // 0ce: bipush 3
      // 0cf: bipush 100
      // 0d1: sastore
      // 0d2: dup
      // 0d3: bipush 4
      // 0d4: sipush 1760
      // 0d7: sastore
      // 0d8: dup
      // 0d9: bipush 5
      // 0da: bipush 100
      // 0dc: sastore
      // 0dd: dup
      // 0de: bipush 6
      // 0e0: sipush 256
      // 0e3: sastore
      // 0e4: dup
      // 0e5: bipush 7
      // 0e7: sipush 3171
      // 0ea: sastore
      // 0eb: dup
      // 0ec: bipush 8
      // 0ee: sipush -30099
      // 0f1: sastore
      // 0f2: dup
      // 0f3: bipush 9
      // 0f5: sipush 27714
      // 0f8: sastore
      // 0f9: dup
      // 0fa: bipush 10
      // 0fc: sipush 27529
      // 0ff: sastore
      // 100: dup
      // 101: bipush 11
      // 103: sipush -23005
      // 106: sastore
      // 107: astore 6
      // 109: aload 6
      // 10b: bipush 100
      // 10d: invokestatic net/rim/device/api/system/Alert.startBuzzer ([SI)V
      // 110: goto 135
      // 113: invokestatic net/rim/device/api/system/Alert.isMIDISupported ()Z
      // 116: ifeq 135
      // 119: invokestatic net/rim/device/resources/Resource.getResourceClass ()Lnet/rim/device/resources/Resource;
      // 11c: astore 6
      // 11e: aload 6
      // 120: ldc_w "ContentProtectionNotification.mid"
      // 123: invokevirtual net/rim/device/resources/Resource.getResource (Ljava/lang/String;)[B
      // 126: astore 7
      // 128: bipush 100
      // 12a: invokestatic net/rim/device/api/system/Alert.setVolume (I)V
      // 12d: aload 7
      // 12f: bipush 2
      // 131: invokestatic net/rim/device/api/system/Alert.startMIDI ([BI)I
      // 134: pop
      // 135: invokestatic net/rim/device/api/system/ApplicationManager.getApplicationManager ()Lnet/rim/device/api/system/ApplicationManager;
      // 138: astore 6
      // 13a: aload 6
      // 13c: invokevirtual net/rim/device/api/system/ApplicationManager.unlockSystem ()V
      // 13f: aload 5
      // 141: invokevirtual net/rim/device/cldc/io/commlink/Protocol.pushStatusScreen ()V
      // 144: aload 0
      // 145: getfield net/rim/device/cldc/io/gme/SBSerialUpdaterThread._mySB Lnet/rim/device/api/servicebook/ServiceBook;
      // 148: aload 1
      // 149: bipush 1
      // 14a: aconst_null
      // 14b: aconst_null
      // 14c: invokevirtual net/rim/device/api/servicebook/ServiceBook.processServiceBookCommandData (Lnet/rim/device/api/util/DataBuffer;ILjava/lang/String;Ljava/lang/String;)[B
      // 14f: astore 5
      // 151: bipush 0
      // 152: istore 3
      // 153: aload 5
      // 155: ifnull 1ae
      // 158: aload 5
      // 15a: arraylength
      // 15b: ifle 1ae
      // 15e: new net/rim/device/api/util/DataBuffer
      // 161: dup
      // 162: aload 5
      // 164: bipush 0
      // 165: aload 5
      // 167: arraylength
      // 168: bipush 1
      // 169: invokespecial net/rim/device/api/util/DataBuffer.<init> ([BIIZ)V
      // 16c: astore 6
      // 16e: aload 6
      // 170: invokevirtual net/rim/device/api/util/DataBuffer.readByte ()B
      // 173: pop
      // 174: aload 6
      // 176: invokevirtual net/rim/device/api/util/DataBuffer.readCompressedInt ()I
      // 179: istore 7
      // 17b: iload 7
      // 17d: ifle 1ae
      // 180: aload 6
      // 182: invokevirtual net/rim/device/api/util/DataBuffer.eof ()Z
      // 185: ifne 1ae
      // 188: aload 6
      // 18a: invokevirtual net/rim/device/api/util/DataBuffer.readByte ()B
      // 18d: pop
      // 18e: aload 6
      // 190: invokevirtual net/rim/device/api/util/DataBuffer.readByte ()B
      // 193: istore 8
      // 195: aload 6
      // 197: invokevirtual net/rim/device/api/util/DataBuffer.readCompressedInt ()I
      // 19a: istore 9
      // 19c: aload 6
      // 19e: iload 9
      // 1a0: invokevirtual net/rim/device/api/util/DataBuffer.skipBytes (I)I
      // 1a3: pop
      // 1a4: iload 8
      // 1a6: sipush 128
      // 1a9: if_icmpne 180
      // 1ac: bipush 1
      // 1ad: istore 3
      // 1ae: iload 3
      // 1af: ifne 1c7
      // 1b2: aload 0
      // 1b3: invokespecial net/rim/device/cldc/io/gme/SBSerialUpdaterThread.sendSerialReply ()V
      // 1b6: goto 1c7
      // 1b9: astore 4
      // 1bb: ldc2_w -4609695795537859281
      // 1be: ldc_w 1430605904
      // 1c1: bipush 2
      // 1c3: invokestatic net/rim/device/api/system/EventLogger.logEvent (JII)Z
      // 1c6: pop
      // 1c7: aload 0
      // 1c8: getfield net/rim/device/cldc/io/gme/SBSerialUpdaterThread._mySB Lnet/rim/device/api/servicebook/ServiceBook;
      // 1cb: bipush 0
      // 1cc: invokevirtual net/rim/device/api/servicebook/ServiceBook.setSerialInjectionInProgress (Z)V
      // 1cf: aload 0
      // 1d0: getfield net/rim/device/cldc/io/gme/SBSerialUpdaterThread._dg Lnet/rim/device/api/io/DatagramBase;
      // 1d3: invokevirtual net/rim/device/api/io/DatagramBase.reset ()V
      // 1d6: aload 0
      // 1d7: getfield net/rim/device/cldc/io/gme/SBSerialUpdaterThread._dg Lnet/rim/device/api/io/DatagramBase;
      // 1da: iload 3
      // 1db: invokevirtual net/rim/device/api/util/DataBuffer.writeShort (I)V
      // 1de: aload 0
      // 1df: getfield net/rim/device/cldc/io/gme/SBSerialUpdaterThread._dg Lnet/rim/device/api/io/DatagramBase;
      // 1e2: bipush 1
      // 1e3: bipush 1
      // 1e4: invokevirtual net/rim/device/api/io/DatagramBase.setFlag (IZ)V
      // 1e7: aload 0
      // 1e8: getfield net/rim/device/cldc/io/gme/SBSerialUpdaterThread._myConn Ljavax/microedition/io/DatagramConnection;
      // 1eb: aload 0
      // 1ec: getfield net/rim/device/cldc/io/gme/SBSerialUpdaterThread._dg Lnet/rim/device/api/io/DatagramBase;
      // 1ef: invokeinterface javax/microedition/io/DatagramConnection.send (Ljavax/microedition/io/Datagram;)V 2
      // 1f4: goto 205
      // 1f7: astore 4
      // 1f9: ldc2_w -4609695795537859281
      // 1fc: ldc_w 1229935442
      // 1ff: bipush 2
      // 201: invokestatic net/rim/device/api/system/EventLogger.logEvent (JII)Z
      // 204: pop
      // 205: aload 0
      // 206: getfield net/rim/device/cldc/io/gme/SBSerialUpdaterThread._dg Lnet/rim/device/api/io/DatagramBase;
      // 209: invokevirtual net/rim/device/api/io/DatagramBase.reset ()V
      // 20c: aconst_null
      // 20d: astore 1
      // 20e: goto 06d
      // 211: astore 3
      // 212: ldc2_w -4609695795537859281
      // 215: ldc_w 1430606674
      // 218: bipush 2
      // 21a: invokestatic net/rim/device/api/system/EventLogger.logEvent (JII)Z
      // 21d: pop
      // 21e: goto 06d
      // try (39 -> 41): 42 null
      // try (49 -> 52): 53 null
      // try (61 -> 215): 216 null
      // try (229 -> 243): 244 null
      // try (49 -> 54): 256 null
      // try (55 -> 255): 256 null
   }

   private final void initConnection() {
      this._myConn = (DatagramConnection)Connector.open("commlink:Service Book");
   }

   private final DataBuffer getNextPacketData() {
      if (this._dg == null) {
         this._dg = (DatagramBase)this._myConn.newDatagram(1);
      }

      do {
         this._myConn.receive(this._dg);
      } while (this._dg.isFlagSet(2) || this._dg.isFlagSet(4) || this._dg.isFlagSet(1));

      return this._dg;
   }

   private final void sendSerialReply() {
      DatagramBase sendMe = (DatagramBase)this._myConn.newDatagram(6);
      sendMe.writeByte(3);
      sendMe.setBigEndian(false);
      sendMe.writeInt(1);
      sendMe.setBigEndian(true);
      sendMe.writeByte(0);
      this._myConn.send(sendMe);

      do {
         this._myConn.receive(this._dg);
      } while (!this._dg.isFlagSet(1));
   }
}
