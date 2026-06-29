package net.rim.wica.runtime.metadata.internal.transaction;

import net.rim.wica.runtime.metadata.Wiclet;
import net.rim.wica.runtime.util.GenericPoolableObjectFactory;

final class LocalTransactionFactory extends GenericPoolableObjectFactory {
   private Wiclet _wiclet;
   private TransactionManager _transManager;

   LocalTransactionFactory(int maxSize, Wiclet wiclet, TransactionManager transM) {
      super(maxSize);
      this._wiclet = wiclet;
      this._transManager = transM;
   }

   @Override
   protected final Object create() {
      return new LocalTransaction(this._wiclet, this._transManager);
   }
}
