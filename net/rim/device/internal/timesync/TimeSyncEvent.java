package net.rim.device.internal.timesync;

public interface TimeSyncEvent {
   long GUID;
   int INIT_SERVICE;
   int SYNCHRONIZE_REQUESTED;
   int SYNCHRONIZE_SEND;
   int SYNCHRONIZE_RECEIVE;
   int SYNC_MISMATCHED_ID;
   int SYNC_BAD_FORMAT;
   int SYNC_BAD_PARMS;
   int SYNC_EXPIRED;
   int NETWORK_TIME_UNSUPPORTED;
   int NETWORK_TIME_INVALID;
}
