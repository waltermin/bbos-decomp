package net.rim.device.cldc.io.btgoep;

import java.io.DataOutputStream;
import java.io.OutputStream;

class OBEXDataOutputStream extends DataOutputStream {
   private OutputStream _portOutput;
   private OBEXByteArrayOutputStream _buffer = (OBEXByteArrayOutputStream)this.out;

   OBEXDataOutputStream(OutputStream portOutput) {
      super(new OBEXByteArrayOutputStream());
      this._portOutput = portOutput;
   }

   public void reset() {
      this._buffer.reset();
   }

   @Override
   public void flush() {
      this._buffer.writeTo(this._portOutput);
   }
}
