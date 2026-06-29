package java.io;

public class OutputStream {
   public void write(int _1) {
      throw null;
   }

   public void write(byte[] b) {
      this.write(b, 0, b.length);
   }

   public void write(byte[] b, int off, int len) {
      if (b == null) {
         throw new NullPointerException();
      }

      if (off < 0 || off > b.length || len < 0 || off + len > b.length || off + len < 0) {
         throw new IndexOutOfBoundsException();
      }

      if (len != 0) {
         for (int i = 0; i < len; i++) {
            this.write(b[off + i]);
         }
      }
   }

   public void flush() {
   }

   public void close() {
   }
}
