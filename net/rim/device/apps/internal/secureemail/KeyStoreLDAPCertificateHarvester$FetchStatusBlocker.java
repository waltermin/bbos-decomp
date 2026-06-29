package net.rim.device.apps.internal.secureemail;

import net.rim.device.api.crypto.certificate.status.CertificateStatusQuery;
import net.rim.device.api.crypto.certificate.status.QueryProgressListener;

final class KeyStoreLDAPCertificateHarvester$FetchStatusBlocker implements QueryProgressListener {
   boolean _queryAborted;

   private KeyStoreLDAPCertificateHarvester$FetchStatusBlocker() {
   }

   @Override
   public final synchronized void updateProgress(CertificateStatusQuery query, int progress) {
      switch (progress) {
         case 9:
         default:
            this._queryAborted = true;
         case 6:
         case 7:
         case 8:
            this.notifyAll();
         case 5:
      }
   }

   KeyStoreLDAPCertificateHarvester$FetchStatusBlocker(KeyStoreLDAPCertificateHarvester$1 x0) {
      this();
   }
}
