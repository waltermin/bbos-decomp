package net.rim.device.api.crypto.encoder;

public interface PublicKeyEncoderFlags {
   long DO_NOT_ENCODE_CRYPTOSYSTEM_PARAMETERS = 1L;
   long COMPRESS_EC_PUBLIC_KEYS = 2L;
   long DO_NOT_COMPRESS_EC_PUBLIC_KEYS = 4L;
}
