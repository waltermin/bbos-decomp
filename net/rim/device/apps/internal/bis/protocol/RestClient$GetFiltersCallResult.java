package net.rim.device.apps.internal.bis.protocol;

import java.util.Vector;

public final class RestClient$GetFiltersCallResult extends RestClient$RESTCallResult {
   private Vector _filters;

   RestClient$GetFiltersCallResult(long restStatusCode, Vector filters) {
      super(restStatusCode);
      this._filters = filters;
   }

   public final Vector getFilters() {
      return this._filters;
   }
}
