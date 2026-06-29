package net.rim.vm;

import java.io.InputStream;

class DebugSupport$DebugSupportInputStream extends InputStream {
   private int _fileno;
   private byte[] _byteBuf = new byte[1];

   DebugSupport$DebugSupportInputStream(int fileno) {
      this._fileno = fileno;
   }

   @Override
   public int read() {
      return DebugSupport.read(this._fileno, this._byteBuf, 0, 1) != 1 ? -1 : this._byteBuf[0] & 0xFF;
   }

   @Override
   public int read(byte[] b, int off, int len) {
      return DebugSupport.read(this._fileno, b, off, len);
   }

   @Override
   public void close() {
      DebugSupport.closeFile(this._fileno);
   }
}
