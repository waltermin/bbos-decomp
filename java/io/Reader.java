package java.io;

public class Reader {
   protected Object lock;
   private char[] skipBuffer;
   private static final int maxSkipBufferSize = 8192;

   protected Reader() {
      this.lock = this;
   }

   protected Reader(Object lock) {
      if (lock == null) {
         throw new NullPointerException();
      }

      this.lock = lock;
   }

   public int read() {
      char[] cb = new char[1];
      return this.read(cb, 0, 1) == -1 ? -1 : cb[0];
   }

   public int read(char[] cbuf) {
      return this.read(cbuf, 0, cbuf.length);
   }

   public int read(char[] _1, int _2, int _3) {
      throw null;
   }

   public long skip(long n) {
      if (n < 0) {
         throw new IllegalArgumentException("skip value is negative");
      }

      int nn = (int)Math.min(n, 8192);
      synchronized (this.lock) {
         if (this.skipBuffer == null || this.skipBuffer.length < nn) {
            this.skipBuffer = new char[nn];
         }

         long r = n;

         while (r > 0) {
            int nc = this.read(this.skipBuffer, 0, (int)Math.min(r, nn));
            if (nc == -1) {
               break;
            }

            r -= nc;
         }

         return n - r;
      }
   }

   public boolean ready() {
      return false;
   }

   public boolean markSupported() {
      return false;
   }

   public void mark(int readAheadLimit) {
      throw new IOException("mark() not supported");
   }

   public void reset() {
      throw new IOException("reset() not supported");
   }

   public void close() {
      throw null;
   }
}
