package net.rim.device.cldc.io.ippp;

import java.io.IOException;
import java.io.InputStream;
import net.rim.device.api.io.IOCancelledException;
import net.rim.device.cldc.io.utility.SessionStats;
import net.rim.device.internal.browser.util.Pipe;
import net.rim.device.internal.browser.util.PipeContext;
import net.rim.device.internal.browser.util.PipeInput;
import net.rim.device.internal.browser.util.PipeInputStream;
import net.rim.device.internal.browser.util.PipePtr;
import net.rim.device.internal.compress.YKDecode;

public class SocketPipeInputStream extends InputStream implements PipeInput {
   private SessionStats _stats = new SessionStats();
   private Pipe _pipe = new Pipe();
   private PipeInputStream _in = this._pipe.getInputStream();
   private Object _syncObject = new Object();
   private IOException _socketBaseException;
   private YKDecode _ykDecoder;
   private int _currentPacket = 0;
   private SocketInputStream _socketIn;
   private boolean _isClosed;

   void setTimeout(long value) {
      this._pipe.setTimeout((int)value);
   }

   public void setStats(SessionStats stats) {
      if (stats != null) {
         synchronized (this._syncObject) {
            stats.addToReceived(this._stats.getBytesReceived());
            stats.addToSent(this._stats.getBytesSent());
            this._stats = stats;
         }
      }
   }

   public int getNumRead() {
      return this._in.getNumRead();
   }

   @Override
   public PipeContext getPosition() {
      return this._in.getPosition();
   }

   @Override
   public Pipe getPipe() {
      return this._pipe;
   }

   @Override
   public int readByteArray(PipePtr ptr, int length) throws IOCancelledException {
      if (this._isClosed) {
         throw new IOCancelledException();
      } else {
         return this._in.readByteArray(ptr, length);
      }
   }

   @Override
   public void skipInlineString() throws IOCancelledException {
      if (this._isClosed) {
         throw new IOCancelledException();
      }

      this._in.skipInlineString();
   }

   @Override
   public String readInlineString(String encoding) throws IOCancelledException {
      if (this._isClosed) {
         throw new IOCancelledException();
      } else {
         return this._in.readInlineString(encoding);
      }
   }

   @Override
   public int readCompressedInt() throws IOCancelledException {
      if (this._isClosed) {
         throw new IOCancelledException();
      } else {
         return this._in.readCompressedInt();
      }
   }

   @Override
   public boolean markSupported() {
      return this._in.markSupported();
   }

   @Override
   public void mark(int limit) {
      this._in.mark(limit);
   }

   @Override
   public void reset() {
      this._in.reset();
   }

   @Override
   public int read(byte[] b, int off, int len) throws IOException, IOCancelledException {
      if (this._isClosed) {
         throw new IOCancelledException();
      } else {
         int value = this._in.read(b, off, len);
         if (this._socketBaseException != null) {
            throw this._socketBaseException;
         } else {
            return value;
         }
      }
   }

   @Override
   public int available() throws IOCancelledException {
      if (this._isClosed) {
         throw new IOCancelledException();
      } else {
         return this._in.available();
      }
   }

   @Override
   public int read() throws IOException, IOCancelledException {
      if (this._isClosed) {
         throw new IOCancelledException();
      } else {
         int value = this._in.read();
         if (this._socketBaseException != null) {
            throw this._socketBaseException;
         } else {
            return value;
         }
      }
   }

   @Override
   public void close() {
      if (!this._isClosed) {
         this._isClosed = true;
         super.close();
         this._in.close();
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public SocketPipeInputStream(SocketInputStream socketIn, boolean ykEncoded, boolean ykMixedEncoded) {
      this._socketIn = socketIn;
      if (ykEncoded || ykMixedEncoded) {
         this._ykDecoder = new YKDecode(ykMixedEncoded);
      }

      boolean success = false;
      boolean var9 = false /* VF: Semaphore variable */;

      try {
         var9 = true;
         byte[] data = socketIn.getRemainingBuffer();
         if (data.length > 0) {
            if (this._ykDecoder != null) {
               data = this._ykDecoder.yk_decode(data, 0, data.length);
            }

            this._pipe.write(data, 0, data.length, this._currentPacket++);
         }

         SocketPipeInputStream$AccumulatorThread thread = new SocketPipeInputStream$AccumulatorThread(this);
         thread.start();
         success = true;
         var9 = false;
      } finally {
         if (var9) {
            if (!success && this._ykDecoder != null) {
               this._ykDecoder.yk_uninit();
            }
         }
      }

      if (!success && this._ykDecoder != null) {
         this._ykDecoder.yk_uninit();
      }
   }

   static int access$208(SocketPipeInputStream x0) {
      return x0._currentPacket++;
   }
}
