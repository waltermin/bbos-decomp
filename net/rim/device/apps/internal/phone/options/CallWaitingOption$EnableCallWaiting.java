package net.rim.device.apps.internal.phone.options;

import net.rim.device.api.system.Branding;
import net.rim.device.api.system.Phone;
import net.rim.device.apps.internal.phone.api.PhoneUtilities;

final class CallWaitingOption$EnableCallWaiting extends SSRequest {
   private boolean _enable;

   CallWaitingOption$EnableCallWaiting(boolean enable) {
      super(PhoneUtilities.cdmaWAFActive() && Branding.getVendorId() != 122);
      this._enable = enable;
   }

   @Override
   protected final void runTask() {
      label17:
      try {
         Phone.getInstance().activateCallWaiting(this._enable);
      } finally {
         break label17;
      }

      this.waitForNetworkResponse();
   }
}
