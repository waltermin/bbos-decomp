package net.rim.device.apps.internal.phone.api.ui.cdma;

import net.rim.device.api.system.Branding;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.ui.UiApplication;

final class ServiceProgramUI$1 implements Runnable {
   private final ServiceProgramUI this$0;

   ServiceProgramUI$1(ServiceProgramUI _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      if (RadioInfo.areWAFsSupported(2)) {
         switch (Branding.getVendorId()) {
            case 1:
            case 104:
            case 213:
            case 225:
               UiApplication.getUiApplication().pushScreen(new IOTAEditScreen());
         }
      }
   }
}
