package net.rim.device.apps.internal.secureemail.cache;

import net.rim.device.api.crypto.PrivateKey;
import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.ui.Manager;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.internal.secureemail.IncludedCertificatesField;
import net.rim.device.apps.internal.secureemail.SecureEmailFactory;
import net.rim.vm.Array;

public class CachedIncludedCertificatesField extends CachedField {
   private Certificate[] _certificates;
   private PrivateKey[] _privateKeys;
   private SecureEmailFactory _secureEmailFactory;

   public CachedIncludedCertificatesField(SecureEmailFactory secureEmailFactory) {
      this._secureEmailFactory = secureEmailFactory;
   }

   @Override
   public void fillManager(Manager manager, Object context) {
      if (!ContextObject.getFlag(context, 101)) {
         manager.add(new IncludedCertificatesField(this._certificates, this._privateKeys, this._secureEmailFactory));
      }
   }

   public void addCertificate(Certificate certificate) {
      this.addCertificate(certificate, null);
   }

   public void addCertificate(Certificate certificate, PrivateKey privateKey) {
      int numCerts = 0;
      if (this._certificates == null) {
         this._certificates = new Object[1];
         this._privateKeys = new Object[1];
      } else {
         numCerts = this._certificates.length;
         Array.resize(this._certificates, numCerts + 1);
         Array.resize(this._privateKeys, numCerts + 1);
      }

      this._certificates[numCerts] = certificate;
      this._privateKeys[numCerts] = privateKey;
   }
}
