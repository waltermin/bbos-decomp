package net.rim.device.cldc.io.https;

import java.io.InputStream;
import net.rim.device.cldc.io.ippp.SocketBaseIOException;

public final class ProxyHttpsConnection$ProxyInputStream extends InputStream {
   private final ProxyHttpsConnection this$0;

   public ProxyHttpsConnection$ProxyInputStream(ProxyHttpsConnection _1, InputStream input) {
      this.this$0 = _1;
      _1._input = input;
   }

   @Override
   public final void close() {
      this.this$0._input.close();
      this.this$0._subConnection.close();
   }

   @Override
   public final boolean markSupported() {
      return false;
   }

   @Override
   public final int available() {
      return this.this$0._input.available();
   }

   @Override
   public final int read() {
      try {
         return this.this$0._input.read();
      } catch (SocketBaseIOException e) {
         this.this$0.handleException(e);
         this.this$0.replay();
         return this.this$0._input.read();
      }
   }
}
