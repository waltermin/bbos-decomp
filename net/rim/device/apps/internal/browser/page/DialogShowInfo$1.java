package net.rim.device.apps.internal.browser.page;

import net.rim.device.api.crypto.certificate.CertificateUtilities;
import net.rim.device.api.crypto.keystore.TrustedKeyStore;
import net.rim.device.api.ui.MenuItem;

class DialogShowInfo$1 extends MenuItem {
   private final DialogShowInfo this$0;

   DialogShowInfo$1(DialogShowInfo _1, String x0, int x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = _1;
   }

   @Override
   public void run() {
      CertificateUtilities.displayCertificateDetails(this.this$0._rimCertificate, TrustedKeyStore.getInstance());
   }
}
