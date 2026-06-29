package net.rim.device.apps.internal.blackberryemail.otasync;

final class WorkUnit {
   int _command;
   Object _parmO1;
   Object _parmO2;
   boolean _parmB1;
   int _parmI1;

   final void reset() {
      this._command = 0;
      this._parmO1 = null;
      this._parmO2 = null;
      this._parmB1 = false;
      this._parmI1 = 0;
   }
}
