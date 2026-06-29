package net.rim.device.apps.internal.bluetooth;

class CallInfo {
   int _callID;
   boolean _outgoing;
   int _status;
   String _number;
   static final int CALL_STATUS_ACTIVE = 1;
   static final int CALL_STATUS_HELD = 2;
   static final int CALL_STATUS_DIALING = 4;
   static final int CALL_STATUS_ALERTING = 8;
   static final int CALL_STATUS_INCOMING = 16;
   static final int CALL_STATUS_WAITING = 32;

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
