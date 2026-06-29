package net.rim.device.apps.internal.elt;

import java.util.Date;
import net.rim.device.api.synchronization.ConverterUtilities;
import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.Persistable;
import net.rim.device.internal.system.InternalServices;

final class ETSyncObject implements Persistable, SyncObject {
   private long _timestamp;
   private int _latitude;
   private int _longitude;
   private int _altitude;
   private float _speed;
   private float _bearing;
   private String _data;
   private int _deviceStatus;
   private int _reserve1;
   private byte[] _byteData;

   final void setData(long timestamp, int latitude, int longitude, int altitude, float speed, float bearing, String data, int deviceStatus, int reserve1) {
      this._timestamp = timestamp;
      this._latitude = latitude;
      this._longitude = longitude;
      this._altitude = altitude;
      this._speed = speed;
      this._bearing = bearing;
      this._data = data;
      this._deviceStatus = deviceStatus;
      this._reserve1 = reserve1;
      this.writeData();
   }

   public final boolean save(DataBuffer data, int version) {
      if (!InternalServices.isDeviceSecure()) {
         this._data = new Date().toString();
      }

      if (this._byteData == null) {
         ETDocumentCollection.writeLocationPosition(
            data, this._timestamp, this._latitude, this._longitude, this._altitude, this._speed, this._bearing, this._data, this._deviceStatus, this._reserve1
         );
         return true;
      } else {
         this.writeData();
         data.setData(this._byteData, 0, this._byteData.length);
         return true;
      }
   }

   public final boolean load(DataBuffer buffer, int version) {
      try {
         if (ConverterUtilities.findType(buffer, 1)) {
            this._byteData = ConverterUtilities.readByteArray(buffer);
            DataBuffer b = new DataBuffer(buffer.isBigEndian());
            b.setData(this._byteData, 0, this._byteData.length);
            return true;
         }
      } finally {
         return false;
      }

      return false;
   }

   @Override
   public final int getUID() {
      return -1492538911;
   }

   public final int getDeviceStatus() {
      return this._deviceStatus;
   }

   public final void setDeviceStatus(int status) {
      this._deviceStatus = status;
      this.writeData();
   }

   ETSyncObject(long timestamp, int latitude, int longitude, int altitude, float speed, float bearing, String data, int deviceStatus, int reserve1) {
      this.setData(timestamp, latitude, longitude, altitude, speed, bearing, data, deviceStatus, reserve1);
   }

   private final void writeData() {
      DataBuffer db = new DataBuffer(false);
      ETDocumentCollection.writeLocationPosition(
         db, this._timestamp, this._latitude, this._longitude, this._altitude, this._speed, this._bearing, this._data, this._deviceStatus, this._reserve1
      );
      this._byteData = db.toArray();
   }

   ETSyncObject() {
   }

   @Override
   public final String toString() {
      return "ETSyncObject[deviceStatus="
         + this._deviceStatus
         + ",timestamp="
         + this._timestamp
         + ",latitude="
         + this._latitude
         + ",longitude="
         + this._longitude
         + "],data: "
         + (this._byteData != null ? this._byteData.length + "" : "<null>");
   }
}
