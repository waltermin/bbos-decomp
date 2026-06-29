package net.rim.device.apps.internal.bis.protocol;

import net.rim.device.apps.internal.bis.data.Filter;

public final class RestClient$AddFilterCallResult extends RestClient$RESTCallResult {
   private Filter _filter;

   RestClient$AddFilterCallResult(long restStatusCode, Filter filter) {
      super(restStatusCode);
      this._filter = filter;
   }

   public final Filter getFilter() {
      return this._filter;
   }
}
