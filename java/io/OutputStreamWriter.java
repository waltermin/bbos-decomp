package java.io;

import com.sun.cldc.i18n.Helper;

public class OutputStreamWriter extends Writer {
   private Writer out;

   public OutputStreamWriter(OutputStream os) {
      this.out = Helper.getStreamWriter(os);
   }

   public OutputStreamWriter(OutputStream os, String enc) {
      this.out = Helper.getStreamWriter(os, enc);
   }

   private void ensureOpen() throws IOException {
      if (this.out == null) {
         throw new IOException("Stream closed");
      }
   }

   @Override
   public void write(int c) {
      this.ensureOpen();
      this.out.write(c);
   }

   @Override
   public void write(char[] cbuf, int off, int len) {
      this.ensureOpen();
      if (off < 0 || off > cbuf.length || len < 0 || off + len > cbuf.length || off + len < 0) {
         throw new IndexOutOfBoundsException();
      }

      if (len != 0) {
         this.out.write(cbuf, off, len);
      }
   }

   @Override
   public void write(String str, int off, int len) {
      this.ensureOpen();
      if (off < 0 || off > str.length() || len < 0 || off + len > str.length() || off + len < 0) {
         throw new IndexOutOfBoundsException();
      }

      if (len != 0) {
         this.out.write(str, off, len);
      }
   }

   @Override
   public void flush() {
      this.ensureOpen();
      this.out.flush();
   }

   @Override
   public void close() {
      if (this.out != null) {
         this.out.close();
         this.out = null;
      }
   }
}
