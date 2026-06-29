package net.rim.device.apps.internal.bluetooth;

import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.Backlight;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.internal.bluetooth.BluetoothME;

final class BluetoothDevice$QuestionDialog extends Dialog {
   private int _type;
   private final BluetoothDevice this$0;
   static final int TYPE_AUTHORIZE;
   static final int TYPE_PAIRING_COMPLETE_CONNECT;
   static final int TYPE_PAIRING_FAILED_TRY_AGAIN;

   BluetoothDevice$QuestionDialog(BluetoothDevice _1, int type) {
      super(3, null, -1, Bitmap.getPredefinedBitmap(1), 33554432);
      this.this$0 = _1;
      this.setDontAskAgainPrompt(type == 0);
      this._type = type;
      int rc;
      switch (type) {
         case 0:
         default:
            rc = 14;
            break;
         case 1:
            rc = 85;
            break;
         case 2:
            rc = 86;
      }

      Object[] args = new Object[]{_1};
      String prompt = MessageFormat.format(BluetoothMainScreen.getString(rc), args);
      this.getLabel().setText(prompt);
      synchronized (Application.getEventLock()) {
         Ui.getUiEngine().pushGlobalScreen(this, -2147483646, 2);
      }

      if (!DeviceInfo.isInHolster()) {
         Backlight.enable(true);
      }
   }

   @Override
   public final void close() {
      boolean yes = this.getSelectedValue() == 4;
      switch (this._type) {
         case -1:
            break;
         case 0:
         default:
            BluetoothME.authorizeDevice(this.this$0._data._address, yes);
            if (this.isDontAskAgainChecked()) {
               this.this$0.setAuthorized(yes ? 1 : 2);
               this.this$0._btManager.commitDeviceData();
            }
            break;
         case 1:
            if (yes) {
               this.this$0.connect(true);
            }
            break;
         case 2:
            if (yes) {
               this.this$0.pairDevice();
            }
      }

      this.dismiss();
   }

   public final void disconnected() {
      if (this._type == 0) {
         this.dismiss();
      }
   }

   public final void dismiss() {
      synchronized (this.this$0) {
         this.this$0._questionDialog = null;
      }

      super.close();
   }
}
