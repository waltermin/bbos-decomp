package net.rim.wica.transport.security;

public interface SecureMessageV1 {
   void secure(int var1);

   void verifySecurity();

   byte[] getPayload();
}
