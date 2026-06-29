package net.rim.device.api.crypto.pgp;

import java.io.OutputStream;

class PGPLengthCounterOutputStream extends OutputStream {
   private OutputStream _output;
   private int _amountWritten;

   public PGPLengthCounterOutputStream(OutputStream output) {
      if (output == null) {
         throw new Object();
      }

      this._output = output;
      this._amountWritten = 0;
   }

   @Override
   public void write(int b) {
      this._amountWritten++;
      this._output.write(b);
   }

   @Override
   public void write(byte[] b) {
      if (b == null) {
         throw new Object();
      }

      this.write(b, 0, b.length);
   }

   @Override
   public void write(byte[] b, int off, int len) {
      if (b != null && off >= 0 && len >= 0 && b.length - len >= off) {
         this._amountWritten += len;
         this._output.write(b, off, len);
      } else {
         throw new Object();
      }
   }

   public int getAmountWritten() {
      return this._amountWritten;
   }
}
