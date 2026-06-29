package net.rim.device.cldc.io.btgoep;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

class OBEXByteArrayOutputStream extends ByteArrayOutputStream {
   public synchronized void writeTo(OutputStream out) {
      out.write(super.buf, 0, super.count);
   }
}
