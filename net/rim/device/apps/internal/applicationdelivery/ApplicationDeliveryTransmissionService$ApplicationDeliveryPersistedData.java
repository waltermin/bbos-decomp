package net.rim.device.apps.internal.applicationdelivery;

import net.rim.device.api.util.LongHashtable;
import net.rim.device.api.util.Persistable;

class ApplicationDeliveryTransmissionService$ApplicationDeliveryPersistedData implements Persistable {
   byte[] _sourceUid;
   String _keyId;
   LongHashtable _pendingApplications;

   ApplicationDeliveryTransmissionService$ApplicationDeliveryPersistedData(LongHashtable pendingApplications) {
      this._pendingApplications = pendingApplications;
   }
}
