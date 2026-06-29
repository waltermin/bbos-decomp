package net.rim.device.cldc.io.lstp;

import net.rim.device.api.system.Application;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.util.CRC16;
import net.rim.device.api.util.DataBuffer;

class SerialLayer extends NativeLayer {
   protected Object _sendLock = new Object();
   private int _rxTimerId = -1;
   private SerialReplyThread _replyThread;
   private int _fragmentSize;
   private Application _app = Application.getApplication();
   private Runnable _rxTimer = new SerialLayer$1(this);
   private static final int TX_EMPTY_TIMEOUT = 3000;
   private static final int RX_TIMEOUT = 3000;
   protected static final byte HEADER_1 = -39;
   protected static final byte HEADER_2 = -82;
   protected static final byte HEADER_3 = -5;
   protected static final byte TRAILER_1 = -65;
   protected static final byte TRAILER_2 = -22;
   protected static final byte TRAILER_3 = -99;
   protected static final byte[] HEADER = new byte[]{-39, -82, -5};
   protected static final int HEADER_INT = 14266107;
   protected static final byte[] TRAILER = new byte[]{-65, -22, -99};
   protected static final int TRAILER_INT = 12577437;
   protected static final int CRC_START = CRC16.update(65535, HEADER);
   protected static final int DATA_HEADER_SIZE = HEADER.length + 1 + 1 + 2;
   protected static final int DATA_TRAILER_SIZE = 2 + TRAILER.length;
   protected static final int DATA_FRAGMENT_SIZE = 2048;
   protected static final int DATA_FRAGMENT_MIN = 128;
   protected static final int DATA_FRAGMENT_PAYLOAD = 2047 - (DATA_HEADER_SIZE + DATA_TRAILER_SIZE + TRAILER.length + 1);
   private static final int VERSION_REQUIRING_DTR_FIX = 65536;
   private static final byte[] HELLO_ACK_BUFFER = new byte[]{-39, -82, -5, 57, -65, -22, -99};
   private static final byte[] ACK_BUFFER = new byte[]{6};
   private static final byte[] NACK_BUFFER = new byte[]{21};

   protected int nativeRead(byte[] _1, int _2, int _3) {
      throw null;
   }

   protected int nativeWrite(byte[] _1, int _2, int _3) {
      throw null;
   }

   protected void nativeEnableDtrFix() {
      throw null;
   }

   protected SerialLayer(Transport transport) {
      super(transport);
      this._replyThread = new SerialReplyThread(this);
      this._fragmentSize = 65535;
   }

   @Override
   protected int getFragmentSize() {
      return this._fragmentSize;
   }

   @Override
   protected void sendPacket(byte[] buffer, int offset, int length) {
      synchronized (this._sendLock) {
         do {
            int chunk = this.nativeWrite(buffer, offset, length);
            if (chunk <= 0) {
               EventLogger.logEvent(-754053862978797267L, 1414424161, 3);
            }

            offset += chunk;
            length -= chunk;

            try {
               this._sendLock.wait(3000);
            } finally {
               continue;
            }
         } while (length > 0);
      }
   }

   @Override
   protected void cancelPacket() {
      synchronized (this._sendLock) {
         this._sendLock.notifyAll();
      }
   }

   @Override
   protected void configure(int baudrate, int fragmentSize) {
      this._fragmentSize = fragmentSize;
      if (this._fragmentSize < 128) {
         EventLogger.logEvent(-754053862978797267L, 1380279911, 3);
         this._fragmentSize = 128;
      }
   }

   @Override
   protected void close(boolean redirect) {
      super.close(redirect);
      this._fragmentSize = 65535;
   }

   @Override
   protected int encodeDataFragment(byte id, byte[] src, int srcOffset, int srcLength, DataBuffer dest) {
      byte type;
      if (srcLength <= this._fragmentSize) {
         type = 64;
      } else {
         srcLength = this._fragmentSize;
         type = 96;
      }

      dest.ensureCapacity(DATA_HEADER_SIZE + srcLength + DATA_TRAILER_SIZE);
      int offset = dest.getArrayPosition();
      dest.write(HEADER, 0, 3);
      dest.writeByte(type);
      dest.writeByte(id);
      dest.writeShort(srcLength);
      if (srcLength > 0) {
         dest.write(src, srcOffset, srcLength);
      }

      int crc = CRC16.update(65535, dest.getArray(), offset, DATA_HEADER_SIZE + srcLength);
      dest.setBigEndian(false);
      dest.writeShort(~crc);
      dest.setBigEndian(true);
      dest.write(TRAILER, 0, 3);
      return srcLength;
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
      // 000: sipush 2048
      // 003: newarray 8
      // 005: astore 1
      // 006: bipush 0
      // 007: istore 2
      // 008: bipush 0
      // 009: istore 3
      // 00a: bipush 0
      // 00b: istore 4
      // 00d: bipush 0
      // 00e: istore 5
      // 010: bipush 0
      // 011: istore 6
      // 013: bipush 0
      // 014: istore 7
      // 016: bipush 0
      // 017: istore 8
      // 019: bipush 0
      // 01a: istore 9
      // 01c: bipush 0
      // 01d: istore 10
      // 01f: aconst_null
      // 020: astore 11
      // 022: bipush 0
      // 023: istore 12
      // 025: bipush 0
      // 026: istore 13
      // 028: bipush 0
      // 029: istore 14
      // 02b: bipush 0
      // 02c: istore 15
      // 02e: aload 0
      // 02f: getfield net/rim/device/cldc/io/lstp/NativeLayer._receiveLock Ljava/lang/Object;
      // 032: dup
      // 033: astore 17
      // 035: monitorenter
      // 036: aload 0
      // 037: getfield net/rim/device/cldc/io/lstp/NativeLayer._dataAvailable I
      // 03a: ifne 044
      // 03d: aload 0
      // 03e: getfield net/rim/device/cldc/io/lstp/NativeLayer._receiveLock Ljava/lang/Object;
      // 041: invokevirtual java/lang/Object.wait ()V
      // 044: aload 0
      // 045: getfield net/rim/device/cldc/io/lstp/NativeLayer._dataAvailable I
      // 048: istore 16
      // 04a: aload 0
      // 04b: bipush 0
      // 04c: putfield net/rim/device/cldc/io/lstp/NativeLayer._dataAvailable I
      // 04f: aload 0
      // 050: aload 1
      // 051: bipush 0
      // 052: sipush 2048
      // 055: invokevirtual net/rim/device/cldc/io/lstp/SerialLayer.nativeRead ([BII)I
      // 058: istore 2
      // 059: iload 16
      // 05b: ifge 08b
      // 05e: ldc2_w -754053862978797267
      // 061: ldc_w 1380281454
      // 064: bipush 3
      // 066: invokestatic net/rim/device/api/system/EventLogger.logEvent (JII)Z
      // 069: pop
      // 06a: iload 16
      // 06c: bipush -2
      // 06e: if_icmpne 08b
      // 071: aload 0
      // 072: bipush 21
      // 074: invokevirtual net/rim/device/cldc/io/lstp/SerialLayer.sendAck (B)V
      // 077: aload 11
      // 079: ifnull 083
      // 07c: aload 11
      // 07e: iload 9
      // 080: invokevirtual net/rim/device/api/util/DataBuffer.setPosition (I)V
      // 083: bipush 0
      // 084: istore 3
      // 085: aload 17
      // 087: monitorexit
      // 088: goto 02e
      // 08b: bipush 0
      // 08c: istore 18
      // 08e: iload 18
      // 090: iload 2
      // 091: if_icmplt 097
      // 094: goto 3ae
      // 097: iload 3
      // 098: tableswitch 48 -1 6 784 48 212 351 379 524 593 734
      // 0c8: aload 1
      // 0c9: iload 18
      // 0cb: baload
      // 0cc: lookupswitch 86 2 6 28 21 57
      // 0e8: ldc2_w -754053862978797267
      // 0eb: ldc_w 1381000289
      // 0ee: bipush 4
      // 0f0: invokestatic net/rim/device/api/system/EventLogger.logEvent (JII)Z
      // 0f3: pop
      // 0f4: aload 0
      // 0f5: getfield net/rim/device/cldc/io/lstp/NativeLayer._transport Lnet/rim/device/cldc/io/lstp/Transport;
      // 0f8: bipush 1
      // 0f9: invokevirtual net/rim/device/cldc/io/lstp/Transport.processResponse (Z)V
      // 0fc: bipush 0
      // 0fd: istore 5
      // 0ff: bipush 0
      // 100: istore 4
      // 102: goto 3a8
      // 105: ldc2_w -754053862978797267
      // 108: ldc_w 1381000302
      // 10b: bipush 3
      // 10d: invokestatic net/rim/device/api/system/EventLogger.logEvent (JII)Z
      // 110: pop
      // 111: aload 0
      // 112: getfield net/rim/device/cldc/io/lstp/NativeLayer._transport Lnet/rim/device/cldc/io/lstp/Transport;
      // 115: bipush 0
      // 116: invokevirtual net/rim/device/cldc/io/lstp/Transport.processResponse (Z)V
      // 119: bipush 0
      // 11a: istore 5
      // 11c: bipush 0
      // 11d: istore 4
      // 11f: goto 3a8
      // 122: iload 4
      // 124: bipush 8
      // 126: ishl
      // 127: aload 1
      // 128: iload 18
      // 12a: baload
      // 12b: sipush 255
      // 12e: iand
      // 12f: ior
      // 130: istore 4
      // 132: iload 4
      // 134: ldc_w 16777215
      // 137: iand
      // 138: istore 19
      // 13a: iload 19
      // 13c: ldc_w 14266107
      // 13f: if_icmpne 14d
      // 142: bipush 0
      // 143: istore 5
      // 145: bipush 0
      // 146: istore 4
      // 148: bipush 1
      // 149: istore 3
      // 14a: goto 3a8
      // 14d: iload 19
      // 14f: ldc_w 12577437
      // 152: if_icmpne 166
      // 155: iload 5
      // 157: bipush 10
      // 159: if_icmplt 160
      // 15c: aload 0
      // 15d: invokespecial net/rim/device/cldc/io/lstp/SerialLayer.startRxTimer ()V
      // 160: bipush 0
      // 161: istore 5
      // 163: goto 3a8
      // 166: iinc 5 1
      // 169: goto 3a8
      // 16c: aload 1
      // 16d: iload 18
      // 16f: baload
      // 170: lookupswitch 118 4 15 82 58 95 64 44 96 44
      // 19c: ldc2_w -754053862978797267
      // 19f: ldc_w 1381528436
      // 1a2: bipush 4
      // 1a4: invokestatic net/rim/device/api/system/EventLogger.logEvent (JII)Z
      // 1a7: pop
      // 1a8: aload 0
      // 1a9: invokespecial net/rim/device/cldc/io/lstp/SerialLayer.startRxTimer ()V
      // 1ac: aload 1
      // 1ad: iload 18
      // 1af: baload
      // 1b0: istore 6
      // 1b2: getstatic net/rim/device/cldc/io/lstp/SerialLayer.CRC_START I
      // 1b5: iload 6
      // 1b7: invokestatic net/rim/device/api/util/CRC16.update (II)I
      // 1ba: istore 13
      // 1bc: bipush 2
      // 1be: istore 3
      // 1bf: goto 3a8
      // 1c2: bipush 0
      // 1c3: istore 14
      // 1c5: bipush 4
      // 1c7: istore 15
      // 1c9: bipush 6
      // 1cb: istore 3
      // 1cc: goto 3a8
      // 1cf: ldc2_w -754053862978797267
      // 1d2: ldc_w 1381001317
      // 1d5: bipush 4
      // 1d7: invokestatic net/rim/device/api/system/EventLogger.logEvent (JII)Z
      // 1da: pop
      // 1db: aload 0
      // 1dc: bipush 57
      // 1de: invokevirtual net/rim/device/cldc/io/lstp/SerialLayer.sendAck (B)V
      // 1e1: bipush 0
      // 1e2: istore 3
      // 1e3: goto 3a8
      // 1e6: ldc2_w -754053862978797267
      // 1e9: ldc_w 1381004651
      // 1ec: bipush 3
      // 1ee: invokestatic net/rim/device/api/system/EventLogger.logEvent (JII)Z
      // 1f1: pop
      // 1f2: bipush 0
      // 1f3: istore 3
      // 1f4: goto 3a8
      // 1f7: aload 1
      // 1f8: iload 18
      // 1fa: baload
      // 1fb: istore 7
      // 1fd: iload 13
      // 1ff: iload 7
      // 201: invokestatic net/rim/device/api/util/CRC16.update (II)I
      // 204: istore 13
      // 206: bipush 0
      // 207: istore 8
      // 209: bipush 2
      // 20b: istore 15
      // 20d: bipush 3
      // 20f: istore 3
      // 210: goto 3a8
      // 213: iload 8
      // 215: bipush 8
      // 217: ishl
      // 218: aload 1
      // 219: iload 18
      // 21b: baload
      // 21c: sipush 255
      // 21f: iand
      // 220: ior
      // 221: istore 8
      // 223: iload 13
      // 225: aload 1
      // 226: iload 18
      // 228: baload
      // 229: invokestatic net/rim/device/api/util/CRC16.update (II)I
      // 22c: istore 13
      // 22e: iinc 15 -1
      // 231: iload 15
      // 233: ifle 239
      // 236: goto 3a8
      // 239: iload 8
      // 23b: getstatic net/rim/device/cldc/io/lstp/SerialLayer.DATA_FRAGMENT_PAYLOAD I
      // 23e: if_icmple 24d
      // 241: ldc2_w -754053862978797267
      // 244: ldc_w 1380279919
      // 247: bipush 3
      // 249: invokestatic net/rim/device/api/system/EventLogger.logEvent (JII)Z
      // 24c: pop
      // 24d: aload 11
      // 24f: ifnonnull 27d
      // 252: new java/lang/Object
      // 255: dup
      // 256: invokespecial net/rim/device/api/io/DatagramBase.<init> ()V
      // 259: astore 11
      // 25b: iload 7
      // 25d: istore 12
      // 25f: aload 0
      // 260: getfield net/rim/device/cldc/io/lstp/NativeLayer._lstpUtil Lnet/rim/device/cldc/io/lstp/LstpUtil;
      // 263: iload 12
      // 265: invokevirtual net/rim/device/cldc/io/lstp/LstpUtil.getAppName (I)Ljava/lang/String;
      // 268: invokevirtual java/lang/String.length ()I
      // 26b: ifgt 290
      // 26e: ldc2_w -754053862978797267
      // 271: ldc_w 1381001572
      // 274: bipush 2
      // 276: invokestatic net/rim/device/api/system/EventLogger.logEvent (JII)Z
      // 279: pop
      // 27a: goto 290
      // 27d: iload 12
      // 27f: iload 7
      // 281: if_icmpeq 290
      // 284: ldc2_w -754053862978797267
      // 287: ldc_w 1381001572
      // 28a: bipush 3
      // 28c: invokestatic net/rim/device/api/system/EventLogger.logEvent (JII)Z
      // 28f: pop
      // 290: aload 11
      // 292: invokevirtual net/rim/device/api/util/DataBuffer.getPosition ()I
      // 295: istore 9
      // 297: aload 11
      // 299: iload 8
      // 29b: invokevirtual net/rim/device/api/util/DataBuffer.ensureCapacity (I)V
      // 29e: bipush 4
      // 2a0: istore 3
      // 2a1: goto 3a8
      // 2a4: aload 11
      // 2a6: invokevirtual net/rim/device/api/util/DataBuffer.available ()I
      // 2a9: iload 2
      // 2aa: iload 18
      // 2ac: isub
      // 2ad: invokestatic java/lang/Math.min (II)I
      // 2b0: istore 19
      // 2b2: aload 11
      // 2b4: aload 1
      // 2b5: iload 18
      // 2b7: iload 19
      // 2b9: invokevirtual net/rim/device/api/util/DataBuffer.write ([BII)V
      // 2bc: iload 13
      // 2be: aload 1
      // 2bf: iload 18
      // 2c1: iload 19
      // 2c3: invokestatic net/rim/device/api/util/CRC16.update (I[BII)I
      // 2c6: istore 13
      // 2c8: iload 18
      // 2ca: iload 19
      // 2cc: bipush 1
      // 2cd: isub
      // 2ce: iadd
      // 2cf: istore 18
      // 2d1: aload 11
      // 2d3: invokevirtual net/rim/device/api/util/DataBuffer.available ()I
      // 2d6: ifle 2dc
      // 2d9: goto 3a8
      // 2dc: bipush 0
      // 2dd: istore 10
      // 2df: bipush 2
      // 2e1: istore 15
      // 2e3: bipush 5
      // 2e5: istore 3
      // 2e6: goto 3a8
      // 2e9: iload 10
      // 2eb: bipush 8
      // 2ed: iushr
      // 2ee: aload 1
      // 2ef: iload 18
      // 2f1: baload
      // 2f2: sipush 255
      // 2f5: iand
      // 2f6: bipush 8
      // 2f8: ishl
      // 2f9: ior
      // 2fa: istore 10
      // 2fc: iinc 15 -1
      // 2ff: iload 15
      // 301: ifle 307
      // 304: goto 3a8
      // 307: aload 0
      // 308: invokespecial net/rim/device/cldc/io/lstp/SerialLayer.stopRxTimer ()V
      // 30b: iload 10
      // 30d: iload 13
      // 30f: bipush -1
      // 311: ixor
      // 312: ldc_w 65535
      // 315: iand
      // 316: if_icmpne 358
      // 319: aload 0
      // 31a: bipush 6
      // 31c: invokevirtual net/rim/device/cldc/io/lstp/SerialLayer.sendAck (B)V
      // 31f: iload 6
      // 321: bipush 64
      // 323: if_icmpne 349
      // 326: aload 11
      // 328: bipush 0
      // 329: invokevirtual net/rim/device/api/util/DataBuffer.trim (Z)V
      // 32c: aload 11
      // 32e: new net/rim/device/cldc/io/lstp/LstpAddress
      // 331: dup
      // 332: iload 12
      // 334: invokespecial net/rim/device/cldc/io/lstp/LstpAddress.<init> (I)V
      // 337: invokevirtual net/rim/device/api/io/DatagramBase.setAddressBase (Lnet/rim/device/api/io/DatagramAddressBase;)V
      // 33a: aload 0
      // 33b: getfield net/rim/device/cldc/io/lstp/NativeLayer._transport Lnet/rim/device/cldc/io/lstp/Transport;
      // 33e: aload 11
      // 340: invokevirtual net/rim/device/cldc/io/lstp/Transport.processReceivedDatagram (Ljavax/microedition/io/Datagram;)V
      // 343: aconst_null
      // 344: astore 11
      // 346: goto 371
      // 349: ldc2_w -754053862978797267
      // 34c: ldc_w 1381005153
      // 34f: bipush 4
      // 351: invokestatic net/rim/device/api/system/EventLogger.logEvent (JII)Z
      // 354: pop
      // 355: goto 371
      // 358: ldc2_w -754053862978797267
      // 35b: ldc_w 1380279154
      // 35e: bipush 3
      // 360: invokestatic net/rim/device/api/system/EventLogger.logEvent (JII)Z
      // 363: pop
      // 364: aload 0
      // 365: bipush 21
      // 367: invokevirtual net/rim/device/cldc/io/lstp/SerialLayer.sendAck (B)V
      // 36a: aload 11
      // 36c: iload 9
      // 36e: invokevirtual net/rim/device/api/util/DataBuffer.setPosition (I)V
      // 371: bipush 0
      // 372: istore 3
      // 373: goto 3a8
      // 376: iload 14
      // 378: bipush 8
      // 37a: ishl
      // 37b: aload 1
      // 37c: iload 18
      // 37e: baload
      // 37f: sipush 255
      // 382: iand
      // 383: ior
      // 384: istore 14
      // 386: iinc 15 -1
      // 389: iload 15
      // 38b: ifgt 3a8
      // 38e: ldc2_w -754053862978797267
      // 391: ldc_w 1381004914
      // 394: bipush 4
      // 396: invokestatic net/rim/device/api/system/EventLogger.logEvent (JII)Z
      // 399: pop
      // 39a: iload 14
      // 39c: ldc_w 65536
      // 39f: if_icmplt 3a6
      // 3a2: aload 0
      // 3a3: invokevirtual net/rim/device/cldc/io/lstp/SerialLayer.nativeEnableDtrFix ()V
      // 3a6: bipush 0
      // 3a7: istore 3
      // 3a8: iinc 18 1
      // 3ab: goto 08e
      // 3ae: aload 17
      // 3b0: monitorexit
      // 3b1: goto 02e
      // 3b4: astore 20
      // 3b6: aload 17
      // 3b8: monitorexit
      // 3b9: aload 20
      // 3bb: athrow
      // 3bc: astore 17
      // 3be: ldc2_w -754053862978797267
      // 3c1: ldc_w 1380152164
      // 3c4: bipush 4
      // 3c6: invokestatic net/rim/device/api/system/EventLogger.logEvent (JII)Z
      // 3c9: pop
      // 3ca: aload 0
      // 3cb: invokespecial net/rim/device/cldc/io/lstp/SerialLayer.stopRxTimer ()V
      // 3ce: return
      // 3cf: astore 17
      // 3d1: goto 02e
      // try (36 -> 76): 414 null
      // try (77 -> 413): 414 null
      // try (414 -> 417): 414 null
      // try (31 -> 76): 419 null
      // try (77 -> 419): 419 null
      // try (31 -> 76): 428 null
      // try (77 -> 419): 428 null
   }

   protected void sendAck(byte type) {
      EventLogger.logEvent(-754053862978797267L, 1414558580, 4);
      byte[] buffer;
      switch (type) {
         case 6:
            buffer = ACK_BUFFER;
            break;
         case 57:
            buffer = HELLO_ACK_BUFFER;
            break;
         default:
            buffer = NACK_BUFFER;
      }

      this._replyThread.addRequest(buffer);
   }

   private void startRxTimer() {
      synchronized (super._receiveLock) {
         if (this._rxTimerId == -1) {
            this._rxTimerId = this._app.invokeLater(this._rxTimer, 3000, false);
         }
      }
   }

   private void stopRxTimer() {
      synchronized (super._receiveLock) {
         if (this._rxTimerId != -1) {
            this._app.cancelInvokeLater(this._rxTimerId);
            this._rxTimerId = -1;
         }
      }
   }
}
