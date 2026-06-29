package net.rim.device.api.crypto.tls.ssl30;

import net.rim.device.api.crypto.tls.RecordProtocolConstants;

public interface SSLRecordProtocolConstants extends RecordProtocolConstants {
   byte NULL;
   byte RC4;
   byte RC2;
   byte DES;
   byte TDES;
   byte DES40;
   byte MD5;
   byte SHA;
   byte RSA;
   byte RSA_EXPORT;
   byte DH_DSS;
   byte DH_RSA;
   byte DH_anon;
   byte DHE_DSS;
   byte DHE_DSS_EXPORT;
   byte DHE_RSA;
   byte DHE_RSA_EXPORT;
   byte FORTEZZA;
   byte DH_anon_EXPORT;
   byte ECDH_ECDSA;
   byte ECDH_ECDSA_EXPORT;
   byte ECDH_RSA;
   byte ECDH_RSA_EXPORT;
   byte ECDH_anon;
   byte ECDH_anon_EXPORT;
   byte CHANGE_CIPHER_SPEC;
   byte ALERT;
   byte HANDSHAKE;
   byte APPLICATION_DATA;
   byte SSLv2;
   byte RSA_SIGN;
   byte DSS_SIGN;
   byte RSA_FIXED_DH;
   byte DSS_FIXED_DH;
   int NOT_NEGOTIATED;
   int NEGOTIATED;
   int HELLO_WAIT;
}
