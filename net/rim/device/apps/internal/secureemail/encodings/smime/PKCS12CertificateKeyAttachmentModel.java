package net.rim.device.apps.internal.secureemail.encodings.smime;

import java.io.OutputStream;
import net.rim.device.api.crypto.CertificatePrivateKeyPair;
import net.rim.device.api.crypto.PKCS12;
import net.rim.device.api.crypto.PKCS12Utilities;
import net.rim.device.api.crypto.PrivateKey;
import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.crypto.keystore.DeviceKeyStore;
import net.rim.device.api.crypto.keystore.KeyStore;
import net.rim.device.api.io.Base64OutputStream;
import net.rim.device.apps.internal.api.crypto.CryptoCommonResources;
import net.rim.device.apps.internal.api.crypto.certificate.CertificateAttachmentModel;
import net.rim.vm.Persistable;

public class PKCS12CertificateKeyAttachmentModel extends CertificateAttachmentModel implements Persistable {
   public static final String PKCS12_CERTIFICATE_KEY_P12 = ".p12";
   public static final String PKCS12_CERTIFICATE_KEY_PFX = ".pfx";
   public static final String STRING_BASE64 = "base64";
   public static final String CERTIFICATE_EXTENSION_CER = ".cer";

   public PKCS12CertificateKeyAttachmentModel(Object initialData) {
   }

   public static boolean isCertificateContentType(String contentType) {
      return false;
   }

   public static boolean isCertificateFileName(String fileName) {
      return fileName.endsWith(".p12") || fileName.endsWith(".pfx");
   }

   @Override
   public void parseCertificatesAndPrivateKeys() {
      CertificatePrivateKeyPair[] pairs = null;
      Certificate[] certificates = null;
      PrivateKey[] keys = null;
      if (!this.isMoreAvailable()) {
         String fileName = new String(this.getNameBytes());
         if (isCertificateFileName(fileName)) {
            label35:
            try {
               byte[] data = this.getData();
               PKCS12 p12 = new PKCS12(data);
               pairs = PKCS12Utilities.getAllCertificateKeyPairs(p12);
               if (pairs != null) {
                  int size = pairs.length;
                  certificates = new Certificate[size];
                  keys = new PrivateKey[size];

                  for (int i = 0; i < size; i++) {
                     certificates[i] = pairs[i].getCertificate();
                     keys[i] = pairs[i].getPrivateKey();
                  }
               }
            } finally {
               break label35;
            }
         }
      }

      super._certificates = certificates;
      super._privateKeys = keys;
   }

   @Override
   public String getOutgoingMIMEContentType() {
      return "application/x-x509-ca-cert";
   }

   @Override
   protected String getOutgoingMIMEEncoding() {
      return "base64";
   }

   @Override
   protected boolean writeToOutputStream(OutputStream outputStream) {
      try {
         Base64OutputStream base64 = new Base64OutputStream(outputStream, true, true);
         base64.write(this.getData());
         base64.close();
         return true;
      } finally {
         ;
      }
   }

   @Override
   protected String getDefaultFileExtension() {
      return ".cer";
   }

   @Override
   protected String getNoCertificatesLabel() {
      return CryptoCommonResources.getString(24);
   }

   @Override
   protected String getRetrieveVerbDescription() {
      return CryptoCommonResources.getString(22);
   }

   @Override
   public KeyStore getPreferredKeyStore() {
      return DeviceKeyStore.getInstance();
   }

   @Override
   public String getPublicKeyContainerString(boolean startWithUpperCase, boolean plural) {
      return CryptoCommonResources.getCertificateContainerString(startWithUpperCase, plural);
   }
}
