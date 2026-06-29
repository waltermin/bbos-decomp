package net.rim.device.apps.internal.phone.api.ui.cdma;

import net.rim.device.api.ui.component.Status;
import net.rim.device.internal.system.InternalServices;

final class ServiceProgramUI$ResetDialog implements Runnable {
   @Override
   public final void run() {
      Status.show("Write Successful. Resetting device");
      InternalServices.initiateReset("CDMASrvPgm");
   }
}
