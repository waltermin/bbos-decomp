package net.rim.wica.transport.handshake;

public interface OkV1 extends HandshakeMessage, SerializableHandshakeMessage, SignedHandshakeMessage, ReplayProtectedHandshakeMessage {
   long getDeviceId();

   void setDeviceId(long var1);

   long getServerId();

   void setServerId(long var1);

   boolean firstHandshake();

   void setFirstHandshake(boolean var1);

   byte[] getServerVersions();

   void setServerVersions(byte[] var1);
}
