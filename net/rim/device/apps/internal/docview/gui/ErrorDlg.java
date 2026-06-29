package net.rim.device.apps.internal.docview.gui;

import net.rim.device.api.ui.component.Dialog;

final class ErrorDlg implements Runnable {
   private String _error;

   ErrorDlg(String strErrorMessage) {
      this._error = strErrorMessage;
   }

   @Override
   public final void run() {
      Dialog.alert(this._error);
   }
}
