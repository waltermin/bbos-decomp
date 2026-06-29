package net.rim.device.apps.internal.options.items;

import net.rim.device.api.ui.component.Status;

final class LocationServicesOptionsItem$StatusRunnable implements Runnable {
   private String _msg;

   public LocationServicesOptionsItem$StatusRunnable(String msg) {
      this._msg = msg;
   }

   @Override
   public final void run() {
      Status.show(this._msg);
   }
}
