package net.rim.device.apps.api.ui;

import net.rim.device.api.ui.component.LabelField;

final class ProgressIndicator$SetStatusStringRunnable implements Runnable {
   private LabelField _labelField;
   private String _statusString;

   ProgressIndicator$SetStatusStringRunnable(LabelField labelField, String statusString) {
      this._labelField = labelField;
      this._statusString = statusString;
   }

   @Override
   public final void run() {
      this._labelField.setText(this._statusString);
   }
}
