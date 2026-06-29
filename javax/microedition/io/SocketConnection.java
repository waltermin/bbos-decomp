package javax.microedition.io;

public interface SocketConnection extends StreamConnection {
   byte DELAY;
   byte LINGER;
   byte KEEPALIVE;
   byte RCVBUF;
   byte SNDBUF;

   void setSocketOption(byte var1, int var2);

   int getSocketOption(byte var1);

   String getLocalAddress();

   int getLocalPort();

   String getAddress();

   int getPort();
}
