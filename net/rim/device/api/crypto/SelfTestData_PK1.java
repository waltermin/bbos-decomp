package net.rim.device.api.crypto;

interface SelfTestData_PK1 {
   int RSA_MODULUS_BIT_LENGTH = 1024;
   byte[] RSA_E;
   byte[] RSA_D;
   byte[] RSA_N;
   byte[] RSA_P;
   byte[] RSA_Q;
   byte[] RSA_DMODPM1;
   byte[] RSA_DMODQM1;
   byte[] RSA_QINVMODP;
   byte[] PLAIN_TEXT_RSA;
   byte[] CIPHER_TEXT_RSA;
   byte[] CIPHER_TEXT_PKCS1_RSA;
   byte[] PLAIN_TEXT_PKCS1_RSA;
   byte[] SIGNATURE_PKCS1_RSA;
   byte[] ENCRYPTION_PLAIN_TEXT;
}
