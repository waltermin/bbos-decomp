package net.rim.device.apps.internal.bluetooth;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

class StupidCLDCByteArrayOutputStream extends ByteArrayOutputStream {
   public synchronized void writeTo(OutputStream out) {
      out.write(super.buf, 0, super.count);
   }
}
