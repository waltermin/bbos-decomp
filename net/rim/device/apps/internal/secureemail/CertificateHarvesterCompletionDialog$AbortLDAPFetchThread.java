package net.rim.device.apps.internal.secureemail;

import net.rim.device.api.crypto.certificate.LDAPCertificateFetch;

class CertificateHarvesterCompletionDialog$AbortLDAPFetchThread extends Thread {
   LDAPCertificateFetch _ldapCertificateFetch;

   CertificateHarvesterCompletionDialog$AbortLDAPFetchThread(LDAPCertificateFetch ldapCertificateFetch) {
      this._ldapCertificateFetch = ldapCertificateFetch;
   }

   @Override
   public void run() {
      this._ldapCertificateFetch.abortFetch();
   }
}
