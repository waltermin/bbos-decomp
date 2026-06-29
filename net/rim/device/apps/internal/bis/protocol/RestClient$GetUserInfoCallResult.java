package net.rim.device.apps.internal.bis.protocol;

import net.rim.device.apps.internal.bis.data.UserInfo;

public final class RestClient$GetUserInfoCallResult extends RestClient$RESTCallResult {
   private UserInfo _userInfo;

   RestClient$GetUserInfoCallResult(long restStatusCode, UserInfo userInfo) {
      super(restStatusCode);
      this._userInfo = userInfo;
   }

   public final UserInfo getUserInfo() {
      return this._userInfo;
   }
}
