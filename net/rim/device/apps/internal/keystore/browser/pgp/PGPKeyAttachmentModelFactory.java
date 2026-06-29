package net.rim.device.apps.internal.keystore.browser.pgp;

import net.rim.device.api.crypto.keystore.KeyStore;
import net.rim.device.api.crypto.keystore.PGPKeyStore;
import net.rim.device.apps.internal.api.crypto.CryptoCommonResources;
import net.rim.device.apps.internal.api.crypto.certificate.CertificateAttachmentModelFactory;

public class PGPKeyAttachmentModelFactory extends CertificateAttachmentModelFactory {
   @Override
   public KeyStore getPreferredKeyStore() {
      return PGPKeyStore.getInstance();
   }

   @Override
   public Object createCertificateAttachmentModel(Object initialData) {
      return new PGPKeyAttachmentModel(initialData);
   }

   @Override
   public boolean recognizeCertificateAttachmentModel(Object o) {
      return o instanceof PGPKeyAttachmentModel;
   }

   @Override
   public String getPublicKeyContainerString(boolean startWithUpperCase, boolean plural) {
      return CryptoCommonResources.getPGPContainerString(startWithUpperCase, plural);
   }

   @Override
   public String getKeyStoreBrowserContextName() {
      return "PGP";
   }
}
