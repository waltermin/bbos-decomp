package net.rim.device.apps.internal.phone;

import net.rim.device.api.system.Application;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.internal.ui.component.PopupDialog;

final class CallControlInformationDialog extends Dialog implements Runnable {
   private boolean _isClosed;
   private PopupDialog _nextDialog;

   public CallControlInformationDialog(String message) {
      super(0, message, 0, null, 0);
      this.setIcon(ThemeManager.getThemeAwareImage("dialog_information"));
   }

   public final void showOnCompletion(PopupDialog dialog) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public final void setTimeout(int timeout) {
      Application.getApplication().invokeLater(this, timeout, false);
   }

   @Override
   public final void run() {
      this.close();
   }

   @Override
   public final synchronized void close() {
      if (!this._isClosed) {
         this._isClosed = true;
         super.close();
         if (this._nextDialog != null) {
            this._nextDialog.show();
         }
      }
   }
}
