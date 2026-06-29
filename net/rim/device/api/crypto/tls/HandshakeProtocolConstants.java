package net.rim.device.api.crypto.tls;

public interface HandshakeProtocolConstants {
   byte HELLO_REQUEST = 0;
   byte CLIENT_HELLO = 1;
   byte SERVER_HELLO = 2;
   byte CERTIFICATE = 11;
   byte SERVER_KEY_EXCHANGE = 12;
   byte CERTIFICATE_REQUEST = 13;
   byte SERVER_HELLO_DONE = 14;
   byte CERTIFICATE_VERIFY = 15;
   byte CLIENT_KEY_EXCHANGE = 16;
   byte FINISHED = 20;
}
