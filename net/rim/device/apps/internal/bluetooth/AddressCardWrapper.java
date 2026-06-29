package net.rim.device.apps.internal.bluetooth;

import net.rim.device.apps.api.addressbook.AddressCardModel;

class AddressCardWrapper {
   AddressCardModel _card;
   int _index;
   long _timestamp;
   String _vCardExtensionName;
   String _vCardExtensionData;

   AddressCardWrapper(AddressCardModel card, int index) {
      this(card, index, 0, null, null);
   }

   AddressCardWrapper(AddressCardModel card, int index, long timestamp, String vCardExtensionName, String vCardExtensionData) {
      this._card = card;
      this._index = index;
      this._timestamp = timestamp;
      this._vCardExtensionName = vCardExtensionName;
      this._vCardExtensionData = vCardExtensionData;
   }
}
