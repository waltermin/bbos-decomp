package net.rim.wica.runtime.metadata.internal;

import net.rim.wica.runtime.metadata.internal.handler.UIHandler;
import net.rim.wica.runtime.metadata.internal.transaction.TransactionManager;
import net.rim.wica.runtime.metadata.internal.util.PersistenceListener;

final class WicletRuntimeImpl$StopRunnable implements Runnable {
   private boolean _errorCondition;
   private final WicletRuntimeImpl this$0;

   WicletRuntimeImpl$StopRunnable(WicletRuntimeImpl this$0, boolean errorCondition) {
      this.this$0 = this$0;
      this._errorCondition = errorCondition;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void run() {
      label125:
      try {
         if (this.this$0._msgHandler != null) {
            this.this$0._msgHandler.shutdown();
         }

         PersistenceListener psListener = (PersistenceListener)this.this$0
            ._container
            .getService(
               WicletRuntimeImpl.class$net$rim$wica$runtime$metadata$internal$util$PersistenceListener == null
                  ? (
                     WicletRuntimeImpl.class$net$rim$wica$runtime$metadata$internal$util$PersistenceListener = WicletRuntimeImpl.class$(
                        "net.rim.wica.runtime.metadata.internal.util.PersistenceListener"
                     )
                  )
                  : WicletRuntimeImpl.class$net$rim$wica$runtime$metadata$internal$util$PersistenceListener
            );
         if (psListener != null) {
            psListener.shutdown();
         }

         UIHandler uiHandler = (UIHandler)this.this$0
            ._container
            .getService(
               WicletRuntimeImpl.class$net$rim$wica$runtime$metadata$internal$handler$UIHandler == null
                  ? (
                     WicletRuntimeImpl.class$net$rim$wica$runtime$metadata$internal$handler$UIHandler = WicletRuntimeImpl.class$(
                        "net.rim.wica.runtime.metadata.internal.handler.UIHandler"
                     )
                  )
                  : WicletRuntimeImpl.class$net$rim$wica$runtime$metadata$internal$handler$UIHandler
            );
         if (uiHandler != null && uiHandler.getCurrentScreenModel() != null) {
            uiHandler.getCurrentScreenModel().updateData();
         }

         TransactionManager transactions = (TransactionManager)this.this$0
            ._container
            .getService(
               WicletRuntimeImpl.class$net$rim$wica$runtime$metadata$internal$transaction$TransactionManager == null
                  ? (
                     WicletRuntimeImpl.class$net$rim$wica$runtime$metadata$internal$transaction$TransactionManager = WicletRuntimeImpl.class$(
                        "net.rim.wica.runtime.metadata.internal.transaction.TransactionManager"
                     )
                  )
                  : WicletRuntimeImpl.class$net$rim$wica$runtime$metadata$internal$transaction$TransactionManager
            );
         if (!this._errorCondition) {
            boolean var9 = false /* VF: Semaphore variable */;

            label110:
            try {
               var9 = true;
               this.this$0._scriptEngine.stopEngine();
               if (uiHandler != null) {
                  uiHandler.shutdown();
               }

               if (transactions != null) {
                  transactions.completeAll(false);
                  transactions.setIgnoreTransactions(true);
                  var9 = false;
               } else {
                  var9 = false;
               }
            } finally {
               if (var9) {
                  if (transactions != null) {
                     transactions.undoAll();
                  }
                  break label110;
               }
            }
         } else if (transactions != null) {
            transactions.undoAll();
         }

         this.this$0._app.save();
      } finally {
         break label125;
      }

      this.this$0.notifyStartupLock();
      this.this$0._app.getContext().stopCompleted();
   }
}
