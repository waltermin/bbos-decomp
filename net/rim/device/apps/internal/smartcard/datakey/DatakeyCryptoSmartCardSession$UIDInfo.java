package net.rim.device.apps.internal.smartcard.datakey;

class DatakeyCryptoSmartCardSession$UIDInfo {
   long _uid;
   boolean _doublePINHashingEnabled;

   DatakeyCryptoSmartCardSession$UIDInfo(long uid, boolean doublePINHashingEnabled) {
      this._uid = uid;
      this._doublePINHashingEnabled = doublePINHashingEnabled;
   }
}
