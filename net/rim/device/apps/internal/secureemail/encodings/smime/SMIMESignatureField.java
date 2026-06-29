package net.rim.device.apps.internal.secureemail.encodings.smime;

import java.util.Enumeration;
import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.crypto.certificate.x509.X509Certificate;
import net.rim.device.api.crypto.cms.CMSEntityIdentifier;
import net.rim.device.api.crypto.cms.CMSSignedDataInputStream;
import net.rim.device.api.crypto.keystore.DeviceKeyStore;
import net.rim.device.api.crypto.keystore.KeyStore;
import net.rim.device.api.crypto.keystore.KeyStoreData;
import net.rim.device.api.crypto.keystore.KeyStoreIndex;
import net.rim.device.api.crypto.keystore.TrustedKeyStore;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.system.Application;
import net.rim.device.api.ui.ContextMenu;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.ObjectUtilities;
import net.rim.device.apps.api.framework.model.MatchProvider;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.VerbMenuItem;
import net.rim.device.apps.internal.secureemail.SecureEmailSignatureField;
import net.rim.device.apps.internal.secureemail.SecureEmailSignatureField$SignatureStatusField;
import net.rim.device.apps.internal.secureemail.SecureEmailSignatureField$TrustStatusField;
import net.rim.device.apps.internal.secureemail.SecureEmailUtilities;

public final class SMIMESignatureField extends SecureEmailSignatureField {
   private CMSSignedDataInputStream _cmsSignedDataInputStream;
   private CMSEntityIdentifier _signerEntity;
   private VerbMenuItem _importSignerCertificateMenuItem;
   private VerbMenuItem _trustSignerCertificateMenuItem;
   private VerbMenuItem _fetchSignerCertificateStatusMenuItem;
   private VerbMenuItem _fetchSignerCertificateChainStatusMenuItem;
   private Object _context;

   public SMIMESignatureField(
      CMSSignedDataInputStream cmsSignedDataInputStream,
      ServiceRecord serviceRecord,
      boolean inbound,
      boolean moreAvailable,
      long creationDate,
      String senderEmailAddress,
      boolean isPINMessage,
      int besVerificationState,
      byte[] besSignerCertificateHash,
      String besNoVerifyReason,
      Manager manager,
      Object context
   ) {
      this(
         cmsSignedDataInputStream,
         serviceRecord,
         inbound,
         moreAvailable,
         creationDate,
         senderEmailAddress,
         isPINMessage,
         besVerificationState,
         besSignerCertificateHash,
         besNoVerifyReason,
         manager,
         context,
         null
      );
   }

   public SMIMESignatureField(
      CMSSignedDataInputStream cmsSignedDataInputStream,
      ServiceRecord serviceRecord,
      boolean inbound,
      boolean moreAvailable,
      long creationDate,
      String senderEmailAddress,
      boolean isPINMessage,
      int besVerificationState,
      byte[] besSignerCertificateHash,
      String besNoVerifyReason,
      Manager manager,
      Object context,
      SecureEmailSignatureField oldField
   ) {
      super(
         SMIMEFactory.getInstance(),
         serviceRecord,
         inbound,
         moreAvailable,
         creationDate,
         senderEmailAddress,
         isPINMessage,
         besVerificationState,
         besSignerCertificateHash,
         besNoVerifyReason,
         manager,
         context,
         oldField
      );
      this._cmsSignedDataInputStream = cmsSignedDataInputStream;
      this._context = context;
      this.initialize();
   }

   @Override
   protected final void getSignerCertificateWithoutVerifying() {
      if (super._besSignerCertificateHash != null && super._secureEmailCertificateServers.length == 0) {
         KeyStore keyStore = super._secureEmailFactory.getPreferredKeyStore();
         keyStore.addIndex((KeyStoreIndex)(new Object()));
         Enumeration enumeration = keyStore.elements(4966172969402917741L, super._besSignerCertificateHash);

         while (super._signerCertificate == null && enumeration.hasMoreElements()) {
            KeyStoreData keyStoreData = (KeyStoreData)enumeration.nextElement();
            super._signerCertificate = keyStoreData.getCertificate();
         }
      }
   }

   @Override
   protected final Certificate[] getIncludedCertificates() {
      return this._cmsSignedDataInputStream == null ? null : this._cmsSignedDataInputStream.getCertificates();
   }

   @Override
   protected final boolean isSignatureVerificationPossible() {
      return this._cmsSignedDataInputStream != null && this._cmsSignedDataInputStream.isVerificationPossible();
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   protected final void verifySignature() {
      if (this._cmsSignedDataInputStream == null) {
         super._signatureStatus.setStatus(4);
      } else {
         CMSEntityIdentifier[] entities = this._cmsSignedDataInputStream.getSigners();
         if (entities == null) {
            super._signatureStatus.setStatus(4);
         } else {
            Exception lastThrown = null;

            for (int i = 0; i < entities.length; i++) {
               try {
                  CMSEntityIdentifier tempEntity = entities[i];
                  Certificate tempCertificate = this.locateEntityCertificate(tempEntity);
                  if (tempCertificate != null) {
                     this._signerEntity = tempEntity;
                     super._signerCertificate = tempCertificate;
                     this._cmsSignedDataInputStream.verify(tempEntity, tempCertificate);
                     super._signatureStatus.setStatus(1);
                     return;
                  }
               } catch (Throwable var7) {
                  lastThrown = e;
                  continue;
               }
            }

            if (lastThrown != null) {
               super._signatureStatus.setThrowable(lastThrown);
            } else {
               super._signatureStatus.setStatus(3);
            }
         }
      }
   }

   @Override
   protected final String getSignatureDigestName() {
      return this._cmsSignedDataInputStream.getSignatureDigestName(this._signerEntity);
   }

   private final Certificate locateEntityCertificate(CMSEntityIdentifier param1) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 0
      // 01: getfield net/rim/device/apps/internal/secureemail/SecureEmailSignatureField._secureEmailCertificateServers [Lnet/rim/device/apps/internal/secureemail/server/SecureEmailCertificateServer;
      // 04: arraylength
      // 05: istore 2
      // 06: iload 2
      // 07: ifne 16
      // 0a: aload 0
      // 0b: getfield net/rim/device/apps/internal/secureemail/encodings/smime/SMIMESignatureField._cmsSignedDataInputStream Lnet/rim/device/api/crypto/cms/CMSSignedDataInputStream;
      // 0e: aload 1
      // 0f: invokevirtual net/rim/device/api/crypto/cms/CMSSignedDataInputStream.getSignerCertificate (Lnet/rim/device/api/crypto/cms/CMSEntityIdentifier;)Lnet/rim/device/api/crypto/certificate/Certificate;
      // 12: areturn
      // 13: astore 3
      // 14: aconst_null
      // 15: areturn
      // 16: aload 0
      // 17: getfield net/rim/device/apps/internal/secureemail/encodings/smime/SMIMESignatureField._cmsSignedDataInputStream Lnet/rim/device/api/crypto/cms/CMSSignedDataInputStream;
      // 1a: invokevirtual net/rim/device/api/crypto/cms/CMSSignedDataInputStream.getCertificates ()[Lnet/rim/device/api/crypto/certificate/Certificate;
      // 1d: astore 3
      // 1e: aload 3
      // 1f: ifnull 27
      // 22: aload 3
      // 23: arraylength
      // 24: goto 28
      // 27: bipush 0
      // 28: istore 4
      // 2a: bipush 0
      // 2b: istore 5
      // 2d: iload 5
      // 2f: iload 4
      // 31: if_icmpge 4d
      // 34: aload 3
      // 35: iload 5
      // 37: aaload
      // 38: astore 6
      // 3a: aload 0
      // 3b: aload 1
      // 3c: aload 6
      // 3e: invokespecial net/rim/device/apps/internal/secureemail/encodings/smime/SMIMESignatureField.certificateMatches (Lnet/rim/device/api/crypto/cms/CMSEntityIdentifier;Lnet/rim/device/api/crypto/certificate/Certificate;)Z
      // 41: ifeq 47
      // 44: aload 6
      // 46: areturn
      // 47: iinc 5 1
      // 4a: goto 2d
      // 4d: aload 0
      // 4e: getfield net/rim/device/apps/internal/secureemail/SecureEmailSignatureField._moreAvailable Z
      // 51: ifne e4
      // 54: aload 0
      // 55: getfield net/rim/device/apps/internal/secureemail/SecureEmailSignatureField._senderEmailAddress Ljava/lang/String;
      // 58: ifnull e4
      // 5b: bipush 0
      // 5c: istore 5
      // 5e: iload 5
      // 60: iload 2
      // 61: if_icmpge e4
      // 64: aload 0
      // 65: getfield net/rim/device/apps/internal/secureemail/SecureEmailSignatureField._secureEmailCertificateServers [Lnet/rim/device/apps/internal/secureemail/server/SecureEmailCertificateServer;
      // 68: iload 5
      // 6a: aaload
      // 6b: aload 0
      // 6c: getfield net/rim/device/apps/internal/secureemail/SecureEmailSignatureField._senderEmailAddress Ljava/lang/String;
      // 6f: bipush 0
      // 70: aload 0
      // 71: invokevirtual net/rim/device/apps/internal/secureemail/SecureEmailSignatureField.getServerOperationListener ()Lnet/rim/device/apps/internal/secureemail/server/SecureEmailServerOperationListener;
      // 74: invokeinterface net/rim/device/apps/internal/secureemail/server/SecureEmailCertificateServer.getCertificateByEmailAddress (Ljava/lang/String;ILnet/rim/device/apps/internal/secureemail/server/SecureEmailServerOperationListener;)Lnet/rim/device/api/crypto/certificate/Certificate; 4
      // 79: astore 6
      // 7b: aload 0
      // 7c: aload 1
      // 7d: aload 6
      // 7f: invokespecial net/rim/device/apps/internal/secureemail/encodings/smime/SMIMESignatureField.certificateMatches (Lnet/rim/device/api/crypto/cms/CMSEntityIdentifier;Lnet/rim/device/api/crypto/certificate/Certificate;)Z
      // 82: ifeq 88
      // 85: aload 6
      // 87: areturn
      // 88: aload 6
      // 8a: dup
      // 8b: instanceof java/lang/Object
      // 8e: ifne 95
      // 91: pop
      // 92: goto de
      // 95: checkcast java/lang/Object
      // 98: astore 7
      // 9a: aload 7
      // 9c: invokevirtual net/rim/device/api/crypto/certificate/pgp/PGPCertificate.containsEmbeddedX509Certificates ()Z
      // 9f: ifeq de
      // a2: aload 7
      // a4: invokevirtual net/rim/device/api/crypto/certificate/pgp/PGPCertificate.getEmbeddedX509Certificates ()[Lnet/rim/device/api/crypto/certificate/Certificate;
      // a7: astore 8
      // a9: aload 8
      // ab: arraylength
      // ac: istore 9
      // ae: bipush 0
      // af: istore 10
      // b1: iload 10
      // b3: iload 9
      // b5: if_icmpge de
      // b8: aload 8
      // ba: iload 10
      // bc: aaload
      // bd: astore 11
      // bf: aload 0
      // c0: aload 1
      // c1: aload 11
      // c3: invokespecial net/rim/device/apps/internal/secureemail/encodings/smime/SMIMESignatureField.certificateMatches (Lnet/rim/device/api/crypto/cms/CMSEntityIdentifier;Lnet/rim/device/api/crypto/certificate/Certificate;)Z
      // c6: ifeq cc
      // c9: aload 11
      // cb: areturn
      // cc: iinc 10 1
      // cf: goto b1
      // d2: astore 6
      // d4: goto de
      // d7: astore 6
      // d9: goto de
      // dc: astore 6
      // de: iinc 5 1
      // e1: goto 5e
      // e4: aconst_null
      // e5: areturn
      // try (6 -> 10): 11 null
      // try (54 -> 71): 107 null
      // try (72 -> 104): 107 null
      // try (105 -> 107): 107 null
      // try (54 -> 71): 109 null
      // try (72 -> 104): 109 null
      // try (105 -> 107): 109 null
      // try (54 -> 71): 111 null
      // try (72 -> 104): 111 null
      // try (105 -> 107): 111 null
   }

   private final boolean certificateMatches(CMSEntityIdentifier entity, Certificate certificate) {
      if (certificate instanceof Object) {
         X509Certificate x509Certificate = (X509Certificate)certificate;
         if (!SecureEmailUtilities.isCertificateSupported(x509Certificate, 2) || x509Certificate.queryKeyUsage(1) == 0) {
            return false;
         }

         if (entity.getSerialNumber() != null && entity.getIssuer() != null) {
            if (Arrays.equals(x509Certificate.getSerialNumber(), entity.getSerialNumber())
               && ObjectUtilities.objEqual(x509Certificate.getIssuer(), entity.getIssuer())) {
               return true;
            }

            return false;
         }

         if (entity.getSubjectKeyIdentifier() != null) {
            return Arrays.equals(x509Certificate.getSubjectKeyIdentifier(), entity.getSubjectKeyIdentifier());
         }
      }

      return false;
   }

   @Override
   protected final void checkSendingDate() {
      if (this._cmsSignedDataInputStream != null
         && this._cmsSignedDataInputStream.isSigned()
         && super._signatureStatus.getStatus() == 1
         && super._trustStatus.getStatus() == 1) {
         SMIMEUtilities smimeUtilities = (SMIMEUtilities)SMIMEFactory.getInstance().getUtilities();
         long signedDate = smimeUtilities.getSignedDate(this._cmsSignedDataInputStream, this._signerEntity);
         if (signedDate != -1 && Math.abs(super._creationDate - signedDate) >= 1800000) {
            super._trustStatus.setStatus(14);
         }
      }
   }

   @Override
   protected final void verifyTrust() {
      super.verifyTrust();
      if (super._signatureStatus.getStatus() == 1 && super._trustStatus.getStatus() == 1 && this._cmsSignedDataInputStream != null && this._context != null) {
         SMIMEUtilities smimeUtilities = (SMIMEUtilities)SMIMEFactory.getInstance().getUtilities();
         smimeUtilities.processCapabilities(this._context, this._cmsSignedDataInputStream);
         this._context = null;
      }
   }

   @Override
   protected final SecureEmailSignatureField$SignatureStatusField createSignatureStatusField(
      Application displayApp, int initialSignatureStatus, String signerName, String signatureStatusDetails
   ) {
      return new SMIMESignatureField$SMIMESignatureStatusField(this, displayApp, initialSignatureStatus, signerName, signatureStatusDetails);
   }

   @Override
   protected final SecureEmailSignatureField$TrustStatusField createTrustStatusField(Application displayApp, int initialTrustStatus, String trustStatusDetails) {
      return new SMIMESignatureField$SMIMETrustStatusField(this, displayApp, initialTrustStatus, trustStatusDetails);
   }

   @Override
   public final void makeDelegateContextMenu(ContextMenu contextMenu) {
      super.makeDelegateContextMenu(contextMenu);
      if (super._signerCertificate != null && super._signerCertificateChain != null) {
         KeyStore deviceKeyStore = DeviceKeyStore.getInstance();
         if (!deviceKeyStore.isMember(super._signerCertificate)) {
            if (this._importSignerCertificateMenuItem == null) {
               this._importSignerCertificateMenuItem = (VerbMenuItem)(new Object(
                  (Verb)(new Object(SMIMEResources.getString(2), super._signerCertificateChain, deviceKeyStore)), Integer.MAX_VALUE
               ));
            }

            contextMenu.addItem(this._importSignerCertificateMenuItem);
         }

         TrustedKeyStore trustedKeyStore = (TrustedKeyStore)TrustedKeyStore.getInstance();
         if ((super._signerCertificateChainProperties & 8) != 0 && trustedKeyStore.isAllowed(super._signerCertificateChain[0])) {
            if (this._trustSignerCertificateMenuItem == null) {
               this._trustSignerCertificateMenuItem = (VerbMenuItem)(new Object(
                  (Verb)(new Object(SMIMEResources.getString(6), super._signerCertificateChain, deviceKeyStore)), Integer.MAX_VALUE
               ));
            }

            contextMenu.addItem(this._trustSignerCertificateMenuItem);
         }

         if (this._fetchSignerCertificateStatusMenuItem == null) {
            this._fetchSignerCertificateStatusMenuItem = (VerbMenuItem)(new Object(
               (Verb)(new Object(SMIMEResources.getBundle(), 7, super._signerCertificateChain, false, deviceKeyStore)), Integer.MAX_VALUE
            ));
         }

         contextMenu.addItem(this._fetchSignerCertificateStatusMenuItem);
         if (this._fetchSignerCertificateChainStatusMenuItem == null) {
            this._fetchSignerCertificateChainStatusMenuItem = (VerbMenuItem)(new Object(
               (Verb)(new Object(SMIMEResources.getBundle(), 8, super._signerCertificateChain, true, deviceKeyStore)), Integer.MAX_VALUE
            ));
         }

         contextMenu.addItem(this._fetchSignerCertificateChainStatusMenuItem);
      }
   }

   @Override
   protected final MatchProvider createSignerCertificateMatchProvider() {
      if (this._cmsSignedDataInputStream != null) {
         try {
            CMSEntityIdentifier[] cmsEntityIdentifiers = this._cmsSignedDataInputStream.getSigners();
            if (cmsEntityIdentifiers != null) {
               return new SMIMECertificateMatchProvider(cmsEntityIdentifiers);
            }
         } finally {
            return super._besSignerCertificateHash != null ? new SMIMECertificateMatchProvider(super._besSignerCertificateHash) : null;
         }
      }

      return super._besSignerCertificateHash != null ? new SMIMECertificateMatchProvider(super._besSignerCertificateHash) : null;
   }
}
