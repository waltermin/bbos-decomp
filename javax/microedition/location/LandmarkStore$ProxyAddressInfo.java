package javax.microedition.location;

import net.rim.device.api.util.Persistable;

class LandmarkStore$ProxyAddressInfo implements Persistable {
   String[] _values = new String[17];

   LandmarkStore$ProxyAddressInfo(AddressInfo addressInfo) {
      if (addressInfo != null) {
         for (int i = 0; i < 17; i++) {
            this._values[i] = addressInfo.getField(i + 1);
         }
      }
   }

   AddressInfo getAddressInfo() {
      AddressInfo addressInfo = new AddressInfo();

      for (int i = 0; i < this._values.length; i++) {
         addressInfo.setField(i + 1, this._values[i]);
      }

      return addressInfo;
   }
}
