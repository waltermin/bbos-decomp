package net.rim.device.apps.internal.bis.protocol;

import net.rim.device.apps.internal.bis.data.UserInfo;

public final class RestClient$SignupUserCallResult extends RestClient$RESTCallResult {
   private UserInfo _userInfo;

   RestClient$SignupUserCallResult(long restStatusCode, UserInfo userInfo) {
      super(restStatusCode);
      this._userInfo = userInfo;
   }

   public final UserInfo getUserInfo() {
      return this._userInfo;
   }
}
