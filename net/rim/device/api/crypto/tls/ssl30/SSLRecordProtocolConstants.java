package net.rim.device.api.crypto.tls.ssl30;

import net.rim.device.api.crypto.tls.RecordProtocolConstants;

public interface SSLRecordProtocolConstants extends RecordProtocolConstants {
   byte NULL = 0;
   byte RC4 = 1;
   byte RC2 = 2;
   byte DES = 3;
   byte TDES = 4;
   byte DES40 = 5;
   byte MD5 = 1;
   byte SHA = 2;
   byte RSA = 1;
   byte RSA_EXPORT = 2;
   byte DH_DSS = 3;
   byte DH_RSA = 4;
   byte DH_anon = 5;
   byte DHE_DSS = 6;
   byte DHE_DSS_EXPORT = 7;
   byte DHE_RSA = 8;
   byte DHE_RSA_EXPORT = 9;
   byte FORTEZZA = 10;
   byte DH_anon_EXPORT = 11;
   byte ECDH_ECDSA = 12;
   byte ECDH_ECDSA_EXPORT = 13;
   byte ECDH_RSA = 14;
   byte ECDH_RSA_EXPORT = 15;
   byte ECDH_anon = 16;
   byte ECDH_anon_EXPORT = 17;
   byte CHANGE_CIPHER_SPEC = 20;
   byte ALERT = 21;
   byte HANDSHAKE = 22;
   byte APPLICATION_DATA = 23;
   byte SSLv2 = 24;
   byte RSA_SIGN = 1;
   byte DSS_SIGN = 2;
   byte RSA_FIXED_DH = 3;
   byte DSS_FIXED_DH = 4;
   int NOT_NEGOTIATED = 0;
   int NEGOTIATED = 1;
   int HELLO_WAIT = 2;
}
