package net.rim.vm;

import java.io.OutputStream;

class DebugSupport$DebugSupportOutputStream extends OutputStream {
   private int _fileno;
   private byte[] _byteBuf = new byte[1];

   DebugSupport$DebugSupportOutputStream(int fileno) {
      this._fileno = fileno;
   }

   @Override
   public void write(int b) {
      this._byteBuf[0] = (byte)b;
      DebugSupport.write(this._fileno, this._byteBuf, 0, 1);
   }

   @Override
   public void write(byte[] b, int off, int len) {
      DebugSupport.write(this._fileno, b, off, len);
   }

   @Override
   public void close() {
      DebugSupport.closeFile(this._fileno);
   }
}
