package net.rim.device.apps.internal.phone.api;

import net.rim.device.apps.internal.phone.data.CallerIDInfo;

public final class PhoneCallInitialData {
   public int _callId;
   public int _flags;
   public byte _type;
   public long _timestamp;
   public CallerIDInfo _callerIDInfo;
   public int _lineId;
   public Object _context;
   public static final long GUID;

   public PhoneCallInitialData(int callId, byte type, int flags, CallerIDInfo callerIDInfo, Object context) {
      this(callId, type, flags, callerIDInfo, context, PhoneUtilities.getCurrentLineId(callId));
   }

   public PhoneCallInitialData(int callId, byte type, int flags, CallerIDInfo callerIDInfo, Object context, int lineId) {
      this._callId = callId;
      this._type = type;
      this._timestamp = System.currentTimeMillis();
      this._flags = flags;
      this._callerIDInfo = callerIDInfo;
      this._lineId = lineId;
      this._context = context;
   }
}
