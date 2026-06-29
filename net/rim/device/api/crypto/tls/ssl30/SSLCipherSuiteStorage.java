package net.rim.device.api.crypto.tls.ssl30;

import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.api.util.Persistable;

final class SSLCipherSuiteStorage implements Persistable {
   private int[] _items = SSLCipherSuites.getDefaultCipherSuites();
   private static final long ID;
   private static PersistentObject _persist;

   private SSLCipherSuiteStorage() {
   }

   public static final SSLCipherSuiteStorage getInstance() {
      _persist = RIMPersistentStore.getPersistentObject(-1593500279593765112L);
      synchronized (_persist) {
         if (_persist.getContents() == null) {
            _persist.setContents(new SSLCipherSuiteStorage(), 4801362);
            _persist.commit();
         }
      }

      return (SSLCipherSuiteStorage)_persist.getContents();
   }

   public final int[] getItems() {
      return this._items;
   }

   public final void setItems(int[] items) {
      this._items = items;
      _persist.commit();
   }
}
