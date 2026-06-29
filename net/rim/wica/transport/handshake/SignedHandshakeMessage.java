package net.rim.wica.transport.handshake;

import net.rim.wica.transport.security.Key;

public interface SignedHandshakeMessage {
   void sign(Key var1);

   boolean verifySignature(Key var1);
}
