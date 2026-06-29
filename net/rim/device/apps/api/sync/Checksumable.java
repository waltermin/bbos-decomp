package net.rim.device.apps.api.sync;

public interface Checksumable {
   long CHECKSUM_PRIVATE_FLAGS;
   int KEY_CHECKSUM;
   int RECORD_CHECKSUM;

   long getChecksum(Object var1);
}
