package net.rim.wica.transport.handshake;

public interface FailureV1 extends HandshakeMessage, SerializableHandshakeMessage, SignedHandshakeMessage, ReplayProtectedHandshakeMessage {
   String getReason();

   void setReason(String var1);
}
