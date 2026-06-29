package net.rim.device.api.crypto.tls.wtls20;

public interface WTLSDataTransport {
   byte CLOSE_NOTIFY;
   byte SESSION_CLOSE_NOTIFY;
   byte NO_CONNECTION;
   byte UNEXPECTED_MESSAGE;
   byte TIME_REQUIRED;
   byte BAD_RECORD_MAC;
   byte DECRYPTION_FAILED;
   byte RECORD_OVERFLOW;
   byte DECOMPRESSION_FAILURE;
   byte HANDSHAKE_FAILURE;
   byte NO_CERTIFICATE;
   byte BAD_CERTIFICATE;
   byte UNSUPPORTED_CERTIFICATE;
   byte CERTIFICATE_REVOKED;
   byte CERTIFICATE_EXPIRED;
   byte CERTIFICATE_UNKNOWN;
   byte ILLEGAL_PARAMETER;
   byte UNKNOWN_CA;
   byte ACCESS_DENIED;
   byte DECODE_ERROR;
   byte DECRYPT_ERROR;
   byte UNKNOWN_KEY_ID;
   byte DISABLED_KEY_ID;
   byte KEY_EXCHANGE_DISABLED;
   byte SESSION_NOT_READY;
   byte UNKNOWN_PARAMETER_INDEX;
   byte DUPLICATE_FINISHED_RECEIVED;
   byte EXPORT_RESTRICTION;
   byte PROTOCOL_VERSION;
   byte INSUFFICIENT_SECURITY;
   byte INTERNAL_ERROR;
   byte USER_CANCELLED;
   byte NO_RENEGOTIATION;
   byte RADIO_KEY_READ_FAILED;
   byte GENERAL_EXCEPTION;
   byte IO_EXCEPTION;
   long WTLS_STATUS_UPDATE;
   int CLIENT_HELLO;
   int SERVER_HELLO;
   int CERT_VERIFY;
   int CLIENT_KEY_EXCH;
   int CLIENT_VERIFY;
   int CLIENT_DONE;
   int SERVER_DONE;
   int FINISHED;

   void write(byte[] var1, int var2, int var3);

   byte[] read(int var1);

   void status(int var1);
}
