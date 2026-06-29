package net.rim.device.apps.internal.explorer.file.menu;

import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.apps.internal.explorer.file.bluetooth.ServiceConnection;
import net.rim.device.apps.internal.explorer.file.resource.ExplorerResources;
import net.rim.device.internal.bluetooth.BluetoothDeviceManager;

public final class ReceiveBluetooth extends MenuItem {
   public ReceiveBluetooth() {
      super(ExplorerResources.getResourceBundleFamily(), 17, 1573122, 0);
   }

   @Override
   public final void run() {
      if (BluetoothDeviceManager.getInstance().isRadioOnPromptIfOff(true)) {
         new Thread(new ServiceConnection()).start();
      } else {
         Dialog.alert(ExplorerResources.getString(134));
      }
   }
}
