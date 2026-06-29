package net.rim.device.apps.internal.iota;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;

final class IOTADialog implements Runnable {
   private String _msg;

   public IOTADialog(String msg) {
      this._msg = msg;
   }

   @Override
   public final void run() {
      Dialog dlg = (Dialog)(new Object(0, this._msg, 0, Bitmap.getPredefinedBitmap(2), 33554432));
      dlg.show();
   }

   public static final void show(String msg) {
      UiApplication.getUiApplication().invokeLater(new IOTADialog(msg));
   }
}
