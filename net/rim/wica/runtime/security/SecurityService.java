package net.rim.wica.runtime.security;

import net.rim.wica.transport.security.KeyProvider;
import net.rim.wica.transport.security.SecurityProvider;
import net.rim.wica.transport.security.SequenceProvider;

public interface SecurityService {
   SecurityProvider getSecurityProvider();

   KeyProvider getKeyProvider();

   SequenceProvider getSequenceProvider();

   void register(HandshakeInfo var1);

   void unregister(HandshakeInfo var1);
}
