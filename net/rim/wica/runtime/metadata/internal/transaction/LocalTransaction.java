package net.rim.wica.runtime.metadata.internal.transaction;

import net.rim.wica.runtime.metadata.Wiclet;
import net.rim.wica.runtime.metadata.component.DataCollection;
import net.rim.wica.runtime.metadata.util.LocalVariable;
import net.rim.wica.runtime.util.LongVector;
import net.rim.wica.runtime.util.Poolable;

public final class LocalTransaction implements Poolable, LocalVariable {
   private LongVector _operations = new LongVector();
   private Wiclet _wiclet;
   private TransactionManager _transManager;

   LocalTransaction(Wiclet wiclet, TransactionManager tranM) {
      this._wiclet = wiclet;
      this._transManager = tranM;
   }

   @Override
   public final void created(long dataHandle) {
      this._operations.addElement(dataHandle);
   }

   @Override
   public final void undo() {
      LongVector operations = this._operations;
      boolean bChanged = false;
      if (!this._transManager.getIgnoreTransactions()) {
         this._transManager.setIgnoreTransactions(true);
         bChanged = true;
      }

      for (int i = operations.size() - 1; i >= 0; i--) {
         long dataH = operations.elementAt(i);
         DataCollection dc = this._wiclet.getDataCollection((int)(dataH >>> 32));
         if (dc.contains(dataH)) {
            dc.remove(dataH);
         }
      }

      if (bChanged) {
         this._transManager.setIgnoreTransactions(false);
      }
   }

   @Override
   public final void clear() {
      this._operations.removeAllElements();
   }
}
