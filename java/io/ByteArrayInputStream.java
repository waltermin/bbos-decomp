package java.io;

public class ByteArrayInputStream extends InputStream {
   protected byte[] buf;
   protected int pos;
   protected int mark = 0;
   protected int count;

   public ByteArrayInputStream(byte[] buf) {
      this.buf = buf;
      this.pos = 0;
      this.count = buf.length;
   }

   public ByteArrayInputStream(byte[] buf, int offset, int length) {
      this.buf = buf;
      this.pos = offset;
      this.count = Math.min(offset + length, buf.length);
      this.mark = offset;
   }

   @Override
   public synchronized int read() {
      return this.pos < this.count ? this.buf[this.pos++] & 0xFF : -1;
   }

   @Override
   public synchronized int read(byte[] b, int off, int len) {
      if (b == null) {
         throw new NullPointerException();
      }

      if (off < 0 || off > b.length || len < 0 || off + len > b.length || off + len < 0) {
         throw new IndexOutOfBoundsException();
      }

      if (this.pos >= this.count) {
         return -1;
      }

      if (this.pos + len > this.count) {
         len = this.count - this.pos;
      }

      if (len <= 0) {
         return 0;
      }

      System.arraycopy(this.buf, this.pos, b, off, len);
      this.pos += len;
      return len;
   }

   @Override
   public synchronized long skip(long n) {
      if (this.pos + n > this.count) {
         n = this.count - this.pos;
      }

      if (n < 0) {
         return 0;
      }

      this.pos = (int)(this.pos + n);
      return n;
   }

   @Override
   public synchronized int available() {
      return this.count - this.pos;
   }

   @Override
   public boolean markSupported() {
      return true;
   }

   @Override
   public void mark(int readAheadLimit) {
      this.mark = this.pos;
   }

   @Override
   public synchronized void reset() {
      this.pos = this.mark;
   }

   @Override
   public synchronized void close() {
   }
}
