package net.rim.device.apps.internal.options.items;

import java.util.Enumeration;
import net.rim.device.api.system.Application;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.apps.api.location.LocationServicesOptionsProvider;

final class LocationServicesOptionsItem$1 implements Runnable {
   private final LocationServicesOptionsItem this$0;

   LocationServicesOptionsItem$1(LocationServicesOptionsItem _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      synchronized (Application.getEventLock()) {
         Dialog.alert(this.this$0._initialMsg);
      }

      Enumeration e = LocationServicesOptionsProvider.getLocationServicesOptionsProviders();

      while (e.hasMoreElements()) {
         LocationServicesOptionsProvider locationProvider = (LocationServicesOptionsProvider)e.nextElement();
         locationProvider.activationStatus();
      }
   }
}
