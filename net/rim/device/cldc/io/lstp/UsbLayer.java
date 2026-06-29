package net.rim.device.cldc.io.lstp;

import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.USBPortListener;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.cldc.io.daemon.ProtocolDaemon;
import net.rim.device.internal.system.USBPortInternal;

final class UsbLayer extends NativeLayer implements USBPortListener {
   private int _channel;
   private Object _portSendLock = new Object();
   private int _portSendStatus;
   private int _fragmentBufferSize;
   private USBPortInternal _port;
   private static final int PORT_SEND_TIMEOUT = 10000;
   private static final int PORT_SEND_NONE = 0;
   private static final int PORT_SEND_SUCCESS = 1;
   private static final int PORT_SEND_CANCEL = 2;
   private static final int PORT_SEND_FAIL = 3;
   private static final int DATA_HEADER_SIZE = 2;

   public UsbLayer(Transport transport) {
      super(transport);
      ProtocolDaemon.getInstance().addIOPortListener(this);
      this._fragmentBufferSize = Math.min(USBPortInternal.getMaximumRxSize(), USBPortInternal.getMaximumTxSize());
      this._channel = USBPortInternal.registerChannel("RIM Bypass", this._fragmentBufferSize, this._fragmentBufferSize);
   }

   @Override
   protected final int getFragmentSize() {
      return this._fragmentBufferSize - 2;
   }

   @Override
   protected final void nativeOpen() {
      this._port = (USBPortInternal)(new Object(this._channel));
   }

   @Override
   protected final void nativeClose() {
      if (this._port != null) {
         this._port.close();
      }
   }

   @Override
   protected final void shutdownReceiveThread() {
      this.dataReceived(0);
   }

   @Override
   protected final MuxerThread createMuxerThread() {
      return new UsbMuxerThread(this);
   }

   @Override
   protected final void sendPacket(byte[] buffer, int offset, int length) {
      if (this._port == null) {
         throw new Object();
      }

      synchronized (this._portSendLock) {
         this._portSendStatus = 0;
      }

      if (this._port.write(buffer, offset, length) != length) {
         EventLogger.logEvent(-754053862978797267L, 1415013985, 3);
         throw new Object();
      }

      synchronized (this._portSendLock) {
         if (this._portSendStatus == 0) {
            label61:
            try {
               this._portSendLock.wait(10000);
            } finally {
               break label61;
            }
         }

         switch (this._portSendStatus) {
            case -1:
               EventLogger.logEvent(-754053862978797267L, 1415016051, 3);
               throw new Object();
            case 0:
               EventLogger.logEvent(-754053862978797267L, 1415017583, 3);
               throw new Object();
            case 1:
               break;
            case 2:
            default:
               throw new Object();
         }
      }

      super._transport.processResponse(true);
   }

   @Override
   protected final void cancelPacket() {
      synchronized (this._portSendLock) {
         this._portSendStatus = 2;
         this._portSendLock.notify();
      }
   }

   @Override
   public final void connected() {
   }

   @Override
   public final void disconnected() {
      this.close(true);
   }

   @Override
   public final void receiveError(int error) {
      synchronized (super._receiveLock) {
         super._dataAvailable = -1;
         super._receiveLock.notifyAll();
      }
   }

   @Override
   public final void dataReceived(int length) {
      synchronized (super._receiveLock) {
         super._dataAvailable = length;
         super._receiveLock.notifyAll();
      }
   }

   @Override
   public final void dataSent() {
      synchronized (this._portSendLock) {
         this._portSendStatus = 1;
         this._portSendLock.notify();
      }
   }

   @Override
   public final int getChannel() {
      return this._channel;
   }

   @Override
   public final void dataNotSent() {
      synchronized (this._portSendLock) {
         this._portSendStatus = 3;
         this._portSendLock.notify();
      }
   }

   @Override
   public final void connectionRequested() {
      this.startNativeLayer();
   }

   @Override
   public final void patternReceived(byte[] pattern) {
   }

   @Override
   protected final int encodeDataFragment(byte id, byte[] src, int srcOffset, int srcLength, DataBuffer dest) {
      byte type;
      if (srcLength <= this._fragmentBufferSize - 2) {
         type = 64;
      } else {
         srcLength = this._fragmentBufferSize - 2;
         type = 96;
      }

      dest.ensureCapacity(2 + srcLength);
      dest.writeByte(type);
      dest.writeByte(id);
      if (srcLength > 0) {
         dest.write(src, srcOffset, srcLength);
      }

      return srcLength;
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
      // 001: getfield net/rim/device/cldc/io/lstp/UsbLayer._fragmentBufferSize I
      // 004: newarray 8
      // 006: astore 1
      // 007: bipush 0
      // 008: istore 2
      // 009: bipush 0
      // 00a: istore 3
      // 00b: bipush 0
      // 00c: istore 4
      // 00e: aconst_null
      // 00f: astore 5
      // 011: bipush 0
      // 012: istore 6
      // 014: aload 0
      // 015: getfield net/rim/device/cldc/io/lstp/NativeLayer._receiveLock Ljava/lang/Object;
      // 018: dup
      // 019: astore 7
      // 01b: monitorenter
      // 01c: aload 0
      // 01d: getfield net/rim/device/cldc/io/lstp/NativeLayer._dataAvailable I
      // 020: ifne 02a
      // 023: aload 0
      // 024: getfield net/rim/device/cldc/io/lstp/NativeLayer._receiveLock Ljava/lang/Object;
      // 027: invokevirtual java/lang/Object.wait ()V
      // 02a: aload 0
      // 02b: getfield net/rim/device/cldc/io/lstp/NativeLayer._dataAvailable I
      // 02e: istore 2
      // 02f: aload 0
      // 030: getfield net/rim/device/cldc/io/lstp/NativeLayer._dataAvailable I
      // 033: ifge 047
      // 036: ldc2_w -754053862978797267
      // 039: ldc_w 1380281454
      // 03c: bipush 3
      // 03e: invokestatic net/rim/device/api/system/EventLogger.logEvent (JII)Z
      // 041: pop
      // 042: aload 0
      // 043: getfield net/rim/device/cldc/io/lstp/UsbLayer._fragmentBufferSize I
      // 046: istore 2
      // 047: aload 0
      // 048: bipush 0
      // 049: putfield net/rim/device/cldc/io/lstp/NativeLayer._dataAvailable I
      // 04c: aload 0
      // 04d: getfield net/rim/device/cldc/io/lstp/UsbLayer._port Lnet/rim/device/internal/system/USBPortInternal;
      // 050: aload 1
      // 051: bipush 0
      // 052: iload 2
      // 053: invokevirtual net/rim/device/internal/system/USBPortInternal.read ([BII)I
      // 056: istore 3
      // 057: iload 3
      // 058: iload 2
      // 059: if_icmpeq 068
      // 05c: ldc2_w -754053862978797267
      // 05f: ldc_w 1380346989
      // 062: bipush 3
      // 064: invokestatic net/rim/device/api/system/EventLogger.logEvent (JII)Z
      // 067: pop
      // 068: iinc 3 -2
      // 06b: iload 3
      // 06c: ifge 081
      // 06f: ldc2_w -754053862978797267
      // 072: ldc_w 1380279919
      // 075: bipush 2
      // 077: invokestatic net/rim/device/api/system/EventLogger.logEvent (JII)Z
      // 07a: pop
      // 07b: aload 7
      // 07d: monitorexit
      // 07e: goto 014
      // 081: aload 1
      // 082: bipush 0
      // 083: baload
      // 084: istore 4
      // 086: iload 4
      // 088: lookupswitch 43 2 64 28 96 28
      // 0a4: ldc2_w -754053862978797267
      // 0a7: ldc_w 1381528436
      // 0aa: bipush 4
      // 0ac: invokestatic net/rim/device/api/system/EventLogger.logEvent (JII)Z
      // 0af: pop
      // 0b0: goto 0c5
      // 0b3: ldc2_w -754053862978797267
      // 0b6: ldc_w 1381004651
      // 0b9: bipush 3
      // 0bb: invokestatic net/rim/device/api/system/EventLogger.logEvent (JII)Z
      // 0be: pop
      // 0bf: aload 7
      // 0c1: monitorexit
      // 0c2: goto 014
      // 0c5: aload 5
      // 0c7: ifnonnull 0f6
      // 0ca: new java/lang/Object
      // 0cd: dup
      // 0ce: invokespecial net/rim/device/api/io/DatagramBase.<init> ()V
      // 0d1: astore 5
      // 0d3: aload 1
      // 0d4: bipush 1
      // 0d5: baload
      // 0d6: istore 6
      // 0d8: aload 0
      // 0d9: getfield net/rim/device/cldc/io/lstp/NativeLayer._lstpUtil Lnet/rim/device/cldc/io/lstp/LstpUtil;
      // 0dc: iload 6
      // 0de: invokevirtual net/rim/device/cldc/io/lstp/LstpUtil.getAppName (I)Ljava/lang/String;
      // 0e1: invokevirtual java/lang/String.length ()I
      // 0e4: ifgt 10a
      // 0e7: ldc2_w -754053862978797267
      // 0ea: ldc_w 1381001572
      // 0ed: bipush 2
      // 0ef: invokestatic net/rim/device/api/system/EventLogger.logEvent (JII)Z
      // 0f2: pop
      // 0f3: goto 10a
      // 0f6: iload 6
      // 0f8: aload 1
      // 0f9: bipush 1
      // 0fa: baload
      // 0fb: if_icmpeq 10a
      // 0fe: ldc2_w -754053862978797267
      // 101: ldc_w 1381001572
      // 104: bipush 3
      // 106: invokestatic net/rim/device/api/system/EventLogger.logEvent (JII)Z
      // 109: pop
      // 10a: aload 5
      // 10c: aload 1
      // 10d: bipush 2
      // 10f: iload 3
      // 110: invokevirtual net/rim/device/api/util/DataBuffer.write ([BII)V
      // 113: iload 3
      // 114: aload 0
      // 115: getfield net/rim/device/cldc/io/lstp/UsbLayer._fragmentBufferSize I
      // 118: if_icmple 127
      // 11b: ldc2_w -754053862978797267
      // 11e: ldc_w 1380279919
      // 121: bipush 3
      // 123: invokestatic net/rim/device/api/system/EventLogger.logEvent (JII)Z
      // 126: pop
      // 127: iload 4
      // 129: bipush 64
      // 12b: if_icmpne 14b
      // 12e: aload 5
      // 130: new net/rim/device/cldc/io/lstp/LstpAddress
      // 133: dup
      // 134: iload 6
      // 136: invokespecial net/rim/device/cldc/io/lstp/LstpAddress.<init> (I)V
      // 139: invokevirtual net/rim/device/api/io/DatagramBase.setAddressBase (Lnet/rim/device/api/io/DatagramAddressBase;)V
      // 13c: aload 0
      // 13d: getfield net/rim/device/cldc/io/lstp/NativeLayer._transport Lnet/rim/device/cldc/io/lstp/Transport;
      // 140: aload 5
      // 142: invokevirtual net/rim/device/cldc/io/lstp/Transport.processReceivedDatagram (Ljavax/microedition/io/Datagram;)V
      // 145: aconst_null
      // 146: astore 5
      // 148: goto 157
      // 14b: ldc2_w -754053862978797267
      // 14e: ldc_w 1381005153
      // 151: bipush 4
      // 153: invokestatic net/rim/device/api/system/EventLogger.logEvent (JII)Z
      // 156: pop
      // 157: aload 7
      // 159: monitorexit
      // 15a: goto 014
      // 15d: astore 8
      // 15f: aload 7
      // 161: monitorexit
      // 162: aload 8
      // 164: athrow
      // 165: astore 7
      // 167: ldc2_w -754053862978797267
      // 16a: ldc_w 1380152164
      // 16d: bipush 4
      // 16f: invokestatic net/rim/device/api/system/EventLogger.logEvent (JII)Z
      // 172: pop
      // 173: return
      // 174: astore 7
      // 176: goto 014
      // try (19 -> 67): 158 null
      // try (68 -> 87): 158 null
      // try (88 -> 157): 158 null
      // try (158 -> 161): 158 null
      // try (14 -> 67): 163 null
      // try (68 -> 87): 163 null
      // try (88 -> 163): 163 null
      // try (14 -> 67): 170 null
      // try (68 -> 87): 170 null
      // try (88 -> 163): 170 null
   }
}
