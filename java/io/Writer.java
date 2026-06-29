package java.io;

public class Writer {
   private char[] writeBuffer;
   protected Object lock;
   private static final int writeBufferSize;

   protected Writer() {
      this.lock = this;
   }

   protected Writer(Object lock) {
      if (lock == null) {
         throw new NullPointerException();
      }

      this.lock = lock;
   }

   public void write(int c) {
      synchronized (this.lock) {
         if (this.writeBuffer == null) {
            this.writeBuffer = new char[1024];
         }

         this.writeBuffer[0] = (char)c;
         this.write(this.writeBuffer, 0, 1);
      }
   }

   public void write(char[] cbuf) {
      this.write(cbuf, 0, cbuf.length);
   }

   public void write(char[] _1, int _2, int _3) {
      throw null;
   }

   public void write(String str) {
      this.write(str, 0, str.length());
   }

   public void write(String str, int off, int len) {
      synchronized (this.lock) {
         char[] cbuf;
         if (len <= 1024) {
            if (this.writeBuffer == null) {
               this.writeBuffer = new char[1024];
            }

            cbuf = this.writeBuffer;
         } else {
            cbuf = new char[len];
         }

         str.getChars(off, off + len, cbuf, 0);
         this.write(cbuf, 0, len);
      }
   }

   public void flush() {
      throw null;
   }

   public void close() {
      throw null;
   }
}
