package net.rim.device.apps.internal.browser.stack;

import net.rim.device.internal.ui.component.UsernamePasswordDialog;

final class UPDRunnable implements Runnable {
   UsernamePasswordDialog _upd;

   public UPDRunnable(UsernamePasswordDialog upd) {
      this._upd = upd;
   }

   @Override
   public final void run() {
      this._upd.show();
   }
}
