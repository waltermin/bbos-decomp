package net.rim.device.apps.api.utility.lowMemory;

import net.rim.device.apps.api.framework.model.KeyProvider;

public interface PurgeProvider extends KeyProvider {
   long PURGE_ORDER;
   int LMM_PURGE;
   int MESSAGE_LIST_CLEANUP_PURGE;
   int PHONE_CALL_LOG_CLEANUP_PURGE;
   int MESSAGE_LIST_SAVED_MESSAGE_CLEANUP_PURGE;

   boolean canPurge(int var1);

   void purge(int var1);
}
