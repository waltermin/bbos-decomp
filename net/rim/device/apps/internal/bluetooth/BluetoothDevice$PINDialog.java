package net.rim.device.apps.internal.bluetooth;

import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.Backlight;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.ui.Ui;
import net.rim.device.internal.bluetooth.BluetoothME;
import net.rim.device.internal.ui.component.SimplePasswordDialog;

final class BluetoothDevice$PINDialog extends SimplePasswordDialog {
   private boolean _numeric;
   private boolean _isSmartcardReader;
   private final BluetoothDevice this$0;
   private static final int BDS_DISCONNECTED = 0;
   private static final int BDS_OUT_CON = 1;
   private static final int BDS_IN_CON = 2;
   private static final int BDS_CONNECTED = 3;
   private static final int BDS_OUT_DISC = 4;
   private static final int BDS_OUT_DISC2 = 5;
   private static final int BDS_OUT_CON2 = 6;

   BluetoothDevice$PINDialog(BluetoothDevice _1) {
      super(null, 1, 16, true, 33554432);
      this.this$0 = _1;
      String deviceName = _1.toString();
      if (deviceName != null && deviceName.startsWith("Smart Card Reader:")) {
         this._isSmartcardReader = true;
      }

      this._numeric = true;
      this.setPrompt();
      this.setIcon(_1._btManager.getDialogImage());
      synchronized (Application.getEventLock()) {
         switch (BluetoothME.getDeviceState(_1.getAddress())) {
            case 0:
            case 2:
            case 3:
               Ui.getUiEngine().pushGlobalScreen(this, -1073741823, 2);
               break;
            case 1:
            case 4:
            case 5:
            case 6:
            default:
               Ui.getUiEngine().pushGlobalScreen(this, -2147483646, 2);
         }
      }

      if (!DeviceInfo.isInHolster()) {
         Backlight.enable(true);
      }
   }

   private final void setPrompt() {
      String prompt;
      if (this._isSmartcardReader) {
         prompt = BluetoothMainScreen.getString(63);
      } else {
         int id = this._numeric ? 7 : 69;
         String deviceName = this.this$0.toString();
         prompt = MessageFormat.format(BluetoothMainScreen.getString(id), new String[]{deviceName});
      }

      this.setPrompt(prompt);
   }

   @Override
   protected final void close(int closeReason) {
      synchronized (this.this$0) {
         this.dismiss();
         this.this$0._waitingForPINRequest = false;
         if (closeReason == -1) {
            BluetoothME.sendPIN(this.this$0._data._address, null);
            this.this$0._waitingForConnection = 0;
         } else {
            int rc = BluetoothME.sendPIN(this.this$0._data._address, this.getText().getBytes());
            if (rc != 0 && rc != 2) {
               this.this$0.connectError();
            } else {
               this.this$0._waitingForPINResponse = true;
               this.this$0.notifyOperationStart();
            }
         }
      }
   }

   public final void dismiss() {
      synchronized (this.this$0) {
         this.this$0._pinDialog = null;
      }

      super.close(0);
   }

   @Override
   public final boolean accept() {
      if (!super.accept()) {
         if (!this._isSmartcardReader) {
            this._numeric = !this._numeric;
            this.setPrompt();
            this.setNumeric(this._numeric);
         }

         return false;
      } else {
         return true;
      }
   }
}
