package net.rim.wica.runtime.metadata.internal.transaction;

import net.rim.wica.runtime.metadata.Wiclet;
import net.rim.wica.runtime.util.GenericPoolableObjectFactory;

final class PooledTransactionFactory extends GenericPoolableObjectFactory {
   private Wiclet _wiclet;

   public PooledTransactionFactory(int maxSize, Wiclet wiclet) {
      super(maxSize);
      this._wiclet = wiclet;
   }

   @Override
   protected final Object create() {
      return new PooledTransaction(this._wiclet);
   }
}
