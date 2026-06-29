package net.rim.device.apps.internal.bluetooth;

class CallInfo {
   int _callID;
   boolean _outgoing;
   int _status;
   String _number;
   static final int CALL_STATUS_ACTIVE;
   static final int CALL_STATUS_HELD;
   static final int CALL_STATUS_DIALING;
   static final int CALL_STATUS_ALERTING;
   static final int CALL_STATUS_INCOMING;
   static final int CALL_STATUS_WAITING;

   CallInfo(int callID) {
      this._callID = callID;
   }

   int getCLCCStatus() {
      switch (this._status) {
         case 2:
            return 1;
         case 4:
            return 2;
         case 8:
            return 3;
         case 16:
            return 4;
         case 32:
            return 5;
         default:
            return 0;
      }
   }

   boolean isActive() {
      return (this._status & 13) != 0;
   }
}
