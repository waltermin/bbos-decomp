package javax.microedition.io;

public interface SocketConnection extends StreamConnection {
   byte DELAY = 0;
   byte LINGER = 1;
   byte KEEPALIVE = 2;
   byte RCVBUF = 3;
   byte SNDBUF = 4;

   void setSocketOption(byte var1, int var2);

   int getSocketOption(byte var1);

   String getLocalAddress();

   int getLocalPort();

   String getAddress();

   int getPort();
}
