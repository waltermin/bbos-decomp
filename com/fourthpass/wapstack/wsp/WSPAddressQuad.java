package com.fourthpass.wapstack.wsp;

public final class WSPAddressQuad {
   private WSPAddress _localAddress;
   private WSPAddress _destAddress;

   public WSPAddressQuad(WSPAddress localAdd, WSPAddress destAddress) {
      this._localAddress = localAdd;
      this._destAddress = destAddress;
   }

   public final WSPAddress getLocalAddress() {
      return this._localAddress;
   }

   public final WSPAddress getDestAddress() {
      return this._destAddress;
   }
}
