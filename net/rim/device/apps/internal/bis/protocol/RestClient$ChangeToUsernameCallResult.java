package net.rim.device.apps.internal.bis.protocol;

public final class RestClient$ChangeToUsernameCallResult extends RestClient$RESTCallResult {
   private String[] _suggestions;

   RestClient$ChangeToUsernameCallResult(long restStatusCode, String[] suggestions) {
      super(restStatusCode);
      this._suggestions = suggestions;
   }

   public final String[] getSuggestions() {
      return this._suggestions;
   }
}
