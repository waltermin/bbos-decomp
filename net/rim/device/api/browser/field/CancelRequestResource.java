package net.rim.device.api.browser.field;

public final class CancelRequestResource extends Event {
   private RequestedResource _resource;

   public CancelRequestResource(Object src, RequestedResource resource) {
      super(10014, src);
      this._resource = resource;
   }

   public final RequestedResource getResource() {
      return this._resource;
   }
}
