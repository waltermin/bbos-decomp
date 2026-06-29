package net.rim.device.cldc.io.https;

import java.io.OutputStream;
import net.rim.device.api.util.DataBuffer;

public final class ProxyHttpsConnection$ProxyOutputStream extends OutputStream {
   private OutputStream _output;
   private final ProxyHttpsConnection this$0;

   public ProxyHttpsConnection$ProxyOutputStream(ProxyHttpsConnection _1, OutputStream output) {
      this.this$0 = _1;
      this._output = output;
      _1._buffer = new DataBuffer();
   }

   @Override
   public final void close() {
      this._output.close();
   }

   public final boolean markSupported() {
      return false;
   }

   @Override
   public final void write(int b) {
      this.this$0._buffer.writeByte(b);
      this._output.write(b);
   }
}
