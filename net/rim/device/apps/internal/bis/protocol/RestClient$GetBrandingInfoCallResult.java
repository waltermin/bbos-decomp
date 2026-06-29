package net.rim.device.apps.internal.bis.protocol;

import net.rim.device.apps.internal.bis.data.BrandingInfo;

public final class RestClient$GetBrandingInfoCallResult extends RestClient$RESTCallResult {
   private BrandingInfo _brandingInfo;

   RestClient$GetBrandingInfoCallResult(long restStatusCode, BrandingInfo brandingInfo) {
      super(restStatusCode);
      this._brandingInfo = brandingInfo;
   }

   public final BrandingInfo getBrandingInfo() {
      return this._brandingInfo;
   }
}
