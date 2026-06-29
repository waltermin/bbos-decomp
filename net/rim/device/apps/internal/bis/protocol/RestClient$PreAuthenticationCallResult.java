package net.rim.device.apps.internal.bis.protocol;

import net.rim.device.apps.internal.bis.data.AuthInfo;

public final class RestClient$PreAuthenticationCallResult extends RestClient$RESTCallResult {
   private AuthInfo _authInfo;

   RestClient$PreAuthenticationCallResult(long restStatusCode, AuthInfo authInfo) {
      super(restStatusCode);
      this._authInfo = authInfo;
   }

   public final AuthInfo getAuthInfo() {
      return this._authInfo;
   }
}
