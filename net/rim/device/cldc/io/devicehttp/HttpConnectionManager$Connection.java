package net.rim.device.cldc.io.devicehttp;

import java.io.InputStream;
import java.io.OutputStream;
import javax.microedition.io.SocketConnection;

class HttpConnectionManager$Connection {
   public SocketConnection _socketConnection;
   public InputStream _inputStream;
   public OutputStream _outputStream;
   public long _allocationTime = System.currentTimeMillis();

   public HttpConnectionManager$Connection(SocketConnection socket, InputStream is, OutputStream os) {
      this._socketConnection = socket;
      this._inputStream = is;
      this._outputStream = os;
   }
}
