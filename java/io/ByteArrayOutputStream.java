package java.io;

import net.rim.vm.Array;

public class ByteArrayOutputStream extends OutputStream {
   protected byte[] buf;
   protected int count;
   private boolean isClosed;

   private void ensureOpen() {
      if (this.isClosed) {
         throw new RuntimeException("Stream closed");
      }
   }

   public ByteArrayOutputStream() {
      this(32);
   }

   public ByteArrayOutputStream(int size) {
      if (size < 0) {
         throw new IllegalArgumentException();
      }

      this.buf = new byte[size];
   }

   @Override
   public synchronized void write(int b) {
      this.ensureOpen();
      int newcount = this.count + 1;
      if (newcount > this.buf.length) {
         try {
            Array.resize(this.buf, Math.max(this.buf.length << 1, newcount));
         } catch (OutOfMemoryError e) {
            Array.resize(this.buf, newcount + Array.getSectionSize(this.buf));
         }
      }

      this.buf[this.count] = (byte)b;
      this.count = newcount;
   }

   @Override
   public synchronized void write(byte[] b, int off, int len) {
      this.ensureOpen();
      if (off < 0 || off > b.length || len < 0 || off + len > b.length || off + len < 0) {
         throw new IndexOutOfBoundsException();
      }

      if (len != 0) {
         int newcount = this.count + len;
         if (newcount > this.buf.length) {
            try {
               Array.resize(this.buf, Math.max(this.buf.length << 1, newcount));
            } catch (OutOfMemoryError e) {
               Array.resize(this.buf, newcount + Array.getSectionSize(this.buf));
            }
         }

         System.arraycopy(b, off, this.buf, this.count, len);
         this.count = newcount;
      }
   }

   public synchronized void reset() {
      this.ensureOpen();
      this.count = 0;
   }

   public synchronized byte[] toByteArray() {
      if (this.isClosed) {
         if (this.buf.length != this.count) {
            Array.resize(this.buf, this.count);
         }

         return this.buf;
      } else {
         byte[] newbuf = new byte[this.count];
         System.arraycopy(this.buf, 0, newbuf, 0, this.count);
         return newbuf;
      }
   }

   public int size() {
      return this.count;
   }

   @Override
   public String toString() {
      return new String(this.buf, 0, this.count);
   }

   @Override
   public synchronized void close() {
      this.isClosed = true;
   }
}
