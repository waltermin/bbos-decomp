package net.rim.wica.runtime.persistence;

import net.rim.device.api.util.LongIntHashtable;
import net.rim.device.api.util.Persistable;

public final class PersKeylessDataCollectionStruct extends PersDataCollectionStruct implements Persistable {
   public LongIntHashtable _persistentRefCounts;
}
