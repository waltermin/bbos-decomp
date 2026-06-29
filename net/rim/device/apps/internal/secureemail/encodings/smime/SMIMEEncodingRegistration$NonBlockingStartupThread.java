package net.rim.device.apps.internal.secureemail.encodings.smime;

import net.rim.device.api.crypto.certificate.CertificateHashKeyStoreIndex;
import net.rim.device.api.crypto.certificate.CertificateKeyStoreIndex;
import net.rim.device.api.crypto.certificate.SerialNumberIssuerKeyStoreIndex;
import net.rim.device.api.crypto.certificate.SubjectKeyStoreIndex;
import net.rim.device.api.crypto.certificate.x509.SubjectKeyIdentifierKeyStoreIndex;
import net.rim.device.api.crypto.certificate.x509.X509PublicKeyHashKeyStoreIndex;
import net.rim.device.api.crypto.keystore.DeviceKeyStore;
import net.rim.device.api.crypto.keystore.KeyStoreIndex;
import net.rim.device.apps.internal.secureemail.KeyUsageEmailAddressKeyStoreIndex;
import net.rim.device.apps.internal.secureemail.KeyUsagePrivateKeysKeyStoreIndex;

class SMIMEEncodingRegistration$NonBlockingStartupThread extends Thread {
   public SMIMEEncodingRegistration$NonBlockingStartupThread() {
   }

   @Override
   public void run() {
      this.setPriority(1);
      KeyStoreIndex[] indexArray = new KeyStoreIndex[]{
         new CertificateHashKeyStoreIndex(),
         new SubjectKeyStoreIndex(),
         new CertificateKeyStoreIndex(),
         new SerialNumberIssuerKeyStoreIndex(),
         new SubjectKeyIdentifierKeyStoreIndex(),
         new KeyUsageEmailAddressKeyStoreIndex(),
         new KeyUsagePrivateKeysKeyStoreIndex(),
         new X509PublicKeyHashKeyStoreIndex()
      };
      DeviceKeyStore.getInstance().addIndices(indexArray);
   }
}
