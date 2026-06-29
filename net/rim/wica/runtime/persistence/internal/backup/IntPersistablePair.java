package net.rim.wica.runtime.persistence.internal.backup;

import net.rim.device.api.util.Persistable;
import net.rim.wica.runtime.persistence.CollectionSyncModel;

public final class IntPersistablePair implements CollectionSyncModel, Persistable {
   private int _defId;
   private Persistable _data;

   public final void setDefId(int defId) {
      this._defId = defId;
   }

   public final void setData(Persistable data) {
      this._data = data;
   }

   @Override
   public final int getDefId() {
      return this._defId;
   }

   @Override
   public final Persistable getData() {
      return this._data;
   }

   public IntPersistablePair(int defId, Persistable data) {
      this._defId = defId;
      this._data = data;
   }

   public IntPersistablePair() {
   }
}
