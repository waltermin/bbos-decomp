package net.rim.device.apps.internal.bluetooth;

import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.system.Application;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.UiEngine;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.DialogFieldManager;
import net.rim.device.api.ui.container.PopupScreen;
import net.rim.device.internal.ui.component.ImageField;

final class BluetoothDevice$StatusDialog extends PopupScreen implements Runnable {
   private Application _app;
   private UiEngine _uiEngine;
   private RichTextField _rtf;
   private String _newPrompt;
   private boolean _autoDismiss;
   private final BluetoothDevice this$0;

   BluetoothDevice$StatusDialog(BluetoothDevice _1) {
      this(_1, null);
   }

   BluetoothDevice$StatusDialog(BluetoothDevice _1, String prompt) {
      this(_1, prompt, false, false);
   }

   BluetoothDevice$StatusDialog(BluetoothDevice _1, String prompt, boolean autoDismiss, boolean showConnectionStatus) {
      super(new DialogFieldManager());
      this.this$0 = _1;
      this._app = Application.getApplication();
      this._uiEngine = Ui.getUiEngine();
      DialogFieldManager dfm = (DialogFieldManager)this.getDelegate();
      prompt = this.setupPrompt(prompt, showConnectionStatus);
      this._rtf = new RichTextField(prompt, 36028797018963968L);
      this._autoDismiss = autoDismiss;
      dfm.setMessage(this._rtf);
      ImageField imageField = new ImageField();
      imageField.setImage(_1._btManager.getDialogImage());
      dfm.setIcon(imageField);
      synchronized (this._app.getAppEventLock()) {
         this._uiEngine.pushGlobalScreen(this, -2147483643, 2);
      }

      if (this._autoDismiss) {
         this._app.invokeLater(this, 4000, false);
      }
   }

   private final String setupPrompt(String prompt, boolean showConnectionStatus) {
      if (showConnectionStatus) {
         synchronized (this._app.getAppEventLock()) {
            BluetoothProfileManager[] managers = this.this$0._btManager.getProfileManagers();
            DialogFieldManager dfm = (DialogFieldManager)this.getDelegate();
            dfm.deleteCustomFields();
            dfm.addCustomField(new SeparatorField());
            int numManagers = managers.length;

            for (int i = 0; i < numManagers; i++) {
               if (managers[i].isConnected(this.this$0)) {
                  Field f = managers[i].getStateField(this.this$0);
                  dfm.addCustomField(f);
               }
            }
         }
      }

      if (prompt == null) {
         Object[] args = new Object[]{this.this$0};
         return MessageFormat.format(BluetoothMainScreen.getString(41), args);
      } else {
         return prompt;
      }
   }

   final void setPrompt(String prompt, boolean autoDismiss, boolean showConnectionStatus) {
      this._newPrompt = this.setupPrompt(prompt, showConnectionStatus);
      this._autoDismiss = autoDismiss;
      this._app.invokeLater(this);
      if (this._autoDismiss) {
         this._app.invokeLater(this, 4000, false);
      }
   }

   final void dismiss() {
      synchronized (this.this$0) {
         if (this.this$0._statusDialog == this) {
            this.this$0._statusDialog = null;
         }
      }

      this._newPrompt = null;
      this._app.invokeLater(this);
   }

   @Override
   protected final boolean keyChar(char c, int status, int time) {
      if (!super.keyChar(c, status, time)) {
         this.dismiss();
         return true;
      } else {
         return false;
      }
   }

   @Override
   protected final boolean trackwheelClick(int status, int time) {
      if (!super.trackwheelClick(status, time)) {
         this.dismiss();
         return true;
      } else {
         return false;
      }
   }

   @Override
   public final void run() {
      if (this._newPrompt == null) {
         if (this.isDisplayed()) {
            synchronized (this.this$0) {
               if (this.this$0._statusDialog == this) {
                  this.this$0._statusDialog = null;
               }
            }

            this._uiEngine.popScreen(this);
            return;
         }
      } else {
         this._rtf.setText(this._newPrompt);
         this._newPrompt = null;
      }
   }
}
