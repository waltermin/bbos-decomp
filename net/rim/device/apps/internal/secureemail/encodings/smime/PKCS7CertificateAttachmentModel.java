package net.rim.device.apps.internal.secureemail.encodings.smime;

import java.io.OutputStream;
import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.crypto.keystore.DeviceKeyStore;
import net.rim.device.api.crypto.keystore.KeyStore;
import net.rim.device.api.io.Base64OutputStream;
import net.rim.device.apps.internal.api.crypto.CryptoCommonResources;
import net.rim.device.apps.internal.api.crypto.certificate.CertificateAttachmentModel;
import net.rim.vm.Persistable;

public class PKCS7CertificateAttachmentModel extends CertificateAttachmentModel implements Persistable {
   public static final String PKCS7_CERTIFICATE_P7B;
   public static final String PKCS7_CERTIFICATE_P7C;
   public static final String STRING_BASE64;
   public static final String CERTIFICATE_EXTENSION_CER;

   public PKCS7CertificateAttachmentModel(Object initialData) {
   }

   public static boolean isCertificateContentType(String contentType) {
      return false;
   }

   public static boolean isCertificateFileName(String fileName) {
      return fileName.endsWith(".p7b") || fileName.endsWith(".p7c");
   }

   @Override
   public void parseCertificatesAndPrivateKeys() {
      Certificate[] certificates = null;
      if (!this.isMoreAvailable()) {
         String fileName = (String)(new Object(this.getNameBytes()));
         if (isCertificateFileName(fileName)) {
            certificates = this.getPKCS7Certificates();
         }
      }

      super._certificates = certificates;
   }

   private Certificate[] getPKCS7Certificates() {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 0
      // 01: invokevirtual net/rim/device/apps/internal/api/crypto/certificate/CertificateAttachmentModel.getData ()[B
      // 04: astore 1
      // 05: new java/lang/Object
      // 08: dup
      // 09: aload 1
      // 0a: invokespecial java/io/ByteArrayInputStream.<init> ([B)V
      // 0d: astore 2
      // 0e: aload 2
      // 0f: aconst_null
      // 10: invokestatic net/rim/device/api/crypto/cms/CMSInputStream.getCMSInputStream (Ljava/io/InputStream;Lnet/rim/device/api/crypto/keystore/KeyStore;)Lnet/rim/device/api/crypto/cms/CMSInputStream;
      // 13: astore 3
      // 14: aload 3
      // 15: dup
      // 16: instanceof java/lang/Object
      // 19: ifne 20
      // 1c: pop
      // 1d: goto 2b
      // 20: checkcast java/lang/Object
      // 23: invokevirtual net/rim/device/api/crypto/cms/CMSSignedDataInputStream.getCertificates ()[Lnet/rim/device/api/crypto/certificate/Certificate;
      // 26: areturn
      // 27: astore 2
      // 28: aconst_null
      // 29: areturn
      // 2a: astore 2
      // 2b: aconst_null
      // 2c: areturn
      // try (3 -> 20): 21 null
      // try (3 -> 20): 24 null
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
         Base64OutputStream base64 = (Base64OutputStream)(new Object(outputStream, true, true));
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
