package net.rim.wica.transport.handshake;

public interface ReplayProtectedHandshakeMessage {
   byte[] getNonce();

   void setNonce(byte[] var1);
}
