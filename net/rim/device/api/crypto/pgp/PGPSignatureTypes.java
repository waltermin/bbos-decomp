package net.rim.device.api.crypto.pgp;

public interface PGPSignatureTypes {
   byte BINARY_DOCUMENT;
   byte CANONICAL_TEXT_DOCUMENT;
   byte STANDALONE;
   byte GENERIC_CERTIFICATION;
   byte PERSONA_CERTIFICATION;
   byte CASUAL_CERTIFICATION;
   byte POSITIVE_CERTIFICATION;
   byte SUBKEY_BINDING;
   byte DIRECTLY_ON_KEY;
   byte KEY_REVOCATION;
   byte SUBKEY_REVOCATION;
   byte CERTIFICATION_REVOCATION;
   byte TIMESTAMP;
}
