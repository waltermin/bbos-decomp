package net.rim.ecmascript.runtime;

class HostFunction$State {
   ESObject _thisObj;
   boolean _isConstructor;
   boolean _calledAsConstructor;
   long[] _parmArray;
   int _firstParm;
   int _numParms;
   Context _context;
   int _expectedNumParms;
   boolean _okToRecurse;
}
