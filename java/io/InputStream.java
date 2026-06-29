package java.io;

public class InputStream {
   public int read() {
      throw null;
   }

   public int read(byte[] b) {
      return this.read(b, 0, b.length);
   }

   public int read(byte[] b, int off, int len) {
      if (b == null) {
         throw new NullPointerException();
      }

      if (off < 0 || off > b.length || len < 0 || off + len > b.length || off + len < 0) {
         throw new IndexOutOfBoundsException();
      }

      if (len == 0) {
         return 0;
      }

      int c = this.read();
      if (c == -1) {
         return -1;
      }

      b[off] = (byte)c;
      int i = 1;

      try {
         while (i < len) {
            c = this.read();
            if (c == -1) {
               break;
            }

            b[off + i] = (byte)c;
            i++;
         }
      } catch (IOException var7) {
      }

      return i;
   }

   public long skip(long n) {
      long m = n;

      while (m > 0 && this.read() >= 0) {
         m -= 1;
      }

      return n - m;
   }

   public int available() {
      return 0;
   }

   public void close() {
   }

   public synchronized void mark(int readlimit) {
   }

   public synchronized void reset() {
      throw new IOException("mark/reset not supported");
   }

   public boolean markSupported() {
      return false;
   }
}
