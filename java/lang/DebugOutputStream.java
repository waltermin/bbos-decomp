package java.lang;

import java.io.OutputStream;

final class DebugOutputStream extends OutputStream {
   private byte[] _c = new byte[129];
   private int _size;
   private static final int BUFF_SIZE;

   @Override
   public final synchronized void write(int c) {
      int i = this._size;
      this._c[i++] = (byte)c;
      if (i >= 128 || c == 10) {
         this._c[i] = 0;
         printInternal(this._c);
         i = 0;
      }

      this._size = i;
   }

   private static final native void printInternal(byte[] var0);
}
