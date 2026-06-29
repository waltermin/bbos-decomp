package net.rim.device.internal.ui;

import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.DialogClosedListener;

public class DialogListenerImpl implements DialogClosedListener {
   private Object _sync;
   private Dialog _dialog;

   public DialogListenerImpl(Dialog d, Object syncobject) {
      this._sync = syncobject;
      this._dialog = d;
   }

   @Override
   public void dialogClosed(Dialog d, int choice) {
      synchronized (this._sync) {
         if (d == this._dialog) {
            this._sync.notifyAll();
         }
      }
   }
}
