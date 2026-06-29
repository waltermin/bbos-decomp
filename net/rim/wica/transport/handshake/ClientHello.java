package net.rim.wica.transport.handshake;

public interface ClientHello extends HandshakeMessage, SerializableHandshakeMessage {
   int getDeviceVersion();

   void setDeviceVersion(int var1);
}
