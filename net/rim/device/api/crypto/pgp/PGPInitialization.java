package net.rim.device.api.crypto.pgp;

import net.rim.device.api.crypto.Initialization;
import net.rim.device.api.crypto.certificate.status.CertificateStatusProvider;
import net.rim.device.api.crypto.keystore.KeyStore;
import net.rim.device.api.crypto.keystore.KeyStoreAddressInjector;
import net.rim.device.api.crypto.keystore.KeyStoreManager;
import net.rim.device.api.crypto.keystore.PGPKeyStore;

public final class PGPInitialization implements Initialization {
   @Override
   public final void initialize() {
      KeyStoreManager.getInstance();
      KeyStore keyStore = PGPKeyStore.getInstance();
      CertificateStatusProvider.register(new PGPStatusProvider());
      KeyStoreAddressInjector.getInstance().addKeyStore(keyStore);
   }
}
