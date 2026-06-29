package net.rim.device.apps.internal.explorer.file.menu;

import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.apps.internal.explorer.file.bluetooth.SendHandler;
import net.rim.device.apps.internal.explorer.file.resource.ExplorerResources;
import net.rim.device.internal.bluetooth.BluetoothDeviceManager;

public final class SendBluetooth extends MenuItem {
   private String _file;

   public SendBluetooth(String file) {
      super(ExplorerResources.getResourceBundleFamily(), 16, 1573121, 0);
      this._file = file;
   }

   @Override
   public final void run() {
      if (BluetoothDeviceManager.getInstance().isRadioOnPromptIfOff(true)) {
         new SendHandler(this._file);
      } else {
         Dialog.alert(ExplorerResources.getString(134));
      }
   }
}
