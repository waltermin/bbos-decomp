package net.rim.device.internal.ui;

import net.rim.device.api.system.Application;
import net.rim.device.internal.ui.component.PleaseWaitWorkerThread;
import net.rim.device.internal.ui.component.PopupDialog;
import net.rim.device.internal.ui.component.PopupDialogClosedListener;

public class PopupDialogWorkerThread extends PleaseWaitWorkerThread implements PopupDialogClosedListener {
   PopupDialog _popupDialog;
   boolean _popupDialogDisplayed;

   public PopupDialogWorkerThread(PopupDialog popupDialog) {
      if (popupDialog == null) {
         throw new IllegalArgumentException();
      }

      this._popupDialog = popupDialog;
   }

   @Override
   public void doWork() {
      this._popupDialogDisplayed = true;
      this._popupDialog.setPopupDialogClosedListener(this);
      synchronized (Application.getApplication().getAppEventLock()) {
         this._popupDialog.show();
      }

      synchronized (this) {
         while (this._popupDialogDisplayed) {
            try {
               this.wait();
            } catch (InterruptedException var4) {
            }
         }
      }
   }

   @Override
   public void dialogClosed(PopupDialog popupDialog, int choice) {
      if (popupDialog == this._popupDialog) {
         synchronized (this) {
            this._popupDialogDisplayed = false;
            this.notifyAll();
         }
      }
   }
}
