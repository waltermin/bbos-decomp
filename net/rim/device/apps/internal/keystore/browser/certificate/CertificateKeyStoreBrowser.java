package net.rim.device.apps.internal.keystore.browser.certificate;

import net.rim.device.apps.api.transmission.rim.CMIMEConverterRegistry;
import net.rim.device.apps.internal.api.crypto.certificate.CertificateConverter;
import net.rim.device.apps.internal.keystore.browser.KeyStoreBrowser;

public class CertificateKeyStoreBrowser {
   public static void libMain(String[] args) {
      KeyStoreBrowser.getInstance().register("Certificate", new CertificateKeyStoreBrowserContext());
      CertificateConverter certificateConverter = new X509WTLSCertificateConverter();
      CMIMEConverterRegistry.addConverter(certificateConverter, 4);
      new PKCS12FileSystemContentTypeListener();
   }
}
