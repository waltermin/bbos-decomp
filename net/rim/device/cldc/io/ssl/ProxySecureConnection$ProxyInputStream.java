package net.rim.device.cldc.io.ssl;

import java.io.InputStream;
import net.rim.device.cldc.io.ippp.SocketBaseIOException;

final class ProxySecureConnection$ProxyInputStream extends InputStream {
   private final ProxySecureConnection this$0;

   public ProxySecureConnection$ProxyInputStream(ProxySecureConnection _1, InputStream input) {
      this.this$0 = _1;
      _1._input = input;
   }

   @Override
   public final void close() {
      this.this$0._input.close();
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
         ProxySecureConnection.handleException(e, this.this$0._name);
         this.this$0.replay();
         return this.this$0._input.read();
      }
   }
}
