package net.rim.device.apps.internal.calendar.ota;

import net.rim.device.api.util.DataBuffer;
import net.rim.device.apps.api.calendar.caldb.CalendarService;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.Event;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.EventUtilities;
import net.rim.device.apps.api.service.ServiceIdentifier;
import net.rim.device.apps.api.service.ServiceObject;
import net.rim.device.apps.api.sync.OTASyncData;

public class CICALSlowSyncEvent implements ServiceObject {
   private DataBuffer _commandData = (DataBuffer)(new Object());
   int _totalRecordCount;
   int _outgoingRecordCount;
   int _incomingRecordCount;
   byte _type;
   int _sessionID;
   byte _result;
   byte _hashVersion = 16;
   DataBuffer _hashes;
   DataBuffer _requestedUIDs;
   long _syncStartDate = Long.MIN_VALUE;
   long _syncEndDate = Long.MAX_VALUE;
   int _rangeHash;
   private CalendarService _calendarService;
   static DataBuffer _recordHashDataBuffer = (DataBuffer)(new Object(true));

   void addEvent(Event event, OTASyncData syncData, boolean debug) {
      this.writeIntegerField(this._commandData, 2, event.getUID());
      int eventFlag = 0;
      eventFlag |= event.isRecurring() ? 1 : 0;
      eventFlag |= event.getRelatedLUID() != 0 ? 2 : 0;
      this.writeByteField(this._commandData, 80, (byte)eventFlag);
      _recordHashDataBuffer.reset();
      _recordHashDataBuffer.setPosition(0);
      byte[] hashData = EventUtilities.getHashData(event, _recordHashDataBuffer);
      syncData.updateChecksum(hashData, false);
      syncData.markClean();
      this._commandData.writeByte(67);
      if (hashData != null) {
         this._commandData.writeByteArray(hashData, 0, 6);
      } else {
         this._commandData.writeCompressedInt(6);
         this._commandData.writeInt(0);
         this._commandData.writeShort(0);
      }

      this._commandData.writeByte(66);
      if (hashData != null) {
         this._commandData.writeByteArray(hashData, 6, 4);
      } else {
         this._commandData.writeCompressedInt(4);
         this._commandData.writeInt(0);
      }

      if (debug) {
         _recordHashDataBuffer.trim();
         byte[] recordHash = _recordHashDataBuffer.toArray();
         if (recordHash != null) {
            this._commandData.writeByte(250);
            this._commandData.writeByteArray(recordHash);
         }
      }

      _recordHashDataBuffer.reset();
   }

   protected void writeIntegerField(DataBuffer dataBuffer, int fieldId, int value) {
      dataBuffer.writeByte(fieldId);
      dataBuffer.writeCompressedInt(4);
      dataBuffer.writeInt(value);
   }

   protected void writeByteField(DataBuffer dataBuffer, int fieldId, byte value) {
      dataBuffer.writeByte(fieldId);
      dataBuffer.writeCompressedInt(1);
      dataBuffer.writeByte(value);
   }

   public long getSyncStartDate() {
      return this._syncStartDate;
   }

   public long getSyncEndDate() {
      return this._syncEndDate;
   }

   public void setSyncStartDate(long date) {
      this._syncStartDate = date;
   }

   public void setSyncEndDate(long date) {
      this._syncEndDate = date;
   }

   public void setRangeHash(int rangeHash) {
      this._rangeHash = rangeHash;
   }

   public void setOutgoingRecordCount(int count) {
      this._outgoingRecordCount = count;
      this._totalRecordCount = this._incomingRecordCount + this._outgoingRecordCount;
   }

   public int getOutgoingRecordCount() {
      return this._outgoingRecordCount;
   }

   public void setIncomingRecordCount(int count) {
      this._incomingRecordCount = count;
      this._totalRecordCount = this._incomingRecordCount + this._outgoingRecordCount;
   }

   public int getIncomingRecordCount() {
      return this._incomingRecordCount;
   }

   public byte getType() {
      return this._type;
   }

   public int getSessionID() {
      return this._sessionID;
   }

   public void setSessionID(int sessionID) {
      this._sessionID = sessionID;
   }

   void setResult(byte result) {
      this._result = result;
   }

   byte getResult() {
      return this._result;
   }

   byte[] getCommandData() {
      return this._commandData == null ? null : this._commandData.toArray();
   }

   public byte[] convert(Object contextObject) {
      switch (this._type) {
         case 30:
            this._commandData.trim();
            return this._commandData.toArray();
         case 32:
            this.writeByteField(this._commandData, 73, this._result);
            return this._commandData.toArray();
         default:
            return null;
      }
   }

   void addEvent(int uid) {
      this.writeIntegerField(this._commandData, 2, uid);
   }

   @Override
   public ServiceIdentifier getServiceIdentifier() {
      return this._calendarService;
   }

   CICALSlowSyncEvent(CalendarService calendarService, byte type, int sessionID, int totalRecordCount, long syncStartDate, long syncEndDate) {
      this(calendarService, type, sessionID, totalRecordCount);
      this._syncStartDate = syncStartDate;
      this._syncEndDate = syncEndDate;
      this.writeIntegerField(this._commandData, 72, (int)(this._syncStartDate / 1000));
      if (this._syncEndDate != Long.MAX_VALUE && this._syncEndDate > this._syncStartDate) {
         this.writeIntegerField(this._commandData, 76, (int)(this._syncEndDate / 1000));
      }
   }

   CICALSlowSyncEvent(CalendarService calendarService, byte type, int sessionID, int totalRecordCount) {
      this(calendarService, type);
      this._sessionID = sessionID;
      this._totalRecordCount = totalRecordCount;
      this.writeIntegerField(this._commandData, 65, this._sessionID);
      this.writeByteField(this._commandData, 82, this._hashVersion);
      if (totalRecordCount >= 0) {
         this.writeIntegerField(this._commandData, 64, this._totalRecordCount);
      }
   }

   CICALSlowSyncEvent(CalendarService calendarService, byte type) {
      this._type = type;
      this._calendarService = calendarService;
   }
}
