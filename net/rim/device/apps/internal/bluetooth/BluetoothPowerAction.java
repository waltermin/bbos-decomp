package net.rim.device.apps.internal.bluetooth;

import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.Bitmap;
import net.rim.device.apps.api.ribbon.Action;
import net.rim.device.internal.bluetooth.BluetoothME;

final class BluetoothPowerAction extends Action {
   private Bitmap _onIcon = Bitmap.getBitmapResource("net_rim_bluetooth_dialog.png");
   private Bitmap _offIcon = Bitmap.getBitmapResource("net_rim_bluetooth_dialog.png");
   private ApplicationDescriptor _descriptor;

   BluetoothPowerAction() {
      super(null, "net_rim_Bluetooth", 231);
      ApplicationDescriptor original = ApplicationDescriptor.currentApplicationDescriptor();
      this._descriptor = new ApplicationDescriptor(original, new String[]{"btonoff"});
   }

   @Override
   public final Object get(long propID, Object defaultReturned) {
      if (propID == 5) {
         return BluetoothME.isPowerOn() ? this._offIcon : this._onIcon;
      } else {
         return defaultReturned;
      }
   }

   @Override
   public final Boolean get(long propID, Boolean defaultReturned) {
      return propID == 7 ? Boolean.TRUE : defaultReturned;
   }

   @Override
   protected final String getDescription() {
      int rc;
      if (BluetoothME.isPowerOn()) {
         rc = 48;
      } else {
         rc = 47;
      }

      return BluetoothMainScreen.getString(rc);
   }

   @Override
   protected final String getState() {
      return BluetoothME.isPowerOn() ? "on" : "off";
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
