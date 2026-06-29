package net.rim.device.api.crypto.tls;

import java.io.OutputStream;
import net.rim.device.api.util.DataBuffer;

public final class TLSOutputStream extends OutputStream {
   private RecordProtocol _recordProtocol;
   private boolean _isClosed;
   private DataBuffer _buffer;
   private static final int MAX_WRITE_SIZE;

   public TLSOutputStream(RecordProtocol recordProtocol) {
      this._recordProtocol = recordProtocol;
      this._buffer = (DataBuffer)(new Object());
   }

   @Override
   public final synchronized void close() {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 0
      // 01: getfield net/rim/device/api/crypto/tls/TLSOutputStream._isClosed Z
      // 04: ifeq 08
      // 07: return
      // 08: aload 0
      // 09: invokespecial net/rim/device/api/crypto/tls/TLSOutputStream.flushImpl ()V
      // 0c: aload 0
      // 0d: getfield net/rim/device/api/crypto/tls/TLSOutputStream._recordProtocol Lnet/rim/device/api/crypto/tls/RecordProtocol;
      // 10: invokevirtual net/rim/device/api/crypto/tls/RecordProtocol.getAlertProtocol ()Lnet/rim/device/api/crypto/tls/AlertProtocolMethods;
      // 13: bipush 1
      // 14: invokeinterface net/rim/device/api/crypto/tls/AlertProtocolMethods.sendCloseNotify (Z)V 2
      // 19: aload 0
      // 1a: bipush 1
      // 1b: putfield net/rim/device/api/crypto/tls/TLSOutputStream._isClosed Z
      // 1e: return
      // 1f: astore 1
      // 20: aload 1
      // 21: invokevirtual net/rim/device/cldc/io/ssl/TLSException.getException ()Ljava/lang/Exception;
      // 24: astore 2
      // 25: aload 2
      // 26: dup
      // 27: instanceof net/rim/device/api/crypto/tls/TLSAlertException
      // 2a: ifne 31
      // 2d: pop
      // 2e: goto 3d
      // 31: checkcast net/rim/device/api/crypto/tls/TLSAlertException
      // 34: invokevirtual net/rim/device/api/crypto/tls/TLSAlertException.getAlertDescription ()B
      // 37: ifne 3d
      // 3a: goto 50
      // 3d: aload 2
      // 3e: instanceof java/lang/Object
      // 41: ifeq 47
      // 44: goto 50
      // 47: new java/lang/Object
      // 4a: dup
      // 4b: aload 1
      // 4c: invokespecial net/rim/device/cldc/io/ssl/TLSIOException.<init> (Lnet/rim/device/cldc/io/ssl/TLSException;)V
      // 4f: athrow
      // 50: aload 0
      // 51: bipush 1
      // 52: putfield net/rim/device/api/crypto/tls/TLSOutputStream._isClosed Z
      // 55: return
      // 56: astore 1
      // 57: aload 0
      // 58: bipush 1
      // 59: putfield net/rim/device/api/crypto/tls/TLSOutputStream._isClosed Z
      // 5c: return
      // 5d: astore 3
      // 5e: aload 0
      // 5f: bipush 1
      // 60: putfield net/rim/device/api/crypto/tls/TLSOutputStream._isClosed Z
      // 63: aload 3
      // 64: athrow
      // try (4 -> 11): 15 null
      // try (4 -> 11): 42 null
      // try (4 -> 11): 47 null
      // try (15 -> 38): 47 null
      // try (42 -> 43): 47 null
      // try (47 -> 48): 47 null
   }

   public final boolean isClosed() {
      return this._isClosed;
   }

   @Override
   public final void write(byte[] b, int off, int len) {
      if (this._isClosed) {
         throw new Object();
      }

      this._buffer.write(b, off, len);
      this.flushFullRecords();
   }

   @Override
   public final void write(int b) {
      if (this._isClosed) {
         throw new Object();
      }

      this._buffer.write(b);
      this.flushFullRecords();
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final void flushFullRecords() {
      int len = this._buffer.getPosition();
      if (len >= 1300) {
         int pos = 0;
         byte[] buffer = this._buffer.getArray();
         int applicationType = this._recordProtocol.getApplicationProtocolConstant();

         try {
            this._buffer.rewind();
            this._recordProtocol.connect();

            while (len - pos >= 1300) {
               this._recordProtocol.write(applicationType, buffer, pos, 1300);
               pos += 1300;
            }

            byte[] data = new byte[len - pos];
            System.arraycopy(buffer, pos, data, 0, data.length);
            this._buffer.setData(data, 0, data.length);
         } catch (Throwable var7) {
            throw new Object(e);
         }
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void flush() {
      if (this._isClosed) {
         throw new Object();
      }

      try {
         this.flushImpl();
      } catch (Throwable var3) {
         throw new Object(e);
      }
   }

   private final void flushImpl() {
      this._recordProtocol.connect();
      if (this._buffer.getLength() > 0) {
         this._buffer.rewind();
         this._recordProtocol.write(this._recordProtocol.getApplicationProtocolConstant(), this._buffer);
         this._buffer.reset();
      }

      this._recordProtocol.flush();
   }
}
