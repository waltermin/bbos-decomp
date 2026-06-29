package net.rim.device.apps.internal.bis.protocol;

public final class RestClient$ValidatePasswordsCallResult extends RestClient$RESTCallResult {
   private int[] _failedMboxes;

   RestClient$ValidatePasswordsCallResult(long restStatusCode, int[] failedMboxes) {
      super(restStatusCode);
      this._failedMboxes = failedMboxes;
   }

   public final int[] getFailedMboxes() {
      return this._failedMboxes;
   }
}
