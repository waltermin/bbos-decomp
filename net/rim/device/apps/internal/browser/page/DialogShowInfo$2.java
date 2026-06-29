package net.rim.device.apps.internal.browser.page;

import javax.microedition.pki.Certificate;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.cldc.io.ssl.CertificateDisplayDialog;

class DialogShowInfo$2 extends MenuItem {
   private final DialogShowInfo this$0;

   DialogShowInfo$2(DialogShowInfo _1, String x0, int x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = _1;
   }

   @Override
   public void run() {
      CertificateDisplayDialog dialog = new CertificateDisplayDialog(new Certificate[]{this.this$0._javaCertificate}, 0);
      dialog.show();
   }
}
