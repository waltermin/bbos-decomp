package net.rim.device.apps.internal.bluetooth;

import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.UiEngine;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.DialogClosedListener;
import net.rim.device.api.ui.component.Status;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.internal.bluetooth.BluetoothME;
import net.rim.device.internal.ui.Image;

class BluetoothDeviceManagerImpl$RadioOnPromptRunnable implements Runnable, DialogClosedListener {
   boolean _result;
   String _applicationName;
   private final BluetoothDeviceManagerImpl this$0;

   boolean getResult() {
      return this._result;
   }

   public void processAnswer(Dialog dialog, int choice) {
      if (choice == 4) {
         Status.show(BluetoothMainScreen.getString(82), (Image)((Object)null), 2000, 33554432, false, false, 0);
         synchronized (this.this$0._radioOnLock) {
            this._result = this.this$0.requestPowerOn();
            if (this._result) {
               label33:
               try {
                  this.this$0._radioOnLock.wait(60000);
               } finally {
                  break label33;
               }

               this._result = BluetoothME.isPowerOn();
            }
         }
      } else {
         this._result = false;
      }
   }

   public void run(boolean modal) {
      Object[] args = new Object[]{this._applicationName};
      String prompt = MessageFormat.format(BluetoothMainScreen.getString(81), args);
      Dialog dialog = (Dialog)(new Object(3, prompt, 4, null, 0));
      dialog.setIcon(ThemeManager.getThemeAwareImage("dialog_question"));
      int dialogFlag = modal ? 1 : 2;
      dialog.setDialogClosedListener(this);
      UiEngine uiEngine = Ui.getUiEngine();
      uiEngine.pushGlobalScreen(dialog, 0, dialogFlag);
   }

   @Override
   public synchronized void dialogClosed(Dialog dialog, int choice) {
      this.processAnswer(dialog, choice);
      this.notify();
   }

   @Override
   public void run() {
      this.run(false);
   }

   BluetoothDeviceManagerImpl$RadioOnPromptRunnable(BluetoothDeviceManagerImpl _1, String applicationName) {
      this.this$0 = _1;
      this._result = false;
      this._applicationName = applicationName;
   }
}
