package net.rim.device.apps.internal.bluetooth;

import javax.bluetooth.DataElement;
import javax.bluetooth.ServiceRecord;
import javax.bluetooth.UUID;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.internal.bluetooth.BluetoothDeviceManager;

class SdpQuery {
   private int _type;
   private int[] _attributeIDs;
   private UUID[] _uuids;
   private int _profileFlag;
   private int _transactionID;
   private RemoteServiceRecord _remoteServiceRecord;

   SdpQuery(int[] attributeIDs, UUID[] uuids, int profileFlag, int transactionID) {
      this._type = 6;
      this._attributeIDs = attributeIDs;
      this._uuids = uuids;
      this._profileFlag = profileFlag;
      this._transactionID = transactionID;
   }

   SdpQuery(RemoteServiceRecord remoteServiceRecord, int[] attributeIDs) {
      this._type = 4;
      this._remoteServiceRecord = remoteServiceRecord;
      this._attributeIDs = attributeIDs;
   }

   byte[] getRequestData() {
      DataBuffer queryData = new DataBuffer();
      switch (this._type) {
         case 4:
            queryData.writeInt(this._remoteServiceRecord.getHandle());
            break;
         case 6:
            DataElement serviceSearchPattern = new DataElement(48);

            for (int i = 0; i < this._uuids.length; i++) {
               if (this._uuids[i] != null) {
                  DataElement uuidDataElement = new DataElement(24, this._uuids[i]);
                  serviceSearchPattern.addElement(uuidDataElement);
               }
            }

            DataElementUtilities.write(serviceSearchPattern, queryData);
            break;
         default:
            throw new IllegalArgumentException();
      }

      queryData.writeShort(672);
      DataElement attributeIDList = new DataElement(48);

      for (int i = 0; i < this._attributeIDs.length; i++) {
         DataElement attributeIDDataElement = new DataElement(9, this._attributeIDs[i]);
         attributeIDList.addElement(attributeIDDataElement);
      }

      DataElementUtilities.write(attributeIDList, queryData);
      return queryData.toArray();
   }

   void processResponse(BluetoothDevice device, DataBuffer buf) {
      switch (this._type) {
         case 4:
            int attributeListLength = DataElementUtilities.readDET_SEQ(buf);
            byte[] attributeList = new byte[attributeListLength];
            buf.read(attributeList);
            this._remoteServiceRecord.updateAttributes(attributeList);
            break;
         case 6:
            ServiceRecord[] records = new ServiceRecord[0];
            DataElementUtilities.readDET_SEQ(buf);

            while (!buf.eof()) {
               int attributeListLengthx = DataElementUtilities.readDET_SEQ(buf);
               byte[] attributeListx = new byte[attributeListLengthx];
               buf.read(attributeListx);
               RemoteServiceRecord record = new RemoteServiceRecord(device.getAddress(), attributeListx);
               device.addServiceRecord(record);
               if (record.containsClasses(this._uuids)) {
                  device.addSupportedProfile(this._profileFlag);
               }

               if (this._transactionID != 0) {
                  Arrays.add(records, record);
               }
            }

            if (this._transactionID != 0) {
               BluetoothDeviceManagerImpl btManager = (BluetoothDeviceManagerImpl)BluetoothDeviceManager.getInstance();
               if (records.length == 0) {
                  btManager.postEvent(9, this._transactionID, 4);
                  return;
               }

               btManager.postEvent(8, this._transactionID, 0, records, null);
               btManager.postEvent(9, this._transactionID, 1);
               return;
            }
      }
   }

   int getTransactionID() {
      return this._transactionID;
   }

   int getType() {
      return this._type;
   }

   void sdpError() {
      if (this._transactionID != 0) {
         BluetoothDeviceManagerImpl btManager = (BluetoothDeviceManagerImpl)BluetoothDeviceManager.getInstance();
         btManager.postEvent(9, this._transactionID, 3);
      }

      if (this._remoteServiceRecord != null) {
         this._remoteServiceRecord.updateAttributesFailed();
      }
   }

   void connectError() {
      if (this._transactionID != 0) {
         BluetoothDeviceManagerImpl btManager = (BluetoothDeviceManagerImpl)BluetoothDeviceManager.getInstance();
         btManager.postEvent(9, this._transactionID, 6);
      }

      if (this._remoteServiceRecord != null) {
         this._remoteServiceRecord.updateAttributesFailed();
      }
   }
}
