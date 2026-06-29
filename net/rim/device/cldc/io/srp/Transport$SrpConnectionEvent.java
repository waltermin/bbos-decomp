package net.rim.device.cldc.io.srp;

final class Transport$SrpConnectionEvent {
   Object _object;
   int _errorCode;

   private Transport$SrpConnectionEvent(Object object, int errorCode) {
      this._object = object;
      this._errorCode = errorCode;
   }

   Transport$SrpConnectionEvent(Object x0, int x1, Transport$1 x2) {
      this(x0, x1);
   }
}
