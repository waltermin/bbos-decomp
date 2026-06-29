package net.rim.device.apps.internal.bluetooth;

import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.ui.theme.Theme;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.apps.api.ribbon.Action;

final class BluetoothSetupAction extends Action {
   private ApplicationDescriptor _descriptor;

   BluetoothSetupAction() {
      super(null, "net_rim_Bluetooth.setup", 230);
      this.updateSetupAction();
   }

   @Override
   public final Boolean get(long propID, Boolean defaultReturned) {
      return propID == 7 ? Boolean.TRUE : defaultReturned;
   }

   @Override
   protected final String getDescription() {
      return BluetoothMainScreen.getString(73);
   }

   protected final void updateSetupAction() {
      Theme theme = ThemeManager.getActiveTheme();
      Bitmap icon = null;

      label20:
      try {
         EncodedImage image = theme.getImage("net_rim_Bluetooth.setup");
         icon = image.getBitmap();
      } finally {
         break label20;
      }

      ApplicationDescriptor original = ApplicationDescriptor.currentApplicationDescriptor();
      this._descriptor = (ApplicationDescriptor)(new Object(
         original, "setup", new String[]{"btsetup"}, icon, -1, "net.rim.device.apps.internal.resource.Bluetooth", 73
      ));
   }

   @Override
   public final void run() {
      try {
         ApplicationManager.getApplicationManager().runApplication(this._descriptor);
      } finally {
         return;
      }
   }
}
