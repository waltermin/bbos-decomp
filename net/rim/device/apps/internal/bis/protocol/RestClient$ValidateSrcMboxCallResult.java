package net.rim.device.apps.internal.bis.protocol;

public final class RestClient$ValidateSrcMboxCallResult extends RestClient$RESTCallResult {
   private boolean _valid;

   RestClient$ValidateSrcMboxCallResult(long restStatusCode, boolean valid) {
      super(restStatusCode);
      this._valid = valid;
   }
}
