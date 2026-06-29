package net.rim.device.apps.internal.keystore.browser.certificate;

import net.rim.device.api.crypto.keystore.DeviceKeyStore;
import net.rim.device.api.crypto.keystore.KeyStore;
import net.rim.device.apps.internal.api.crypto.CryptoCommonResources;
import net.rim.device.apps.internal.api.crypto.certificate.CertificateAttachmentModelFactory;

public class X509WTLSCertificateAttachmentModelFactory extends CertificateAttachmentModelFactory {
   @Override
   public KeyStore getPreferredKeyStore() {
      return DeviceKeyStore.getInstance();
   }

   @Override
   public Object createCertificateAttachmentModel(Object initialData) {
      return new X509WTLSCertificateAttachmentModel(initialData);
   }

   @Override
   public boolean recognizeCertificateAttachmentModel(Object o) {
      return o instanceof X509WTLSCertificateAttachmentModel;
   }

   @Override
   public String getPublicKeyContainerString(boolean startWithUpperCase, boolean plural) {
      return CryptoCommonResources.getCertificateContainerString(startWithUpperCase, plural);
   }

   @Override
   public String getKeyStoreBrowserContextName() {
      return "Certificate";
   }
}
