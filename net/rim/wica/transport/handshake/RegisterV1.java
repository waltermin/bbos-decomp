package net.rim.wica.transport.handshake;

import net.rim.wica.transport.security.Key;

public interface RegisterV1 extends ClientHelloV1, SignedHandshakeMessage, ReplayProtectedHandshakeMessage {
   String getPIN();

   void setPIN(String var1);

   String getReVersion();

   void setReVersion(String var1);

   byte[] getDeviceVersions();

   void setDeviceVersions(byte[] var1);

   boolean resetState();

   void setResetState(boolean var1);

   byte[] getResetKey();

   void setResetKey(byte[] var1);

   byte[] getRK();

   void setRK(byte[] var1);

   void secure(Key var1);

   void unsecure(Key var1);
}
