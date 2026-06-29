package net.rim.device.cldc.io.ssl;

import java.io.OutputStream;
import net.rim.device.api.util.DataBuffer;

final class ProxySecureConnection$ProxyOutputStream extends OutputStream {
   private final ProxySecureConnection this$0;

   public ProxySecureConnection$ProxyOutputStream(ProxySecureConnection _1, OutputStream output) {
      this.this$0 = _1;
      _1._output = output;
      _1._buffer = (DataBuffer)(new Object());
   }

   @Override
   public final void close() {
      this.this$0._output.close();
   }

   public final boolean markSupported() {
      return false;
   }

   @Override
   public final void write(int b) {
      this.this$0._buffer.writeByte(b);
      this.this$0._output.write(b);
   }

   @Override
   public final void flush() {
      this.this$0._output.flush();
   }
}
