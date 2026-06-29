package net.rim.device.internal.ui.component;

class BackgroundDialog$ShowDialogDisplayRunnable extends BackgroundDialog$DialogDisplayRunnable {
   private PopupDialog _dialogToShow;

   BackgroundDialog$ShowDialogDisplayRunnable(PopupDialog dialogToShow) {
      this._dialogToShow = dialogToShow;
   }

   @Override
   public PopupDialog getDialog() {
      return this._dialogToShow;
   }
}
