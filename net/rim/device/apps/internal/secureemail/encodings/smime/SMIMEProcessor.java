package net.rim.device.apps.internal.secureemail.encodings.smime;

import java.io.InputStream;
import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.crypto.cms.CMSEnvelopedDataInputStream;
import net.rim.device.api.crypto.cms.CMSInputStream;
import net.rim.device.api.crypto.cms.CMSSignedDataInputStream;
import net.rim.device.api.crypto.cms.EMSAcceptRequestInputStream;
import net.rim.device.api.mime.MIMEInputStream;
import net.rim.device.api.mime.MIMEParsingException;
import net.rim.device.apps.internal.secureemail.SecureEmailConstants;
import net.rim.device.apps.internal.secureemail.SecureEmailProcessor;
import net.rim.device.apps.internal.secureemail.cache.CachedIncludedCertificatesField;
import net.rim.device.apps.internal.secureemail.cache.CachedManager;
import net.rim.device.apps.internal.secureemail.cache.CachedMessage;
import net.rim.device.apps.internal.secureemail.encodings.smime.cache.CachedEMSAcceptRequestField;
import net.rim.device.apps.internal.secureemail.encodings.smime.cache.CachedSMIMEEncryptionField;
import net.rim.device.apps.internal.secureemail.encodings.smime.cache.CachedSMIMEMessage;
import net.rim.device.apps.internal.secureemail.encodings.smime.cache.CachedSMIMESignatureField;

final class SMIMEProcessor extends SecureEmailProcessor {
   public SMIMEProcessor(SMIMEBodyModel smimeBodyModel, Object target, boolean allowUI, Object context) {
      super(SMIMEFactory.getInstance(), smimeBodyModel, target, allowUI, context);
   }

   @Override
   protected final byte[] getSupportedSecurityEncoding() {
      return SMIMEConstants.SECURITY_ENCODING_SMIME_BYTES;
   }

   @Override
   public final InputStream getInputStream(byte[] param1, CachedMessage param2) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: aload 0
      // 001: ldc_w "be"
      // 004: getstatic net/rim/device/apps/internal/secureemail/SecureEmailConstants.BES_ENCRYPTION_STATES [Ljava/lang/String;
      // 007: invokevirtual net/rim/device/apps/internal/secureemail/SecureEmailProcessor.mapParameterValueStringToInt (Ljava/lang/String;[Ljava/lang/String;)I
      // 00a: istore 3
      // 00b: iload 3
      // 00c: ifeq 03f
      // 00f: aload 0
      // 010: getfield net/rim/device/apps/internal/secureemail/SecureEmailProcessor._securityEncodingOptionalParameterTable Ljava/util/Hashtable;
      // 013: ldc_w "nv"
      // 016: invokevirtual java/util/Hashtable.get (Ljava/lang/Object;)Ljava/lang/Object;
      // 019: astore 4
      // 01b: aload 4
      // 01d: ifnull 03f
      // 020: new java/io/ByteArrayInputStream
      // 023: dup
      // 024: aload 1
      // 025: invokespecial java/io/ByteArrayInputStream.<init> ([B)V
      // 028: astore 5
      // 02a: new net/rim/device/api/mime/MIMEInputStream
      // 02d: dup
      // 02e: aload 5
      // 030: invokespecial net/rim/device/api/mime/MIMEInputStream.<init> (Ljava/io/InputStream;)V
      // 033: astore 6
      // 035: new net/rim/device/apps/internal/secureemail/encodings/smime/SMIMEProcessor$SMIMENoVerifyInputStream
      // 038: dup
      // 039: aload 6
      // 03b: invokespecial net/rim/device/apps/internal/secureemail/encodings/smime/SMIMEProcessor$SMIMENoVerifyInputStream.<init> (Lnet/rim/device/api/mime/MIMEInputStream;)V
      // 03e: areturn
      // 03f: aconst_null
      // 040: astore 4
      // 042: aload 0
      // 043: getfield net/rim/device/apps/internal/secureemail/SecureEmailProcessor._secureEmailCache Lnet/rim/device/apps/internal/secureemail/SecureEmailCache;
      // 046: aload 0
      // 047: getfield net/rim/device/apps/internal/secureemail/SecureEmailProcessor._secureEmailBodyModel Lnet/rim/device/apps/internal/secureemail/SecureEmailBodyModel;
      // 04a: invokevirtual net/rim/device/apps/internal/secureemail/SecureEmailCache.getAndRemoveProcessingContext (Lnet/rim/device/apps/internal/commonmodels/body/BodyModel;)Ljava/lang/Object;
      // 04d: checkcast net/rim/device/api/crypto/cms/CMSContext
      // 050: astore 4
      // 052: aload 4
      // 054: ifnonnull 081
      // 057: new java/io/ByteArrayInputStream
      // 05a: dup
      // 05b: aload 1
      // 05c: invokespecial java/io/ByteArrayInputStream.<init> ([B)V
      // 05f: astore 5
      // 061: aload 0
      // 062: getfield net/rim/device/apps/internal/secureemail/SecureEmailProcessor._secureEmailBodyModel Lnet/rim/device/apps/internal/secureemail/SecureEmailBodyModel;
      // 065: checkcast net/rim/device/apps/internal/secureemail/encodings/smime/SMIMEBodyModel
      // 068: getfield net/rim/device/apps/internal/secureemail/encodings/smime/SMIMEBodyModel._isStoredAsBase64 Z
      // 06b: ifeq 07a
      // 06e: new net/rim/device/api/io/Base64InputStream
      // 071: dup
      // 072: aload 5
      // 074: bipush 1
      // 075: invokespecial net/rim/device/api/io/Base64InputStream.<init> (Ljava/io/InputStream;Z)V
      // 078: astore 5
      // 07a: aload 5
      // 07c: invokestatic net/rim/device/api/crypto/cms/CMSInputStream.getCMSContext (Ljava/io/InputStream;)Lnet/rim/device/api/crypto/cms/CMSContext;
      // 07f: astore 4
      // 081: aload 4
      // 083: aload 0
      // 084: getfield net/rim/device/apps/internal/secureemail/SecureEmailProcessor._preferredKeyStore Lnet/rim/device/api/crypto/keystore/KeyStore;
      // 087: aload 0
      // 088: invokevirtual net/rim/device/apps/internal/secureemail/SecureEmailProcessor.getCachedSessionKey ()Lnet/rim/device/api/crypto/SymmetricKey;
      // 08b: aload 0
      // 08c: getfield net/rim/device/apps/internal/secureemail/SecureEmailProcessor._allowUI Z
      // 08f: invokestatic net/rim/device/api/crypto/cms/CMSInputStream.getCMSInputStream (Lnet/rim/device/api/crypto/cms/CMSContext;Lnet/rim/device/api/crypto/keystore/KeyStore;Lnet/rim/device/api/crypto/SymmetricKey;Z)Lnet/rim/device/api/crypto/cms/CMSInputStream;
      // 092: astore 5
      // 094: aload 5
      // 096: areturn
      // 097: astore 5
      // 099: sipush 13002
      // 09c: invokestatic net/rim/device/apps/internal/secureemail/encodings/smime/SMIMEResources.getString (I)Ljava/lang/String;
      // 09f: astore 6
      // 0a1: aload 4
      // 0a3: invokevirtual net/rim/device/api/crypto/cms/CMSContext.getEncryptionRecipients ()[Lnet/rim/device/api/crypto/cms/CMSEntityIdentifier;
      // 0a6: astore 7
      // 0a8: aload 7
      // 0aa: arraylength
      // 0ab: istore 8
      // 0ad: iload 8
      // 0af: ifle 0cc
      // 0b2: new java/lang/StringBuffer
      // 0b5: dup
      // 0b6: invokespecial java/lang/StringBuffer.<init> ()V
      // 0b9: aload 6
      // 0bb: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 0be: sipush 13004
      // 0c1: invokestatic net/rim/device/apps/internal/secureemail/encodings/smime/SMIMEResources.getString (I)Ljava/lang/String;
      // 0c4: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 0c7: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 0ca: astore 6
      // 0cc: bipush 0
      // 0cd: istore 9
      // 0cf: iload 9
      // 0d1: iload 8
      // 0d3: if_icmpge 13e
      // 0d6: aload 7
      // 0d8: iload 9
      // 0da: aaload
      // 0db: invokevirtual net/rim/device/api/crypto/cms/CMSEntityIdentifier.getSerialNumber ()[B
      // 0de: astore 10
      // 0e0: aload 7
      // 0e2: iload 9
      // 0e4: aaload
      // 0e5: invokevirtual net/rim/device/api/crypto/cms/CMSEntityIdentifier.getIssuer ()Lnet/rim/device/api/crypto/certificate/x509/X509DistinguishedName;
      // 0e8: astore 11
      // 0ea: aload 10
      // 0ec: ifnull 138
      // 0ef: aload 11
      // 0f1: ifnull 138
      // 0f4: aload 10
      // 0f6: invokestatic net/rim/device/api/crypto/certificate/CertificateUtilities.getHexAsciiString ([B)Ljava/lang/String;
      // 0f9: astore 12
      // 0fb: aload 11
      // 0fd: invokeinterface net/rim/device/api/crypto/certificate/DistinguishedName.getCommonName ()Ljava/lang/String; 1
      // 102: astore 13
      // 104: aload 13
      // 106: ifnonnull 111
      // 109: sipush 2028
      // 10c: invokestatic net/rim/device/apps/internal/secureemail/encodings/smime/SMIMEResources.getString (I)Ljava/lang/String;
      // 10f: astore 13
      // 111: new java/lang/StringBuffer
      // 114: dup
      // 115: invokespecial java/lang/StringBuffer.<init> ()V
      // 118: aload 6
      // 11a: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 11d: ldc_w "\n• "
      // 120: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 123: aload 12
      // 125: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 128: ldc_w ", "
      // 12b: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 12e: aload 13
      // 130: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 133: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 136: astore 6
      // 138: iinc 9 1
      // 13b: goto 0cf
      // 13e: new net/rim/device/apps/internal/secureemail/encodings/smime/cache/CachedSMIMEMissingPrivateKeyField
      // 141: dup
      // 142: invokespecial net/rim/device/apps/internal/secureemail/encodings/smime/cache/CachedSMIMEMissingPrivateKeyField.<init> ()V
      // 145: astore 9
      // 147: aload 9
      // 149: new net/rim/device/apps/internal/secureemail/cache/CachedBodyField
      // 14c: dup
      // 14d: aload 6
      // 14f: invokespecial net/rim/device/apps/internal/secureemail/cache/CachedBodyField.<init> (Ljava/lang/String;)V
      // 152: invokevirtual net/rim/device/apps/internal/secureemail/cache/CachedManager.addField (Lnet/rim/device/apps/internal/secureemail/cache/CachedField;)V
      // 155: aload 2
      // 156: aload 9
      // 158: invokevirtual net/rim/device/apps/internal/secureemail/cache/CachedManager.addField (Lnet/rim/device/apps/internal/secureemail/cache/CachedField;)V
      // 15b: aload 2
      // 15c: invokevirtual net/rim/device/apps/internal/secureemail/cache/CachedMessage.setDoNotCache ()V
      // 15f: goto 175
      // 162: astore 5
      // 164: aload 0
      // 165: getfield net/rim/device/apps/internal/secureemail/SecureEmailProcessor._allowUI Z
      // 168: ifeq 175
      // 16b: aload 2
      // 16c: sipush 2105
      // 16f: invokestatic net/rim/device/apps/internal/secureemail/encodings/smime/SMIMEResources.getString (I)Ljava/lang/String;
      // 172: invokevirtual net/rim/device/apps/internal/secureemail/cache/CachedMessage.setErrorString (Ljava/lang/String;)V
      // 175: aload 4
      // 177: ifnull 187
      // 17a: aload 0
      // 17b: getfield net/rim/device/apps/internal/secureemail/SecureEmailProcessor._secureEmailCache Lnet/rim/device/apps/internal/secureemail/SecureEmailCache;
      // 17e: aload 0
      // 17f: getfield net/rim/device/apps/internal/secureemail/SecureEmailProcessor._secureEmailBodyModel Lnet/rim/device/apps/internal/secureemail/SecureEmailBodyModel;
      // 182: aload 4
      // 184: invokevirtual net/rim/device/apps/internal/secureemail/SecureEmailCache.putProcessingContext (Lnet/rim/device/apps/internal/commonmodels/body/BodyModel;Ljava/lang/Object;)V
      // 187: aconst_null
      // 188: areturn
      // try (31 -> 69): 70 null
      // try (31 -> 69): 155 null
   }

   @Override
   protected final boolean getFieldsFromInputStream(InputStream inputStream, CachedManager cachedManager) {
      if (inputStream instanceof CMSInputStream) {
         return this.getFieldsFromInputStream_CMS((CMSInputStream)inputStream, cachedManager);
      } else {
         return inputStream instanceof SMIMEProcessor$SMIMENoVerifyInputStream
            ? this.getFieldsFromInputStream_NoVerify((SMIMEProcessor$SMIMENoVerifyInputStream)inputStream, cachedManager)
            : super.getFieldsFromInputStream(inputStream, cachedManager);
      }
   }

   protected final boolean getFieldsFromInputStream_NoVerify(SMIMEProcessor$SMIMENoVerifyInputStream noVerifyStream, CachedManager cachedManager) {
      int besVerificationState = this.mapParameterValueStringToInt("v", SecureEmailConstants.BES_VERIFICATION_STATES);
      String cannotVerifyOnDeviceReasonValue = (String)super._securityEncodingOptionalParameterTable.get("nv");
      CachedManager innerManager = new CachedSMIMESignatureField(
         null, besVerificationState, this.getBESSignerCertificateHash(), cannotVerifyOnDeviceReasonValue
      );
      cachedManager.addField(innerManager);
      this.getFieldsFromInputStream(noVerifyStream.getInnerInputStream(), innerManager);
      return true;
   }

   protected final boolean getFieldsFromInputStream_CMS(CMSInputStream cmsStream, CachedManager cachedManager) {
      boolean gotFields = false;
      CachedManager innerManager = cachedManager;
      Certificate[] includedCertificates = null;
      if (!(cmsStream instanceof CMSSignedDataInputStream)) {
         if (!(cmsStream instanceof CMSEnvelopedDataInputStream)) {
            if (cmsStream instanceof EMSAcceptRequestInputStream) {
               cachedManager.addField(new CachedEMSAcceptRequestField((EMSAcceptRequestInputStream)cmsStream));
               gotFields = true;
            }
         } else {
            CMSEnvelopedDataInputStream cmsEnvelopedDataInputStream = (CMSEnvelopedDataInputStream)cmsStream;
            int besEncryptionState = -1;
            int besWeakRecipientState = -1;
            CachedMessage cachedMessage = cachedManager.getCachedMessage();
            if (!cachedMessage.isEncrypted()) {
               besEncryptionState = this.mapParameterValueStringToInt("be", SecureEmailConstants.BES_ENCRYPTION_STATES);
               besWeakRecipientState = this.mapParameterValueStringToInt("wr", SecureEmailConstants.BES_WEAK_RECIPIENT_STATES);
            }

            Certificate decryptionCertificate = cmsEnvelopedDataInputStream.getCertificate(cmsEnvelopedDataInputStream.getRecipient());
            cachedMessage.addDecryptionCertificate(decryptionCertificate, super._preferredKeyStore);
            cachedMessage.setEncrypted(true);
            innerManager = new CachedSMIMEEncryptionField(cmsEnvelopedDataInputStream, besEncryptionState, besWeakRecipientState);
            cachedManager.addField(innerManager);
            gotFields = true;
            includedCertificates = cmsEnvelopedDataInputStream.getCertificates();
         }
      } else {
         CMSSignedDataInputStream signedStream = (CMSSignedDataInputStream)cmsStream;
         cachedManager.getCachedMessage().setSigned(true);
         if (signedStream.isDataPresent()) {
            int besVerificationState = this.mapParameterValueStringToInt("v", SecureEmailConstants.BES_VERIFICATION_STATES);
            String cannotVerifyOnDeviceReasonValue = (String)super._securityEncodingOptionalParameterTable.get("nv");
            innerManager = new CachedSMIMESignatureField(
               signedStream, besVerificationState, this.getBESSignerCertificateHash(), cannotVerifyOnDeviceReasonValue
            );
            cachedManager.addField(innerManager);
            gotFields = true;
         }

         if (signedStream.isSignedReceiptRequested()) {
            CachedSMIMEMessage cachedMessage = (CachedSMIMEMessage)innerManager.getCachedMessage();
            cachedMessage.addSignedReceiptRequestedField(signedStream);
            gotFields = true;
         }

         SignedReceiptHelper.processSignedReceipt(signedStream, innerManager, (SMIMEBodyModel)super._secureEmailBodyModel);
         includedCertificates = signedStream.getCertificates();
      }

      InputStream innerInputStream = cmsStream.getCMSInputStream();
      if (innerInputStream == null && cmsStream.getContentType() == 10) {
         innerInputStream = new MIMEInputStream(cmsStream);
         String cannotVerifyOnDeviceReasonValue = (String)super._securityEncodingOptionalParameterTable.get("nv");
         if (cannotVerifyOnDeviceReasonValue != null) {
            innerInputStream = new SMIMEProcessor$SMIMENoVerifyInputStream((MIMEInputStream)innerInputStream);
         }

         if (cmsStream.isContentComplete()) {
            cachedManager.getCachedMessage().setBodyTruncated(false);
         }
      }

      if (innerInputStream != null) {
         gotFields |= this.getFieldsFromInputStream(innerInputStream, innerManager);
      }

      if (includedCertificates != null) {
         int numCerts = includedCertificates.length;
         if (numCerts > 0) {
            CachedIncludedCertificatesField includedCertificatesField = new CachedIncludedCertificatesField(super._secureEmailFactory);

            for (int i = 0; i < numCerts; i++) {
               includedCertificatesField.addCertificate(includedCertificates[i]);
            }

            innerManager.addField(includedCertificatesField);
            gotFields = true;
         }
      }

      return gotFields;
   }

   @Override
   protected final boolean getFieldsFromInputStream_MIME_Multipart(MIMEInputStream mimeStream, CachedManager cachedManager) {
      String contentType = mimeStream.getContentType();
      return contentType != null && contentType.equals("multipart/signed")
         ? this.getFieldsFromInputStream_MIME_MultipartSigned(mimeStream, cachedManager)
         : super.getFieldsFromInputStream_MIME_Multipart(mimeStream, cachedManager);
   }

   private final boolean getFieldsFromInputStream_MIME_MultipartSigned(MIMEInputStream mimeStream, CachedManager cachedManager) throws MIMEParsingException {
      boolean gotFields = false;
      MIMEInputStream[] innerParts = mimeStream.getParts();
      int numInnerParts = innerParts.length;
      if (numInnerParts > 2) {
         throw new MIMEParsingException();
      }

      if (numInnerParts > 0) {
         MIMEInputStream messageMIMEStream = innerParts[0];
         if (numInnerParts == 2) {
            label31:
            try {
               MIMEInputStream signatureMIMEStream = innerParts[1];
               CMSInputStream signedStream = CMSInputStream.getCMSInputStream(
                  signatureMIMEStream, super._preferredKeyStore, this.getCachedSessionKey(), super._allowUI
               );
               signedStream.setData(messageMIMEStream.getRawMIMEInputStream());
               return gotFields | this.getFieldsFromInputStream(signedStream, cachedManager);
            } finally {
               break label31;
            }
         }

         SMIMEProcessor$SMIMENoVerifyInputStream noVerifyInputStream = new SMIMEProcessor$SMIMENoVerifyInputStream(messageMIMEStream);
         gotFields |= this.getFieldsFromInputStream(noVerifyInputStream, cachedManager);
      }

      return gotFields;
   }

   @Override
   protected final boolean getFieldsFromInputStream_MIME_Application(MIMEInputStream mimeStream, CachedManager cachedManager) {
      String contentType = mimeStream.getContentType();
      return contentType == null || !contentType.equals("application/x-pkcs7-mime") && !contentType.equals("application/pkcs7-mime")
         ? super.getFieldsFromInputStream_MIME_Application(mimeStream, cachedManager)
         : this.getFieldsFromInputStream_MIME_ApplicationPKCS7MIME(mimeStream, cachedManager);
   }

   private final boolean getFieldsFromInputStream_MIME_ApplicationPKCS7MIME(MIMEInputStream mimeStream, CachedManager cachedManager) {
      CMSInputStream cmsStream = CMSInputStream.getCMSInputStream(mimeStream, super._preferredKeyStore, this.getCachedSessionKey(), super._allowUI);
      return this.getFieldsFromInputStream(cmsStream, cachedManager);
   }
}
