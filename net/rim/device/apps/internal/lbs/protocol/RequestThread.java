package net.rim.device.apps.internal.lbs.protocol;

import net.rim.device.api.servicebook.ServiceBook;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.util.Arrays;
import net.rim.device.apps.internal.lbs.LBSApplication;
import net.rim.device.apps.internal.lbs.LBSOptions;
import net.rim.device.apps.internal.lbs.maplet.MapletCache;
import net.rim.device.cldc.io.ippp.SBApplicationData;

final class RequestThread extends Thread implements GlobalEventListener {
   private Request[] _pending = new Request[0];
   private String _uid;
   private String _mdsUID;
   private boolean _isBISAvailable = false;
   private boolean _isBESAvailable = false;
   private String _radioErrorMsg = null;
   private String _coverageErrorMsg = null;
   private boolean _outOfCoverage = false;
   private boolean _radioTurnedOn = false;
   private boolean _stop = false;
   private static RequestThread _instance = new RequestThread();
   private static boolean _mapReqRadioChecked = false;
   private static long _503ErrorTimer = -1;
   private static final byte CLIENT_VERSION = 20;
   private static final int TYPE_IPPP_TYPE = 6;
   private static final int IPPP_TYPE_CORPORATE = 0;
   private static final int IPPP_TYPE_PUBLIC = 1;
   private static final int IPPP_TYPE_PROVISIONING = 2;

   RequestThread() {
      this.setPriority(3);
      this._uid = this.findUid();
      Application.getApplication().addGlobalEventListener(this);
      this.start();
      this._mdsUID = LBSOptions.getString(4638775647628867689L, null);
   }

   static final void addRequest(Request request) {
      _instance.internalAddRequest(request);
   }

   private final synchronized void internalAddRequest(Request request) {
      synchronized (this._pending) {
         Arrays.add(this._pending, request);
      }

      this.notify();
   }

   final int getServiceRecordType(ServiceRecord param1) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 1
      // 01: invokevirtual net/rim/device/api/servicebook/ServiceRecord.getApplicationData ()[B
      // 04: astore 2
      // 05: aload 2
      // 06: ifnonnull 0b
      // 09: bipush 0
      // 0a: ireturn
      // 0b: new java/io/ByteArrayInputStream
      // 0e: dup
      // 0f: aload 2
      // 10: invokespecial java/io/ByteArrayInputStream.<init> ([B)V
      // 13: astore 3
      // 14: new java/io/DataInputStream
      // 17: dup
      // 18: aload 3
      // 19: invokespecial java/io/DataInputStream.<init> (Ljava/io/InputStream;)V
      // 1c: astore 4
      // 1e: bipush 0
      // 1f: istore 5
      // 21: iload 5
      // 23: ifne 90
      // 26: aload 4
      // 28: invokevirtual java/io/DataInputStream.readShort ()S
      // 2b: istore 6
      // 2d: aload 4
      // 2f: invokevirtual java/io/DataInputStream.readByte ()B
      // 32: istore 7
      // 34: iload 7
      // 36: bipush 6
      // 38: if_icmpne 74
      // 3b: iload 6
      // 3d: newarray 8
      // 3f: astore 8
      // 41: aload 4
      // 43: aload 8
      // 45: invokevirtual java/io/DataInputStream.readFully ([B)V
      // 48: bipush 0
      // 49: istore 9
      // 4b: bipush 0
      // 4c: istore 10
      // 4e: iload 10
      // 50: aload 8
      // 52: arraylength
      // 53: if_icmpge 71
      // 56: iload 9
      // 58: bipush 8
      // 5a: ishl
      // 5b: istore 9
      // 5d: iload 9
      // 5f: aload 8
      // 61: iload 10
      // 63: baload
      // 64: sipush 255
      // 67: iand
      // 68: ior
      // 69: istore 9
      // 6b: iinc 10 1
      // 6e: goto 4e
      // 71: iload 9
      // 73: ireturn
      // 74: aload 4
      // 76: iload 6
      // 78: i2l
      // 79: invokevirtual java/io/DataInputStream.skip (J)J
      // 7c: pop2
      // 7d: goto 21
      // 80: astore 6
      // 82: bipush 1
      // 83: istore 5
      // 85: goto 21
      // 88: astore 6
      // 8a: bipush 1
      // 8b: istore 5
      // 8d: goto 21
      // 90: bipush 0
      // 91: ireturn
      // try (21 -> 59): 66 null
      // try (60 -> 65): 66 null
      // try (21 -> 59): 70 null
      // try (60 -> 65): 70 null
   }

   final boolean isMDSUIDFound(String mdsUID) {
      ServiceBook sb = ServiceBook.getSB();

      for (ServiceRecord sr : sb.findRecordsByCid("IPPP")) {
         if (mdsUID.equals(sr.getUid())) {
            return true;
         }
      }

      return false;
   }

   final String findUid() {
      if (!LBSOptions.getBoolean(2585038783968687563L, true)) {
         return null;
      }

      String publicUid = null;
      String corporateUid = null;
      ServiceBook sb = ServiceBook.getSB();

      for (ServiceRecord sr : sb.findRecordsByCid("IPPP")) {
         switch (this.getServiceRecordType(sr)) {
            case -1:
               break;
            case 0:
               if (corporateUid == null) {
                  corporateUid = sr.getUid();
               }
               break;
            case 1:
            default:
               if (publicUid == null) {
                  publicUid = sr.getUid();
               }
         }
      }

      String uid = publicUid != null ? publicUid : corporateUid;
      EventLogger.logEvent(LBSApplication.UID, ("LBS UID = " + uid).getBytes(), 5);
      return uid;
   }

   final String appendURLOptions(String url) {
      if (DeviceInfo.isSimulator()) {
         return url + ";deviceside=true";
      }

      if (this._uid != null) {
         url = url + ";deviceside=false;ConnectionUID=" + this._uid;
      }

      return url;
   }

   private final boolean isBESRoutable() {
      ServiceBook sb = ServiceBook.getSB();
      ServiceRecord[] _ipppServiceRecords = sb.findRecordsByCid("IPPP");

      for (int i = _ipppServiceRecords.length - 1; i >= 0; i--) {
         ServiceRecord record = _ipppServiceRecords[i];

         try {
            int ipppType = new SBApplicationData(record).getValueAsInt(6);
            if (ipppType == 0) {
               return true;
            }
         } finally {
            continue;
         }
      }

      return false;
   }

   private final boolean isBISRoutable() {
      ServiceBook sb = ServiceBook.getSB();
      ServiceRecord[] _ipppServiceRecords = sb.findRecordsByCid("IPPP");

      for (int i = _ipppServiceRecords.length - 1; i >= 0; i--) {
         ServiceRecord record = _ipppServiceRecords[i];

         try {
            int ipppType = new SBApplicationData(record).getValueAsInt(6);
            if (ipppType == 1) {
               return true;
            }
         } finally {
            continue;
         }
      }

      return false;
   }

   final void submitRequest(Request param1) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: aconst_null
      // 001: astore 2
      // 002: aconst_null
      // 003: astore 3
      // 004: aconst_null
      // 005: astore 4
      // 007: bipush 1
      // 008: istore 5
      // 00a: bipush 1
      // 00b: istore 7
      // 00d: aconst_null
      // 00e: astore 8
      // 010: aload 0
      // 011: getfield net/rim/device/apps/internal/lbs/protocol/RequestThread._mdsUID Ljava/lang/String;
      // 014: ifnull 039
      // 017: aload 0
      // 018: aload 0
      // 019: getfield net/rim/device/apps/internal/lbs/protocol/RequestThread._mdsUID Ljava/lang/String;
      // 01c: invokevirtual net/rim/device/apps/internal/lbs/protocol/RequestThread.isMDSUIDFound (Ljava/lang/String;)Z
      // 01f: ifeq 02d
      // 022: aload 0
      // 023: aload 0
      // 024: getfield net/rim/device/apps/internal/lbs/protocol/RequestThread._mdsUID Ljava/lang/String;
      // 027: putfield net/rim/device/apps/internal/lbs/protocol/RequestThread._uid Ljava/lang/String;
      // 02a: goto 109
      // 02d: aload 1
      // 02e: bipush -10
      // 030: invokevirtual net/rim/device/apps/internal/lbs/protocol/Request.setResponse (I)V
      // 033: bipush 0
      // 034: istore 7
      // 036: goto 109
      // 039: invokestatic net/rim/device/api/system/RadioInfo.getActiveWAFs ()I
      // 03c: istore 9
      // 03e: iload 9
      // 040: ifne 04c
      // 043: aload 1
      // 044: bipush -5
      // 046: invokevirtual net/rim/device/apps/internal/lbs/protocol/Request.setResponse (I)V
      // 049: goto 109
      // 04c: iload 9
      // 04e: bipush 4
      // 050: iand
      // 051: bipush 4
      // 053: if_icmpne 0b8
      // 056: ldc2_w -6773447903022085068
      // 059: bipush 1
      // 05a: invokestatic net/rim/device/apps/internal/lbs/LBSOptions.getBoolean (JZ)Z
      // 05d: ifeq 07e
      // 060: aload 0
      // 061: invokespecial net/rim/device/apps/internal/lbs/protocol/RequestThread.isBISRoutable ()Z
      // 064: ifeq 072
      // 067: aload 0
      // 068: bipush 1
      // 069: invokestatic net/rim/device/cldc/io/ippp/SocketTransportBase.getFirstUidByIpppType (I)Ljava/lang/String;
      // 06c: putfield net/rim/device/apps/internal/lbs/protocol/RequestThread._uid Ljava/lang/String;
      // 06f: goto 109
      // 072: aload 1
      // 073: bipush -7
      // 075: invokevirtual net/rim/device/apps/internal/lbs/protocol/Request.setResponse (I)V
      // 078: bipush 0
      // 079: istore 7
      // 07b: goto 109
      // 07e: ldc2_w -6773447903022085068
      // 081: bipush 1
      // 082: invokestatic net/rim/device/apps/internal/lbs/LBSOptions.getBoolean (JZ)Z
      // 085: ifeq 08b
      // 088: goto 109
      // 08b: aload 0
      // 08c: invokespecial net/rim/device/apps/internal/lbs/protocol/RequestThread.isBESRoutable ()Z
      // 08f: ifeq 09a
      // 092: aload 0
      // 093: aconst_null
      // 094: putfield net/rim/device/apps/internal/lbs/protocol/RequestThread._uid Ljava/lang/String;
      // 097: goto 109
      // 09a: aload 0
      // 09b: invokespecial net/rim/device/apps/internal/lbs/protocol/RequestThread.isBISRoutable ()Z
      // 09e: ifeq 0ac
      // 0a1: aload 0
      // 0a2: bipush 1
      // 0a3: invokestatic net/rim/device/cldc/io/ippp/SocketTransportBase.getFirstUidByIpppType (I)Ljava/lang/String;
      // 0a6: putfield net/rim/device/apps/internal/lbs/protocol/RequestThread._uid Ljava/lang/String;
      // 0a9: goto 109
      // 0ac: aload 1
      // 0ad: bipush -9
      // 0af: invokevirtual net/rim/device/apps/internal/lbs/protocol/Request.setResponse (I)V
      // 0b2: bipush 0
      // 0b3: istore 7
      // 0b5: goto 109
      // 0b8: aload 0
      // 0b9: aload 0
      // 0ba: invokevirtual net/rim/device/apps/internal/lbs/protocol/RequestThread.findUid ()Ljava/lang/String;
      // 0bd: putfield net/rim/device/apps/internal/lbs/protocol/RequestThread._uid Ljava/lang/String;
      // 0c0: aload 0
      // 0c1: getfield net/rim/device/apps/internal/lbs/protocol/RequestThread._uid Ljava/lang/String;
      // 0c4: ifnonnull 0e1
      // 0c7: ldc2_w 2585038783968687563
      // 0ca: bipush 1
      // 0cb: invokestatic net/rim/device/apps/internal/lbs/LBSOptions.getBoolean (JZ)Z
      // 0ce: ifne 0e1
      // 0d1: aload 0
      // 0d2: invokespecial net/rim/device/apps/internal/lbs/protocol/RequestThread.isBESRoutable ()Z
      // 0d5: ifne 0e1
      // 0d8: aload 1
      // 0d9: bipush -8
      // 0db: invokevirtual net/rim/device/apps/internal/lbs/protocol/Request.setResponse (I)V
      // 0de: bipush 0
      // 0df: istore 7
      // 0e1: aload 0
      // 0e2: getfield net/rim/device/apps/internal/lbs/protocol/RequestThread._uid Ljava/lang/String;
      // 0e5: ifnonnull 109
      // 0e8: aload 0
      // 0e9: invokespecial net/rim/device/apps/internal/lbs/protocol/RequestThread.isBISRoutable ()Z
      // 0ec: ifne 109
      // 0ef: aload 0
      // 0f0: invokespecial net/rim/device/apps/internal/lbs/protocol/RequestThread.isBESRoutable ()Z
      // 0f3: ifne 109
      // 0f6: ldc2_w 2585038783968687563
      // 0f9: bipush 1
      // 0fa: invokestatic net/rim/device/apps/internal/lbs/LBSOptions.getBoolean (JZ)Z
      // 0fd: ifeq 109
      // 100: aload 1
      // 101: bipush -9
      // 103: invokevirtual net/rim/device/apps/internal/lbs/protocol/Request.setResponse (I)V
      // 106: bipush 0
      // 107: istore 7
      // 109: getstatic net/rim/device/apps/internal/lbs/protocol/RequestThread._503ErrorTimer J
      // 10c: bipush -1
      // 10e: i2l
      // 10f: lcmp
      // 110: ifeq 133
      // 113: invokestatic java/lang/System.currentTimeMillis ()J
      // 116: lstore 9
      // 118: lload 9
      // 11a: getstatic net/rim/device/apps/internal/lbs/protocol/RequestThread._503ErrorTimer J
      // 11d: lsub
      // 11e: invokestatic java/lang/Math.abs (J)J
      // 121: sipush 30000
      // 124: i2l
      // 125: lcmp
      // 126: ifgt 133
      // 129: aload 1
      // 12a: sipush 503
      // 12d: invokevirtual net/rim/device/apps/internal/lbs/protocol/Request.setResponse (I)V
      // 130: bipush 0
      // 131: istore 7
      // 133: aload 1
      // 134: invokevirtual net/rim/device/apps/internal/lbs/protocol/Request.getURL ()Ljava/lang/String;
      // 137: ifnonnull 143
      // 13a: aload 1
      // 13b: bipush -11
      // 13d: invokevirtual net/rim/device/apps/internal/lbs/protocol/Request.setResponse (I)V
      // 140: bipush 0
      // 141: istore 7
      // 143: iload 7
      // 145: ifne 14b
      // 148: goto 364
      // 14b: aload 1
      // 14c: getfield net/rim/device/apps/internal/lbs/protocol/Request._listener Lnet/rim/device/apps/internal/lbs/protocol/Request$Listener;
      // 14f: bipush 2
      // 151: invokeinterface net/rim/device/apps/internal/lbs/protocol/Request$Listener.setState (I)V 2
      // 156: new net/rim/device/api/util/DataBuffer
      // 159: dup
      // 15a: bipush 1
      // 15b: invokespecial net/rim/device/api/util/DataBuffer.<init> (Z)V
      // 15e: astore 9
      // 160: aload 9
      // 162: bipush 0
      // 163: invokevirtual net/rim/device/api/util/DataBuffer.writeInt (I)V
      // 166: aload 9
      // 168: bipush 20
      // 16a: invokevirtual net/rim/device/api/util/DataBuffer.writeByte (I)V
      // 16d: aload 9
      // 16f: aload 1
      // 170: invokevirtual net/rim/device/apps/internal/lbs/protocol/Request.getCommand ()B
      // 173: invokevirtual net/rim/device/api/util/DataBuffer.writeByte (I)V
      // 176: aload 9
      // 178: invokestatic net/rim/device/api/system/DeviceInfo.getDeviceId ()I
      // 17b: invokevirtual net/rim/device/api/util/DataBuffer.writeInt (I)V
      // 17e: aload 9
      // 180: bipush 0
      // 181: invokevirtual net/rim/device/api/util/DataBuffer.writeByte (I)V
      // 184: aload 1
      // 185: aload 9
      // 187: invokevirtual net/rim/device/apps/internal/lbs/protocol/Request.writeRequest (Lnet/rim/device/api/util/DataBuffer;)Z
      // 18a: ifne 190
      // 18d: goto 364
      // 190: aload 1
      // 191: bipush -1
      // 193: invokevirtual net/rim/device/apps/internal/lbs/protocol/Request.setResponse (I)V
      // 196: aload 1
      // 197: invokevirtual net/rim/device/apps/internal/lbs/protocol/Request.getURL ()Ljava/lang/String;
      // 19a: astore 10
      // 19c: aload 0
      // 19d: aload 10
      // 19f: invokevirtual net/rim/device/apps/internal/lbs/protocol/RequestThread.appendURLOptions (Ljava/lang/String;)Ljava/lang/String;
      // 1a2: astore 10
      // 1a4: new java/lang/StringBuffer
      // 1a7: dup
      // 1a8: ldc_w "Sending to "
      // 1ab: invokespecial java/lang/StringBuffer.<init> (Ljava/lang/String;)V
      // 1ae: aload 10
      // 1b0: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 1b3: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 1b6: bipush 32
      // 1b8: invokestatic net/rim/device/api/lbs/Logger.logEvent (Ljava/lang/String;I)V
      // 1bb: aload 10
      // 1bd: invokestatic javax/microedition/io/Connector.open (Ljava/lang/String;)Ljavax/microedition/io/Connection;
      // 1c0: checkcast javax/microedition/io/HttpConnection
      // 1c3: astore 2
      // 1c4: goto 1cf
      // 1c7: astore 11
      // 1c9: aload 1
      // 1ca: bipush -4
      // 1cc: invokevirtual net/rim/device/apps/internal/lbs/protocol/Request.setResponse (I)V
      // 1cf: aload 2
      // 1d0: ifnull 1e4
      // 1d3: aload 2
      // 1d4: ldc_w "POST"
      // 1d7: invokeinterface javax/microedition/io/HttpConnection.setRequestMethod (Ljava/lang/String;)V 2
      // 1dc: aload 1
      // 1dd: aload 2
      // 1de: invokevirtual net/rim/device/apps/internal/lbs/protocol/Request.setConnection (Ljavax/microedition/io/Connection;)V
      // 1e1: goto 1ea
      // 1e4: aload 1
      // 1e5: bipush -4
      // 1e7: invokevirtual net/rim/device/apps/internal/lbs/protocol/Request.setResponse (I)V
      // 1ea: aconst_null
      // 1eb: astore 11
      // 1ed: bipush 1
      // 1ee: istore 12
      // 1f0: aload 1
      // 1f1: invokevirtual net/rim/device/apps/internal/lbs/protocol/Request.getResponseCode ()I
      // 1f4: bipush -4
      // 1f6: if_icmpne 1fc
      // 1f9: goto 340
      // 1fc: aload 9
      // 1fe: invokevirtual net/rim/device/api/util/DataBuffer.getPosition ()I
      // 201: istore 6
      // 203: aload 9
      // 205: bipush 0
      // 206: invokevirtual net/rim/device/api/util/DataBuffer.setPosition (I)V
      // 209: aload 9
      // 20b: iload 6
      // 20d: bipush 4
      // 20f: isub
      // 210: invokevirtual net/rim/device/api/util/DataBuffer.writeInt (I)V
      // 213: aload 2
      // 214: invokeinterface javax/microedition/io/OutputConnection.openOutputStream ()Ljava/io/OutputStream; 1
      // 219: astore 3
      // 21a: aload 9
      // 21c: invokevirtual net/rim/device/api/util/DataBuffer.getArray ()[B
      // 21f: astore 11
      // 221: aload 3
      // 222: aload 11
      // 224: invokevirtual java/io/OutputStream.write ([B)V
      // 227: aload 3
      // 228: invokevirtual java/io/OutputStream.close ()V
      // 22b: aload 1
      // 22c: getfield net/rim/device/apps/internal/lbs/protocol/Request._listener Lnet/rim/device/apps/internal/lbs/protocol/Request$Listener;
      // 22f: bipush 3
      // 231: invokeinterface net/rim/device/apps/internal/lbs/protocol/Request$Listener.setState (I)V 2
      // 236: aload 2
      // 237: invokeinterface javax/microedition/io/InputConnection.openDataInputStream ()Ljava/io/DataInputStream; 1
      // 23c: astore 4
      // 23e: goto 268
      // 241: astore 13
      // 243: getstatic java/lang/System.err Ljava/io/PrintStream;
      // 246: new java/lang/StringBuffer
      // 249: dup
      // 24a: ldc_w "Connection error: "
      // 24d: invokespecial java/lang/StringBuffer.<init> (Ljava/lang/String;)V
      // 250: aload 13
      // 252: invokevirtual java/lang/Throwable.getMessage ()Ljava/lang/String;
      // 255: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 258: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 25b: invokevirtual java/io/PrintStream.println (Ljava/lang/String;)V
      // 25e: aload 1
      // 25f: invokevirtual net/rim/device/apps/internal/lbs/protocol/Request.isConnectionForcedClosed ()Z
      // 262: ifeq 268
      // 265: bipush 0
      // 266: istore 5
      // 268: iload 5
      // 26a: ifne 270
      // 26d: goto 340
      // 270: aload 2
      // 271: invokeinterface javax/microedition/io/HttpConnection.getResponseCode ()I 1
      // 276: istore 13
      // 278: aload 1
      // 279: iload 13
      // 27b: invokevirtual net/rim/device/apps/internal/lbs/protocol/Request.setResponse (I)V
      // 27e: iload 13
      // 280: sipush 204
      // 283: if_icmpne 28d
      // 286: aload 1
      // 287: invokevirtual net/rim/device/apps/internal/lbs/protocol/Request.emptyResponse ()V
      // 28a: goto 340
      // 28d: iload 13
      // 28f: sipush 200
      // 292: if_icmplt 2f7
      // 295: iload 13
      // 297: sipush 300
      // 29a: if_icmpge 2f7
      // 29d: new java/io/DataInputStream
      // 2a0: dup
      // 2a1: aload 4
      // 2a3: invokespecial java/io/DataInputStream.<init> (Ljava/io/InputStream;)V
      // 2a6: astore 14
      // 2a8: aload 14
      // 2aa: invokevirtual java/io/DataInputStream.readInt ()I
      // 2ad: istore 6
      // 2af: aload 1
      // 2b0: iload 6
      // 2b2: invokevirtual net/rim/device/apps/internal/lbs/protocol/Request.setLength (I)V
      // 2b5: aload 1
      // 2b6: getfield net/rim/device/apps/internal/lbs/protocol/Request._listener Lnet/rim/device/apps/internal/lbs/protocol/Request$Listener;
      // 2b9: bipush 0
      // 2ba: iload 6
      // 2bc: invokeinterface net/rim/device/apps/internal/lbs/protocol/Request$Listener.progressTick (II)V 3
      // 2c1: aload 1
      // 2c2: aload 14
      // 2c4: invokevirtual java/io/DataInputStream.readByte ()B
      // 2c7: invokevirtual net/rim/device/apps/internal/lbs/protocol/Request.setVersion (B)V
      // 2ca: aload 14
      // 2cc: invokevirtual java/io/DataInputStream.readByte ()B
      // 2cf: pop
      // 2d0: aload 14
      // 2d2: invokevirtual java/io/DataInputStream.readInt ()I
      // 2d5: pop
      // 2d6: aload 1
      // 2d7: aload 14
      // 2d9: invokevirtual java/io/DataInputStream.readByte ()B
      // 2dc: invokevirtual net/rim/device/apps/internal/lbs/protocol/Request.setResponse (I)V
      // 2df: aload 1
      // 2e0: aload 14
      // 2e2: iload 6
      // 2e4: invokevirtual net/rim/device/apps/internal/lbs/protocol/Request.readResponse (Ljava/io/DataInputStream;I)V
      // 2e7: aload 1
      // 2e8: getfield net/rim/device/apps/internal/lbs/protocol/Request._listener Lnet/rim/device/apps/internal/lbs/protocol/Request$Listener;
      // 2eb: iload 6
      // 2ed: iload 6
      // 2ef: invokeinterface net/rim/device/apps/internal/lbs/protocol/Request$Listener.progressTick (II)V 3
      // 2f4: goto 340
      // 2f7: bipush 0
      // 2f8: istore 14
      // 2fa: iload 13
      // 2fc: sipush 400
      // 2ff: if_icmpne 327
      // 302: new java/io/DataInputStream
      // 305: dup
      // 306: aload 4
      // 308: invokespecial java/io/DataInputStream.<init> (Ljava/io/InputStream;)V
      // 30b: astore 15
      // 30d: aload 15
      // 30f: invokevirtual java/io/DataInputStream.readInt ()I
      // 312: istore 6
      // 314: aload 1
      // 315: iload 6
      // 317: invokevirtual net/rim/device/apps/internal/lbs/protocol/Request.setLength (I)V
      // 31a: aload 15
      // 31c: invokevirtual java/io/DataInputStream.readByte ()B
      // 31f: istore 14
      // 321: aload 1
      // 322: iload 14
      // 324: invokevirtual net/rim/device/apps/internal/lbs/protocol/Request.setVersion (B)V
      // 327: aload 1
      // 328: invokevirtual net/rim/device/apps/internal/lbs/protocol/Request.badRequest ()V
      // 32b: goto 340
      // 32e: astore 15
      // 330: aload 1
      // 331: invokevirtual net/rim/device/apps/internal/lbs/protocol/Request.badRequest ()V
      // 334: goto 340
      // 337: astore 16
      // 339: aload 1
      // 33a: invokevirtual net/rim/device/apps/internal/lbs/protocol/Request.badRequest ()V
      // 33d: aload 16
      // 33f: athrow
      // 340: iload 12
      // 342: ifne 34b
      // 345: aload 1
      // 346: aload 11
      // 348: putfield net/rim/device/apps/internal/lbs/protocol/Request._lastRequest [B
      // 34b: aload 1
      // 34c: invokevirtual net/rim/device/apps/internal/lbs/protocol/Request.getResponseCode ()I
      // 34f: sipush 503
      // 352: if_icmpne 35e
      // 355: invokestatic java/lang/System.currentTimeMillis ()J
      // 358: putstatic net/rim/device/apps/internal/lbs/protocol/RequestThread._503ErrorTimer J
      // 35b: goto 364
      // 35e: bipush -1
      // 360: i2l
      // 361: putstatic net/rim/device/apps/internal/lbs/protocol/RequestThread._503ErrorTimer J
      // 364: aload 1
      // 365: getfield net/rim/device/apps/internal/lbs/protocol/Request._listener Lnet/rim/device/apps/internal/lbs/protocol/Request$Listener;
      // 368: aload 1
      // 369: invokeinterface net/rim/device/apps/internal/lbs/protocol/Request$Listener.requestComplete (Lnet/rim/device/apps/internal/lbs/protocol/Request;)V 2
      // 36e: aload 3
      // 36f: ifnull 37b
      // 372: aload 3
      // 373: invokevirtual java/io/OutputStream.close ()V
      // 376: goto 37b
      // 379: astore 19
      // 37b: aload 4
      // 37d: ifnull 38a
      // 380: aload 4
      // 382: invokevirtual java/io/InputStream.close ()V
      // 385: goto 38a
      // 388: astore 19
      // 38a: aload 2
      // 38b: ifnull 399
      // 38e: aload 2
      // 38f: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 394: goto 399
      // 397: astore 19
      // 399: aload 1
      // 39a: aconst_null
      // 39b: putfield net/rim/device/apps/internal/lbs/protocol/Request._lastRequest [B
      // 39e: goto 4c5
      // 3a1: astore 9
      // 3a3: aload 1
      // 3a4: invokevirtual net/rim/device/apps/internal/lbs/protocol/Request.isConnectionForcedClosed ()Z
      // 3a7: ifne 3c9
      // 3aa: getstatic java/lang/System.out Ljava/io/PrintStream;
      // 3ad: new java/lang/StringBuffer
      // 3b0: dup
      // 3b1: ldc_w "EOFException: "
      // 3b4: invokespecial java/lang/StringBuffer.<init> (Ljava/lang/String;)V
      // 3b7: aload 9
      // 3b9: invokevirtual java/lang/Throwable.getMessage ()Ljava/lang/String;
      // 3bc: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 3bf: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 3c2: invokevirtual java/io/PrintStream.println (Ljava/lang/String;)V
      // 3c5: aload 9
      // 3c7: astore 8
      // 3c9: aload 3
      // 3ca: ifnull 3d6
      // 3cd: aload 3
      // 3ce: invokevirtual java/io/OutputStream.close ()V
      // 3d1: goto 3d6
      // 3d4: astore 19
      // 3d6: aload 4
      // 3d8: ifnull 3e5
      // 3db: aload 4
      // 3dd: invokevirtual java/io/InputStream.close ()V
      // 3e0: goto 3e5
      // 3e3: astore 19
      // 3e5: aload 2
      // 3e6: ifnull 3f4
      // 3e9: aload 2
      // 3ea: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 3ef: goto 3f4
      // 3f2: astore 19
      // 3f4: aload 1
      // 3f5: aconst_null
      // 3f6: putfield net/rim/device/apps/internal/lbs/protocol/Request._lastRequest [B
      // 3f9: goto 4c5
      // 3fc: astore 9
      // 3fe: aload 9
      // 400: astore 8
      // 402: aload 3
      // 403: ifnull 40f
      // 406: aload 3
      // 407: invokevirtual java/io/OutputStream.close ()V
      // 40a: goto 40f
      // 40d: astore 19
      // 40f: aload 4
      // 411: ifnull 41e
      // 414: aload 4
      // 416: invokevirtual java/io/InputStream.close ()V
      // 419: goto 41e
      // 41c: astore 19
      // 41e: aload 2
      // 41f: ifnull 42d
      // 422: aload 2
      // 423: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 428: goto 42d
      // 42b: astore 19
      // 42d: aload 1
      // 42e: aconst_null
      // 42f: putfield net/rim/device/apps/internal/lbs/protocol/Request._lastRequest [B
      // 432: goto 4c5
      // 435: astore 9
      // 437: aload 1
      // 438: invokevirtual net/rim/device/apps/internal/lbs/protocol/Request.isConnectionForcedClosed ()Z
      // 43b: ifne 45d
      // 43e: getstatic java/lang/System.out Ljava/io/PrintStream;
      // 441: new java/lang/StringBuffer
      // 444: dup
      // 445: ldc_w "Exception in request: "
      // 448: invokespecial java/lang/StringBuffer.<init> (Ljava/lang/String;)V
      // 44b: aload 9
      // 44d: invokevirtual java/lang/Throwable.getMessage ()Ljava/lang/String;
      // 450: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 453: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 456: invokevirtual java/io/PrintStream.println (Ljava/lang/String;)V
      // 459: aload 9
      // 45b: astore 8
      // 45d: aload 3
      // 45e: ifnull 46a
      // 461: aload 3
      // 462: invokevirtual java/io/OutputStream.close ()V
      // 465: goto 46a
      // 468: astore 19
      // 46a: aload 4
      // 46c: ifnull 479
      // 46f: aload 4
      // 471: invokevirtual java/io/InputStream.close ()V
      // 474: goto 479
      // 477: astore 19
      // 479: aload 2
      // 47a: ifnull 488
      // 47d: aload 2
      // 47e: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 483: goto 488
      // 486: astore 19
      // 488: aload 1
      // 489: aconst_null
      // 48a: putfield net/rim/device/apps/internal/lbs/protocol/Request._lastRequest [B
      // 48d: goto 4c5
      // 490: astore 17
      // 492: aload 3
      // 493: ifnull 49f
      // 496: aload 3
      // 497: invokevirtual java/io/OutputStream.close ()V
      // 49a: goto 49f
      // 49d: astore 19
      // 49f: aload 4
      // 4a1: ifnull 4ae
      // 4a4: aload 4
      // 4a6: invokevirtual java/io/InputStream.close ()V
      // 4a9: goto 4ae
      // 4ac: astore 19
      // 4ae: aload 2
      // 4af: ifnull 4bd
      // 4b2: aload 2
      // 4b3: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 4b8: goto 4bd
      // 4bb: astore 19
      // 4bd: aload 1
      // 4be: aconst_null
      // 4bf: putfield net/rim/device/apps/internal/lbs/protocol/Request._lastRequest [B
      // 4c2: aload 17
      // 4c4: athrow
      // 4c5: aload 8
      // 4c7: ifnull 513
      // 4ca: getstatic net/rim/device/apps/internal/lbs/LBSApplication.UID J
      // 4cd: new java/lang/StringBuffer
      // 4d0: dup
      // 4d1: ldc_w "Request thread command: "
      // 4d4: invokespecial java/lang/StringBuffer.<init> (Ljava/lang/String;)V
      // 4d7: aload 1
      // 4d8: invokevirtual net/rim/device/apps/internal/lbs/protocol/Request.getCommand ()B
      // 4db: invokevirtual java/lang/StringBuffer.append (I)Ljava/lang/StringBuffer;
      // 4de: ldc_w ", response code: "
      // 4e1: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 4e4: aload 1
      // 4e5: invokevirtual net/rim/device/apps/internal/lbs/protocol/Request.getResponseCode ()I
      // 4e8: invokevirtual java/lang/StringBuffer.append (I)Ljava/lang/StringBuffer;
      // 4eb: ldc_w ", error: "
      // 4ee: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 4f1: aload 8
      // 4f3: invokevirtual java/lang/Exception.toString ()Ljava/lang/String;
      // 4f6: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 4f9: ldc_w ", message: "
      // 4fc: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 4ff: aload 8
      // 501: invokevirtual java/lang/Throwable.getMessage ()Ljava/lang/String;
      // 504: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 507: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 50a: invokevirtual java/lang/String.getBytes ()[B
      // 50d: bipush 2
      // 50f: invokestatic net/rim/device/api/system/EventLogger.logEvent (J[BI)Z
      // 512: pop
      // 513: return
      // try (205 -> 209): 210 null
      // try (257 -> 264): 265 null
      // try (344 -> 364): 367 null
      // try (344 -> 364): 371 null
      // try (367 -> 368): 371 null
      // try (371 -> 372): 371 null
      // try (125 -> 395): 417 null
      // try (125 -> 395): 455 null
      // try (125 -> 395): 480 null
      // try (125 -> 395): 518 null
      // try (417 -> 433): 518 null
      // try (455 -> 458): 518 null
      // try (480 -> 496): 518 null
      // try (518 -> 519): 518 null
      // try (521 -> 523): 524 null
      // try (498 -> 500): 501 null
      // try (460 -> 462): 463 null
      // try (435 -> 437): 438 null
      // try (397 -> 399): 400 null
      // try (527 -> 529): 530 null
      // try (504 -> 506): 507 null
      // try (466 -> 468): 469 null
      // try (441 -> 443): 444 null
      // try (403 -> 405): 406 null
      // try (533 -> 535): 536 null
      // try (510 -> 512): 513 null
      // try (472 -> 474): 475 null
      // try (447 -> 449): 450 null
      // try (409 -> 411): 412 null
   }

   @Override
   public final void interrupt() {
      this._stop = true;
      synchronized (this) {
         this.notify();
      }
   }

   public final boolean isInterrupted() {
      return this._stop;
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void run() {
      Request request = null;

      while (!this._stop) {
         synchronized (this) {
            label111: {
               try {
                  do {
                     if (this._pending.length != 0) {
                        break label111;
                     }

                     this.wait();
                  } while (!this.isInterrupted());
               } finally {
                  break label111;
               }

               return;
            }
         }

         synchronized (this._pending) {
            request = this._pending[this._pending.length - 1];
            Arrays.removeAt(this._pending, this._pending.length - 1);
         }

         label100:
         try {
            this.submitRequest(request);
         } catch (Throwable var15) {
            System.out.println(e);
            break label100;
         }

         if (this._pending.length == 0) {
            MapletCache.getInstance().commit();
         }
      }
   }

   @Override
   public final synchronized void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == -4220058463650496006L && this._uid == null) {
         this._uid = this.findUid();
      } else {
         if (guid == 2522898683889177438L || guid == 8288627527798139133L) {
            ServiceRecord sr = ServiceBook.getSB().getRecordById(data0);
            if (this._uid == null || sr != null && this._uid.equals(sr.getUid())) {
               this._uid = this.findUid();
            }
         }
      }
   }
}
