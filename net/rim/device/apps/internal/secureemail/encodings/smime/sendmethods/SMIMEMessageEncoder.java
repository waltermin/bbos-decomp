package net.rim.device.apps.internal.secureemail.encodings.smime.sendmethods;

import java.io.OutputStream;
import net.rim.device.api.crypto.PrivateKey;
import net.rim.device.api.crypto.PublicKey;
import net.rim.device.api.crypto.SignatureSigner;
import net.rim.device.api.crypto.SignatureSignerFactory;
import net.rim.device.api.crypto.SymmetricKey;
import net.rim.device.api.crypto.asn1.ASN1OutputStream;
import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.crypto.cms.CMSAttribute;
import net.rim.device.api.crypto.cms.CMSEnvelopedDataOutputStream;
import net.rim.device.api.crypto.cms.CMSSignedDataOutputStream;
import net.rim.device.api.crypto.cms.CMSSigner;
import net.rim.device.api.crypto.keystore.KeyStoreData;
import net.rim.device.api.crypto.keystore.KeyStoreManager;
import net.rim.device.api.io.Base64OutputStream;
import net.rim.device.api.mime.MIMEOutputStream;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.addressbook.EmailAddressModel;
import net.rim.device.apps.api.addressbook.GroupAddressCardModel;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.transmission.rim.CMIMEStringConverter;
import net.rim.device.apps.api.utility.framework.SubmemberUtilities;
import net.rim.device.apps.internal.blackberryemail.email.EmailMessageModel;
import net.rim.device.apps.internal.blackberryemail.header.EmailHeaderModel;
import net.rim.device.apps.internal.blackberryemail.header.EmailHeaderModelFactory;
import net.rim.device.apps.internal.blackberryemail.header.HeaderTypes;
import net.rim.device.apps.internal.secureemail.AbortSendSecureEmailException;
import net.rim.device.apps.internal.secureemail.RecipientData;
import net.rim.device.apps.internal.secureemail.RecipientData$CertificateDetails;
import net.rim.device.apps.internal.secureemail.SecureEmailOptions;
import net.rim.device.apps.internal.secureemail.SecureEmailResources;
import net.rim.device.apps.internal.secureemail.encodings.smime.EMSEmailHeaderModel;
import net.rim.device.apps.internal.secureemail.encodings.smime.SMIMEFactory;
import net.rim.device.apps.internal.secureemail.encodings.smime.SMIMEOptions;
import net.rim.device.apps.internal.secureemail.encodings.smime.SMIMEResources;
import net.rim.device.apps.internal.secureemail.encodings.smime.SMIMEUtilities;
import net.rim.device.apps.internal.secureemail.encodings.smime.SignedReceiptHelper;
import net.rim.device.apps.internal.secureemail.sendmethods.SecureEmailMessageEncoder;
import net.rim.device.apps.internal.secureemail.sendmethods.SecureEmailMessageEncoder$HeaderModelRecognizer;

public class SMIMEMessageEncoder extends SecureEmailMessageEncoder {
   private static final int EMS_TO_RECIPIENT_NAMES_TAG = 0;
   private static final int EMS_CC_RECIPIENT_NAMES_TAG = 1;
   private static final int EMS_BCC_RECIPIENT_NAMES_TAG = 2;

   public SMIMEMessageEncoder(
      EmailMessageModel message,
      RecipientData[] messageRecipientData,
      Certificate[] additionalCertificates,
      ServiceRecord serviceRecord,
      int encodingAction,
      Object context
   ) {
      super(message, messageRecipientData, additionalCertificates, serviceRecord, encodingAction, SMIMEFactory.getInstance(), context);
   }

   @Override
   protected void setGlobalOptionsDefaults(SecureEmailOptions options) {
      ((SMIMEOptions)options).setIncludeCertificatesFlag(!super._isPINMessage);
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   protected OutputStream createOutputStream(OutputStream innermostOutputStream, SecureEmailOptions secureEmailOptions) {
      SMIMEOptions smimeOptions = (SMIMEOptions)secureEmailOptions;
      Base64OutputStream base64OutputStream = new Base64OutputStream(innermostOutputStream);
      OutputStream currentInnermostOutputStream = base64OutputStream;
      CMSEnvelopedDataOutputStream encryptedCMSStream = null;
      CMSSignedDataOutputStream signedCMSStream = null;
      KeyStoreData myEncryptionData = secureEmailOptions.getEncryptionKeyStoreData();
      KeyStoreData mySigningData = secureEmailOptions.getSigningKeyStoreData();
      PrivateKey mySigningPrivateKey = null;
      boolean signMessage = (super._encodingAction & 1) != 0;
      boolean encryptMessage = (super._encodingAction & 2) != 0;
      boolean sameSignAndEncryptCert = myEncryptionData != null ? myEncryptionData.equals(mySigningData) : false;
      boolean warnedAboutSameCertificate = false;
      if (encryptMessage && myEncryptionData != null) {
         this.showCertificateWarning(myEncryptionData, 2, secureEmailOptions);
         if (sameSignAndEncryptCert) {
            warnedAboutSameCertificate = true;
         }
      }

      if (signMessage && mySigningData != null && !warnedAboutSameCertificate) {
         this.showCertificateWarning(mySigningData, 1, secureEmailOptions);
      }

      if (encryptMessage) {
         CMSSigner emsRequestSigner = null;
         CMSAttribute emsRequest = null;
         String emsAddress = this.getEMSEmailAddress();
         if (emsAddress != null && emsAddress.length() > 0) {
            boolean var31 = false /* VF: Semaphore variable */;

            try {
               var31 = true;
               int passwordPromptResourceID = signMessage ? 2104 : 2103;
               mySigningPrivateKey = this.locateMySigningPrivateKey(mySigningData, secureEmailOptions, SMIMEResources.getString(passwordPromptResourceID));
               emsRequestSigner = this.createCMSSigner(mySigningData, mySigningPrivateKey, true);
               emsRequest = this.createEMSRequestAttribute();
               emsRequestSigner.addAttribute(emsRequest);
               CMSSignedDataOutputStream emsRequestStream = new CMSSignedDataOutputStream(currentInnermostOutputStream, 10, true, true, true);
               emsRequestStream.addSigner(emsRequestSigner);
               MIMEOutputStream xPKCS7MIMEOutputStream = new MIMEOutputStream(emsRequestStream, false, "binary");
               xPKCS7MIMEOutputStream.setContentType("application/x-pkcs7-mime");
               currentInnermostOutputStream = xPKCS7MIMEOutputStream;
               var31 = false;
            } finally {
               if (var31) {
                  throw new AbortSendSecureEmailException();
               }
            }
         }

         int contentCipher = this.selectContentCipher(smimeOptions);
         int cmsContentCipher = super._secureEmailUtilities.getConstantForContentCipher(contentCipher);
         encryptedCMSStream = new CMSEnvelopedDataOutputStream(currentInnermostOutputStream, 10, true, cmsContentCipher);
         if (emsRequestSigner != null && emsRequest != null) {
            SymmetricKey sessionKey = encryptedCMSStream.getSessionKey();
            emsRequestSigner.addAttribute(this.createEMSProofOfIntention(sessionKey, emsRequest));
         }

         RecipientData[] messageRecipientData = this.getMessageRecipientData();
         int numMessageRecipientData = messageRecipientData != null ? messageRecipientData.length : 0;

         for (int i = 0; i < numMessageRecipientData; i++) {
            RecipientData$CertificateDetails[] currentCertificateDetails = super._messageRecipientData[i].getSelectedCertificates();
            int numCurrentCertificateDetails = currentCertificateDetails != null ? currentCertificateDetails.length : 0;

            for (int j = 0; j < numCurrentCertificateDetails; j++) {
               this.encryptToCertificate(encryptedCMSStream, currentCertificateDetails[j].getCertificate());
            }
         }

         Certificate[] additionalCertificates = this.getAdditionalCertificates();
         int numAdditionalCertificates = additionalCertificates != null ? additionalCertificates.length : 0;

         for (int i = 0; i < numAdditionalCertificates; i++) {
            this.encryptToCertificate(encryptedCMSStream, additionalCertificates[i]);
         }

         currentInnermostOutputStream = encryptedCMSStream;
      }

      if (signMessage) {
         boolean var28 = false /* VF: Semaphore variable */;

         try {
            var28 = true;
            if (mySigningPrivateKey == null) {
               mySigningPrivateKey = this.locateMySigningPrivateKey(mySigningData, secureEmailOptions, SecureEmailResources.getString(6));
            }

            CMSSigner cmsSigner = this.createCMSSigner(mySigningData, mySigningPrivateKey, !super._isPINMessage && !encryptMessage);
            boolean includeCerts = smimeOptions.getIncludeCertificatesFlag();
            if (encryptMessage) {
               MIMEOutputStream xPKCS7MIMEOutputStream = new MIMEOutputStream(currentInnermostOutputStream, false, "base64");
               xPKCS7MIMEOutputStream.setContentType("application/x-pkcs7-mime");
               Base64OutputStream innerBase64OutputStream = new Base64OutputStream(xPKCS7MIMEOutputStream);
               signedCMSStream = new CMSSignedDataOutputStream(innerBase64OutputStream, 10, true, true, includeCerts);
            } else {
               signedCMSStream = new CMSSignedDataOutputStream(currentInnermostOutputStream, 10, true, true, includeCerts);
            }

            if (includeCerts && myEncryptionData != null && !myEncryptionData.equals(mySigningData)) {
               Certificate encryptionCertificate = myEncryptionData.getCertificate();
               boolean useCertificateShortForm = !super._isPINMessage
                  && !encryptMessage
                  && KeyStoreManager.getInstance().isSyncedWithBES(encryptionCertificate);
               signedCMSStream.addCertificates(new Certificate[]{encryptionCertificate}, useCertificateShortForm);
            }

            signedCMSStream.addSigner(cmsSigner);
            if (smimeOptions.getRequestSignedReceipts()) {
               SignedReceiptHelper.addReceiptRequest(signedCMSStream, super._serviceRecord, super._message);
            }

            currentInnermostOutputStream = signedCMSStream;
            var28 = false;
         } finally {
            if (var28) {
               throw new AbortSendSecureEmailException();
            }
         }
      }

      return currentInnermostOutputStream;
   }

   private void encryptToCertificate(CMSEnvelopedDataOutputStream encryptedCMSStream, Certificate certificate) {
      PublicKey publicKey = certificate.getPublicKey();
      publicKey.verify();
      encryptedCMSStream.addRecipient(certificate);
   }

   protected CMSSigner createCMSSigner(KeyStoreData mySigningData, PrivateKey mySigningPrivateKey, boolean besCanReplaceCertificateShortForm) {
      SignatureSigner signer = SignatureSignerFactory.getInstance(mySigningPrivateKey, null);
      Certificate signingCert = mySigningData.getCertificate();
      boolean isSigningKeySynchedWithBES = KeyStoreManager.getInstance().isSyncedWithBES(signingCert);
      CMSSigner cmsSigner = new CMSSigner(signer, signingCert, besCanReplaceCertificateShortForm && isSigningKeySynchedWithBES);
      SMIMEUtilities.addSignedDateAttribute(cmsSigner);
      return cmsSigner;
   }

   private String getEMSEmailAddress() {
      return !super._isPINMessage && (super._encodingAction & 2) != 0
         ? ((SMIMEUtilities)super._secureEmailUtilities).getEMSEmailAddress(super._serviceRecord.getUid())
         : null;
   }

   private CMSAttribute createEMSProofOfIntention(SymmetricKey param1, CMSAttribute param2) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: aload 1
      // 001: ifnull 008
      // 004: aload 2
      // 005: ifnonnull 010
      // 008: new java/lang/IllegalArgumentException
      // 00b: dup
      // 00c: invokespecial java/lang/IllegalArgumentException.<init> ()V
      // 00f: athrow
      // 010: new net/rim/device/api/crypto/SPKMKDFPseudoRandomSource
      // 013: dup
      // 014: aload 1
      // 015: invokeinterface net/rim/device/api/crypto/SymmetricKey.getData ()[B 1
      // 01a: invokespecial net/rim/device/api/crypto/SPKMKDFPseudoRandomSource.<init> ([B)V
      // 01d: astore 3
      // 01e: aload 3
      // 01f: bipush 24
      // 021: invokevirtual net/rim/device/api/crypto/AbstractPseudoRandomSource.getBytes (I)[B
      // 024: astore 4
      // 026: new net/rim/device/api/crypto/asn1/ASN1InputByteArray
      // 029: dup
      // 02a: aload 2
      // 02b: invokevirtual net/rim/device/api/crypto/cms/CMSAttribute.getValue ()[B
      // 02e: invokespecial net/rim/device/api/crypto/asn1/ASN1InputByteArray.<init> ([B)V
      // 031: astore 5
      // 033: aload 5
      // 035: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.readSet ()V
      // 038: aload 5
      // 03a: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.readFieldAsByteArray ()[B
      // 03d: astore 6
      // 03f: bipush 8
      // 041: aload 6
      // 043: arraylength
      // 044: bipush 8
      // 046: irem
      // 047: isub
      // 048: istore 7
      // 04a: iload 7
      // 04c: bipush 8
      // 04e: if_icmpne 054
      // 051: bipush 0
      // 052: istore 7
      // 054: aload 6
      // 056: arraylength
      // 057: iload 7
      // 059: iadd
      // 05a: newarray 8
      // 05c: astore 8
      // 05e: aload 6
      // 060: bipush 0
      // 061: aload 8
      // 063: bipush 0
      // 064: aload 6
      // 066: arraylength
      // 067: invokestatic java/lang/System.arraycopy (Ljava/lang/Object;ILjava/lang/Object;II)V
      // 06a: new net/rim/device/api/crypto/CBCMAC
      // 06d: dup
      // 06e: new net/rim/device/api/crypto/TripleDESKey
      // 071: dup
      // 072: aload 4
      // 074: invokespecial net/rim/device/api/crypto/TripleDESKey.<init> ([B)V
      // 077: invokespecial net/rim/device/api/crypto/CBCMAC.<init> (Lnet/rim/device/api/crypto/SymmetricKey;)V
      // 07a: astore 9
      // 07c: aload 9
      // 07e: aload 8
      // 080: invokevirtual net/rim/device/api/crypto/AbstractMAC.update ([B)V
      // 083: aload 9
      // 085: invokevirtual net/rim/device/api/crypto/AbstractMAC.getMAC ()[B
      // 088: astore 10
      // 08a: new net/rim/device/api/crypto/asn1/ASN1OutputStream
      // 08d: dup
      // 08e: invokespecial net/rim/device/api/crypto/asn1/ASN1OutputStream.<init> ()V
      // 091: astore 11
      // 093: new net/rim/device/api/crypto/asn1/ASN1OutputStream
      // 096: dup
      // 097: invokespecial net/rim/device/api/crypto/asn1/ASN1OutputStream.<init> ()V
      // 09a: astore 12
      // 09c: new net/rim/device/api/crypto/asn1/ASN1OutputStream
      // 09f: dup
      // 0a0: invokespecial net/rim/device/api/crypto/asn1/ASN1OutputStream.<init> ()V
      // 0a3: astore 13
      // 0a5: new net/rim/device/api/crypto/asn1/ASN1OutputStream
      // 0a8: dup
      // 0a9: invokespecial net/rim/device/api/crypto/asn1/ASN1OutputStream.<init> ()V
      // 0ac: astore 14
      // 0ae: aload 14
      // 0b0: ldc_w 273417789
      // 0b3: invokestatic net/rim/device/api/crypto/oid/OIDs.getOID (I)Lnet/rim/device/api/crypto/oid/OID;
      // 0b6: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.writeOID (Lnet/rim/device/api/crypto/oid/OID;)V
      // 0b9: aload 13
      // 0bb: aload 14
      // 0bd: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.writeSequence (Lnet/rim/device/api/crypto/asn1/ASN1OutputStream;)V
      // 0c0: new net/rim/device/api/crypto/asn1/ASN1OutputStream
      // 0c3: dup
      // 0c4: invokespecial net/rim/device/api/crypto/asn1/ASN1OutputStream.<init> ()V
      // 0c7: astore 15
      // 0c9: aload 15
      // 0cb: ldc_w -472306990
      // 0ce: invokestatic net/rim/device/api/crypto/oid/OIDs.getOID (I)Lnet/rim/device/api/crypto/oid/OID;
      // 0d1: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.writeOID (Lnet/rim/device/api/crypto/oid/OID;)V
      // 0d4: aload 15
      // 0d6: bipush 2
      // 0d7: newarray 8
      // 0d9: dup
      // 0da: bipush 0
      // 0db: bipush 4
      // 0dc: bastore
      // 0dd: dup
      // 0de: bipush 1
      // 0df: bipush 0
      // 0e0: bastore
      // 0e1: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.writeRawByteArray ([B)V
      // 0e4: aload 13
      // 0e6: aload 15
      // 0e8: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.writeSequence (Lnet/rim/device/api/crypto/asn1/ASN1OutputStream;)V
      // 0eb: aload 13
      // 0ed: aload 10
      // 0ef: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.writeBitString ([B)V
      // 0f2: aload 12
      // 0f4: aload 13
      // 0f6: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.writeSequence (Lnet/rim/device/api/crypto/asn1/ASN1OutputStream;)V
      // 0f9: aload 11
      // 0fb: aload 12
      // 0fd: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.writeSet (Lnet/rim/device/api/crypto/asn1/ASN1OutputStream;)V
      // 100: new net/rim/device/api/crypto/cms/CMSAttribute
      // 103: dup
      // 104: ldc_w -477712246
      // 107: invokestatic net/rim/device/api/crypto/oid/OIDs.getOID (I)Lnet/rim/device/api/crypto/oid/OID;
      // 10a: aload 11
      // 10c: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.toByteArray ()[B
      // 10f: bipush 1
      // 110: invokespecial net/rim/device/api/crypto/cms/CMSAttribute.<init> (Lnet/rim/device/api/crypto/oid/OID;[BZ)V
      // 113: areturn
      // 114: astore 3
      // 115: aconst_null
      // 116: areturn
      // 117: astore 3
      // 118: aconst_null
      // 119: areturn
      // 11a: astore 3
      // 11b: aconst_null
      // 11c: areturn
      // try (8 -> 131): 132 null
      // try (8 -> 131): 135 null
      // try (8 -> 131): 138 null
   }

   private CMSAttribute createEMSRequestAttribute() {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: bipush 0
      // 001: anewarray 912
      // 004: astore 1
      // 005: bipush 0
      // 006: anewarray 914
      // 009: astore 2
      // 00a: bipush 0
      // 00b: anewarray 916
      // 00e: astore 3
      // 00f: aload 0
      // 010: getfield net/rim/device/apps/internal/secureemail/sendmethods/SecureEmailMessageEncoder._message Lnet/rim/device/apps/internal/blackberryemail/email/EmailMessageModel;
      // 013: new net/rim/device/apps/internal/secureemail/sendmethods/SecureEmailMessageEncoder$HeaderModelRecognizer
      // 016: dup
      // 017: invokespecial net/rim/device/apps/internal/secureemail/sendmethods/SecureEmailMessageEncoder$HeaderModelRecognizer.<init> ()V
      // 01a: invokestatic net/rim/device/apps/api/utility/framework/SubmemberUtilities.getSubmembers (Lnet/rim/device/api/collection/ReadableList;Lnet/rim/device/apps/api/framework/model/Recognizer;)[Ljava/lang/Object;
      // 01d: astore 4
      // 01f: aload 4
      // 021: arraylength
      // 022: istore 5
      // 024: bipush 0
      // 025: istore 6
      // 027: iload 6
      // 029: iload 5
      // 02b: if_icmplt 031
      // 02e: goto 0eb
      // 031: aload 0
      // 032: getfield net/rim/device/apps/internal/secureemail/sendmethods/SecureEmailMessageEncoder._secureEmailUtilities Lnet/rim/device/apps/internal/secureemail/SecureEmailUtilities;
      // 035: pop
      // 036: aload 4
      // 038: iload 6
      // 03a: aaload
      // 03b: checkcast net/rim/device/apps/internal/blackberryemail/header/EmailHeaderModel
      // 03e: bipush 0
      // 03f: invokestatic net/rim/device/apps/internal/secureemail/SecureEmailUtilities.getRecipientData (Lnet/rim/device/apps/internal/blackberryemail/header/EmailHeaderModel;Z)[Lnet/rim/device/apps/internal/secureemail/RecipientData;
      // 042: astore 7
      // 044: aload 7
      // 046: ifnonnull 04c
      // 049: goto 0e5
      // 04c: aload 7
      // 04e: arraylength
      // 04f: ifne 055
      // 052: goto 0e5
      // 055: aload 7
      // 057: arraylength
      // 058: istore 8
      // 05a: bipush 0
      // 05b: istore 9
      // 05d: iload 9
      // 05f: iload 8
      // 061: if_icmpge 0e5
      // 064: aload 7
      // 066: iload 9
      // 068: aaload
      // 069: invokevirtual net/rim/device/apps/internal/secureemail/RecipientData.getAddresses ()[Ljava/lang/String;
      // 06c: astore 10
      // 06e: aload 10
      // 070: ifnull 0df
      // 073: aload 10
      // 075: arraylength
      // 076: ifne 07c
      // 079: goto 0df
      // 07c: aload 7
      // 07e: iload 9
      // 080: aaload
      // 081: invokevirtual net/rim/device/apps/internal/secureemail/RecipientData.getEmailHeaderModel ()Lnet/rim/device/apps/internal/blackberryemail/header/EmailHeaderModel;
      // 084: astore 11
      // 086: aload 10
      // 088: arraylength
      // 089: istore 12
      // 08b: bipush 0
      // 08c: istore 13
      // 08e: iload 13
      // 090: iload 12
      // 092: if_icmpge 0df
      // 095: aload 11
      // 097: invokevirtual net/rim/device/apps/internal/blackberryemail/header/EmailHeaderModel.getHeaderType ()I
      // 09a: tableswitch 30 -1 2 63 30 42 54
      // 0b8: aload 1
      // 0b9: aload 10
      // 0bb: iload 13
      // 0bd: aaload
      // 0be: invokestatic net/rim/device/api/util/Arrays.add ([Ljava/lang/Object;Ljava/lang/Object;)V
      // 0c1: goto 0d9
      // 0c4: aload 2
      // 0c5: aload 10
      // 0c7: iload 13
      // 0c9: aaload
      // 0ca: invokestatic net/rim/device/api/util/Arrays.add ([Ljava/lang/Object;Ljava/lang/Object;)V
      // 0cd: goto 0d9
      // 0d0: aload 3
      // 0d1: aload 10
      // 0d3: iload 13
      // 0d5: aaload
      // 0d6: invokestatic net/rim/device/api/util/Arrays.add ([Ljava/lang/Object;Ljava/lang/Object;)V
      // 0d9: iinc 13 1
      // 0dc: goto 08e
      // 0df: iinc 9 1
      // 0e2: goto 05d
      // 0e5: iinc 6 1
      // 0e8: goto 027
      // 0eb: aload 1
      // 0ec: arraylength
      // 0ed: ifne 102
      // 0f0: aload 2
      // 0f1: arraylength
      // 0f2: ifne 102
      // 0f5: aload 3
      // 0f6: arraylength
      // 0f7: ifne 102
      // 0fa: new java/lang/IllegalStateException
      // 0fd: dup
      // 0fe: invokespecial java/lang/IllegalStateException.<init> ()V
      // 101: athrow
      // 102: new net/rim/device/api/crypto/asn1/ASN1OutputStream
      // 105: dup
      // 106: invokespecial net/rim/device/api/crypto/asn1/ASN1OutputStream.<init> ()V
      // 109: astore 6
      // 10b: new net/rim/device/api/crypto/asn1/ASN1OutputStream
      // 10e: dup
      // 10f: invokespecial net/rim/device/api/crypto/asn1/ASN1OutputStream.<init> ()V
      // 112: astore 7
      // 114: new net/rim/device/api/crypto/asn1/ASN1OutputStream
      // 117: dup
      // 118: invokespecial net/rim/device/api/crypto/asn1/ASN1OutputStream.<init> ()V
      // 11b: astore 8
      // 11d: aload 1
      // 11e: arraylength
      // 11f: ifle 12a
      // 122: aload 0
      // 123: aload 1
      // 124: aload 8
      // 126: bipush 0
      // 127: invokespecial net/rim/device/apps/internal/secureemail/encodings/smime/sendmethods/SMIMEMessageEncoder.writeEMSRecipientNames ([Ljava/lang/String;Lnet/rim/device/api/crypto/asn1/ASN1OutputStream;I)V
      // 12a: aload 2
      // 12b: arraylength
      // 12c: ifle 137
      // 12f: aload 0
      // 130: aload 2
      // 131: aload 8
      // 133: bipush 1
      // 134: invokespecial net/rim/device/apps/internal/secureemail/encodings/smime/sendmethods/SMIMEMessageEncoder.writeEMSRecipientNames ([Ljava/lang/String;Lnet/rim/device/api/crypto/asn1/ASN1OutputStream;I)V
      // 137: aload 3
      // 138: arraylength
      // 139: ifle 145
      // 13c: aload 0
      // 13d: aload 3
      // 13e: aload 8
      // 140: bipush 2
      // 142: invokespecial net/rim/device/apps/internal/secureemail/encodings/smime/sendmethods/SMIMEMessageEncoder.writeEMSRecipientNames ([Ljava/lang/String;Lnet/rim/device/api/crypto/asn1/ASN1OutputStream;I)V
      // 145: aload 7
      // 147: aload 8
      // 149: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.writeSequence (Lnet/rim/device/api/crypto/asn1/ASN1OutputStream;)V
      // 14c: aload 6
      // 14e: aload 7
      // 150: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.writeSet (Lnet/rim/device/api/crypto/asn1/ASN1OutputStream;)V
      // 153: new net/rim/device/api/crypto/cms/CMSAttribute
      // 156: dup
      // 157: ldc_w -477712250
      // 15a: invokestatic net/rim/device/api/crypto/oid/OIDs.getOID (I)Lnet/rim/device/api/crypto/oid/OID;
      // 15d: aload 6
      // 15f: invokevirtual net/rim/device/api/crypto/asn1/ASN1OutputStream.toByteArray ()[B
      // 162: bipush 1
      // 163: invokespecial net/rim/device/api/crypto/cms/CMSAttribute.<init> (Lnet/rim/device/api/crypto/oid/OID;[BZ)V
      // 166: areturn
      // 167: astore 6
      // 169: aconst_null
      // 16a: areturn
      // 16b: astore 6
      // 16d: aconst_null
      // 16e: areturn
      // try (113 -> 163): 164 null
      // try (113 -> 163): 167 null
   }

   private void writeEMSRecipientNames(String[] data, ASN1OutputStream emsRequest, int tag) {
      ASN1OutputStream recipientNames = new ASN1OutputStream();

      for (int i = data.length - 1; i >= 0; i--) {
         ASN1OutputStream generalNames = new ASN1OutputStream();
         generalNames.writeIA5String(data[i], 2, 1);
         ASN1OutputStream recipientName = new ASN1OutputStream();
         recipientName.writeSequence(generalNames, 2, 0);
         recipientNames.writeSequence(recipientName);
      }

      emsRequest.writeSequence(recipientNames, 2, tag);
   }

   @Override
   protected void modifyRecipientList() {
      String emsAddress = this.getEMSEmailAddress();
      if (emsAddress != null && emsAddress.length() != 0) {
         String[] emsArray = new String[]{emsAddress, emsAddress};
         ContextObject co = new ContextObject();
         co.put(-4054673099568009991L, HeaderTypes._typesAsInteger[0]);
         co.put(251, emsArray);
         EMSEmailHeaderModel emsEmailHeaderModel = new EMSEmailHeaderModel(co);
         Object[] messageHeaderModels = SubmemberUtilities.getSubmembers(super._message, new SecureEmailMessageEncoder$HeaderModelRecognizer());
         int numMessageHeaderModels = messageHeaderModels.length;

         for (int i = 0; i < numMessageHeaderModels; i++) {
            EmailHeaderModel currentHeaderModel = (EmailHeaderModel)messageHeaderModels[i];
            RIMModel currentInsideModel = currentHeaderModel.getInsideModel();
            if (!(currentInsideModel instanceof GroupAddressCardModel)) {
               emsEmailHeaderModel.addDisplayModel(currentHeaderModel);
            } else {
               GroupAddressCardModel groupModel = (GroupAddressCardModel)currentInsideModel;
               int groupModelSize = groupModel.size();

               for (int j = 0; j < groupModelSize; j++) {
                  if (groupModel.getAddressModelTypeAt(j) == 0) {
                     RIMModel memberAddress = groupModel.getAddressModelAt(j);
                     if (memberAddress != null && memberAddress instanceof EmailAddressModel) {
                        ContextObject memberContext = new ContextObject();
                        ContextObject.put(memberContext, 254, memberAddress);
                        EmailHeaderModel memberHeader = EmailHeaderModelFactory.createInstance(currentHeaderModel.getHeaderType(), memberContext);
                        emsEmailHeaderModel.addDisplayModel(memberHeader);
                     }
                  }
               }
            }

            super._message.remove(currentHeaderModel);
         }

         super._message.add(emsEmailHeaderModel);
      } else {
         super.modifyRecipientList();
      }
   }

   @Override
   protected void writeDataToOutputStream(OutputStream outermostOutputStream, StringBuffer dataToEncode, Object[] attachments) {
      MIMEOutputStream mimeOutput = new MIMEOutputStream(outermostOutputStream, attachments.length > 0, "7bit");
      MIMEOutputStream textPlainOutput = attachments.length == 0 ? mimeOutput : mimeOutput.getPartOutputStream(false, "7bit");
      String stringToEncode = dataToEncode.toString();
      int characterWidth = StringUtilities.getCharacterSize(stringToEncode);
      String textPlainContentType = characterWidth == 1 ? "text/plain; charset=iso-8859-1" : "text/plain; charset=utf-8";
      textPlainOutput.setContentType(textPlainContentType);
      byte[] textPlainBytes = CMIMEStringConverter.getInstance().convert(stringToEncode, textPlainContentType);
      textPlainOutput.write(textPlainBytes);
      textPlainOutput.close();
      if (attachments.length > 0) {
         this.addAttachments(mimeOutput, attachments);
         mimeOutput.close();
      }

      if (outermostOutputStream instanceof CMSSignedDataOutputStream) {
         CMSSignedDataOutputStream signedOutputStream = (CMSSignedDataOutputStream)outermostOutputStream;
         SignedReceiptHelper.processSignedReceiptForSend(signedOutputStream, super._message);
      }
   }
}
