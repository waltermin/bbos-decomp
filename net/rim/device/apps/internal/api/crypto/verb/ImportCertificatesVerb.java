package net.rim.device.apps.internal.api.crypto.verb;

import net.rim.device.api.crypto.PrivateKey;
import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.crypto.certificate.CertificateImporterFactory;
import net.rim.device.api.crypto.keystore.KeyStore;
import net.rim.device.apps.api.framework.verb.Verb;

public final class ImportCertificatesVerb extends Verb {
   private String _description;
   private KeyStore _keyStore;
   private Certificate[] _certs;
   private PrivateKey[] _privateKeys;

   public ImportCertificatesVerb(String description, Certificate[] certs, KeyStore keyStore) {
      this(description, certs, null, keyStore);
   }

   public ImportCertificatesVerb(String description, Certificate[] certs, PrivateKey[] privateKeys, KeyStore keyStore) {
      super(1200226);
      if (certs != null && certs.length != 0 && keyStore != null) {
         this._description = description;
         this._certs = certs;
         this._privateKeys = privateKeys;
         this._keyStore = keyStore;
      } else {
         throw new IllegalArgumentException();
      }
   }

   @Override
   public final String toString() {
      return this._description;
   }

   @Override
   public final Object invoke(Object context) {
      CertificateImporterFactory.importCertificates(this._certs, this._privateKeys, null, this._keyStore, null);
      return null;
   }
}
