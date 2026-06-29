package net.rim.device.api.crypto.certificate;

import java.util.Enumeration;
import net.rim.device.api.crypto.keystore.KeyStoreOptions;
import net.rim.device.api.ldap.LDAPEntry;
import net.rim.device.api.ldap.LDAPQuery;

public class LDAPCertificateFetch {
   protected LDAPQuery _query;
   protected Object _queryLock = new Object();
   protected boolean _fetchAborted;
   protected int _ldapErrorCode;

   public void abortFetch() {
      synchronized (this._queryLock) {
         this._fetchAborted = true;
         if (this._query != null) {
            this._query.abort();
         }
      }
   }

   public boolean fetchAborted() {
      return this._fetchAborted;
   }

   public int getLDAPErrorCode() {
      return this._ldapErrorCode;
   }

   public Certificate[] fetchCertificates(String emailAddress, CertificateServerInfo[] info) {
      String[] emailAddresses = new Object[]{emailAddress};
      CertificateUtilities.canonicalizeEmailAddresses(emailAddresses);
      boolean atLeastOneSuccessfulQuery = false;
      this._ldapErrorCode = 0;
      Certificate[] certificates = new Object[0];
      int numServers = info == null ? 1 : info.length;

      for (int i = 0; i < numServers; i++) {
         try {
            synchronized (this._queryLock) {
               if (this._fetchAborted) {
                  break;
               }

               this._query = this.createBaseQuery(info == null ? null : info[i]);
               this.addAttributesAndFilters(this._query, emailAddresses);
               this._query.start();
            }

            atLeastOneSuccessfulQuery |= this.processResults(certificates, atLeastOneSuccessfulQuery);
         } finally {
            continue;
         }
      }

      return certificates;
   }

   public Certificate[] fetchCertificates(Object keyID, CertificateServerInfo[] info) {
      Object[] keyIDs = new Object[]{keyID};
      boolean atLeastOneSuccessfulQuery = false;
      this._ldapErrorCode = 0;
      Certificate[] certificates = new Object[0];
      int numServers = info == null ? 1 : info.length;

      for (int i = 0; i < numServers; i++) {
         try {
            synchronized (this._queryLock) {
               if (this._fetchAborted) {
                  break;
               }

               this._query = this.createBaseQuery(info == null ? null : info[i]);
               this.addAttributesAndFilters(this._query, keyIDs);
               this._query.start();
            }

            atLeastOneSuccessfulQuery |= this.processResults(certificates, atLeastOneSuccessfulQuery);
         } finally {
            continue;
         }
      }

      return certificates;
   }

   private LDAPQuery createBaseQuery(CertificateServerInfo info) {
      LDAPQuery ldapQuery = (LDAPQuery)(new Object(null, KeyStoreOptions.getCertificateServiceUID(), null));
      if (info != null) {
         ldapQuery.setHost(info.getServer(), info.getPort(), info.getBaseQuery());
         ldapQuery.setAuthType(info.getAuthType());
         ldapQuery.setConnectionType(info.getConnectionType());
      }

      ldapQuery.setScope(2);
      return ldapQuery;
   }

   private boolean processResults(Certificate[] certificates, boolean atLeastOneSuccessfulQuery) {
      Enumeration enumeration = this._query.getResults();

      while (enumeration.hasMoreElements()) {
         LDAPEntry entry = (LDAPEntry)enumeration.nextElement();
         this.addCertificates(entry, certificates);
      }

      int currentErrorCode = this._query.getErrorCode();
      if (currentErrorCode == -1) {
         this._ldapErrorCode = -1;
         return true;
      }

      if (!atLeastOneSuccessfulQuery) {
         this._ldapErrorCode = currentErrorCode;
      }

      return false;
   }

   protected void addAttributesAndFilters(LDAPQuery _1, String[] _2) {
      throw null;
   }

   protected void addAttributesAndFilters(LDAPQuery _1, Object[] _2) {
      throw null;
   }

   protected void addCertificates(LDAPEntry _1, Certificate[] _2) {
      throw null;
   }
}
