package net.rim.wica.runtime.metadata.internal.component;

import net.rim.device.api.util.LongIntHashtable;
import net.rim.wica.common.metadata.component.DataComponentDef;
import net.rim.wica.runtime.metadata.internal.WicletEx;
import net.rim.wica.runtime.persistence.PersDataCollectionStruct;
import net.rim.wica.runtime.persistence.PersKeylessDataCollectionStruct;

final class KeylessDataCollection$PersRefDataCollectionImpl extends DataCollectionImpl {
   private LongIntHashtable _persistentRefCounts;

   public KeylessDataCollection$PersRefDataCollectionImpl(WicletEx wiclet, DataComponentDef defs, boolean allowPersistence) {
      super(wiclet, defs, allowPersistence);
   }

   @Override
   protected final boolean loadExtraData(PersDataCollectionStruct dcData) {
      this._persistentRefCounts = ((PersKeylessDataCollectionStruct)dcData)._persistentRefCounts;
      return true;
   }

   protected final LongIntHashtable getPersistentRefCounts() {
      return this._persistentRefCounts;
   }
}
