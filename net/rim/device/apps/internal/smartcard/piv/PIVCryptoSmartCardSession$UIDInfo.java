package net.rim.device.apps.internal.smartcard.piv;

class PIVCryptoSmartCardSession$UIDInfo {
   String _friendlyName;
   long _uid;

   PIVCryptoSmartCardSession$UIDInfo(long uid, String friendlyName) {
      this._friendlyName = friendlyName;
      this._uid = uid;
   }
}
