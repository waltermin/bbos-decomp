package net.rim.device.apps.internal.ribbon.system;

import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.DialogClosedListener;

final class SystemOnOffManager$USBLowChargingDialogClosedListener implements DialogClosedListener {
   @Override
   public final void dialogClosed(Dialog dialog, int choice) {
      if (dialog == SystemOnOffManager._usbLowChargingDialog) {
         SystemOnOffManager.access$302(null);
      }
   }
}
