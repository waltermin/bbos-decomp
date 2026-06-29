package net.rim.device.apps.api.calendar.ota;

import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.util.Persistable;
import net.rim.device.apps.api.framework.model.EncryptableProvider;

public class TrackingData implements Persistable, EncryptableProvider {
   private Object _data;
   public int _statusCode;
   public long _lastUpdate;
   public long _createDate;
   public int _failCount = 0;
   public int _uid = -1;
   public int _packetType = 0;
   public static final int UNSENT = 0;
   public static final int SENT = 1;
   public static final int SENDING = 2;
   public static final int ERROR = 3;
   public static final int QUEUED = 4;
   public static final int NO_TRACKING_DATA = -1;

   public TrackingData(byte[] data) {
      this._statusCode = 0;
      this._lastUpdate = System.currentTimeMillis();
      this._createDate = this._lastUpdate;
      this._data = PersistentContent.encode(data);
   }

   public void setUID(int uid) {
      this._uid = uid;
   }

   public void setPacketType(int packetType) {
      this._packetType = packetType;
   }

   public byte[] getData() {
      return PersistentContent.decodeByteArray(this._data);
   }

   @Override
   public boolean checkCrypt(boolean compress, boolean encrypt) {
      return PersistentContent.checkEncoding(this._data, compress, encrypt);
   }

   @Override
   public Object reCrypt(boolean compress, boolean encrypt) {
      this._data = PersistentContent.reEncode(this._data, compress, encrypt);
      return null;
   }
}
