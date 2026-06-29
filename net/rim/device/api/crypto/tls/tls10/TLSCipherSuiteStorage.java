package net.rim.device.api.crypto.tls.tls10;

import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.api.util.Persistable;

final class TLSCipherSuiteStorage implements Persistable {
   private int[] _items = TLSCipherSuites.getDefaultCipherSuites();
   private static final long ID;
   private static PersistentObject _persist;

   private TLSCipherSuiteStorage() {
   }

   public static final TLSCipherSuiteStorage getInstance() {
      _persist = RIMPersistentStore.getPersistentObject(-464406423737186631L);
      synchronized (_persist) {
         if (_persist.getContents() == null) {
            _persist.setContents(new TLSCipherSuiteStorage(), 4801362);
            _persist.commit();
         }
      }

      return (TLSCipherSuiteStorage)_persist.getContents();
   }

   public final int[] getItems() {
      return this._items;
   }

   public final void setItems(int[] items) {
      this._items = items;
      _persist.commit();
   }
}
