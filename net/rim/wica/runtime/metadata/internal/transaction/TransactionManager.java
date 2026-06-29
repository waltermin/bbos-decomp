package net.rim.wica.runtime.metadata.internal.transaction;

import java.util.Stack;
import net.rim.wica.runtime.metadata.Wiclet;
import net.rim.wica.runtime.metadata.WicletRuntime;
import net.rim.wica.runtime.metadata.util.LocalVariable;
import net.rim.wica.runtime.service.ServiceProvider;
import net.rim.wica.runtime.service.Serviceable;
import net.rim.wica.runtime.util.LongVector;

public final class TransactionManager implements Serviceable {
   private Stack _transactions;
   private boolean _ignoreNewTransactions;
   private PooledTransactionFactory _transPool;
   private LocalTransactionFactory _localTransPool;
   LongVector _dataHandles = new LongVector();
   private static int INIT_TRANSACTION_POOL_SIZE = 50;
   static Class class$net$rim$wica$runtime$metadata$WicletRuntime;

   public final void returnLocalTransaction(LocalVariable localTrans) {
      this._localTransPool.returnBack(localTrans);
   }

   public final void setIgnoreTransactions(boolean ignore) {
      this._ignoreNewTransactions = ignore;
   }

   public final boolean getIgnoreTransactions() {
      return this._ignoreNewTransactions;
   }

   public final void complete(boolean bRemoveTrans, int transID, boolean bNeedTransUpdated, boolean including) {
      LongVector dataHandles;
      if (bNeedTransUpdated) {
         dataHandles = this._dataHandles;
         dataHandles.removeAllElements();
      } else {
         dataHandles = null;
      }

      boolean bChanged = false;
      if (!this.getIgnoreTransactions()) {
         this.setIgnoreTransactions(true);
         bChanged = true;
      }

      if (bRemoveTrans) {
         while (!this._transactions.empty()) {
            PooledTransaction t = (PooledTransaction)this._transactions.pop();
            int theTransID = t.getTransID();
            if (!including && theTransID == transID) {
               this._transactions.push(t);
               break;
            }

            t.complete(dataHandles);
            this.updateTransactionInStack(dataHandles);
            this._transPool.returnBack(t);
            if (theTransID == transID) {
               break;
            }
         }
      } else {
         int numTrans = this._transactions.size();

         for (int currentNumEle = numTrans; currentNumEle != 0; currentNumEle--) {
            PooledTransaction t = (PooledTransaction)this._transactions.elementAt(currentNumEle - 1);
            int theTransID = t.getTransID();
            if (!including && theTransID == transID) {
               break;
            }

            t.complete(dataHandles);
            this.updateTransactionInStack(dataHandles);
            if (theTransID == transID) {
               break;
            }
         }
      }

      if (bChanged) {
         this.setIgnoreTransactions(false);
      }
   }

   public final void completeAll(boolean bNeedTransUpdated) {
      this.complete(true, -1, bNeedTransUpdated, true);
   }

   public final boolean contains(int transID) {
      for (int i = this._transactions.size() - 1; i >= 0; i--) {
         PooledTransaction t = (PooledTransaction)this._transactions.elementAt(i);
         if (transID == t.getTransID()) {
            return true;
         }
      }

      return false;
   }

   public final void undo(boolean bRemoveTrans, int transID, boolean including) {
      boolean bChanged = false;
      if (!this.getIgnoreTransactions()) {
         this.setIgnoreTransactions(true);
         bChanged = true;
      }

      if (bRemoveTrans) {
         while (!this._transactions.empty()) {
            PooledTransaction t = (PooledTransaction)this._transactions.pop();
            int thisID = t.getTransID();
            if (!including && thisID == transID) {
               this._transactions.push(t);
               break;
            }

            t.undo();
            this._transPool.returnBack(t);
            if (thisID == transID) {
               break;
            }
         }
      } else {
         int numTrans = this._transactions.size();

         for (int currentNumEle = numTrans; currentNumEle != 0; currentNumEle--) {
            PooledTransaction t = (PooledTransaction)this._transactions.elementAt(currentNumEle - 1);
            int thisID = t.getTransID();
            if (!including && thisID == transID) {
               break;
            }

            t.undo();
            if (thisID == transID) {
               break;
            }
         }
      }

      if (bChanged) {
         this.setIgnoreTransactions(false);
      }
   }

   public final void undoAll() {
      this.undo(true, -1, true);
   }

   public final boolean removeTrans(int id) {
      boolean bRemoved = false;
      int numTrans = this._transactions.size();

      for (int currentNumEle = numTrans; currentNumEle != 0; currentNumEle--) {
         PooledTransaction t = (PooledTransaction)this._transactions.elementAt(currentNumEle - 1);
         if (t.getTransID() == id) {
            this._transactions.removeElementAt(currentNumEle - 1);
            bRemoved = true;
            this._transPool.returnBack(t);
            return bRemoved;
         }
      }

      return bRemoved;
   }

   public final void created(long dataHandle) {
      if (!this._ignoreNewTransactions) {
         PooledTransaction t = (PooledTransaction)this._transactions.peek();
         t.created(dataHandle);
      }
   }

   public final boolean markDeleted(long dataHandle) {
      if (this._ignoreNewTransactions) {
         return false;
      }

      PooledTransaction t = (PooledTransaction)this._transactions.peek();
      t.markDeleted(dataHandle);
      return true;
   }

   public final void deleted(long dataHandle) {
      if (!this._ignoreNewTransactions) {
         PooledTransaction t = (PooledTransaction)this._transactions.peek();
         t.deleted(dataHandle);
      }
   }

   public final void modified(long dataHandle, int field) {
      if (!this._ignoreNewTransactions) {
         PooledTransaction t = (PooledTransaction)this._transactions.peek();
         t.modified(dataHandle, field);
      }
   }

   public final int startTransaction() {
      PooledTransaction trans = (PooledTransaction)this._transPool.getInstance();
      this._transactions.push(trans);
      return trans.getTransID();
   }

   public final LocalVariable startLocalTransaction() {
      return (LocalTransaction)this._localTransPool.getInstance();
   }

   @Override
   public final void setServices(ServiceProvider provider) {
      WicletRuntime runtime = (WicletRuntime)provider.getService(
         class$net$rim$wica$runtime$metadata$WicletRuntime == null
            ? (class$net$rim$wica$runtime$metadata$WicletRuntime = class$("net.rim.wica.runtime.metadata.WicletRuntime"))
            : class$net$rim$wica$runtime$metadata$WicletRuntime
      );
      Wiclet wiclet = runtime.getWiclet();
      this.init(wiclet);
   }

   private final void updateTransactionInStack(LongVector dataHandles) {
      if (dataHandles != null) {
         long[] dHandles = dataHandles.toArray();
         int numDHandles = dHandles.length;
         Stack trans = this._transactions;
         int numTrans = trans.size();

         for (int i = 0; i < numDHandles; i++) {
            for (int j = 0; j < numTrans; j++) {
               PooledTransaction tran = (PooledTransaction)trans.elementAt(j);
               long dh = dHandles[i];
               if (tran.contains(dh)) {
                  tran.setOpCode(tran.getEntry(dh), 0);
               }
            }
         }
      }
   }

   private final void init(Wiclet wiclet) {
      this._transPool = new PooledTransactionFactory(INIT_TRANSACTION_POOL_SIZE, wiclet);
      this._localTransPool = new LocalTransactionFactory(INIT_TRANSACTION_POOL_SIZE, wiclet, this);
   }

   public TransactionManager() {
      this._transactions = (Stack)(new Object());
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   static final Class class$(String x0) {
      try {
         return Class.forName(x0);
      } catch (Throwable var3) {
         throw new Object(x1.getMessage());
      }
   }
}
