package net.rim.device.apps.internal.keystore.browser.pgp;

import java.io.ByteArrayInputStream;
import java.io.OutputStream;
import net.rim.device.api.crypto.PrivateKey;
import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.crypto.certificate.pgp.PGPCertificate;
import net.rim.device.api.crypto.keystore.DeviceKeyStore;
import net.rim.device.api.crypto.keystore.KeyStore;
import net.rim.device.api.crypto.keystore.PGPKeyStore;
import net.rim.device.api.crypto.pgp.PGPArmorDecoder;
import net.rim.device.api.crypto.pgp.PGPArmorEncoder;
import net.rim.device.api.crypto.pgp.PGPKeyEncoder;
import net.rim.device.api.crypto.pgp.PGPPrivateKey;
import net.rim.device.api.util.Arrays;
import net.rim.device.apps.internal.api.crypto.CryptoApplicationProperties;
import net.rim.device.apps.internal.api.crypto.CryptoCommonResources;
import net.rim.device.apps.internal.api.crypto.certificate.CertificateAttachmentModel;
import net.rim.vm.Persistable;

public class PGPKeyAttachmentModel extends CertificateAttachmentModel implements Persistable {
   public static final String ASCII_ARMOUR_EXTENSION = ".asc";
   public static final String TYPE_APPLICATION_PGP_KEYS = "application/pgp-keys";
   public static final String STRING_7BIT = "7bit";
   public static String VERSION_RIM_10 = "Research In Motion 1.0";

   public PGPKeyAttachmentModel(Object initialData) {
   }

   public static boolean isCertificateContentType(String contentType) {
      return contentType.equals("application/pgp-keys");
   }

   public static boolean isCertificateFileName(String fileName) {
      return fileName.endsWith(".asc");
   }

   @Override
   public void parseCertificatesAndPrivateKeys() {
      if (!this.isMoreAvailable()) {
         try {
            ByteArrayInputStream inputStream = (ByteArrayInputStream)(new Object(this.getData()));
            PGPArmorDecoder armorDecoder = (PGPArmorDecoder)(new Object(inputStream));
            int numKeys = armorDecoder.numCertificates();
            if (numKeys != 0) {
               CryptoApplicationProperties cryptoApplicationProperties = CryptoApplicationProperties.getInstance();
               super._certificates = new Object[numKeys];
               super._privateKeys = new Object[numKeys];

               for (int i = 0; i < numKeys; i++) {
                  super._privateKeys[i] = armorDecoder.getPrivateKey(i);
                  if (super._privateKeys[i] == null && cryptoApplicationProperties.testFlags(1)) {
                     super._certificates[i] = null;
                  } else {
                     super._certificates[i] = armorDecoder.getCertificate(i);
                  }
               }
            }
         } finally {
            super._certificates = null;
            super._privateKeys = null;
            return;
         }
      }
   }

   @Override
   public String getOutgoingMIMEContentType() {
      return "application/pgp-keys";
   }

   @Override
   protected String getOutgoingMIMEEncoding() {
      return "7bit";
   }

   @Override
   protected boolean writeToOutputStream(OutputStream outputStream) {
      try {
         PGPArmorEncoder pgpArmorEncoder = (PGPArmorEncoder)(new Object(
            outputStream, PGPArmorEncoder.BEGIN_PUBLIC_KEY_DASHES, PGPArmorEncoder.END_PUBLIC_KEY_DASHES, VERSION_RIM_10, null, null, false
         ));
         pgpArmorEncoder.write(PGPKeyEncoder.getEncoding(this.getData()));
         pgpArmorEncoder.close();
         return true;
      } finally {
         ;
      }
   }

   @Override
   protected String getDefaultFileExtension() {
      return ".asc";
   }

   @Override
   protected String getNoCertificatesLabel() {
      return CryptoCommonResources.getString(25);
   }

   @Override
   protected String getRetrieveVerbDescription() {
      return CryptoCommonResources.getString(23);
   }

   @Override
   public KeyStore getPreferredKeyStore() {
      return PGPKeyStore.getInstance();
   }

   @Override
   public String getPublicKeyContainerString(boolean startWithUpperCase, boolean plural) {
      return CryptoCommonResources.getPGPContainerString(startWithUpperCase, plural);
   }

   @Override
   public boolean showImportVerb(int index) {
      if (super.showImportVerb(index)) {
         return true;
      }

      Certificate var10000 = super._certificates[index];
      if (super._certificates[index] instanceof Object) {
         PGPCertificate pgpCert = (PGPCertificate)var10000;
         KeyStore x509KeyStore = DeviceKeyStore.getInstance();
         Certificate[] certsInsertedWithPrivateData = new Object[0];
         if (super._privateKeys != null) {
            PrivateKey var15 = super._privateKeys[index];
            if (super._privateKeys[index] instanceof Object) {
               PGPPrivateKey pgpPrivateKey = (PGPPrivateKey)var15;
               PrivateKey dummyKey = pgpPrivateKey;
               Certificate[] parentKeyX509Certs = pgpCert.getEmbeddedX509Certificates(pgpPrivateKey.getKeyID());

               for (int i = 0; i < parentKeyX509Certs.length; i++) {
                  if (!CertificateAttachmentModel.isCertificateImported(parentKeyX509Certs[i], dummyKey, x509KeyStore)) {
                     return true;
                  }

                  Arrays.add(certsInsertedWithPrivateData, parentKeyX509Certs[i]);
               }

               byte[][][] subKeyIDs = (byte[][][])pgpPrivateKey.getSubKeyIDs();

               for (int i = 0; i < subKeyIDs.length; i++) {
                  Certificate[] subKeyX509Certs = pgpCert.getEmbeddedX509Certificates((byte[])subKeyIDs[i]);

                  for (int j = 0; j < subKeyX509Certs.length; j++) {
                     if (!CertificateAttachmentModel.isCertificateImported(subKeyX509Certs[j], dummyKey, x509KeyStore)) {
                        return true;
                     }

                     Arrays.add(certsInsertedWithPrivateData, subKeyX509Certs[j]);
                  }
               }
            }
         }

         Certificate[] x509Certs = pgpCert.getEmbeddedX509Certificates();

         for (int i = 0; i < x509Certs.length; i++) {
            if (!Arrays.contains(certsInsertedWithPrivateData, x509Certs[i])
               && !CertificateAttachmentModel.isCertificateImported(x509Certs[i], null, x509KeyStore)) {
               return true;
            }
         }
      }

      return false;
   }

   @Override
   public boolean showTrustVerb(Certificate[] certificateChain, PrivateKey privateKey, KeyStore keyStore) {
      return privateKey != null ? false : super.showTrustVerb(certificateChain, privateKey, keyStore);
   }
}
