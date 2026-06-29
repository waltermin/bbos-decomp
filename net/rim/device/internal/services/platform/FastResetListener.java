package net.rim.device.internal.services.platform;

import net.rim.device.api.system.AudioRouter;
import net.rim.device.api.system.SystemListener2;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.internal.deviceoptions.DeviceOptions;
import net.rim.device.internal.system.ITPolicyInternal;
import net.rim.device.internal.ui.Initialization;

final class FastResetListener implements SystemListener2 {
   @Override
   public final void powerOff() {
   }

   @Override
   public final void powerUp() {
   }

   @Override
   public final void batteryLow() {
   }

   @Override
   public final void batteryGood() {
   }

   @Override
   public final void batteryStatusChange(int status) {
   }

   @Override
   public final void powerOffRequested(int reason) {
   }

   @Override
   public final void cradleMismatch(boolean mismatch) {
   }

   @Override
   public final void backlightStateChange(boolean on) {
   }

   @Override
   public final void usbConnectionStateChange(int state) {
   }

   @Override
   public final void fastReset() {
      Initialization.UIMain();
      DeviceOptions.init();
      ITPolicyInternal.initialize();
      ThemeManager.getActiveTheme().initializeFastReset();
      AudioRouter.getInstance().fastReset();
   }
}
