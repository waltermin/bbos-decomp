package net.rim.device.apps.internal.idlescreen;

import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.Phone;
import net.rim.device.api.system.RealtimeClockListener;
import net.rim.device.internal.proxy.Proxy;
import net.rim.device.internal.system.ITPolicyInternal;

final class IdleScreenApplication$MyRealtimeClockListener implements RealtimeClockListener {
   final void start() {
      Proxy proxy = Proxy.getInstance();
      if (proxy == null) {
         throw new RuntimeException();
      }

      proxy.addRealtimeClockListener(this);
   }

   final void stop() {
      Proxy proxy = Proxy.getInstance();
      if (proxy == null) {
         throw new RuntimeException();
      }

      proxy.removeRealtimeClockListener(this);
   }

   @Override
   public final void clockUpdated() {
      int timeout = IdleScreenOptions.getTimeout();
      if (timeout > 0) {
         if (!ITPolicyInternal.policyHasChanged()) {
            if (DeviceInfo.getIdleTime() >= timeout && this.isPhoneOff()) {
               IdleScreenApplication.show(false);
            }
         }
      }
   }

   private final boolean isPhoneOff() {
      return !Phone.isSupported() || !Phone.getInstance().isActive();
   }
}
