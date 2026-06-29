package net.rim.device.api.crypto.pgp;

import java.io.OutputStream;

final class PGPDashEscapedOutputStream extends OutputStream {
   private int _currentState;
   private OutputStream _output;
   private byte[] _writeBuffer;
   private static final int STATE_LINE_START = 0;
   private static final int STATE_LINE_MIDDLE = 1;
   private static final int STATE_CR_READ = 2;
   private static final byte CR = 13;
   private static final byte LF = 10;
   private static final byte DASH = 45;
   private static final byte SPACE = 32;

   public PGPDashEscapedOutputStream(OutputStream output) {
      if (output == null) {
         throw new Object();
      }

      this._output = output;
      this._currentState = 0;
   }

   @Override
   public final void write(int b) {
      if (this._writeBuffer == null) {
         this._writeBuffer = new byte[1];
      }

      this._writeBuffer[0] = (byte)b;
      this.write(this._writeBuffer, 0, 1);
   }

   @Override
   public final void write(byte[] b, int off, int len) {
      if (b != null && off >= 0 && len >= 0 && b.length - len >= off) {
         byte[] trueOutput = new byte[2 * len];
         int trueOutputOffset = 0;

         while (len > 0) {
            byte temp;
            len--;
            temp = b[off++];
            label32:
            switch (this._currentState) {
               case -1:
                  break;
               case 0:
               default:
                  switch (temp) {
                     case 10:
                        break label32;
                     case 13:
                        this._currentState = 2;
                        break label32;
                     case 45:
                        trueOutput[trueOutputOffset++] = 45;
                        trueOutput[trueOutputOffset++] = 32;
                     default:
                        this._currentState = 1;
                        break label32;
                  }
               case 1:
                  switch (temp) {
                     case 10:
                        this._currentState = 0;
                        break label32;
                     case 13:
                        this._currentState = 2;
                     default:
                        break label32;
                  }
               case 2:
                  switch (temp) {
                     case 10:
                        this._currentState = 0;
                     case 13:
                        break;
                     default:
                        this._currentState = 1;
                  }
            }

            trueOutput[trueOutputOffset++] = temp;
         }

         this._output.write(trueOutput, 0, trueOutputOffset);
      } else {
         throw new Object();
      }
   }

   @Override
   public final void flush() {
      this._output.flush();
   }

   @Override
   public final void close() {
      this.flush();
      this._output.close();
   }
}
