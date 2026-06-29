package net.rim.device.api.crypto.pgp;

public interface PGPSignatureTypes {
   byte BINARY_DOCUMENT = 0;
   byte CANONICAL_TEXT_DOCUMENT = 1;
   byte STANDALONE = 2;
   byte GENERIC_CERTIFICATION = 16;
   byte PERSONA_CERTIFICATION = 17;
   byte CASUAL_CERTIFICATION = 18;
   byte POSITIVE_CERTIFICATION = 19;
   byte SUBKEY_BINDING = 24;
   byte DIRECTLY_ON_KEY = 31;
   byte KEY_REVOCATION = 32;
   byte SUBKEY_REVOCATION = 40;
   byte CERTIFICATION_REVOCATION = 48;
   byte TIMESTAMP = 64;
}
