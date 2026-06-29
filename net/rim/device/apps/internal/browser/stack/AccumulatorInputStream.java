package net.rim.device.apps.internal.browser.stack;

import java.io.IOException;
import java.io.InputStream;
import javax.microedition.io.InputConnection;
import net.rim.device.cldc.io.utility.SessionStats;
import net.rim.device.internal.browser.util.Pipe;
import net.rim.device.internal.browser.util.PipeContext;
import net.rim.device.internal.browser.util.PipeInput;
import net.rim.device.internal.browser.util.PipeInputStream;
import net.rim.device.internal.browser.util.PipePtr;

public final class AccumulatorInputStream extends InputStream implements PipeInput {
   private InputConnection _connection;
   private PipeInputStream _in;
   private InputStream _subIn;
   private Pipe _pipe = new Pipe();
   private IOException _ioException;

   public AccumulatorInputStream(InputConnection connection, InputStream in, SessionStats stats, boolean startThread) {
      this._subIn = in;
      this._in = this._pipe.getInputStream();
      this._connection = connection;
      AccumulatorInputStream$AccumulatorThread thread = new AccumulatorInputStream$AccumulatorThread(this, stats);
      if (startThread) {
         thread.start();
      } else {
         thread.run();
      }
   }

   @Override
   public final int read(byte[] b, int off, int len) throws IOException {
      if (this._ioException != null) {
         throw this._ioException;
      } else {
         return this._in.read(b, off, len);
      }
   }

   @Override
   public final int read() throws IOException {
      if (this._ioException != null) {
         throw this._ioException;
      } else {
         return this._in.read();
      }
   }

   @Override
   public final long skip(long n) throws IOException {
      if (this._ioException != null) {
         throw this._ioException;
      } else {
         return this._in.skip(n);
      }
   }

   @Override
   public final int available() throws IOException {
      if (this._ioException != null) {
         throw this._ioException;
      } else {
         return this._in.available();
      }
   }

   @Override
   public final void mark(int n) {
      this._in.mark(n);
   }

   @Override
   public final void reset() throws IOException {
      if (this._ioException != null) {
         throw this._ioException;
      }

      this._in.reset();
   }

   @Override
   public final boolean markSupported() {
      return this._in.markSupported();
   }

   @Override
   public final int readByteArray(PipePtr ptr, int length) throws IOException {
      if (this._ioException != null) {
         throw this._ioException;
      } else {
         return this._in.readByteArray(ptr, length);
      }
   }

   @Override
   public final int readCompressedInt() throws IOException {
      if (this._ioException != null) {
         throw this._ioException;
      } else {
         return this._in.readCompressedInt();
      }
   }

   @Override
   public final String readInlineString(String encoding) {
      return this._in.readInlineString(encoding);
   }

   @Override
   public final void skipInlineString() throws IOException {
      if (this._ioException != null) {
         throw this._ioException;
      }

      this._in.skipInlineString();
   }

   @Override
   public final Pipe getPipe() {
      return this._in.getPipe();
   }

   @Override
   public final PipeContext getPosition() {
      return this._in.getPosition();
   }

   @Override
   public final void close() {
      this._in.close();
      this._subIn.close();
      if (this._connection != null) {
         this._connection.close();
      }
   }
}
