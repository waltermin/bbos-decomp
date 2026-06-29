package net.rim.device.cldc.io.lstp;

import net.rim.device.api.io.DatagramAddressBase;

public final class LstpAddress extends DatagramAddressBase {
   private int _appId;

   public LstpAddress() {
      this._appId = -1;
   }

   public LstpAddress(int appId) {
      this._appId = appId;
   }

   public LstpAddress(DatagramAddressBase addressBase) {
      if (addressBase instanceof LstpAddress) {
         this._appId = ((LstpAddress)addressBase)._appId;
      } else {
         this.setAddress(addressBase.getAddress());
      }
   }

   public LstpAddress(String address) {
      this.setAddress(address);
   }

   public final int getAppId() {
      return this._appId;
   }

   @Override
   public final void setAddress(String address) {
      if (address != null && address.length() > 0) {
         this._appId = LstpUtil.getInstance().getAppId(address);
      } else {
         this._appId = -1;
      }
   }

   @Override
   public final String getAddress() {
      return this._appId != -1 ? LstpUtil.getInstance().getAppName(this._appId) : "";
   }

   @Override
   public final boolean equals(Object addressBase) {
      if (addressBase == this) {
         return true;
      }

      if (!(addressBase instanceof LstpAddress)) {
         return false;
      }

      LstpAddress address = (LstpAddress)addressBase;
      return this._appId == -1 || address._appId == -1 || this._appId == address._appId;
   }

   @Override
   public final int hashCode() {
      return 217 + this._appId;
   }
}
