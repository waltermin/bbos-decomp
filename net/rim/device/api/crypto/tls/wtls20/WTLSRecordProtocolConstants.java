package net.rim.device.api.crypto.tls.wtls20;

import net.rim.device.api.crypto.tls.RecordProtocolConstants;

public interface WTLSRecordProtocolConstants extends RecordProtocolConstants {
   byte CHANGE_CIPHER_SPEC;
   byte ALERT;
   byte HANDSHAKE;
   byte APPLICATION_DATA;
   byte DH_anon;
   byte DH_anon_512;
   byte DH_anon_768;
   byte RSA_anon;
   byte RSA_anon_512;
   byte RSA_anon_768;
   byte RSA;
   byte RSA_512;
   byte RSA_768;
   byte ECDH_anon;
   byte ECDH_ECDSA;
   byte WTLS_1;
   byte WTLS_2;
   byte WTLS_EC_3;
   byte WTLS_EC_5;
   byte WTLS_EC_7;
   byte NULL_PARAM;
   byte WTLSCert;
   byte X509Cert;
   byte X968Cert;
   byte CertURL;
}
