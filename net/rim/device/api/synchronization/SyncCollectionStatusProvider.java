package net.rim.device.api.synchronization;

public interface SyncCollectionStatusProvider {
   int SCSP_BACKUP_NO_REMOVE = 1;
   int SCSP_NULL = 0;

   boolean isWritableForSerialSync();

   boolean isReadableForSerialSync();

   boolean isWritableForOTASL();

   int getOTASLControlMask();
}
