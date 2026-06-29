package net.rim.device.api.crypto.keystore;

import net.rim.device.api.crypto.Initialization;

public final class KeyStoreInitialization2 implements Initialization {
   @Override
   public final void initialize() {
      KeyStoreSyncFields.initialize();
      BaltimoreRootCertificates.injectCertificates();
      CerticomRootCertificates.injectCertificates();
      EntrustRootCertificates.injectCertificates();
      GeoTrustRootCertificates.injectCertificates();
      QuovadisRootCertificates.injectCertificates();
      RSARootCertificates.injectCertificates();
      ThawteRootCertificates.injectCertificates();
      VerisignRootCertificates.injectCertificates();
      GoDaddyRootCertificates.injectCertificates();
      GlobalSignRootCertificates.injectCertificates();
      ComodoRootCerts.injectCertificates();
      VodafoneRootCertificates.injectCertificates();
      Equifax.injectCertificates();
   }
}
