package net.rim.device.api.crypto;

interface SelfTestData_PK2 {
   byte[] SIGNATURE_PSS_RSA;
   byte[] PSS_SALT;
   byte[] KEA_P;
   byte[] KEA_Q;
   byte[] KEA_G;
   byte[] KEA_STATIC_PRIVATE_KEY_A;
   byte[] KEA_EPHEMERAL_PRIVATE_KEY_A;
   byte[] KEA_STATIC_PRIVATE_KEY_B;
   byte[] KEA_EPHEMERAL_PRIVATE_KEY_B;
   byte[] KEA_SHARED_SECRET_2_WAY;
   byte[] ELGAMAL_LOCAL_PUBLIC_KEY;
   byte[] ELGAMAL_LOCAL_PRIVATE_KEY;
   byte[] ELGAMAL_CIPHERTEXT;
   byte[] ELGAMAL_EPHEMERAL_PRIVATE_KEY;
   byte[] ELGAMAL_EPHEMERAL_PUBLIC_KEY;
}
