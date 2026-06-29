package com.fourthpass.wapstack.util;

import com.fourthpass.wapstack.wsp.WSPAddress;

public final class RedirectEvent {
   private WSPAddress[] _redirectAdress;
   private byte _flag;

   public RedirectEvent(WSPAddress[] redirects, WSPAddress oldAddress, byte permanent) {
      this._redirectAdress = redirects;
      this._flag = permanent;
   }

   public final WSPAddress[] getRedirectAddress() {
      return this._redirectAdress;
   }

   public final boolean isReuseSecurity() {
      return this._flag == 64;
   }
}
