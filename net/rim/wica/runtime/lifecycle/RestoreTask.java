package net.rim.wica.runtime.lifecycle;

import net.rim.device.api.util.Persistable;
import net.rim.wica.runtime.persistence.CollectionSyncModel;

public final class RestoreTask implements Persistable {
   private CollectionSyncModel[] _collections;

   public RestoreTask(CollectionSyncModel[] collections) {
      this._collections = collections;
   }

   public final CollectionSyncModel[] getCollections() {
      return this._collections;
   }
}
