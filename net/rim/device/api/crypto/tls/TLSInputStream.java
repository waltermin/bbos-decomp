package net.rim.device.api.crypto.tls;

import java.io.InputStream;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.cldc.io.ssl.TLSIOException;

public final class TLSInputStream extends InputStream {
   private RecordProtocol _recordProtocol;
   private DataBuffer _buffer;
   private boolean _isClosed;
   private static final byte ALERT = 21;
   private static final byte APPLICATION_DATA = 23;

   public TLSInputStream(RecordProtocol recordProtocol) {
      if (recordProtocol == null) {
         throw new IllegalArgumentException();
      }

      this._recordProtocol = recordProtocol;
      this._buffer = new DataBuffer();
   }

   @Override
   public final int available() {
      return this._buffer.getLength() - this._buffer.getPosition();
   }

   @Override
   public final void close() {
      this._isClosed = true;
   }

   public final boolean isClosed() {
      return this._isClosed;
   }

   @Override
   public final int read() throws TLSIOException {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 0
      // 01: getfield net/rim/device/api/crypto/tls/TLSInputStream._recordProtocol Lnet/rim/device/api/crypto/tls/RecordProtocol;
      // 04: invokevirtual net/rim/device/api/crypto/tls/RecordProtocol.connect ()V
      // 07: aload 0
      // 08: getfield net/rim/device/api/crypto/tls/TLSInputStream._buffer Lnet/rim/device/api/util/DataBuffer;
      // 0b: invokevirtual net/rim/device/api/util/DataBuffer.available ()I
      // 0e: ifgt 38
      // 11: aload 0
      // 12: getfield net/rim/device/api/crypto/tls/TLSInputStream._recordProtocol Lnet/rim/device/api/crypto/tls/RecordProtocol;
      // 15: aload 0
      // 16: getfield net/rim/device/api/crypto/tls/TLSInputStream._buffer Lnet/rim/device/api/util/DataBuffer;
      // 19: invokevirtual net/rim/device/api/crypto/tls/RecordProtocol.read (Lnet/rim/device/api/util/DataBuffer;)I
      // 1c: istore 1
      // 1d: iload 1
      // 1e: bipush 23
      // 20: if_icmpeq 07
      // 23: iload 1
      // 24: bipush 21
      // 26: if_icmpne 2c
      // 29: goto 07
      // 2c: new net/rim/device/api/crypto/tls/TLSAlertException
      // 2f: dup
      // 30: bipush 3
      // 32: bipush 10
      // 34: invokespecial net/rim/device/api/crypto/tls/TLSAlertException.<init> (BB)V
      // 37: athrow
      // 38: aload 0
      // 39: getfield net/rim/device/api/crypto/tls/TLSInputStream._buffer Lnet/rim/device/api/util/DataBuffer;
      // 3c: invokevirtual net/rim/device/api/util/DataBuffer.readUnsignedByte ()I
      // 3f: ireturn
      // 40: astore 1
      // 41: new net/rim/device/cldc/io/ssl/TLSIOException
      // 44: dup
      // 45: aload 1
      // 46: invokespecial net/rim/device/cldc/io/ssl/TLSIOException.<init> (Lnet/rim/device/cldc/io/ssl/TLSException;)V
      // 49: athrow
      // 4a: astore 1
      // 4b: bipush -1
      // 4d: ireturn
      // try (0 -> 29): 30 null
      // try (0 -> 29): 36 null
   }

   @Override
   public final int read(byte[] param1, int param2, int param3) throws TLSIOException {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 1
      // 01: ifnull 14
      // 04: iload 2
      // 05: iflt 14
      // 08: iload 3
      // 09: iflt 14
      // 0c: iload 2
      // 0d: iload 3
      // 0e: iadd
      // 0f: aload 1
      // 10: arraylength
      // 11: if_icmple 1c
      // 14: new java/lang/IllegalArgumentException
      // 17: dup
      // 18: invokespecial java/lang/IllegalArgumentException.<init> ()V
      // 1b: athrow
      // 1c: aload 0
      // 1d: getfield net/rim/device/api/crypto/tls/TLSInputStream._recordProtocol Lnet/rim/device/api/crypto/tls/RecordProtocol;
      // 20: invokevirtual net/rim/device/api/crypto/tls/RecordProtocol.connect ()V
      // 23: aload 0
      // 24: getfield net/rim/device/api/crypto/tls/TLSInputStream._buffer Lnet/rim/device/api/util/DataBuffer;
      // 27: invokevirtual net/rim/device/api/util/DataBuffer.available ()I
      // 2a: ifgt 57
      // 2d: aload 0
      // 2e: getfield net/rim/device/api/crypto/tls/TLSInputStream._recordProtocol Lnet/rim/device/api/crypto/tls/RecordProtocol;
      // 31: aload 0
      // 32: getfield net/rim/device/api/crypto/tls/TLSInputStream._buffer Lnet/rim/device/api/util/DataBuffer;
      // 35: invokevirtual net/rim/device/api/crypto/tls/RecordProtocol.read (Lnet/rim/device/api/util/DataBuffer;)I
      // 38: istore 4
      // 3a: iload 4
      // 3c: bipush 23
      // 3e: if_icmpeq 23
      // 41: iload 4
      // 43: bipush 21
      // 45: if_icmpne 4b
      // 48: goto 23
      // 4b: new net/rim/device/api/crypto/tls/TLSAlertException
      // 4e: dup
      // 4f: bipush 3
      // 51: bipush 10
      // 53: invokespecial net/rim/device/api/crypto/tls/TLSAlertException.<init> (BB)V
      // 56: athrow
      // 57: aload 0
      // 58: getfield net/rim/device/api/crypto/tls/TLSInputStream._buffer Lnet/rim/device/api/util/DataBuffer;
      // 5b: aload 1
      // 5c: iload 2
      // 5d: iload 3
      // 5e: invokevirtual net/rim/device/api/util/DataBuffer.read ([BII)I
      // 61: ireturn
      // 62: astore 4
      // 64: new net/rim/device/cldc/io/ssl/TLSIOException
      // 67: dup
      // 68: aload 4
      // 6a: invokespecial net/rim/device/cldc/io/ssl/TLSIOException.<init> (Lnet/rim/device/cldc/io/ssl/TLSException;)V
      // 6d: athrow
      // 6e: astore 4
      // 70: bipush -1
      // 72: ireturn
      // try (16 -> 48): 49 null
      // try (16 -> 48): 55 null
   }
}
