package net.rim.device.cldc.io.proxyhttp;

import javax.microedition.io.ServerSocketConnection;
import javax.microedition.io.StreamConnection;
import net.rim.device.cldc.io.http.HttpServerSocketConnectionBase;
import net.rim.device.cldc.io.utility.URL;

public final class HttpServerSocketConnection extends HttpServerSocketConnectionBase {
   public HttpServerSocketConnection(URL url, ServerSocketConnection serverConnection) {
      super(url, serverConnection);
   }

   @Override
   public final StreamConnection acceptAndOpen() {
      return new ServerProtocol(super._url, super._serverConnection.acceptAndOpen());
   }
}
