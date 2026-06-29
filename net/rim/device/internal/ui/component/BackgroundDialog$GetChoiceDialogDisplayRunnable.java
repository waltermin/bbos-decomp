package net.rim.device.internal.ui.component;

import net.rim.device.api.system.Bitmap;

class BackgroundDialog$GetChoiceDialogDisplayRunnable extends BackgroundDialog$DialogDisplayRunnable {
   private String _label;
   private Object[] _choices;
   private int _defaultChoice;
   private Bitmap _bitmap;
   private int _priority;

   BackgroundDialog$GetChoiceDialogDisplayRunnable(String label, Object[] choices, int defaultChoice, Bitmap bitmap, int priority) {
      this._label = label;
      this._choices = choices;
      this._defaultChoice = defaultChoice;
      this._bitmap = bitmap;
      this._priority = priority;
   }

   @Override
   public PopupDialog getDialog() {
      PopupDialog dialog = new SimpleChoiceDialog(this._label, this._choices, this._defaultChoice, this._bitmap, 134217728);
      dialog.setStatusPriority(this._priority);
      return dialog;
   }
}
