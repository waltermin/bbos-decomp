package net.rim.device.api.crypto.pgp;

public interface PGPConstants {
   int NO_COMPRESSION = 0;
   int ZIP = 1;
   int ZLIB = 2;
   int TEXT = 0;
   int BINARY = 1;
   int TRIPLE_DES = 2;
   int CAST5 = 3;
   int AES_128 = 7;
   int AES_192 = 8;
   int AES_256 = 9;
   int MD5 = 1;
   int SHA1 = 2;
   int RIPEMD160 = 3;
   int MD2 = 5;
   int SHA256 = 8;
   int SHA384 = 9;
   int SHA512 = 10;
   int RSA = 1;
   int RSA_ENCRYPT = 2;
   int RSA_SIGN = 3;
   int ELGAMAL_ENCRYPT = 16;
   int DSA = 17;
   int ECAES = 18;
   int ECDSA = 19;
   int ELGAMAL = 20;
   int DH = 21;
   int MAX_PASSPHRASE_LENGTH = 255;
}
