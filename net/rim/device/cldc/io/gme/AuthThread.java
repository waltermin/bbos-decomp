package net.rim.device.cldc.io.gme;

import javax.microedition.io.Connector;
import javax.microedition.io.Datagram;
import net.rim.device.api.crypto.BBRClientAuth;
import net.rim.device.api.io.DatagramBase;
import net.rim.device.api.io.DatagramConnectionBase;
import net.rim.device.api.io.DatagramStatusListener;
import net.rim.device.api.servicebook.ServiceRouting;
import net.rim.device.api.servicebook.ServiceRoutingProperties;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.api.util.TLEUtilities;
import net.rim.device.internal.crypto.CryptoBlock;

final class AuthThread extends Thread implements DatagramStatusListener {
   private DatagramConnectionBase _conn;
   private String _service;
   private int _routingHandle;
   private byte _state = 0;
   private boolean _receivedRefusal;
   private boolean _runAuth;
   private boolean _useConnAddress;
   private int _serviceCapabilities;
   private DatagramBase _dgram;
   private byte[] _RB;
   private byte[] _eD;
   private byte[] _yB;
   private DatagramConnectionBase _gme;
   private int _backoff = 10000;
   private long _sessionTimestamp = System.currentTimeMillis();
   private static final int MIN_BACKOFF = 10000;
   private static final int MAX_BACKOFF = 120000;
   private static String BBR = "BBR";
   static final int STATE_AUTH_NOT_STARTED = 0;
   static final int STATE_AUTH_STARTED = 1;
   static final int STATE_AUTH_SUCCEEDED = 2;
   static final int STATE_AUTH_FAILED = 3;
   static final int STATE_AUTH_TIMEOUT = 4;

   public AuthThread(String service, int serviceCapabilities, String connection, int routingHandle) {
      boolean runAuth = (serviceCapabilities & 2) == 2;
      if (runAuth && CryptoBlock.getSymmetricKey(service) == null) {
         throw new Object();
      }

      label85:
      try {
         this._conn = (DatagramConnectionBase)Connector.open(connection);
         this._gme = (DatagramConnectionBase)Connector.open("gme:");
      } finally {
         break label85;
      }

      this._serviceCapabilities = serviceCapabilities;
      this._runAuth = runAuth;
      this._service = service;
      this._routingHandle = routingHandle;

      try {
         ServiceRoutingProperties props = ServiceRouting.getInstance().getInterface(this._routingHandle);
         this._useConnAddress = props != null && (props.getName() == ServiceRoutingProperties.SRP_WI_FI || props.getName() == ServiceRoutingProperties.SRP_RF);
      } finally {
         return;
      }
   }

   public final int getRoutingHandle() {
      return this._routingHandle;
   }

   final byte getAuthState() {
      synchronized (this._service) {
         return this._state;
      }
   }

   final long getSessionTimestamp() {
      return this._sessionTimestamp;
   }

   public final void shutdown() {
      DatagramConnectionBase conn;
      DatagramBase dgram;
      synchronized (this._service) {
         conn = this._conn;
         dgram = this._dgram;
         this._conn = null;
         this._dgram = null;
         this._backoff = 0;
         this._receivedRefusal = false;
         this._service.notifyAll();
      }

      if (conn != null) {
         if (dgram != null) {
            label103:
            try {
               conn.cancel(dgram);
            } finally {
               break label103;
            }
         }

         label101:
         try {
            conn.close();
         } finally {
            break label101;
         }
      }

      if (this._gme != null) {
         try {
            this._gme.close();
         } finally {
            return;
         }
      }
   }

   @Override
   public final void updateDatagramStatus(int subId, int event, Object context) {
      synchronized (this._service) {
         switch (event) {
            case 1:
            case 2:
               return;
            case 8337:
               EventLogger.logEvent(1866032962523356178L, 1096118886, 3);
               this._receivedRefusal = true;
            default:
               if (!this._receivedRefusal) {
                  this._service.notifyAll();
               }
         }
      }
   }

   private final DatagramConnectionBase getConnection() {
      synchronized (this._service) {
         if (this._conn == null) {
            throw new ClosedException();
         } else {
            return this._conn;
         }
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
      // 000: aload 0
      // 001: getfield net/rim/device/cldc/io/gme/AuthThread._service Ljava/lang/String;
      // 004: dup
      // 005: astore 1
      // 006: monitorenter
      // 007: aload 0
      // 008: bipush 1
      // 009: putfield net/rim/device/cldc/io/gme/AuthThread._state B
      // 00c: aload 1
      // 00d: monitorexit
      // 00e: goto 016
      // 011: astore 2
      // 012: aload 1
      // 013: monitorexit
      // 014: aload 2
      // 015: athrow
      // 016: aload 0
      // 017: getfield net/rim/device/cldc/io/gme/AuthThread._runAuth Z
      // 01a: ifne 058
      // 01d: aload 0
      // 01e: getfield net/rim/device/cldc/io/gme/AuthThread._service Ljava/lang/String;
      // 021: dup
      // 022: astore 1
      // 023: monitorenter
      // 024: aload 0
      // 025: bipush 2
      // 027: putfield net/rim/device/cldc/io/gme/AuthThread._state B
      // 02a: aload 1
      // 02b: monitorexit
      // 02c: goto 034
      // 02f: astore 3
      // 030: aload 1
      // 031: monitorexit
      // 032: aload 3
      // 033: athrow
      // 034: ldc2_w 1866032962523356178
      // 037: ldc_w 1165258867
      // 03a: bipush 0
      // 03b: invokestatic net/rim/device/api/system/EventLogger.logEvent (JII)Z
      // 03e: pop
      // 03f: invokestatic net/rim/device/api/servicebook/ServiceRouting.getInstance ()Lnet/rim/device/api/servicebook/ServiceRouting;
      // 042: aload 0
      // 043: getfield net/rim/device/cldc/io/gme/AuthThread._service Ljava/lang/String;
      // 046: aload 0
      // 047: getfield net/rim/device/cldc/io/gme/AuthThread._serviceCapabilities I
      // 04a: aload 0
      // 04b: getfield net/rim/device/cldc/io/gme/AuthThread._routingHandle I
      // 04e: bipush 1
      // 04f: bipush 0
      // 050: invokevirtual net/rim/device/api/servicebook/ServiceRouting.setServiceState (Ljava/lang/String;IIZZ)V
      // 053: aload 0
      // 054: invokevirtual net/rim/device/cldc/io/gme/AuthThread.shutdown ()V
      // 057: return
      // 058: new java/lang/Object
      // 05b: dup
      // 05c: aload 0
      // 05d: getfield net/rim/device/cldc/io/gme/AuthThread._service Ljava/lang/String;
      // 060: invokestatic net/rim/device/internal/crypto/CryptoBlock.getSymmetricKey (Ljava/lang/String;)[B
      // 063: invokespecial net/rim/device/api/crypto/BBRClientAuth.<init> ([B)V
      // 066: astore 1
      // 067: new java/lang/Object
      // 06a: dup
      // 06b: invokespecial java/util/Random.<init> ()V
      // 06e: invokevirtual java/util/Random.nextInt ()I
      // 071: istore 2
      // 072: ldc2_w 1866032962523356178
      // 075: ldc_w 1098216552
      // 078: bipush 0
      // 079: invokestatic net/rim/device/api/system/EventLogger.logEvent (JII)Z
      // 07c: pop
      // 07d: aload 0
      // 07e: aload 1
      // 07f: iload 2
      // 080: invokespecial net/rim/device/cldc/io/gme/AuthThread.sendCommit (Lnet/rim/device/api/crypto/BBRClientAuth;I)V
      // 083: aload 0
      // 084: iload 2
      // 085: invokespecial net/rim/device/cldc/io/gme/AuthThread.verifyChallenge (I)V
      // 088: aload 0
      // 089: aload 1
      // 08a: iload 2
      // 08b: invokespecial net/rim/device/cldc/io/gme/AuthThread.sendProof (Lnet/rim/device/api/crypto/BBRClientAuth;I)V
      // 08e: aload 0
      // 08f: iload 2
      // 090: invokespecial net/rim/device/cldc/io/gme/AuthThread.verifyProof (I)V
      // 093: aload 1
      // 094: aload 0
      // 095: getfield net/rim/device/cldc/io/gme/AuthThread._yB [B
      // 098: invokevirtual net/rim/device/api/crypto/BBRClientAuth.verify ([B)Z
      // 09b: ifeq 0d9
      // 09e: aload 0
      // 09f: getfield net/rim/device/cldc/io/gme/AuthThread._service Ljava/lang/String;
      // 0a2: dup
      // 0a3: astore 3
      // 0a4: monitorenter
      // 0a5: aload 0
      // 0a6: bipush 2
      // 0a8: putfield net/rim/device/cldc/io/gme/AuthThread._state B
      // 0ab: aload 3
      // 0ac: monitorexit
      // 0ad: goto 0b7
      // 0b0: astore 4
      // 0b2: aload 3
      // 0b3: monitorexit
      // 0b4: aload 4
      // 0b6: athrow
      // 0b7: ldc2_w 1866032962523356178
      // 0ba: ldc_w 1348563827
      // 0bd: bipush 0
      // 0be: invokestatic net/rim/device/api/system/EventLogger.logEvent (JII)Z
      // 0c1: pop
      // 0c2: invokestatic net/rim/device/api/servicebook/ServiceRouting.getInstance ()Lnet/rim/device/api/servicebook/ServiceRouting;
      // 0c5: aload 0
      // 0c6: getfield net/rim/device/cldc/io/gme/AuthThread._service Ljava/lang/String;
      // 0c9: aload 0
      // 0ca: getfield net/rim/device/cldc/io/gme/AuthThread._serviceCapabilities I
      // 0cd: aload 0
      // 0ce: getfield net/rim/device/cldc/io/gme/AuthThread._routingHandle I
      // 0d1: bipush 1
      // 0d2: bipush 0
      // 0d3: invokevirtual net/rim/device/api/servicebook/ServiceRouting.setServiceState (Ljava/lang/String;IIZZ)V
      // 0d6: goto 156
      // 0d9: ldc2_w 1866032962523356178
      // 0dc: ldc_w 1180789100
      // 0df: bipush 3
      // 0e1: invokestatic net/rim/device/api/system/EventLogger.logEvent (JII)Z
      // 0e4: pop
      // 0e5: goto 156
      // 0e8: astore 1
      // 0e9: ldc2_w 1866032962523356178
      // 0ec: ldc_w 1098216552
      // 0ef: bipush 2
      // 0f1: invokestatic net/rim/device/api/system/EventLogger.logEvent (JII)Z
      // 0f4: pop
      // 0f5: goto 156
      // 0f8: astore 1
      // 0f9: ldc2_w 1866032962523356178
      // 0fc: new java/lang/Object
      // 0ff: dup
      // 100: ldc_w "AUnr-"
      // 103: invokespecial java/lang/StringBuffer.<init> (Ljava/lang/String;)V
      // 106: aload 0
      // 107: getfield net/rim/device/cldc/io/gme/AuthThread._service Ljava/lang/String;
      // 10a: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 10d: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 110: invokevirtual java/lang/String.getBytes ()[B
      // 113: bipush 0
      // 114: invokestatic net/rim/device/api/system/EventLogger.logEvent (J[BI)Z
      // 117: pop
      // 118: goto 156
      // 11b: astore 1
      // 11c: aload 1
      // 11d: invokevirtual java/lang/Throwable.getMessage ()Ljava/lang/String;
      // 120: astore 2
      // 121: aload 2
      // 122: ifnonnull 129
      // 125: ldc_w ""
      // 128: astore 2
      // 129: ldc2_w 1866032962523356178
      // 12c: new java/lang/Object
      // 12f: dup
      // 130: ldc_w "AUnrE "
      // 133: invokespecial java/lang/StringBuffer.<init> (Ljava/lang/String;)V
      // 136: aload 0
      // 137: getfield net/rim/device/cldc/io/gme/AuthThread._service Ljava/lang/String;
      // 13a: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 13d: ldc_w " "
      // 140: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 143: aload 2
      // 144: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 147: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 14a: invokevirtual java/lang/String.getBytes ()[B
      // 14d: bipush 0
      // 14e: invokestatic net/rim/device/api/system/EventLogger.logEvent (J[BI)Z
      // 151: pop
      // 152: goto 156
      // 155: astore 1
      // 156: aload 0
      // 157: getfield net/rim/device/cldc/io/gme/AuthThread._service Ljava/lang/String;
      // 15a: dup
      // 15b: astore 1
      // 15c: monitorenter
      // 15d: aload 0
      // 15e: getfield net/rim/device/cldc/io/gme/AuthThread._state B
      // 161: bipush 2
      // 163: if_icmpeq 16c
      // 166: aload 0
      // 167: bipush 3
      // 169: putfield net/rim/device/cldc/io/gme/AuthThread._state B
      // 16c: aload 1
      // 16d: monitorexit
      // 16e: goto 178
      // 171: astore 5
      // 173: aload 1
      // 174: monitorexit
      // 175: aload 5
      // 177: athrow
      // 178: aload 0
      // 179: invokevirtual net/rim/device/cldc/io/gme/AuthThread.shutdown ()V
      // 17c: return
      // try (5 -> 10): 11 null
      // try (11 -> 14): 11 null
      // try (24 -> 29): 30 null
      // try (30 -> 33): 30 null
      // try (94 -> 99): 100 null
      // try (100 -> 103): 100 null
      // try (0 -> 52): 127 net/rim/device/cldc/io/gme/ClosedException
      // try (53 -> 126): 127 net/rim/device/cldc/io/gme/ClosedException
      // try (0 -> 52): 134 null
      // try (53 -> 126): 134 null
      // try (0 -> 52): 149 null
      // try (53 -> 126): 149 null
      // try (0 -> 52): 175 null
      // try (53 -> 126): 175 null
      // try (181 -> 190): 191 null
      // try (191 -> 194): 191 null
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final void sendCommit(BBRClientAuth auth, int sessionId) {
      String key = CryptoBlock.getKeyIDForUID(this._service);
      if (key != null && key.length() > 0) {
         DatagramBase dgram = (DatagramBase)this.getConnection().newDatagram();
         this._gme.allocateDatagramId(dgram);
         DataBuffer buf = (DataBuffer)(new Object());
         buf.writeByte(1);
         TLEUtilities.writeIntegerField(buf, 1, sessionId, true);
         TLEUtilities.writeDataField(buf, 2, key.getBytes());
         TLEUtilities.writeDataField(buf, 3, auth.getRD());
         writeGmeData(dgram, this._service, dgram.getDatagramId(), buf.getArray(), buf.getArrayPosition());
         dgram.setDatagramStatusListener(this);
         this._dgram = dgram;

         while (true) {
            boolean var20 = false /* VF: Semaphore variable */;

            try {
               var20 = true;
               int localState = 0;
               synchronized (this._service) {
                  label210:
                  try {
                     this._receivedRefusal = false;
                     if (this._useConnAddress) {
                        dgram.setAddressBase(this.getConnection().newDatagramAddressBase(this._service, false));
                     }

                     this.getConnection().send(dgram);
                  } finally {
                     break label210;
                  }

                  boolean var27 = false /* VF: Semaphore variable */;

                  label206:
                  try {
                     var27 = true;
                     if (this._backoff > 0) {
                        this._service.wait(this._backoff);
                        var27 = false;
                     } else {
                        var27 = false;
                     }
                  } finally {
                     if (var27) {
                        this._backoff = 120000;
                        break label206;
                     }
                  }

                  if (this._receivedRefusal) {
                     this.sendAbort(sessionId, 4, true);
                     if (this._backoff >= 120000) {
                        localState = 1;
                     } else if (this._backoff > 0) {
                        localState = 2;
                        this._backoff = Math.min(this._backoff * 3, 120000);
                     }
                  }
               }

               switch (localState) {
                  case 0:
                     break;
                  case 1:
                  default:
                     EventLogger.logEvent(1866032962523356178L, 1096117601, 3);
                     throw new Object("Connection refused");
                  case 2:
                     EventLogger.logEvent(1866032962523356178L, 1096118884, 3);
               }

               if (!this._receivedRefusal) {
                  var20 = false;
                  break;
               }
            } finally {
               if (var20) {
                  dgram.setDatagramId(0);
                  dgram.setDatagramStatusListener(null);
                  this._dgram = null;
                  synchronized (this._service) {
                     this._receivedRefusal = false;
                     this._backoff = 0;
                  }
               }
            }
         }

         dgram.setDatagramId(0);
         dgram.setDatagramStatusListener(null);
         this._dgram = null;
         synchronized (this._service) {
            this._receivedRefusal = false;
            this._backoff = 0;
         }
      } else {
         throw new Object();
      }
   }

   private final void verifyChallenge(int sessionId) {
      DatagramBase dgram = null;
      AuthThread$GmeInfo info = null;
      DataBuffer buf = null;

      do {
         info = null;
         buf = null;
         var5 = this.getConnection().newDatagram();
         this.getConnection().receive((Datagram)var5);
         info = readGmeData((DataBuffer)var5);
         if (info == null) {
            EventLogger.logEvent(1866032962523356178L, 1096118386, 3);
         }
      } while ((buf = this.getDataIfAddressed(info, sessionId)) == null);

      this.getConnection().datagramProcessed(((DatagramBase)var5).getDatagramId());
      if (buf.readByte() != 2) {
         throw new Object();
      }

      while (!buf.eof()) {
         switch (TLEUtilities.getType(buf)) {
            case 0:
               TLEUtilities.skipField(buf);
               break;
            case 1:
            default:
               if (TLEUtilities.readIntegerField(buf, 1) != sessionId) {
                  throw new Object();
               }
               break;
            case 2:
               if (!Arrays.equals(CryptoBlock.getKeyIDForUID(this._service).getBytes(), TLEUtilities.readDataField(buf, 2))) {
                  throw new Object();
               }
               break;
            case 3:
               if (this._RB == null) {
                  this._RB = TLEUtilities.readDataField(buf, 3);
               }
               break;
            case 4:
               if (this._eD == null) {
                  this._eD = TLEUtilities.readDataField(buf, 4);
               }
         }
      }
   }

   private final void sendProof(BBRClientAuth auth, int sessionId) {
      DatagramBase dgram = (DatagramBase)this.getConnection().newDatagram();
      this._gme.allocateDatagramId(dgram);
      DataBuffer buf = (DataBuffer)(new Object());
      buf.writeByte(3);
      TLEUtilities.writeIntegerField(buf, 1, sessionId, true);
      TLEUtilities.writeDataField(buf, 5, auth.getYD(this._RB, this._eD));
      TLEUtilities.writeDataField(buf, 4, auth.getEB());
      writeGmeData(dgram, this._service, dgram.getDatagramId(), buf.getArray(), buf.getArrayPosition());
      dgram.setDatagramId(0);
      this._dgram = dgram;
      if (this._useConnAddress) {
         dgram.setAddressBase(this.getConnection().newDatagramAddressBase(this._service, false));
      }

      this.getConnection().send(dgram);
      this._dgram = null;
   }

   private final void verifyProof(int sessionId) {
      DatagramBase dgram = null;
      AuthThread$GmeInfo info = null;
      DataBuffer buf = null;

      do {
         info = null;
         buf = null;
         var5 = this.getConnection().newDatagram();
         this.getConnection().receive((Datagram)var5);
         info = readGmeData((DataBuffer)var5);
         if (info == null) {
            EventLogger.logEvent(1866032962523356178L, 1096118386, 3);
         }
      } while ((buf = this.getDataIfAddressed(info, sessionId)) == null);

      this.getConnection().datagramProcessed(((DatagramBase)var5).getDatagramId());
      if (buf.readByte() != 4) {
         throw new Object();
      }

      while (!buf.eof()) {
         switch (TLEUtilities.getType(buf)) {
            case 1:
               if (TLEUtilities.readIntegerField(buf, 1) != sessionId) {
                  throw new Object();
               }
               break;
            case 5:
               if (this._yB == null) {
                  this._yB = TLEUtilities.readDataField(buf, 5);
               }
               break;
            default:
               TLEUtilities.skipField(buf);
         }
      }
   }

   private final void sendAbort(int sessionId, int reasonCode, boolean useTemporaryDatagram) {
      DatagramBase dgram = (DatagramBase)this.getConnection().newDatagram();
      this._gme.allocateDatagramId(dgram);
      DataBuffer buf = (DataBuffer)(new Object());
      buf.writeByte(9);
      TLEUtilities.writeIntegerField(buf, 1, sessionId, true);
      TLEUtilities.writeIntegerField(buf, 8, reasonCode, true);
      writeGmeData(dgram, this._service, dgram.getDatagramId(), buf.getArray(), buf.getArrayPosition());
      dgram.setDatagramId(0);
      if (!useTemporaryDatagram) {
         this._dgram = dgram;
      }

      if (this._useConnAddress) {
         dgram.setAddressBase(this.getConnection().newDatagramAddressBase(this._service, false));
      }

      this.getConnection().send(dgram);
      if (!useTemporaryDatagram) {
         this._dgram = null;
      }
   }

   private static final void writeGmeData(DataBuffer buf, String service, int transactionId, byte[] data, int length) {
      buf.writeByte(32);
      TLEUtilities.writeStringField(buf, 16, Long.toString(DeviceInfo.getDeviceId() & 4294967295L, 16), false);
      TLEUtilities.writeStringField(buf, 32, service, false);
      buf.writeByte(0);
      buf.writeInt(transactionId);
      TLEUtilities.writeStringField(buf, 80, BBR, false);
      buf.writeByte(3);
      buf.writeByte(64);
      buf.writeCompressedInt(4 + length);
      buf.writeInt(0);
      buf.write(data, 0, length);
   }

   private static final AuthThread$GmeInfo readGmeData(DataBuffer buf) {
      AuthThread$GmeInfo info = new AuthThread$GmeInfo();
      info.version = buf.readByte();
      if ((info.version & 240) != 32) {
         throw new Object();
      }

      int type;
      while ((type = TLEUtilities.getType(buf)) != 0) {
         switch (type & 240) {
            case 16:
               if (info.source == null) {
                  info.source = TLEUtilities.readStringField(buf, 16, false);
               }
               break;
            case 32:
               if (info.destination == null) {
                  info.destination = TLEUtilities.readStringField(buf, 32, false);
               }
               break;
            default:
               TLEUtilities.skipField(buf);
         }
      }

      if (buf.readUnsignedByte() != 0) {
         throw new Object();
      }

      info.transactionId = buf.readInt();
      info.cid = TLEUtilities.readStringField(buf, 80, false);
      info.type = buf.readByte();
      if (info.type != 3) {
         return null;
      }

      info.data = TLEUtilities.readDataField(buf, 64);
      if (info.data != null) {
         info.length = info.data.length;
         if (info.length >= 4) {
            info.offset = 4;
            info.length -= 4;
         }
      }

      return info;
   }

   private final boolean isAddressed(AuthThread$GmeInfo info) {
      return info != null
         && StringUtilities.strEqualIgnoreCase(info.cid, BBR, 1701707776)
         && StringUtilities.strEqualIgnoreCase(info.source, this._service, 1701707776)
         && StringUtilities.strEqualIgnoreCase(info.destination, Long.toString(DeviceInfo.getDeviceId() & 4294967295L, 16), 1701707776);
   }

   private final DataBuffer getDataIfAddressed(AuthThread$GmeInfo param1, int param2) {
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
      // 00: aload 0
      // 01: aload 1
      // 02: invokespecial net/rim/device/cldc/io/gme/AuthThread.isAddressed (Lnet/rim/device/cldc/io/gme/AuthThread$GmeInfo;)Z
      // 05: ifeq 8c
      // 08: new java/lang/Object
      // 0b: dup
      // 0c: aload 1
      // 0d: getfield net/rim/device/cldc/io/gme/AuthThread$GmeInfo.data [B
      // 10: aload 1
      // 11: getfield net/rim/device/cldc/io/gme/AuthThread$GmeInfo.offset I
      // 14: aload 1
      // 15: getfield net/rim/device/cldc/io/gme/AuthThread$GmeInfo.length I
      // 18: bipush 1
      // 19: invokespecial net/rim/device/api/util/DataBuffer.<init> ([BIIZ)V
      // 1c: astore 3
      // 1d: aload 3
      // 1e: invokevirtual net/rim/device/api/util/DataBuffer.getPosition ()I
      // 21: istore 4
      // 23: aload 3
      // 24: invokevirtual net/rim/device/api/util/DataBuffer.readByte ()B
      // 27: pop
      // 28: aload 3
      // 29: invokevirtual net/rim/device/api/util/DataBuffer.eof ()Z
      // 2c: ifne 70
      // 2f: aload 3
      // 30: invokestatic net/rim/device/api/util/TLEUtilities.getType (Lnet/rim/device/api/util/DataBuffer;)I
      // 33: lookupswitch 54 1 1 17
      // 44: aload 3
      // 45: bipush 1
      // 46: invokestatic net/rim/device/api/util/TLEUtilities.readIntegerField (Lnet/rim/device/api/util/DataBuffer;I)I
      // 49: iload 2
      // 4a: if_icmpne 5e
      // 4d: aload 3
      // 4e: iload 4
      // 50: invokevirtual net/rim/device/api/util/DataBuffer.setPosition (I)V
      // 53: aload 3
      // 54: astore 5
      // 56: aload 3
      // 57: iload 4
      // 59: invokevirtual net/rim/device/api/util/DataBuffer.setPosition (I)V
      // 5c: aload 3
      // 5d: areturn
      // 5e: aconst_null
      // 5f: astore 5
      // 61: aload 3
      // 62: iload 4
      // 64: invokevirtual net/rim/device/api/util/DataBuffer.setPosition (I)V
      // 67: aload 3
      // 68: areturn
      // 69: aload 3
      // 6a: invokestatic net/rim/device/api/util/TLEUtilities.skipField (Lnet/rim/device/api/util/DataBuffer;)V
      // 6d: goto 28
      // 70: aload 3
      // 71: iload 4
      // 73: invokevirtual net/rim/device/api/util/DataBuffer.setPosition (I)V
      // 76: aload 3
      // 77: areturn
      // 78: astore 5
      // 7a: aload 3
      // 7b: iload 4
      // 7d: invokevirtual net/rim/device/api/util/DataBuffer.setPosition (I)V
      // 80: aload 3
      // 81: areturn
      // 82: astore 6
      // 84: aload 3
      // 85: iload 4
      // 87: invokevirtual net/rim/device/api/util/DataBuffer.setPosition (I)V
      // 8a: aload 3
      // 8b: areturn
      // 8c: aconst_null
      // 8d: areturn
      // try (18 -> 37): 57 null
      // try (42 -> 44): 57 null
      // try (49 -> 52): 57 null
      // try (18 -> 37): 63 null
      // try (42 -> 44): 63 null
      // try (49 -> 52): 63 null
      // try (57 -> 58): 63 null
      // try (63 -> 64): 63 null
   }
}
