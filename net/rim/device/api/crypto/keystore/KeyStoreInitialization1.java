package net.rim.device.api.crypto.keystore;

import net.rim.device.api.crypto.Initialization;
import net.rim.device.apps.api.options.OptionsProviderRegistration;

public final class KeyStoreInitialization1 implements Initialization {
   @Override
   public final void initialize() {
      KeyStoreManager.getInstance();
      TrustedKeyStore.getInstance();
      KeyStore deviceKeyStore = DeviceKeyStore.getInstance();
      KeyStoreITPolicyListener.getInstance();
      CertificateStatusManager.getInstance();
      KeyStorePasswordManager.getInstance();
      KeyStoreITPolicyListener.launchITPolicyCheck(false);
      KeyStoreInitialization1$KeyStoreOptionsItemInitializer app = new KeyStoreInitialization1$KeyStoreOptionsItemInitializer();
      OptionsProviderRegistration.registerOptionsProvider(app);
      KeyStoreOptions.register();
      WTLSRootCertificates.injectCertificates();
      KeyStoreAddressInjector.getInstance().addKeyStore(deviceKeyStore);
   }
}
