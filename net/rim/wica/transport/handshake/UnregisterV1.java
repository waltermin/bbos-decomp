package net.rim.wica.transport.handshake;

public interface UnregisterV1 extends HandshakeMessage, SerializableHandshakeMessage, SignedHandshakeMessage, ReplayProtectedHandshakeMessage {
   String getPIN();

   void setPIN(String var1);
}
