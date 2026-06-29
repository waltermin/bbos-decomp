package net.rim.device.apps.internal.phone.api.ui.cdma;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.component.Dialog;

final class CDMAServiceFilter$1 implements Runnable {
   private final CDMAServiceFilter this$0;

   CDMAServiceFilter$1(CDMAServiceFilter _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      ResourceBundle srb = ResourceBundle.getBundle(-1488627819050031640L, "net.rim.device.apps.internal.resource.Security");
      Dialog dlg = (Dialog)(new Object(srb.getString(704), null, null, 0, Bitmap.getPredefinedBitmap(2)));
      dlg.setEscapeEnabled(false);
      dlg.show();
   }
}
