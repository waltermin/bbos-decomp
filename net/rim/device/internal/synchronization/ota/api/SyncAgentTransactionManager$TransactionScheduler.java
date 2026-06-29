package net.rim.device.internal.synchronization.ota.api;

import java.util.Enumeration;
import java.util.Vector;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.PersistentStore;

final class SyncAgentTransactionManager$TransactionScheduler extends Thread {
   private Vector _scheduledTransactions;
   private PersistentObject _persistedTransactions;
   private int SYSTEM_STARTUP_WINDOW;
   private final SyncAgentTransactionManager this$0;

   SyncAgentTransactionManager$TransactionScheduler(SyncAgentTransactionManager _1) {
      this.this$0 = _1;
      this.SYSTEM_STARTUP_WINDOW = 3;
      this._persistedTransactions = PersistentStore.getPersistentObject(2523630400836903352L);
      this._scheduledTransactions = this.createTransactionsVector();
      this.updatePersistentStore();
   }

   private final Vector createTransactionsVector() {
      Object xObject = this._persistedTransactions.getContents();
      Vector xTransactionVector = new Vector(0);
      if (xObject instanceof Vector) {
         xTransactionVector = (Vector)xObject;
         Enumeration xTransactionsList = xTransactionVector.elements();

         while (xTransactionsList.hasMoreElements()) {
            SyncAgentTransaction xTransaction = (SyncAgentTransaction)xTransactionsList.nextElement();
            xTransaction.scheduleTransactionFor(this.SYSTEM_STARTUP_WINDOW);
         }
      }

      return xTransactionVector;
   }

   final void addScheduledTransaction(SyncAgentTransaction aSyncAgentTransaction) {
      synchronized (this._scheduledTransactions) {
         this._scheduledTransactions.addElement(aSyncAgentTransaction);
         this.updatePersistentStore();
      }

      if (this._scheduledTransactions.size() == 1) {
         synchronized (this._scheduledTransactions) {
            this._scheduledTransactions.notify();
         }
      }
   }

   private final void updatePersistentStore() {
      this._persistedTransactions.setContents(this._scheduledTransactions, 51);
      this._persistedTransactions.commit();
   }

   @Override
   public final void run() {
      while (true) {
         long timeToWait = 60000;
         if (this._scheduledTransactions.isEmpty()) {
            timeToWait = 0;
         }

         label63:
         try {
            synchronized (this._scheduledTransactions) {
               this._scheduledTransactions.wait(timeToWait);
            }
         } finally {
            break label63;
         }

         for (int i = this._scheduledTransactions.size() - 1; i >= 0; i--) {
            SyncAgentTransaction xSyncAgentTransaction = (SyncAgentTransaction)this._scheduledTransactions.elementAt(i);
            if (!xSyncAgentTransaction.tick()) {
               synchronized (this._scheduledTransactions) {
                  this._scheduledTransactions.removeElementAt(i);
               }

               this.updatePersistentStore();
               this.this$0.excecuteSyncAgentTransaction(xSyncAgentTransaction);
            }
         }
      }
   }
}
