package net.rim.wica.runtime.persistence.internal;

import java.util.Hashtable;
import net.rim.device.api.util.Persistable;
import net.rim.device.api.util.ToIntHashtable;
import net.rim.wica.runtime.lifecycle.WicletInfo;
import net.rim.wica.runtime.metadata.internal.def.ComponentDefStruct;

final class PersistedApplicationModel implements Persistable {
   WicletInfo _info;
   IntSubstore _dataSubstore;
   byte[] _mappingTable;
   ToIntHashtable _defCollisions;
   int _globalDefId;
   ComponentDefStruct _dataDefs;
   ComponentDefStruct _msgDefs;
   ComponentDefStruct _scriptDefs;
   ComponentDefStruct _uiDefs;
   byte[] _applicationPackage;
   boolean _safeData = true;
   Hashtable _resourceCache = (Hashtable)(new Object(10));
}
