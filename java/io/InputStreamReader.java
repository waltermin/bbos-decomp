package java.io;

import com.sun.cldc.i18n.Helper;

public class InputStreamReader extends Reader {
   private Reader in;

   public InputStreamReader(InputStream is) {
      this.in = Helper.getStreamReader(is);
   }

   public InputStreamReader(InputStream is, String enc) {
      this.in = Helper.getStreamReader(is, enc);
   }

   private void ensureOpen() throws IOException {
      if (this.in == null) {
         throw new IOException("Stream closed");
      }
   }

   @Override
   public int read() {
      this.ensureOpen();
      return this.in.read();
   }

   @Override
   public int read(char[] cbuf, int off, int len) {
      this.ensureOpen();
      if (off < 0 || off > cbuf.length || len < 0 || off + len > cbuf.length || off + len < 0) {
         throw new IndexOutOfBoundsException();
      } else {
         return len == 0 ? 0 : this.in.read(cbuf, off, len);
      }
   }

   @Override
   public long skip(long n) {
      this.ensureOpen();
      return this.in.skip(n);
   }

   @Override
   public boolean ready() {
      this.ensureOpen();
      return this.in.ready();
   }

   @Override
   public boolean markSupported() {
      return this.in == null ? false : this.in.markSupported();
   }

   @Override
   public void mark(int readAheadLimit) {
      this.ensureOpen();
      this.in.mark(readAheadLimit);
   }

   @Override
   public void reset() {
      this.ensureOpen();
      this.in.reset();
   }

   @Override
   public void close() {
      if (this.in != null) {
         this.in.close();
         this.in = null;
      }
   }
}
