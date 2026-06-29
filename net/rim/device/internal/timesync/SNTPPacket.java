package net.rim.device.internal.timesync;

import net.rim.device.api.system.DeviceInternal;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.util.DataBuffer;

public final class SNTPPacket {
   private long _timestamp = System.currentTimeMillis();
   private int _transactionId;
   private DataBuffer _requestData;
   private static final int SNTP_PACKET_SIZE = 48;
   private static final byte SNTP_VERSION = 4;
   private static final byte SNTP_MODE_CLIENT = 3;
   private static final byte SNTP_MODE_SERVER = 4;
   private static final byte SNTP_PRECISION = -20;
   private static final long SNTP_TO_UNIX = 2085978496L;
   private static final int SNTP_EXPIRY_TMO = 120000;

   public SNTPPacket(int transactionId) {
      this._transactionId = transactionId;
      this._requestData = (DataBuffer)(new Object(48, true));
      this._requestData.setLength(48);
      this._requestData.writeByte(35);
      this._requestData.skipBytes(2);
      this._requestData.writeByte(-20);
      this._requestData.skipBytes(36);
      this._requestData.writeInt(convertToSntpTimestamp(this._timestamp));
      this._requestData.writeInt(convertToSntpTimestampFraction(this._timestamp));
   }

   public final byte[] getRequestData() {
      EventLogger.logEvent(1339175110175922940L, 1399156324, 0);
      return this._requestData.toArray();
   }

   public final boolean processResponseData(int transactionId, byte[] responseData, int timeOffset) {
      EventLogger.logEvent(1339175110175922940L, 1382376310, 0);
      if (transactionId != this._transactionId) {
         EventLogger.logEvent(1339175110175922940L, 1113678921, 3);
         return false;
      }

      if (responseData.length < 48) {
         EventLogger.logEvent(1339175110175922940L, 1113678918, 3);
         return false;
      }

      DataBuffer buf = (DataBuffer)(new Object(responseData, 0, responseData.length, true));

      try {
         byte byte0 = buf.readByte();
         int leapIndicator = byte0 >> 6 & 3;
         int versionNumber = byte0 >> 3 & 7;
         int mode = byte0 & 7;
         buf.skipBytes(23);
         int originateTimestamp = buf.readInt();
         int originateTimestampFraction = buf.readInt();
         long receiveTimestamp = convertFromSntpTimestamp(buf.readInt(), buf.readInt());
         long transmitTimestamp = convertFromSntpTimestamp(buf.readInt(), buf.readInt());
         if (leapIndicator <= 2
            && versionNumber == 4
            && mode == 4
            && originateTimestamp == convertToSntpTimestamp(this._timestamp)
            && originateTimestampFraction == convertToSntpTimestampFraction(this._timestamp)
            && receiveTimestamp <= transmitTimestamp) {
            long diff = System.currentTimeMillis() - this._timestamp;
            if (diff <= 120000 && diff >= 0) {
               long delay = diff - timeOffset - (receiveTimestamp - transmitTimestamp) >> 1;
               return DeviceInternal.setDateTime(transmitTimestamp + delay);
            } else {
               EventLogger.logEvent(1339175110175922940L, 1165520996, 3);
               return false;
            }
         } else {
            EventLogger.logEvent(1339175110175922940L, 1113678928, 3);
            return false;
         }
      } finally {
         ;
      }
   }

   private static final int convertToSntpTimestamp(long timestamp) {
      if (timestamp < 0 && timestamp >= -9223372036854774809L) {
         timestamp -= 999;
      }

      return (int)(timestamp / 1000 - 2085978496);
   }

   private static final int convertToSntpTimestampFraction(long timestamp) {
      return 0;
   }

   private static final long convertFromSntpTimestamp(int integerPart, int fractionPart) {
      return ((long)integerPart + 2085978496) * 1000;
   }
}
