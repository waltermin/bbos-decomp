package net.rim.device.apps.internal.options.items;

import net.rim.device.api.ui.component.Dialog;
import net.rim.device.apps.internal.options.resources.OptionsResources;

final class LocationServicesOptionsItem$GPSLocationVerb$1 implements Runnable {
   private final LocationServicesOptionsItem$GPSLocationVerb this$1;

   LocationServicesOptionsItem$GPSLocationVerb$1(LocationServicesOptionsItem$GPSLocationVerb _1) {
      this.this$1 = _1;
   }

   @Override
   public final void run() {
      Dialog.alert(OptionsResources.getString(1920));
   }
}
