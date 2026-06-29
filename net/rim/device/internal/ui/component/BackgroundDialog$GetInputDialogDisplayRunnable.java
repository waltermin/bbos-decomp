package net.rim.device.internal.ui.component;

class BackgroundDialog$GetInputDialogDisplayRunnable extends BackgroundDialog$DialogDisplayRunnable {
   private String _label;
   private int _minLength;
   private int _maxLength;
   private int _type;

   BackgroundDialog$GetInputDialogDisplayRunnable(String label, int minLength, int maxLength, int type) {
      this._label = label;
      this._minLength = minLength;
      this._maxLength = maxLength;
      this._type = type;
   }

   @Override
   public PopupDialog getDialog() {
      return new SimpleOKCancelInputDialog(this._type, this._label, this._minLength, this._maxLength, 134217728);
   }
}
