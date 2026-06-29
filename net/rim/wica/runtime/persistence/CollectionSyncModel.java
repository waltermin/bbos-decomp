package net.rim.wica.runtime.persistence;

import net.rim.device.api.util.Persistable;

public interface CollectionSyncModel extends Persistable {
   Persistable getData();

   int getDefId();
}
