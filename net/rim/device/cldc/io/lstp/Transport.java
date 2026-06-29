package net.rim.device.cldc.io.lstp;

import javax.microedition.io.Datagram;
import net.rim.device.api.bluetooth.BluetoothSerialPort;
import net.rim.device.api.io.ConnEvent;
import net.rim.device.api.io.DatagramAddressBase;
import net.rim.device.api.io.DatagramBase;
import net.rim.device.api.io.DatagramStatusListener;
import net.rim.device.api.io.DatagramTransportBase;
import net.rim.device.api.io.IOProperties;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.internal.system.USBPortInternal;

public final class Transport extends DatagramTransportBase implements LstpListener, ConnEvent {
   private int _nextDgramId;
   private Object _lock = new Object();
   private Datagram _txDatagram;
   private int _txStatus;
   private DataBuffer _txBuffer;
   private NativeLayer _activeNativeLayer;
   private NativeLayer _primaryNativeLayer;
   protected static final long ID = -754053862978797267L;
   private static final String LSTP_STR = "net.rim.lstp";
   private static final int WAIT_FOR_ACK_TIMEOUT = 5000;
   private static final int MAX_RETRIES = 3;
   private static final int STATUS_NONE = 0;
   private static final int STATUS_ACKED = 1;
   private static final int STATUS_NON_FATAL = 2;
   private static final int STATUS_NOT_ROUTABLE = 3;
   private static final int STATUS_CANCELLED = 4;
   private static final int STATUS_FATAL = 5;

   public Transport() {
      super.GUID = -754053862978797267L;
      super.STR = "net.rim.lstp";
   }

   @Override
   public final void init() {
      super.init(null);
      EventLogger.register(-754053862978797267L, "net.rim.lstp", 2);
      this._txBuffer = (DataBuffer)(new Object());
      LstpUtil.getInstance().addListener(this);
      this.createNativeLayer();
      EventLogger.logEvent(-754053862978797267L, 1229878386, 0);
   }

   private final void createNativeLayer() {
      if (USBPortInternal.isSupported()) {
         this._primaryNativeLayer = new UsbLayer(this);
         if (BluetoothSerialPort.isSupported()) {
            new BluetoothLayer(this);
         }
      } else {
         throw new Object();
      }
   }

   private final NativeLayer getNativeLayer() {
      return this._activeNativeLayer != null ? this._activeNativeLayer : this._primaryNativeLayer;
   }

   final boolean activateNativeLayer(NativeLayer nativeLayer) {
      if (this._activeNativeLayer == nativeLayer) {
         return true;
      }

      if (this._activeNativeLayer != null) {
         if (nativeLayer != this._primaryNativeLayer) {
            return false;
         }

         this._activeNativeLayer.close(true);
      }

      this._activeNativeLayer = nativeLayer;
      return true;
   }

   final void deactivateNativeLayer(NativeLayer nativeLayer) {
      if (this._activeNativeLayer == nativeLayer) {
         this._activeNativeLayer = null;
      }
   }

   @Override
   public final int getMaximumLength() {
      return Integer.MAX_VALUE;
   }

   @Override
   public final int getNominalLength() {
      return this.getNativeLayer().getFragmentSize();
   }

   @Override
   protected final synchronized int getNextDatagramId(DatagramBase dgram) {
      return ++this._nextDgramId;
   }

   @Override
   public final DatagramAddressBase newDatagramAddressBase(String address, boolean swap) {
      return new LstpAddress(address);
   }

   @Override
   public final DatagramAddressBase newDatagramAddressBase(DatagramAddressBase addressBase, boolean swap) {
      return new LstpAddress(addressBase);
   }

   @Override
   public final void send(Datagram datagram) {
      this.send(datagram, null, null, null, 0);
   }

   @Override
   public final void send(Datagram param1, DatagramAddressBase param2, IOProperties param3, DatagramStatusListener param4, int param5) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: ldc2_w -754053862978797267
      // 003: ldc_w 1415082868
      // 006: bipush 4
      // 008: invokestatic net/rim/device/api/system/EventLogger.logEvent (JII)Z
      // 00b: pop
      // 00c: aconst_null
      // 00d: astore 6
      // 00f: aload 2
      // 010: dup
      // 011: instanceof net/rim/device/cldc/io/lstp/LstpAddress
      // 014: ifne 01b
      // 017: pop
      // 018: goto 020
      // 01b: checkcast net/rim/device/cldc/io/lstp/LstpAddress
      // 01e: astore 6
      // 020: aload 6
      // 022: ifnonnull 034
      // 025: new net/rim/device/cldc/io/lstp/LstpAddress
      // 028: dup
      // 029: aload 1
      // 02a: invokeinterface javax/microedition/io/Datagram.getAddress ()Ljava/lang/String; 1
      // 02f: invokespecial net/rim/device/cldc/io/lstp/LstpAddress.<init> (Ljava/lang/String;)V
      // 032: astore 6
      // 034: aload 6
      // 036: invokevirtual net/rim/device/cldc/io/lstp/LstpAddress.getAppId ()I
      // 039: istore 7
      // 03b: iload 7
      // 03d: bipush -1
      // 03f: if_icmpne 062
      // 042: ldc2_w -754053862978797267
      // 045: ldc_w 1413834351
      // 048: bipush 2
      // 04a: invokestatic net/rim/device/api/system/EventLogger.logEvent (JII)Z
      // 04d: pop
      // 04e: aload 0
      // 04f: aload 4
      // 051: iload 5
      // 053: sipush 12674
      // 056: aconst_null
      // 057: invokevirtual net/rim/device/api/io/DatagramTransportBase.xmitDgslEvent (Lnet/rim/device/api/io/DatagramStatusListener;IILjava/lang/Object;)V
      // 05a: new java/lang/Object
      // 05d: dup
      // 05e: invokespecial net/rim/device/api/io/IOFormatException.<init> ()V
      // 061: athrow
      // 062: aload 0
      // 063: getfield net/rim/device/cldc/io/lstp/Transport._lock Ljava/lang/Object;
      // 066: dup
      // 067: astore 8
      // 069: monitorenter
      // 06a: aload 0
      // 06b: aload 1
      // 06c: putfield net/rim/device/cldc/io/lstp/Transport._txDatagram Ljavax/microedition/io/Datagram;
      // 06f: aload 8
      // 071: monitorexit
      // 072: goto 07d
      // 075: astore 9
      // 077: aload 8
      // 079: monitorexit
      // 07a: aload 9
      // 07c: athrow
      // 07d: aload 1
      // 07e: invokeinterface javax/microedition/io/Datagram.getOffset ()I 1
      // 083: istore 8
      // 085: aload 1
      // 086: invokeinterface javax/microedition/io/Datagram.getLength ()I 1
      // 08b: istore 9
      // 08d: aload 1
      // 08e: invokeinterface javax/microedition/io/Datagram.getData ()[B 1
      // 093: astore 10
      // 095: aload 0
      // 096: aload 4
      // 098: iload 5
      // 09a: bipush 2
      // 09c: aconst_null
      // 09d: invokevirtual net/rim/device/api/io/DatagramTransportBase.xmitDgslEvent (Lnet/rim/device/api/io/DatagramStatusListener;IILjava/lang/Object;)V
      // 0a0: aload 0
      // 0a1: getfield net/rim/device/cldc/io/lstp/Transport._txBuffer Lnet/rim/device/api/util/DataBuffer;
      // 0a4: invokevirtual net/rim/device/api/util/DataBuffer.reset ()V
      // 0a7: aload 0
      // 0a8: invokespecial net/rim/device/cldc/io/lstp/Transport.getNativeLayer ()Lnet/rim/device/cldc/io/lstp/NativeLayer;
      // 0ab: iload 7
      // 0ad: i2b
      // 0ae: aload 10
      // 0b0: iload 8
      // 0b2: iload 9
      // 0b4: aload 0
      // 0b5: getfield net/rim/device/cldc/io/lstp/Transport._txBuffer Lnet/rim/device/api/util/DataBuffer;
      // 0b8: invokevirtual net/rim/device/cldc/io/lstp/NativeLayer.encodeDataFragment (B[BIILnet/rim/device/api/util/DataBuffer;)I
      // 0bb: istore 11
      // 0bd: iload 8
      // 0bf: iload 11
      // 0c1: iadd
      // 0c2: istore 8
      // 0c4: iload 9
      // 0c6: iload 11
      // 0c8: isub
      // 0c9: istore 9
      // 0cb: bipush 0
      // 0cc: istore 12
      // 0ce: aload 0
      // 0cf: getfield net/rim/device/cldc/io/lstp/Transport._lock Ljava/lang/Object;
      // 0d2: dup
      // 0d3: astore 13
      // 0d5: monitorenter
      // 0d6: aload 0
      // 0d7: bipush 0
      // 0d8: putfield net/rim/device/cldc/io/lstp/Transport._txStatus I
      // 0db: aload 13
      // 0dd: monitorexit
      // 0de: goto 0e9
      // 0e1: astore 14
      // 0e3: aload 13
      // 0e5: monitorexit
      // 0e6: aload 14
      // 0e8: athrow
      // 0e9: ldc2_w -754053862978797267
      // 0ec: ldc_w 1415082850
      // 0ef: bipush 5
      // 0f1: invokestatic net/rim/device/api/system/EventLogger.logEvent (JII)Z
      // 0f4: pop
      // 0f5: aload 0
      // 0f6: invokespecial net/rim/device/cldc/io/lstp/Transport.getNativeLayer ()Lnet/rim/device/cldc/io/lstp/NativeLayer;
      // 0f9: aload 0
      // 0fa: getfield net/rim/device/cldc/io/lstp/Transport._txBuffer Lnet/rim/device/api/util/DataBuffer;
      // 0fd: invokevirtual net/rim/device/api/util/DataBuffer.getArray ()[B
      // 100: aload 0
      // 101: getfield net/rim/device/cldc/io/lstp/Transport._txBuffer Lnet/rim/device/api/util/DataBuffer;
      // 104: invokevirtual net/rim/device/api/util/DataBuffer.getArrayStart ()I
      // 107: aload 0
      // 108: getfield net/rim/device/cldc/io/lstp/Transport._txBuffer Lnet/rim/device/api/util/DataBuffer;
      // 10b: invokevirtual net/rim/device/api/util/DataBuffer.getLength ()I
      // 10e: invokevirtual net/rim/device/cldc/io/lstp/NativeLayer.sendPacket ([BII)V
      // 111: goto 16e
      // 114: astore 13
      // 116: aload 0
      // 117: getfield net/rim/device/cldc/io/lstp/Transport._lock Ljava/lang/Object;
      // 11a: dup
      // 11b: astore 14
      // 11d: monitorenter
      // 11e: aload 0
      // 11f: bipush 3
      // 121: putfield net/rim/device/cldc/io/lstp/Transport._txStatus I
      // 124: aload 14
      // 126: monitorexit
      // 127: goto 16e
      // 12a: astore 15
      // 12c: aload 14
      // 12e: monitorexit
      // 12f: aload 15
      // 131: athrow
      // 132: astore 13
      // 134: aload 0
      // 135: getfield net/rim/device/cldc/io/lstp/Transport._lock Ljava/lang/Object;
      // 138: dup
      // 139: astore 14
      // 13b: monitorenter
      // 13c: aload 0
      // 13d: bipush 4
      // 13f: putfield net/rim/device/cldc/io/lstp/Transport._txStatus I
      // 142: aload 14
      // 144: monitorexit
      // 145: goto 16e
      // 148: astore 16
      // 14a: aload 14
      // 14c: monitorexit
      // 14d: aload 16
      // 14f: athrow
      // 150: astore 13
      // 152: aload 0
      // 153: getfield net/rim/device/cldc/io/lstp/Transport._lock Ljava/lang/Object;
      // 156: dup
      // 157: astore 14
      // 159: monitorenter
      // 15a: aload 0
      // 15b: bipush 5
      // 15d: putfield net/rim/device/cldc/io/lstp/Transport._txStatus I
      // 160: aload 14
      // 162: monitorexit
      // 163: goto 16e
      // 166: astore 17
      // 168: aload 14
      // 16a: monitorexit
      // 16b: aload 17
      // 16d: athrow
      // 16e: aload 0
      // 16f: getfield net/rim/device/cldc/io/lstp/Transport._lock Ljava/lang/Object;
      // 172: dup
      // 173: astore 14
      // 175: monitorenter
      // 176: aload 0
      // 177: getfield net/rim/device/cldc/io/lstp/Transport._txStatus I
      // 17a: ifne 18d
      // 17d: aload 0
      // 17e: getfield net/rim/device/cldc/io/lstp/Transport._lock Ljava/lang/Object;
      // 181: sipush 5000
      // 184: i2l
      // 185: invokevirtual java/lang/Object.wait (J)V
      // 188: goto 18d
      // 18b: astore 15
      // 18d: aload 0
      // 18e: getfield net/rim/device/cldc/io/lstp/Transport._txStatus I
      // 191: istore 13
      // 193: aload 14
      // 195: monitorexit
      // 196: goto 1a1
      // 199: astore 18
      // 19b: aload 14
      // 19d: monitorexit
      // 19e: aload 18
      // 1a0: athrow
      // 1a1: iload 13
      // 1a3: bipush 1
      // 1a4: if_icmpne 1aa
      // 1a7: goto 287
      // 1aa: iload 13
      // 1ac: bipush 2
      // 1ae: if_icmpne 1fc
      // 1b1: iinc 12 1
      // 1b4: iload 12
      // 1b6: bipush 3
      // 1b8: if_icmplt 1e3
      // 1bb: ldc2_w -754053862978797267
      // 1be: ldc_w 1413836129
      // 1c1: bipush 2
      // 1c3: invokestatic net/rim/device/api/system/EventLogger.logEvent (JII)Z
      // 1c6: pop
      // 1c7: aload 0
      // 1c8: invokespecial net/rim/device/cldc/io/lstp/Transport.getNativeLayer ()Lnet/rim/device/cldc/io/lstp/NativeLayer;
      // 1cb: bipush 1
      // 1cc: invokevirtual net/rim/device/cldc/io/lstp/NativeLayer.close (Z)V
      // 1cf: aload 0
      // 1d0: aload 4
      // 1d2: iload 5
      // 1d4: sipush 8321
      // 1d7: aconst_null
      // 1d8: invokevirtual net/rim/device/api/io/DatagramTransportBase.xmitDgslEvent (Lnet/rim/device/api/io/DatagramStatusListener;IILjava/lang/Object;)V
      // 1db: new java/lang/Object
      // 1de: dup
      // 1df: invokespecial java/io/IOException.<init> ()V
      // 1e2: athrow
      // 1e3: ldc2_w -754053862978797267
      // 1e6: ldc_w 1415082596
      // 1e9: bipush 3
      // 1eb: invokestatic net/rim/device/api/system/EventLogger.logEvent (JII)Z
      // 1ee: pop
      // 1ef: aload 0
      // 1f0: aload 4
      // 1f2: iload 5
      // 1f4: bipush 1
      // 1f5: aconst_null
      // 1f6: invokevirtual net/rim/device/api/io/DatagramTransportBase.xmitDgslEvent (Lnet/rim/device/api/io/DatagramStatusListener;IILjava/lang/Object;)V
      // 1f9: goto 0ce
      // 1fc: iload 13
      // 1fe: ifne 21d
      // 201: ldc2_w -754053862978797267
      // 204: ldc_w 1415083119
      // 207: bipush 3
      // 209: invokestatic net/rim/device/api/system/EventLogger.logEvent (JII)Z
      // 20c: pop
      // 20d: aload 0
      // 20e: invokespecial net/rim/device/cldc/io/lstp/Transport.getNativeLayer ()Lnet/rim/device/cldc/io/lstp/NativeLayer;
      // 211: bipush 1
      // 212: invokevirtual net/rim/device/cldc/io/lstp/NativeLayer.close (Z)V
      // 215: new java/lang/Object
      // 218: dup
      // 219: invokespecial net/rim/device/api/io/IONotRoutableException.<init> ()V
      // 21c: athrow
      // 21d: iload 13
      // 21f: bipush 3
      // 221: if_icmpne 238
      // 224: ldc2_w -754053862978797267
      // 227: ldc_w 1415081586
      // 22a: bipush 3
      // 22c: invokestatic net/rim/device/api/system/EventLogger.logEvent (JII)Z
      // 22f: pop
      // 230: new java/lang/Object
      // 233: dup
      // 234: invokespecial net/rim/device/api/io/IONotRoutableException.<init> ()V
      // 237: athrow
      // 238: iload 13
      // 23a: bipush 4
      // 23c: if_icmpne 25f
      // 23f: ldc2_w -754053862978797267
      // 242: ldc_w 1415078753
      // 245: bipush 3
      // 247: invokestatic net/rim/device/api/system/EventLogger.logEvent (JII)Z
      // 24a: pop
      // 24b: aload 0
      // 24c: aload 4
      // 24e: iload 5
      // 250: sipush 129
      // 253: aconst_null
      // 254: invokevirtual net/rim/device/api/io/DatagramTransportBase.xmitDgslEvent (Lnet/rim/device/api/io/DatagramStatusListener;IILjava/lang/Object;)V
      // 257: new java/lang/Object
      // 25a: dup
      // 25b: invokespecial net/rim/device/api/io/IOCancelledException.<init> ()V
      // 25e: athrow
      // 25f: ldc2_w -754053862978797267
      // 262: ldc_w 1413834337
      // 265: bipush 2
      // 267: invokestatic net/rim/device/api/system/EventLogger.logEvent (JII)Z
      // 26a: pop
      // 26b: aload 0
      // 26c: invokespecial net/rim/device/cldc/io/lstp/Transport.getNativeLayer ()Lnet/rim/device/cldc/io/lstp/NativeLayer;
      // 26f: bipush 1
      // 270: invokevirtual net/rim/device/cldc/io/lstp/NativeLayer.close (Z)V
      // 273: aload 0
      // 274: aload 4
      // 276: iload 5
      // 278: sipush 12673
      // 27b: aconst_null
      // 27c: invokevirtual net/rim/device/api/io/DatagramTransportBase.xmitDgslEvent (Lnet/rim/device/api/io/DatagramStatusListener;IILjava/lang/Object;)V
      // 27f: new java/lang/Object
      // 282: dup
      // 283: invokespecial java/io/IOException.<init> ()V
      // 286: athrow
      // 287: iload 9
      // 289: ifle 28f
      // 28c: goto 095
      // 28f: aload 0
      // 290: getfield net/rim/device/cldc/io/lstp/Transport._lock Ljava/lang/Object;
      // 293: dup
      // 294: astore 8
      // 296: monitorenter
      // 297: aload 0
      // 298: aconst_null
      // 299: putfield net/rim/device/cldc/io/lstp/Transport._txDatagram Ljavax/microedition/io/Datagram;
      // 29c: aload 0
      // 29d: getfield net/rim/device/cldc/io/lstp/Transport._txBuffer Lnet/rim/device/api/util/DataBuffer;
      // 2a0: invokevirtual net/rim/device/api/util/DataBuffer.reset ()V
      // 2a3: aload 8
      // 2a5: monitorexit
      // 2a6: goto 2d8
      // 2a9: astore 19
      // 2ab: aload 8
      // 2ad: monitorexit
      // 2ae: aload 19
      // 2b0: athrow
      // 2b1: astore 20
      // 2b3: aload 0
      // 2b4: getfield net/rim/device/cldc/io/lstp/Transport._lock Ljava/lang/Object;
      // 2b7: dup
      // 2b8: astore 21
      // 2ba: monitorenter
      // 2bb: aload 0
      // 2bc: aconst_null
      // 2bd: putfield net/rim/device/cldc/io/lstp/Transport._txDatagram Ljavax/microedition/io/Datagram;
      // 2c0: aload 0
      // 2c1: getfield net/rim/device/cldc/io/lstp/Transport._txBuffer Lnet/rim/device/api/util/DataBuffer;
      // 2c4: invokevirtual net/rim/device/api/util/DataBuffer.reset ()V
      // 2c7: aload 21
      // 2c9: monitorexit
      // 2ca: goto 2d5
      // 2cd: astore 22
      // 2cf: aload 21
      // 2d1: monitorexit
      // 2d2: aload 22
      // 2d4: athrow
      // 2d5: aload 20
      // 2d7: athrow
      // 2d8: ldc2_w -754053862978797267
      // 2db: ldc_w 1415082867
      // 2de: bipush 4
      // 2e0: invokestatic net/rim/device/api/system/EventLogger.logEvent (JII)Z
      // 2e3: pop
      // 2e4: aload 0
      // 2e5: aload 4
      // 2e7: iload 5
      // 2e9: bipush 0
      // 2ea: aconst_null
      // 2eb: invokevirtual net/rim/device/api/io/DatagramTransportBase.xmitDgslEvent (Lnet/rim/device/api/io/DatagramStatusListener;IILjava/lang/Object;)V
      // 2ee: return
      // try (49 -> 54): 55 null
      // try (55 -> 58): 55 null
      // try (104 -> 109): 110 null
      // try (110 -> 113): 110 null
      // try (120 -> 132): 133 null
      // try (139 -> 144): 145 null
      // try (145 -> 148): 145 null
      // try (120 -> 132): 150 null
      // try (156 -> 161): 162 null
      // try (162 -> 165): 162 null
      // try (120 -> 132): 167 null
      // try (173 -> 178): 179 null
      // try (179 -> 182): 179 null
      // try (192 -> 197): 198 null
      // try (189 -> 204): 205 null
      // try (205 -> 208): 205 null
      // try (324 -> 332): 333 null
      // try (333 -> 336): 333 null
      // try (60 -> 319): 338 null
      // try (344 -> 352): 353 null
      // try (353 -> 356): 353 null
      // try (338 -> 339): 338 null
   }

   @Override
   public final void cancel(Datagram datagram) {
      synchronized (this._lock) {
         if (datagram == this._txDatagram) {
            this.getNativeLayer().cancelPacket();
            this._txDatagram = null;
            this._txStatus = 4;
            this._lock.notify();
         }
      }
   }

   protected final void processResponse(boolean acked) {
      synchronized (this._lock) {
         if (this._txDatagram != null) {
            this._txStatus = acked ? 1 : 2;
            this._lock.notify();
         } else {
            EventLogger.logEvent(-754053862978797267L, 1381196905, 3);
         }
      }
   }

   @Override
   protected final void processReceivedDatagram(Datagram datagram) {
      EventLogger.logEvent(-754053862978797267L, 1381527669, 5);
      if (!this.passUpDatagram(datagram)) {
         EventLogger.logEvent(-754053862978797267L, 1381527152, 3);
      }
   }

   @Override
   public final void lstpLinkStateChanged(boolean linkState) {
      if (!linkState) {
         synchronized (this._lock) {
            if (this._txDatagram != null) {
               this._txStatus = 3;
               this._lock.notify();
            }
         }
      }
   }
}
