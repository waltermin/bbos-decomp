package net.rim.wica.transport.handshake;

public interface ServerHello extends HandshakeMessage, SerializableHandshakeMessage {
   int getServerVersion();

   void setServerVersion(int var1);
}
