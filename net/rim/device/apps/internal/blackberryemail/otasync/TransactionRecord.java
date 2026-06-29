package net.rim.device.apps.internal.blackberryemail.otasync;

import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.util.Persistable;
import net.rim.device.apps.api.transmission.rim.CMIMEUtilities;

final class TransactionRecord implements Persistable {
   ServiceRecord _serviceRecord;
   int _id;
   long _lastChanged;
   Object _payload;
   int _status;
   int _retryCount;
   static final int PENDING;
   static final int SENT;
   static final int ERROR;
   static final int DELIVERED;
   static final int RETRY_REQUIRED;

   TransactionRecord(ServiceRecord serviceRecord, Object payload) {
      this._serviceRecord = serviceRecord;
      this._id = CMIMEUtilities.newDeviceSideIdentifier();
      this._payload = payload;
      this._status = 0;
      this.updateLastChanged();
   }

   final void updateLastChanged() {
      this._lastChanged = System.currentTimeMillis();
   }

   final boolean sentSuccessfully() {
      return this._status == 1 || this._status == 3;
   }

   final boolean packetNotSent() {
      return this._status == 0;
   }
}
