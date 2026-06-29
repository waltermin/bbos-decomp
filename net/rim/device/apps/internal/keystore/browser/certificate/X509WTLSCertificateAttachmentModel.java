package net.rim.device.apps.internal.keystore.browser.certificate;

import java.io.InputStream;
import java.io.OutputStream;
import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.crypto.certificate.CertificateFactory;
import net.rim.device.api.crypto.certificate.CertificateUtilities;
import net.rim.device.api.crypto.keystore.DeviceKeyStore;
import net.rim.device.api.crypto.keystore.KeyStore;
import net.rim.device.api.io.Base64OutputStream;
import net.rim.device.apps.internal.api.crypto.CryptoCommonResources;
import net.rim.device.apps.internal.api.crypto.certificate.CertificateAttachmentModel;
import net.rim.vm.Array;
import net.rim.vm.Persistable;

public class X509WTLSCertificateAttachmentModel extends CertificateAttachmentModel implements Persistable {
   private String _fileName;
   public static final String CERTIFICATE_EXTENSION_CER;
   public static final String CERTIFICATE_EXTENSION_CERT;
   public static final String CERTIFICATE_EXTENSION_CRT;
   public static final String CERTIFICATE_EXTENSION_DER;
   public static final String ENTRUST_KEY_EXTENSION;
   public static final String STRING_BASE64;
   private static final byte[] ENTRUST_ENCRYPTION_CERT = "EncryptionCertificate=".getBytes();
   private static final byte[] ENTRUST_SIGNING_CERT = "SigningCertificate=".getBytes();
   private static final byte[] ENTRUST_CA_CERT = "CaCertificate=".getBytes();
   private static final byte[] ENTRUST_CONTINUE = "_continue_=".getBytes();

   public X509WTLSCertificateAttachmentModel(Object initialData) {
   }

   public static boolean isCertificateContentType(String contentType) {
      return contentType.equals("application/x-x509-ca-cert");
   }

   public static boolean isCertificateFileName(String fileName) {
      return isCertificateFormat(fileName) || isEntrustKeyFormat(fileName);
   }

   private static boolean isCertificateFormat(String fileName) {
      return fileName.endsWith(".der") || fileName.endsWith(".cer") || fileName.endsWith(".crt") || fileName.endsWith(".cert");
   }

   private static boolean isEntrustKeyFormat(String fileName) {
      return fileName.endsWith(".key");
   }

   @Override
   public void parseCertificatesAndPrivateKeys() {
      Certificate[] certificates = null;
      if (!this.isMoreAvailable()) {
         String fileName = (String)(new Object(this.getNameBytes()));
         if (isCertificateFormat(fileName)) {
            certificates = this.getStandardCertificates();
            this._fileName = fileName;
         } else if (isEntrustKeyFormat(fileName)) {
            certificates = this.getEntrustKeyCertificates();
            this._fileName = fileName;
         }
      }

      super._certificates = certificates;
   }

   public Certificate[] getStandardCertificates() {
      Certificate cert = CertificateUtilities.readCertificateFile(null, this.getData());
      return cert != null ? new Object[]{cert} : null;
   }

   private Certificate[] getEntrustKeyCertificates() {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: new java/lang/Object
      // 003: dup
      // 004: new java/lang/Object
      // 007: dup
      // 008: aload 0
      // 009: invokevirtual net/rim/device/apps/internal/api/crypto/certificate/CertificateAttachmentModel.getData ()[B
      // 00c: invokespecial java/io/ByteArrayInputStream.<init> ([B)V
      // 00f: invokespecial net/rim/device/api/io/LineReader.<init> (Ljava/io/InputStream;)V
      // 012: astore 1
      // 013: aconst_null
      // 014: astore 2
      // 015: bipush 0
      // 016: anewarray 252
      // 019: astore 3
      // 01a: aload 1
      // 01b: invokevirtual net/rim/device/api/io/LineReader.readLine ()[B
      // 01e: astore 4
      // 020: bipush 0
      // 021: istore 5
      // 023: bipush 0
      // 024: istore 6
      // 026: bipush 0
      // 027: istore 7
      // 029: aload 4
      // 02b: bipush 0
      // 02c: getstatic net/rim/device/apps/internal/keystore/browser/certificate/X509WTLSCertificateAttachmentModel.ENTRUST_ENCRYPTION_CERT [B
      // 02f: bipush 0
      // 030: getstatic net/rim/device/apps/internal/keystore/browser/certificate/X509WTLSCertificateAttachmentModel.ENTRUST_ENCRYPTION_CERT [B
      // 033: arraylength
      // 034: invokestatic net/rim/device/api/util/Arrays.equals ([BI[BII)Z
      // 037: ifeq 046
      // 03a: getstatic net/rim/device/apps/internal/keystore/browser/certificate/X509WTLSCertificateAttachmentModel.ENTRUST_ENCRYPTION_CERT [B
      // 03d: arraylength
      // 03e: istore 7
      // 040: bipush 1
      // 041: istore 5
      // 043: goto 09f
      // 046: aload 4
      // 048: bipush 0
      // 049: getstatic net/rim/device/apps/internal/keystore/browser/certificate/X509WTLSCertificateAttachmentModel.ENTRUST_SIGNING_CERT [B
      // 04c: bipush 0
      // 04d: getstatic net/rim/device/apps/internal/keystore/browser/certificate/X509WTLSCertificateAttachmentModel.ENTRUST_SIGNING_CERT [B
      // 050: arraylength
      // 051: invokestatic net/rim/device/api/util/Arrays.equals ([BI[BII)Z
      // 054: ifeq 063
      // 057: getstatic net/rim/device/apps/internal/keystore/browser/certificate/X509WTLSCertificateAttachmentModel.ENTRUST_SIGNING_CERT [B
      // 05a: arraylength
      // 05b: istore 7
      // 05d: bipush 1
      // 05e: istore 5
      // 060: goto 09f
      // 063: aload 4
      // 065: bipush 0
      // 066: getstatic net/rim/device/apps/internal/keystore/browser/certificate/X509WTLSCertificateAttachmentModel.ENTRUST_CA_CERT [B
      // 069: bipush 0
      // 06a: getstatic net/rim/device/apps/internal/keystore/browser/certificate/X509WTLSCertificateAttachmentModel.ENTRUST_CA_CERT [B
      // 06d: arraylength
      // 06e: invokestatic net/rim/device/api/util/Arrays.equals ([BI[BII)Z
      // 071: ifeq 080
      // 074: getstatic net/rim/device/apps/internal/keystore/browser/certificate/X509WTLSCertificateAttachmentModel.ENTRUST_CA_CERT [B
      // 077: arraylength
      // 078: istore 7
      // 07a: bipush 1
      // 07b: istore 5
      // 07d: goto 09f
      // 080: aload 4
      // 082: bipush 0
      // 083: getstatic net/rim/device/apps/internal/keystore/browser/certificate/X509WTLSCertificateAttachmentModel.ENTRUST_CONTINUE [B
      // 086: bipush 0
      // 087: getstatic net/rim/device/apps/internal/keystore/browser/certificate/X509WTLSCertificateAttachmentModel.ENTRUST_CONTINUE [B
      // 08a: arraylength
      // 08b: invokestatic net/rim/device/api/util/Arrays.equals ([BI[BII)Z
      // 08e: ifeq 09f
      // 091: getstatic net/rim/device/apps/internal/keystore/browser/certificate/X509WTLSCertificateAttachmentModel.ENTRUST_CONTINUE [B
      // 094: arraylength
      // 095: istore 7
      // 097: bipush 1
      // 098: istore 6
      // 09a: goto 09f
      // 09d: astore 8
      // 09f: aload 4
      // 0a1: arraylength
      // 0a2: iload 7
      // 0a4: isub
      // 0a5: istore 8
      // 0a7: iload 6
      // 0a9: ifeq 0cc
      // 0ac: aload 2
      // 0ad: ifnull 0cc
      // 0b0: aload 2
      // 0b1: arraylength
      // 0b2: istore 9
      // 0b4: aload 2
      // 0b5: iload 9
      // 0b7: iload 8
      // 0b9: iadd
      // 0ba: invokestatic net/rim/vm/Array.resize (Ljava/lang/Object;I)V
      // 0bd: aload 4
      // 0bf: iload 7
      // 0c1: aload 2
      // 0c2: iload 9
      // 0c4: iload 8
      // 0c6: invokestatic java/lang/System.arraycopy (Ljava/lang/Object;ILjava/lang/Object;II)V
      // 0c9: goto 01a
      // 0cc: aload 2
      // 0cd: ifnull 0d6
      // 0d0: aload 0
      // 0d1: aload 3
      // 0d2: aload 2
      // 0d3: invokespecial net/rim/device/apps/internal/keystore/browser/certificate/X509WTLSCertificateAttachmentModel.appendCertificate ([Lnet/rim/device/api/crypto/certificate/Certificate;[B)V
      // 0d6: iload 5
      // 0d8: ifeq 0e8
      // 0db: aload 4
      // 0dd: iload 7
      // 0df: iload 8
      // 0e1: invokestatic net/rim/device/api/util/Arrays.copy ([BII)[B
      // 0e4: astore 2
      // 0e5: goto 01a
      // 0e8: aconst_null
      // 0e9: astore 2
      // 0ea: goto 01a
      // 0ed: astore 4
      // 0ef: aload 2
      // 0f0: ifnull 102
      // 0f3: aload 0
      // 0f4: aload 3
      // 0f5: aload 2
      // 0f6: invokespecial net/rim/device/apps/internal/keystore/browser/certificate/X509WTLSCertificateAttachmentModel.appendCertificate ([Lnet/rim/device/api/crypto/certificate/Certificate;[B)V
      // 0f9: aconst_null
      // 0fa: astore 2
      // 0fb: aload 3
      // 0fc: areturn
      // 0fd: astore 4
      // 0ff: goto 01a
      // 102: aload 3
      // 103: areturn
      // try (23 -> 78): 79 null
      // try (14 -> 120): 121 null
      // try (14 -> 120): 132 null
   }

   private void appendCertificate(Certificate[] certificates, byte[] newCertificate) {
      try {
         Certificate certificate = CertificateFactory.getInstance("X509", (InputStream)(new Object((InputStream)(new Object(newCertificate)))));
         Array.resize(certificates, certificates.length + 1);
         certificates[certificates.length - 1] = certificate;
      } finally {
         return;
      }
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
      return ".der";
   }

   @Override
   protected String getNoCertificatesLabel() {
      return this._fileName != null ? this._fileName : CryptoCommonResources.getString(24);
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
