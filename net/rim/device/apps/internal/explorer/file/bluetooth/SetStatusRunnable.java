package net.rim.device.apps.internal.explorer.file.bluetooth;

import net.rim.device.api.system.Application;
import net.rim.device.api.ui.component.LabelField;

final class SetStatusRunnable implements Runnable {
   private LabelField _labelField;
   private String _statusString;

   SetStatusRunnable(LabelField labelField) {
      this._labelField = labelField;
   }

   public final void setStatus(String text) {
      this._statusString = text;
      Application.getApplication().invokeAndWait(this);
   }

   @Override
   public final void run() {
      this._labelField.setText(this._statusString);
   }
}
