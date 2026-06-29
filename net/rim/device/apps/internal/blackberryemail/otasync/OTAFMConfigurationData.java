package net.rim.device.apps.internal.blackberryemail.otasync;

import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.Persistable;
import net.rim.vm.Array;

final class OTAFMConfigurationData implements Persistable {
   ServiceRecord[] _serviceRecords = new ServiceRecord[0];
   ServiceSyncInfo[] _serviceSyncInfo = new ServiceSyncInfo[0];
   boolean _messageListRestored;

   final void remove(ServiceRecord serviceRecord) {
      synchronized (this._serviceRecords) {
         int index = Arrays.getIndex(this._serviceRecords, serviceRecord);
         if (index != -1) {
            int newLength = this._serviceRecords.length - 1;
            System.arraycopy(this._serviceRecords, index + 1, this._serviceRecords, index, newLength - index);
            System.arraycopy(this._serviceSyncInfo, index + 1, this._serviceSyncInfo, index, newLength - index);
            Array.resize(this._serviceRecords, newLength);
            Array.resize(this._serviceSyncInfo, newLength);
         }
      }
   }

   final ServiceSyncInfo get(ServiceRecord serviceRecord) {
      synchronized (this._serviceRecords) {
         int index = Arrays.getIndex(this._serviceRecords, serviceRecord);
         return index != -1 ? this._serviceSyncInfo[index] : null;
      }
   }

   final int size() {
      return this._serviceRecords.length;
   }

   final void put(ServiceRecord serviceRecord, ServiceSyncInfo serviceSyncInfo) {
      synchronized (this._serviceRecords) {
         int index = Arrays.getIndex(this._serviceRecords, serviceRecord);
         if (index != -1) {
            this._serviceSyncInfo[index] = serviceSyncInfo;
         } else {
            Arrays.add(this._serviceRecords, serviceRecord);
            Arrays.add(this._serviceSyncInfo, serviceSyncInfo);
         }
      }
   }
}
