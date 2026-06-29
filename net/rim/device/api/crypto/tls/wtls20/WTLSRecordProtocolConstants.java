package net.rim.device.api.crypto.tls.wtls20;

import net.rim.device.api.crypto.tls.RecordProtocolConstants;

public interface WTLSRecordProtocolConstants extends RecordProtocolConstants {
   byte CHANGE_CIPHER_SPEC = 1;
   byte ALERT = 2;
   byte HANDSHAKE = 3;
   byte APPLICATION_DATA = 4;
   byte DH_anon = 2;
   byte DH_anon_512 = 3;
   byte DH_anon_768 = 4;
   byte RSA_anon = 5;
   byte RSA_anon_512 = 6;
   byte RSA_anon_768 = 7;
   byte RSA = 8;
   byte RSA_512 = 9;
   byte RSA_768 = 10;
   byte ECDH_anon = 11;
   byte ECDH_ECDSA = 14;
   byte WTLS_1 = 1;
   byte WTLS_2 = 2;
   byte WTLS_EC_3 = 3;
   byte WTLS_EC_5 = 5;
   byte WTLS_EC_7 = 7;
   byte NULL_PARAM = 0;
   byte WTLSCert = 1;
   byte X509Cert = 2;
   byte X968Cert = 3;
   byte CertURL = 4;
}
