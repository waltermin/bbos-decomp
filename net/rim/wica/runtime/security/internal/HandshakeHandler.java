package net.rim.wica.runtime.security.internal;

import net.rim.wica.runtime.security.HandshakeException;
import net.rim.wica.runtime.security.HandshakeInfo;
import net.rim.wica.transport.security.Key;

interface HandshakeHandler {
   int getMinSecurityVersion();

   int getMaxSecurityVersion();

   void registrationCompleted(HandshakeInfo var1, Key[] var2);

   void registrationFailed(HandshakeException var1);

   void unregistrationCompleted(HandshakeInfo var1);

   void unregistrationFailed(HandshakeException var1);
}
