package net.rim.device.apps.internal.keystore.browser.pgp;

import net.rim.device.api.crypto.CryptoSystemProperties;
import net.rim.device.api.crypto.certificate.CertificateImporterFactory;
import net.rim.device.apps.api.transmission.rim.CMIMEConverterRegistry;
import net.rim.device.apps.internal.api.crypto.certificate.CertificateConverter;
import net.rim.device.apps.internal.keystore.browser.KeyStoreBrowser;

public class PGPKeyStoreBrowser {
   public static void register(CryptoSystemProperties pgpCryptoSystemProperties) {
      KeyStoreBrowser.getInstance().register("PGP", new PGPKeyStoreBrowserContext(pgpCryptoSystemProperties));
      CertificateConverter certificateConverter = new PGPCertificateConverter();
      CMIMEConverterRegistry.addConverter(certificateConverter, 4);
      CertificateImporterFactory.register((CertificateImporterFactory)(new Object()));
   }
}
