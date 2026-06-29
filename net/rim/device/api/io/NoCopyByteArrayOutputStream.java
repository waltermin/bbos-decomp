package net.rim.device.api.io;

import java.io.ByteArrayOutputStream;

public class NoCopyByteArrayOutputStream extends ByteArrayOutputStream {
   public NoCopyByteArrayOutputStream() {
   }

   public NoCopyByteArrayOutputStream(int size) {
      super(size);
   }

   public NoCopyByteArrayOutputStream(byte[] buffer, int offset) {
      super.buf = buffer;
      super.count = offset;
   }

   public byte[] getByteArray() {
      return super.buf;
   }
}
