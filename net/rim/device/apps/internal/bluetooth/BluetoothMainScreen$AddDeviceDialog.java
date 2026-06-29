package net.rim.device.apps.internal.bluetooth;

import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.GaugeField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.component.Status;
import net.rim.device.api.ui.container.DialogFieldManager;
import net.rim.device.api.ui.container.PopupScreen;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.internal.bluetooth.BluetoothME;
import net.rim.device.internal.ui.component.ImageField;

final class BluetoothMainScreen$AddDeviceDialog extends PopupScreen implements Runnable {
   private GaugeField _gaugeField;
   private int _gaugeCount;
   private int _gaugeLength;
   private int _gaugeInterval;
   private int _deviceCount;
   private boolean _inquiry;
   private final BluetoothMainScreen this$0;
   private static final int INQUIRY_LENGTH;
   private static final int INQUIRY_INTERVAL;
   private static final int TIMEOUT_LENGTH;
   private static final int TIMEOUT_INTERVAL;

   final void start() {
      if (!this.this$0._isPowerOn) {
         if (this.this$0._btManager.setPowerOn(true, true)) {
            this.this$0._turnPowerOffOnExit = true;
            if (this.this$0._pleaseWaitDialog == null) {
               this.this$0._pleaseWaitDialog = new BluetoothMainScreen$PleaseWaitDialog(this.this$0, 82);
               this.this$0._app.pushScreen(this.this$0._pleaseWaitDialog);
            }
         }
      } else {
         if (this._inquiry) {
            if (!this.this$0._btManager.startInquiry(10390323)) {
               Status.show(BluetoothMainScreen.getString(50), this.this$0._btManager.getDialogImage(), 3000, 0, true, false, 0);
               return;
            }
         } else {
            BluetoothME.setDiscoverable(true);
            BluetoothME.enableSecurityMode3(false, false);
         }

         this.this$0._app.pushScreen(this);
         this.this$0._app.invokeLater(this, this._gaugeInterval, false);
      }
   }

   final void deviceAdded() {
      this._deviceCount++;
      Object[] args = new Object[]{Integer.toString(this._deviceCount)};
      String label = MessageFormat.format(BluetoothMainScreen.getString(84), args);
      this._gaugeField.setLabel(label);
   }

   @Override
   public final void run() {
      if (this.isDisplayed()) {
         this._gaugeField.setValue(++this._gaugeCount);
         if (this._gaugeCount < this._gaugeLength) {
            this.this$0._app.invokeLater(this, this._gaugeInterval, false);
            return;
         }

         this.stop();
      }
   }

   private final void stop() {
      if (this._inquiry) {
         this.this$0._btManager.cancelInquiry();
      } else {
         this.close();
         if (Dialog.ask(3, BluetoothMainScreen.getString(97)) == 4) {
            this.this$0._addDeviceDialog = new BluetoothMainScreen$AddDeviceDialog(this.this$0, false);
            this.this$0._addDeviceDialog.start();
         }
      }
   }

   @Override
   protected final boolean keyChar(char key, int status, int time) {
      if (key != 27 && key != '\n' && key != ' ') {
         return super.keyChar(key, status, time);
      }

      this.stop();
      return true;
   }

   @Override
   protected final boolean trackwheelClick(int status, int time) {
      super.trackwheelClick(status, time);
      this.stop();
      return true;
   }

   @Override
   protected final boolean navigationClick(int status, int time) {
      return this.trackwheelClick(status, time);
   }

   @Override
   public final void close() {
      if (!this._inquiry) {
         this.this$0._btManager.updateSecurityMode();
         this.this$0._btManager.updateDiscoverableMode();
      }

      this.this$0._addDeviceDialog = null;
      super.close();
   }

   BluetoothMainScreen$AddDeviceDialog(BluetoothMainScreen _1, boolean inquiry) {
      super((Manager)(new Object()));
      this.this$0 = _1;
      this._inquiry = inquiry;
      DialogFieldManager dfm = (DialogFieldManager)this.getDelegate();
      dfm.setMessage((RichTextField)(new Object(BluetoothMainScreen.getString(inquiry ? 5 : 96), 36028797018963968L)));
      int fontHeight = dfm.getFont().getHeight();
      if (Ui.convertSize(fontHeight, 0, 2) < 12) {
         ImageField imageField = (ImageField)(new Object());
         imageField.setImage(_1._btManager.getDialogImage());
         dfm.setIcon(imageField);
      }

      if (inquiry) {
         this._gaugeLength = 90;
         this._gaugeInterval = 100;
      } else {
         this._gaugeLength = 120;
         this._gaugeInterval = 1000;
      }

      this._gaugeField = (GaugeField)(new Object("", 0, this._gaugeLength, 0, inquiry ? 65536 : 2));
      dfm.addCustomField(this._gaugeField);
      dfm.addCustomField((Field)(new Object(5)));
      dfm.addCustomField((Field)(new Object(CommonResources.getString(inquiry ? 9132 : 9042), 12884901888L)));
   }
}
