package net.rim.device.apps.api.utility.lowMemory;

import net.rim.device.apps.api.framework.model.KeyProvider;

public interface PurgeProvider extends KeyProvider {
   long PURGE_ORDER = -7628247220259263034L;
   int LMM_PURGE = 0;
   int MESSAGE_LIST_CLEANUP_PURGE = 1;
   int PHONE_CALL_LOG_CLEANUP_PURGE = 2;
   int MESSAGE_LIST_SAVED_MESSAGE_CLEANUP_PURGE = 3;

   boolean canPurge(int var1);

   void purge(int var1);
}
