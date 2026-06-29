package net.rim.blackberry.api.mail;

final class DefaultService$DefaultTransport extends Transport {
   private Service _service;

   public DefaultService$DefaultTransport(Service s) {
      this._service = s;
      this.setConnected(this._service.isConnected());
   }

   @Override
   public final String toString() {
      return this._service.toString();
   }

   @Override
   public final ServiceConfiguration getServiceConfiguration() {
      return this._service.getServiceConfiguration();
   }
}
