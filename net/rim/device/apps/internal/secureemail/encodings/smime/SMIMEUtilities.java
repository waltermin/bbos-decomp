package net.rim.device.apps.internal.secureemail.encodings.smime;

import net.rim.device.api.crypto.asn1.ASN1InputByteArray;
import net.rim.device.api.crypto.asn1.ASN1OutputStream;
import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.crypto.certificate.CertificateChainProperties;
import net.rim.device.api.crypto.certificate.CertificateExtension;
import net.rim.device.api.crypto.certificate.CertificateUtilities;
import net.rim.device.api.crypto.certificate.LDAPCertificateFetch;
import net.rim.device.api.crypto.certificate.x509.X509LDAPCertificateFetch;
import net.rim.device.api.crypto.cms.CMSAttribute;
import net.rim.device.api.crypto.cms.CMSEntityIdentifier;
import net.rim.device.api.crypto.cms.CMSSignedDataInputStream;
import net.rim.device.api.crypto.cms.CMSSigner;
import net.rim.device.api.crypto.keystore.DeviceKeyStore;
import net.rim.device.api.crypto.keystore.KeyStore;
import net.rim.device.api.crypto.keystore.KeyStoreData;
import net.rim.device.api.crypto.keystore.KeyStoreIndex;
import net.rim.device.api.crypto.keystore.TrustedKeyStore;
import net.rim.device.api.crypto.oid.OID;
import net.rim.device.api.crypto.oid.OIDs;
import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.apps.internal.blackberryemail.email.recipientcache.RecipientCache;
import net.rim.device.apps.internal.blackberryemail.email.recipientcache.RecipientCacheData;
import net.rim.device.apps.internal.secureemail.SecureEmailUtilities;
import net.rim.device.internal.system.FIPSPolicy;

public final class SMIMEUtilities extends SecureEmailUtilities {
   private KeyStore _deviceKeyStore = DeviceKeyStore.getInstance();
   private KeyStore _trustedKeyStore = TrustedKeyStore.getInstance();

   SMIMEUtilities() {
   }

   @Override
   public final long getCertificateProperties(Certificate certificate) {
      Certificate[][] certificateChains = CertificateUtilities.buildCertificateChains(certificate, this._deviceKeyStore);
      long[] certificateChainProperties = CertificateChainProperties.getCertificateChainProperties(
         certificateChains, this._trustedKeyStore, System.currentTimeMillis(), SMIMEFactory.getInstance().getCryptoSystemProperties()
      );
      return CertificateChainProperties.selectBestCertificateChainProperties(certificateChainProperties);
   }

   @Override
   public final boolean isCertificateAllowed(KeyStoreData keyStoreData, int operationType) {
      if (keyStoreData == null) {
         return false;
      }

      if (operationType == 4) {
         boolean requireSmartCardUse = ITPolicy.getBoolean(25, 8, false);
         if (requireSmartCardUse && keyStoreData.getAssociatedData(-4699629744920546763L) == null) {
            return false;
         }
      }

      return super.isCertificateAllowed(keyStoreData, operationType);
   }

   @Override
   public final boolean isSignatureRequired() {
      return ITPolicy.getBoolean(25, 6, false);
   }

   @Override
   public final boolean isEncryptionRequired() {
      return ITPolicy.getBoolean(25, 7, false);
   }

   @Override
   public final int getITPolicyContentCiphers() {
      return FIPSPolicy.getInteger(25, 10, 63, 39) & 0xFF;
   }

   @Override
   public final int getConstantForContentCipher(int contentCipher) {
      switch (contentCipher) {
         case 1:
            return 106;
         case 2:
            return 105;
         case 4:
            return 104;
         case 8:
            return 108;
         case 16:
            return 103;
         case 32:
            return 100;
         case 64:
            return 102;
         case 128:
            return 101;
         case 256:
            return 109;
         default:
            throw new IllegalArgumentException();
      }
   }

   @Override
   public final int getContentCipherForConstant(int constant) {
      switch (constant) {
         case 99:
         case 107:
            throw new IllegalArgumentException();
         case 100:
            return 32;
         case 101:
            return 128;
         case 102:
            return 64;
         case 103:
            return 16;
         case 104:
            return 4;
         case 105:
            return 2;
         case 106:
         default:
            return 1;
         case 108:
            return 8;
         case 109:
            return 256;
      }
   }

   @Override
   public final int getRecipientContentCiphers(String recipient) {
      RecipientCache recipientCache = RecipientCache.getInstance();
      RecipientCacheData cacheData = recipientCache.get(recipient);
      int flags = 0;
      if (cacheData != null) {
         flags = cacheData.getFlags();
      }

      return this.createContentCipherBitField(flags);
   }

   @Override
   public final int getCertificateContentCiphers(Certificate certificate) {
      int flags = 0;
      CertificateExtension extension = certificate.getExtension(OIDs.getOID(545531452));
      if (extension != null) {
         try {
            ASN1InputByteArray data = new ASN1InputByteArray(extension.getValue());
            flags = this.readCapabilitiesFlags(data);
            return this.createContentCipherBitField(flags);
         } finally {
            return 255;
         }
      } else {
         return 255;
      }
   }

   private final int createContentCipherBitField(int flags) {
      int bitfield = 240;
      if (flags != 0) {
         if ((flags & 4) != 0) {
            bitfield |= 4;
         }

         if ((flags & 2) != 0) {
            bitfield |= 2;
         }

         if ((flags & 1) != 0) {
            bitfield |= 1;
         }

         if ((flags & 8) != 0) {
            bitfield |= 8;
         }
      }

      return bitfield;
   }

   public final int getRecipientCacheCipherFlags(CMSSignedDataInputStream input) {
      int flags = 0;

      try {
         CMSEntityIdentifier[] signers = input.getSigners();
         if (signers == null) {
            return flags;
         }

         for (int i = 0; i < signers.length; i++) {
            CMSAttribute attribute = input.getSignerAttribute(OIDs.getOID(545531452), signers[i]);
            if (attribute != null) {
               ASN1InputByteArray attributeStream = new ASN1InputByteArray(attribute.getValue());
               attributeStream.readSet();
               flags = this.readCapabilitiesFlags(attributeStream);
            }
         }
      } finally {
         return flags;
      }

      return flags;
   }

   public final long getSignedDate(CMSSignedDataInputStream param1, CMSEntityIdentifier param2) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: bipush -1
      // 02: i2l
      // 03: lstore 3
      // 04: aload 1
      // 05: ldc_w 542910012
      // 08: invokestatic net/rim/device/api/crypto/oid/OIDs.getOID (I)Lnet/rim/device/api/crypto/oid/OID;
      // 0b: aload 2
      // 0c: invokevirtual net/rim/device/api/crypto/cms/CMSSignedDataInputStream.getSignerAttribute (Lnet/rim/device/api/crypto/oid/OID;Lnet/rim/device/api/crypto/cms/CMSEntityIdentifier;)Lnet/rim/device/api/crypto/cms/CMSAttribute;
      // 0f: astore 5
      // 11: aload 5
      // 13: ifnull 5d
      // 16: new net/rim/device/api/crypto/asn1/ASN1InputByteArray
      // 19: dup
      // 1a: aload 5
      // 1c: invokevirtual net/rim/device/api/crypto/cms/CMSAttribute.getValue ()[B
      // 1f: invokespecial net/rim/device/api/crypto/asn1/ASN1InputByteArray.<init> ([B)V
      // 22: astore 6
      // 24: aload 6
      // 26: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.readSet ()V
      // 29: aload 6
      // 2b: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.peekNextTag ()I
      // 2e: bipush 23
      // 30: if_icmpne 3c
      // 33: aload 6
      // 35: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.readUTCTime ()J
      // 38: lstore 3
      // 39: goto 5d
      // 3c: aload 6
      // 3e: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.peekNextTag ()I
      // 41: bipush 24
      // 43: if_icmpne 4f
      // 46: aload 6
      // 48: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.readGeneralizedTime ()J
      // 4b: lstore 3
      // 4c: goto 5d
      // 4f: new net/rim/device/api/crypto/asn1/ASN1EncodingException
      // 52: dup
      // 53: invokespecial net/rim/device/api/crypto/asn1/ASN1EncodingException.<init> ()V
      // 56: athrow
      // 57: astore 5
      // 59: lload 3
      // 5a: lreturn
      // 5b: astore 5
      // 5d: lload 3
      // 5e: lreturn
      // try (3 -> 39): 39 null
      // try (3 -> 39): 42 null
   }

   public static final void addSignedDateAttribute(CMSSigner signer) {
      ASN1OutputStream dateStream = new ASN1OutputStream();
      ASN1OutputStream attributeStream = new ASN1OutputStream();

      label20:
      try {
         dateStream.writeTime(System.currentTimeMillis());
         attributeStream.writeSet(dateStream);
      } finally {
         break label20;
      }

      byte[] value = attributeStream.toByteArray();
      CMSAttribute attribute = new CMSAttribute(OIDs.getOID(542910012), value, true);
      signer.addAttribute(attribute);
   }

   private final int readCapabilitiesFlags(ASN1InputByteArray attributeStream) {
      int flags = 0;
      attributeStream.readSequence();
      OID aes128 = OIDs.getOID(540861300);
      OID aes192 = OIDs.getOID(546104180);
      OID aes256 = OIDs.getOID(551347060);
      OID cast128 = OIDs.getOID(552133493);
      int endOffset = attributeStream.getEndPosition();

      while (attributeStream.getStartPosition() < endOffset) {
         attributeStream.readSequence();
         int innerEndOffset = attributeStream.getEndPosition();
         OID algorithm = attributeStream.readOID();
         if (algorithm.equals(aes128)) {
            flags |= 4;
         } else if (algorithm.equals(aes192)) {
            flags |= 2;
         } else if (algorithm.equals(aes256)) {
            flags |= 1;
         } else if (algorithm.equals(cast128)) {
            flags |= 8;
         }

         if (attributeStream.getStartPosition() < innerEndOffset) {
            attributeStream.skipField();
         }
      }

      return flags;
   }

   public final void processCapabilities(Object param1, CMSSignedDataInputStream param2) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 1
      // 01: sipush 246
      // 04: i2l
      // 05: invokestatic net/rim/device/apps/api/framework/model/ContextObject.get (Ljava/lang/Object;J)Ljava/lang/Object;
      // 08: checkcast net/rim/device/apps/internal/blackberryemail/email/EmailMessageModel
      // 0b: astore 3
      // 0c: aload 3
      // 0d: ifnull 5d
      // 10: aload 3
      // 11: invokeinterface net/rim/device/apps/internal/blackberryemail/email/EmailMessageModel.getFlags ()I 1
      // 16: ldc_w 131072
      // 19: iand
      // 1a: ifeq 21
      // 1d: bipush 1
      // 1e: goto 22
      // 21: bipush 0
      // 22: istore 4
      // 24: iload 4
      // 26: ifne 5d
      // 29: aload 3
      // 2a: invokeinterface net/rim/device/apps/internal/blackberryemail/email/EmailMessageModel.inbound ()Z 1
      // 2f: ifeq 5d
      // 32: aload 0
      // 33: aload 3
      // 34: invokevirtual net/rim/device/apps/internal/secureemail/SecureEmailUtilities.getSender (Lnet/rim/device/apps/internal/blackberryemail/email/EmailMessageModel;)Ljava/lang/String;
      // 37: astore 5
      // 39: invokestatic net/rim/device/apps/internal/blackberryemail/email/recipientcache/RecipientCache.getInstance ()Lnet/rim/device/apps/internal/blackberryemail/email/recipientcache/RecipientCache;
      // 3c: astore 6
      // 3e: aload 6
      // 40: aload 5
      // 42: aload 0
      // 43: aload 2
      // 44: invokevirtual net/rim/device/apps/internal/secureemail/encodings/smime/SMIMEUtilities.getRecipientCacheCipherFlags (Lnet/rim/device/api/crypto/cms/CMSSignedDataInputStream;)I
      // 47: invokevirtual net/rim/device/apps/internal/blackberryemail/email/recipientcache/RecipientCache.setFlag (Ljava/lang/String;I)V
      // 4a: aload 3
      // 4b: ldc_w 131072
      // 4e: invokeinterface net/rim/device/apps/internal/blackberryemail/email/EmailMessageModel.setFlags (I)V 2
      // 53: aload 3
      // 54: invokestatic net/rim/device/api/system/PersistentObject.commit (Ljava/lang/Object;)V
      // 57: return
      // 58: astore 5
      // 5a: return
      // 5b: astore 5
      // 5d: return
      // try (22 -> 39): 40 null
      // try (22 -> 39): 42 null
   }

   @Override
   public final LDAPCertificateFetch getLDAPCertificateFetch() {
      return new X509LDAPCertificateFetch();
   }

   @Override
   public final String getAlwaysBCCEmailAddress(String serviceUID) {
      if (serviceUID != null && !SecureEmailUtilities.checkITAdminBoundUID(serviceUID)) {
         return null;
      } else {
         return !ITPolicy.getBoolean(16, true) ? null : ITPolicy.getString(25, 9);
      }
   }

   public final String getEMSEmailAddress(String serviceUID) {
      return serviceUID != null && !SecureEmailUtilities.checkITAdminBoundUID(serviceUID) ? null : ITPolicy.getString(25, 12);
   }

   @Override
   protected final KeyStoreIndex getCertificateIDKeyStoreIndex() {
      return null;
   }
}
