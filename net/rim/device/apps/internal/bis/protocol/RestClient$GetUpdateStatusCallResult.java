package net.rim.device.apps.internal.bis.protocol;

import net.rim.device.apps.internal.bis.data.UpdateInfo;

public final class RestClient$GetUpdateStatusCallResult extends RestClient$RESTCallResult {
   private UpdateInfo _updateInfo;

   RestClient$GetUpdateStatusCallResult(long restStatusCode, UpdateInfo updateInfo) {
      super(restStatusCode);
      this._updateInfo = updateInfo;
   }
}
