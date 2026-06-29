package net.rim.device.apps.internal.bis.protocol;

public final class RestClient$LoginCallResult extends RestClient$RESTCallResult {
   private String _currentPin;

   RestClient$LoginCallResult(long restStatusCode, String currentPin) {
      super(restStatusCode);
      this._currentPin = currentPin;
   }

   public final String getCurrentPin() {
      return this._currentPin;
   }
}
