package net.rim.wica.transport.security;

public interface KeyProvider {
   Key getPrivateKey();

   Key getRemotePublicKey(long var1);

   Key getPrimaryRegistrationKey(long var1);

   Key getSecondaryRegistrationKey(long var1);

   Key getResetKey(long var1);
}
