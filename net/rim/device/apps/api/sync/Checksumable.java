package net.rim.device.apps.api.sync;

public interface Checksumable {
   long CHECKSUM_PRIVATE_FLAGS = -2183283945546558784L;
   int KEY_CHECKSUM = 1;
   int RECORD_CHECKSUM = 2;

   long getChecksum(Object var1);
}
