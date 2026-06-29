package net.rim.device.api.crypto.tls.wtls20;

public interface WTLSDataTransport {
   byte CLOSE_NOTIFY = 0;
   byte SESSION_CLOSE_NOTIFY = 1;
   byte NO_CONNECTION = 5;
   byte UNEXPECTED_MESSAGE = 10;
   byte TIME_REQUIRED = 11;
   byte BAD_RECORD_MAC = 20;
   byte DECRYPTION_FAILED = 21;
   byte RECORD_OVERFLOW = 22;
   byte DECOMPRESSION_FAILURE = 30;
   byte HANDSHAKE_FAILURE = 40;
   byte NO_CERTIFICATE = 41;
   byte BAD_CERTIFICATE = 42;
   byte UNSUPPORTED_CERTIFICATE = 43;
   byte CERTIFICATE_REVOKED = 44;
   byte CERTIFICATE_EXPIRED = 45;
   byte CERTIFICATE_UNKNOWN = 46;
   byte ILLEGAL_PARAMETER = 47;
   byte UNKNOWN_CA = 48;
   byte ACCESS_DENIED = 49;
   byte DECODE_ERROR = 50;
   byte DECRYPT_ERROR = 51;
   byte UNKNOWN_KEY_ID = 52;
   byte DISABLED_KEY_ID = 53;
   byte KEY_EXCHANGE_DISABLED = 54;
   byte SESSION_NOT_READY = 55;
   byte UNKNOWN_PARAMETER_INDEX = 56;
   byte DUPLICATE_FINISHED_RECEIVED = 57;
   byte EXPORT_RESTRICTION = 60;
   byte PROTOCOL_VERSION = 70;
   byte INSUFFICIENT_SECURITY = 71;
   byte INTERNAL_ERROR = 80;
   byte USER_CANCELLED = 90;
   byte NO_RENEGOTIATION = 100;
   byte RADIO_KEY_READ_FAILED = -3;
   byte GENERAL_EXCEPTION = -2;
   byte IO_EXCEPTION = -1;
   long WTLS_STATUS_UPDATE = -1602902615298266273L;
   int CLIENT_HELLO = 1;
   int SERVER_HELLO = 2;
   int CERT_VERIFY = 3;
   int CLIENT_KEY_EXCH = 4;
   int CLIENT_VERIFY = 5;
   int CLIENT_DONE = 6;
   int SERVER_DONE = 7;
   int FINISHED = 8;

   void write(byte[] var1, int var2, int var3);

   byte[] read(int var1);

   void status(int var1);
}
