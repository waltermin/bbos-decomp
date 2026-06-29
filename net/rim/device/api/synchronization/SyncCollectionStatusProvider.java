package net.rim.device.api.synchronization;

public interface SyncCollectionStatusProvider {
   int SCSP_BACKUP_NO_REMOVE;
   int SCSP_NULL;

   boolean isWritableForSerialSync();

   boolean isReadableForSerialSync();

   boolean isWritableForOTASL();

   int getOTASLControlMask();
}
