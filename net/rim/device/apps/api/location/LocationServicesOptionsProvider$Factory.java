package net.rim.device.apps.api.location;

import net.rim.device.api.util.Arrays;

class LocationServicesOptionsProvider$Factory {
   private LocationServicesOptionsProvider[] _optionProviders = new LocationServicesOptionsProvider[0];

   private LocationServicesOptionsProvider$Factory() {
   }

   private void add(LocationServicesOptionsProvider provider) {
      String clazz = provider.getClass().getName();

      for (int i = 0; i < this._optionProviders.length; i++) {
         String checkClass = this._optionProviders[i].getClass().getName();
         if (checkClass.equals(clazz)) {
            return;
         }
      }

      Arrays.add(this._optionProviders, provider);
   }

   LocationServicesOptionsProvider$Factory(LocationServicesOptionsProvider$1 x0) {
      this();
   }
}
