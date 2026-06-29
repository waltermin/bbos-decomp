package net.rim.device.cldc.io.devicehttps;

import javax.microedition.io.SecureConnection;
import javax.microedition.io.ServerSocketConnection;
import javax.microedition.io.StreamConnection;
import net.rim.device.cldc.io.http.HttpServerProtocolBase;
import net.rim.device.cldc.io.http.HttpServerSocketConnectionBase;
import net.rim.device.cldc.io.ssl.ConnectionFactory;
import net.rim.device.cldc.io.utility.URL;

public final class HttpsServerSocket extends HttpServerSocketConnectionBase {
   private SecureConnection _activeSSLConnection;
   private ConnectionFactory _connectionFactory;
   private Object _certificate;
   private Object _privateKey;

   public HttpsServerSocket(URL url, ServerSocketConnection serverConnection, ConnectionFactory factory) {
      super(url, serverConnection);
      this._connectionFactory = factory;
   }

   @Override
   public final StreamConnection acceptAndOpen() {
      this._activeSSLConnection = this._connectionFactory
         .createServerInstance(super._serverConnection.acceptAndOpen(), super._url.toString(), this._certificate, this._privateKey);
      return new HttpServerProtocolBase(
         super._url, null, this._activeSSLConnection, this._activeSSLConnection.openDataInputStream(), this._activeSSLConnection.openDataOutputStream()
      );
   }

   public final void setCertificateAndKey(Object x509Cert, Object privateKey) {
      this._privateKey = privateKey;
      this._certificate = x509Cert;
   }
}
