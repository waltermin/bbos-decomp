package net.rim.device.apps.api.utility.framework;

import net.rim.device.api.collection.util.BigVector;
import net.rim.device.api.util.Persistable;

public final class SimplePersistentSyncCollection$SimpleData implements Persistable {
   public BigVector _items;

   public SimplePersistentSyncCollection$SimpleData(int n) {
      this.clear(n);
   }

   public final void clear(int n) {
      this._items = new BigVector(n);
   }
}
