package net.rim.wica.transport.handshake;

public interface HandshakeMessageHandler {
   void handleMessage(ClientHello var1);

   void handleMessage(ServerHello var1);

   void handleMessage(ClientHelloV1 var1);

   void handleMessage(ServerHelloV1 var1);

   void handleMessage(RegisterV1 var1);

   void handleMessage(UnregisterV1 var1);

   void handleMessage(OkV1 var1);

   void handleMessage(FailureV1 var1);
}
