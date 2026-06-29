package net.rim.device.apps.api.framework.model;

import java.util.Calendar;
import java.util.TimeZone;
import net.rim.device.api.collection.ReadableList;
import net.rim.device.api.synchronization.ConverterUtilities;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.DateTimeUtilities;
import net.rim.device.cldc.util.CalendarExtensions;
import net.rim.vm.WeakReference;

public class SyncBuffer {
   private DataBuffer _dataBuffer;
   private int _version;
   private int _uid;
   private static Calendar _cal;
   private static byte[] _wireDate = new byte[10];

   public SyncBuffer(DataBuffer dataBuffer, int version, int uid) {
      this.initialize(dataBuffer, version, uid);
   }

   public final void initialize(DataBuffer dataBuffer, int version, int uid) {
      this._dataBuffer = dataBuffer;
      this._version = version;
      this._uid = uid;
   }

   public int getVersion() {
      return this._version;
   }

   public int getUID() {
      return this._uid;
   }

   public boolean isEmpty() {
      return this._dataBuffer.eof();
   }

   public boolean addModel(RIMModel model, Object context) {
      if (!(model instanceof ConversionProvider)) {
         return false;
      }

      ConversionProvider converter = (ConversionProvider)model;
      return converter.convert(context, this);
   }

   public boolean addSubmembers(ReadableList submembers, Object context) {
      int count = submembers.size();

      for (int i = 0; i < count; i++) {
         Object item = submembers.getAt(i);
         if (item instanceof ConversionProvider) {
            ConversionProvider converter = (ConversionProvider)item;
            if (!converter.convert(context, this)) {
               return false;
            }
         }
      }

      return true;
   }

   public void addField(int fieldID, String value) {
      ConverterUtilities.writeStringSmart(this._dataBuffer, fieldID, value);
   }

   public void addBytes(int fieldID, byte[] data) {
      ConverterUtilities.writeByteArray(this._dataBuffer, fieldID, data);
   }

   public void addInts(int fieldID, int[] data) {
      ConverterUtilities.writeIntArray(this._dataBuffer, fieldID, data);
   }

   public void addInt(int fieldID, int data, int len) {
      ConverterUtilities.convertInt(this._dataBuffer, fieldID, data, len);
   }

   public void addLong(int fieldID, long data) {
      ConverterUtilities.writeLong(this._dataBuffer, fieldID, data);
   }

   private void initilizeCalendar() {
      synchronized (_wireDate) {
         if (_cal == null) {
            _cal = Calendar.getInstance(TimeZone.getTimeZone(DateTimeUtilities.GMT));
         }
      }
   }

   public void addDateTimeField(int fieldID, long date) {
      this.initilizeCalendar();
      ((CalendarExtensions)_cal).setTimeLong(date);
      _wireDate[0] = (byte)(_cal.get(13) & 0xFF);
      _wireDate[1] = (byte)(_cal.get(12) & 0xFF);
      _wireDate[2] = (byte)(_cal.get(11) & 0xFF);
      _wireDate[3] = (byte)(_cal.get(5) & 0xFF);
      _wireDate[4] = (byte)(_cal.get(2) + 1 & 0xFF);
      _wireDate[5] = (byte)(_cal.get(7) & 0xFF);
      int year = _cal.get(1);
      _wireDate[6] = (byte)(year & 0xFF);
      _wireDate[7] = (byte)(year >>> 8 & 0xFF);
      _wireDate[8] = (byte)(_cal.getTimeZone().getRawOffset() / 1000 / 60 / 6 & 0xFF);
      _wireDate[9] = 0;
      this.addBytes(fieldID, _wireDate);
   }

   public int getPosition() {
      return this._dataBuffer.getPosition();
   }

   public void setPosition(int newPosition) {
      this._dataBuffer.setPosition(newPosition);
   }

   public boolean containsType(int type) {
      return ConverterUtilities.findType(this._dataBuffer, type, false);
   }

   public boolean containsType(int type, boolean convertTag) {
      return ConverterUtilities.findType(this._dataBuffer, type, convertTag);
   }

   public int getFieldType() {
      try {
         return ConverterUtilities.getType(this._dataBuffer, false);
      } finally {
         ;
      }
   }

   public int getFieldType(boolean convertTag) {
      try {
         return ConverterUtilities.getType(this._dataBuffer, convertTag);
      } finally {
         ;
      }
   }

   public String getString(int position, int type, boolean consumed) {
      this._dataBuffer.setPosition(position);
      String value = this.getString(type, consumed);
      if (value != null && value.length() == 0) {
         value = null;
      }

      return value;
   }

   public String getString(int type, boolean consumed) {
      String value = null;
      if (ConverterUtilities.findType(this._dataBuffer, type, true)) {
         value = ConverterUtilities.readString(this._dataBuffer, consumed);
         if (value != null && value.length() == 0) {
            value = null;
         }
      }

      return value;
   }

   public String getString() {
      String value = ConverterUtilities.readString(this._dataBuffer);
      if (value != null && value.length() == 0) {
         value = null;
      }

      return value;
   }

   public byte[] getBytes(int position, int type, boolean consumed) {
      this._dataBuffer.setPosition(position);
      return this.getBytes(type, consumed);
   }

   public byte[] getBytes(int type, boolean consumed) {
      if (ConverterUtilities.findType(this._dataBuffer, type, false)) {
         return ConverterUtilities.readByteArray(this._dataBuffer, consumed);
      } else {
         throw new Object();
      }
   }

   public byte[] getBytes() {
      return ConverterUtilities.readByteArray(this._dataBuffer);
   }

   public int getInt(int position, int type, boolean consumed) {
      this._dataBuffer.setPosition(position);
      return this.getInt(type, consumed);
   }

   public int getInt(int type, boolean consumed) {
      if (ConverterUtilities.findType(this._dataBuffer, type, false)) {
         return ConverterUtilities.readInt(this._dataBuffer, consumed);
      } else {
         throw new Object();
      }
   }

   public int getInt() {
      return ConverterUtilities.readInt(this._dataBuffer);
   }

   public long getLong(int type, boolean consumed) {
      if (ConverterUtilities.findType(this._dataBuffer, type, false)) {
         return ConverterUtilities.readLong(this._dataBuffer, consumed);
      } else {
         throw new Object();
      }
   }

   public long getLong() {
      return ConverterUtilities.readLong(this._dataBuffer);
   }

   public long getDateGMT(int position, int type, boolean consumed) {
      this._dataBuffer.setPosition(position);
      return this.getDateGMT(type, consumed);
   }

   public long getDateGMT(int type, boolean consumed) {
      if (ConverterUtilities.findType(this._dataBuffer, type, false)) {
         return ConverterUtilities.getDateGMT(this._dataBuffer, consumed);
      } else {
         throw new Object();
      }
   }

   public long getDateTime(int type, boolean consumed) {
      if (ConverterUtilities.findType(this._dataBuffer, type, false)) {
         return ConverterUtilities.getDateTime(this._dataBuffer, consumed);
      } else {
         throw new Object();
      }
   }

   public long getDate() {
      return ConverterUtilities.getDateGMT(this._dataBuffer);
   }

   public void skipField() {
      int len = this._dataBuffer.readUnsignedShort();
      this._dataBuffer.readByte();
      if (this._dataBuffer.available() >= len) {
         this._dataBuffer.skipBytes(len);
      }
   }

   public DataBuffer getDataBuffer() {
      return this._dataBuffer;
   }

   public static SyncBuffer getSyncBuffer(WeakReference wr) {
      SyncBuffer _syncBuffer = (SyncBuffer)wr.get();
      if (_syncBuffer == null) {
         _syncBuffer = new SyncBuffer(null, -1, -1);
         wr.set(_syncBuffer);
      }

      return _syncBuffer;
   }
}
