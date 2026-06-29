package net.rim.device.apps.api.location;

import java.util.Enumeration;

class LocationServicesOptionsProvider$ProviderEnumeration implements Enumeration {
   private int _counter;
   private LocationServicesOptionsProvider$Factory _factory = LocationServicesOptionsProvider.getFactoryInstance();

   private LocationServicesOptionsProvider$ProviderEnumeration() {
      this._counter = 0;
   }

   @Override
   public boolean hasMoreElements() {
      return this._counter < this._factory._optionProviders.length;
   }

   @Override
   public Object nextElement() {
      return this._factory._optionProviders[this._counter++];
   }

   LocationServicesOptionsProvider$ProviderEnumeration(LocationServicesOptionsProvider$1 x0) {
      this();
   }
}
