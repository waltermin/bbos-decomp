package net.rim.device.internal.ui.component;

class SimplePasswordDialog$SimplePasswordDialogDismisser implements Runnable {
   private SimplePasswordDialog _dialog;

   public SimplePasswordDialog$SimplePasswordDialogDismisser(SimplePasswordDialog dialog) {
      this._dialog = dialog;
   }

   @Override
   public void run() {
      this._dialog.cancel();
   }
}
