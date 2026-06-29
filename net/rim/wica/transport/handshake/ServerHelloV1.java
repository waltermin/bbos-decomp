package net.rim.wica.transport.handshake;

public interface ServerHelloV1 extends ServerHello, HandshakeMessage {
   long getServerId();

   void setServerId(long var1);

   EncodedCertificate[] getServerCertificateChain();

   void setServerCertificateChain(EncodedCertificate[] var1);
}
