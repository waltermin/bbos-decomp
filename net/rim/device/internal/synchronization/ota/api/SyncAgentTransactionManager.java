package net.rim.device.internal.synchronization.ota.api;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.internal.proxy.Proxy;

public class SyncAgentTransactionManager {
   private Object _transactionLock = new Object();
   private SyncAgentTransactionManager$TransactionScheduler _transactionScheduler = new SyncAgentTransactionManager$TransactionScheduler(this);
   private static final long GUID_TRANSACTION_MANAGER;

   private SyncAgentTransactionManager() {
      Proxy.getInstance().startThread(this._transactionScheduler);
   }

   public int excecuteSyncAgentTransaction(SyncAgentTransaction aSyncAgentTransaction) {
      int xCommandErrorCode = 0;
      if (!aSyncAgentTransaction.isScheduledForFutureExecution()) {
         synchronized (this._transactionLock) {
            return aSyncAgentTransaction.execute();
         }
      } else {
         this._transactionScheduler.addScheduledTransaction(aSyncAgentTransaction);
         return xCommandErrorCode;
      }
   }

   public static SyncAgentTransactionManager getInstance() {
      ApplicationRegistry appRegistry = ApplicationRegistry.getApplicationRegistry();
      SyncAgentTransactionManager transactionManager = (SyncAgentTransactionManager)appRegistry.getOrWaitFor(2523630400836903352L);
      if (transactionManager == null) {
         transactionManager = new SyncAgentTransactionManager();
         appRegistry.put(2523630400836903352L, transactionManager);
      }

      return transactionManager;
   }
}
