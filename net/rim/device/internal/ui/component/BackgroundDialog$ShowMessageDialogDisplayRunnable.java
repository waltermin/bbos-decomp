package net.rim.device.internal.ui.component;

import net.rim.device.api.system.Bitmap;
import net.rim.device.internal.i18n.CommonResource;

class BackgroundDialog$ShowMessageDialogDisplayRunnable extends BackgroundDialog$DialogDisplayRunnable {
   private String _label;
   private int _priority;

   BackgroundDialog$ShowMessageDialogDisplayRunnable(String label, int priority) {
      this._label = label;
      this._priority = priority;
   }

   @Override
   public PopupDialog getDialog() {
      PopupDialog dialog = new SimpleChoiceDialog(this._label, CommonResource.getStringArray(10004), 0, Bitmap.getPredefinedBitmap(0), 134217728);
      dialog.setStatusPriority(this._priority);
      return dialog;
   }
}
