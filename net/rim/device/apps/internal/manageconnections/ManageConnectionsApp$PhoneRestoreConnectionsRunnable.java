package net.rim.device.apps.internal.manageconnections;

import net.rim.device.api.system.Phone;
import net.rim.device.apps.internal.phone.api.PhoneUtilities;
import net.rim.device.internal.bluetooth.BluetoothDeviceManager;
import net.rim.device.internal.system.RadioInternal;
import net.rim.vm.PersistentInteger;

public final class ManageConnectionsApp$PhoneRestoreConnectionsRunnable implements Runnable {
   @Override
   public final void run() {
      int restoreStateHandle = PersistentInteger.getId(-8312528208360996369L, 0);
      int restoreState = PersistentInteger.get(restoreStateHandle);
      PersistentInteger.set(restoreStateHandle, 0);
      int newState = 0;
      boolean cellRequired = true;
      boolean wifiRequired = true;
      int requiredWafs = Phone.getInstance().getWAFs(PhoneUtilities.getCurrentLineId());
      if ((ManageConnectionsApp.CELL_WAFS & requiredWafs) == 0) {
         cellRequired = false;
      }

      if ((4 & requiredWafs) == 0) {
         wifiRequired = false;
      }

      if (cellRequired || (restoreState & 2) != 0) {
         if (!RadioInternal.activateRadios(ManageConnectionsApp.CELL_RADIOS)) {
            ManageConnectionsApp.informUser(32);
         } else {
            newState |= 2;
         }
      }

      if (wifiRequired || (restoreState & 4) != 0) {
         if (!RadioInternal.activateRadios(4)) {
            ManageConnectionsApp.informUser(33);
         } else {
            newState |= 4;
         }
      }

      if ((restoreState & 8) != 0) {
         BluetoothDeviceManager.getInstance().setPowerOn(true);
         newState |= 8;
      }

      if (newState == 0) {
         restoreState |= 1;
         PersistentInteger.set(restoreStateHandle, restoreState);
      } else {
         PersistentInteger.set(restoreStateHandle, newState);
      }
   }
}
