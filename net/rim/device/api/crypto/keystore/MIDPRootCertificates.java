package net.rim.device.api.crypto.keystore;

import net.rim.device.api.crypto.SHA1Digest;
import net.rim.device.api.crypto.certificate.CertificateStatus;
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.Branding;
import net.rim.device.api.system.CodeModuleManager;
import net.rim.device.api.system.CodeSigningKey;
import net.rim.device.api.system.ControlledAccess;
import net.rim.device.api.util.Arrays;
import net.rim.device.internal.system.InternalServices;
import net.rim.device.internal.system.MIDletSecurity;
import net.rim.vm.PersistentInteger;

final class MIDPRootCertificates {
   private static final long ID = -32671864274826206L;
   private static final byte[] US_cingular_Trusted3rd = new byte[]{
      4, 4, 4, 4, 4, 4, 4, 4, 4, 3, 3, 3, 3, 4, 4, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 0, 3, 3, 4, 4, 4, 4, 0, 0, 0, 3
   };
   private static final byte[] US_cingular_SemiTrusted = new byte[]{
      4, 4, 4, 4, 4, 4, 4, 4, 4, 3, 3, 3, 3, 4, 4, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 0, 3, 3, 4, 4, 4, 4, 0, 0, 0, 3
   };
   private static final byte[] US_tmobile_SemiTrusted = new byte[]{
      3, 3, 3, 3, 3, 3, 3, 3, 4, 2, 2, 2, 2, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 0, 3, 3, 4, 4, 4, 4, 0, 0, 0, 3
   };
   private static final byte[] EU_tmobile_UnTrusted = new byte[]{
      3, 3, 3, 3, 3, 3, 3, 3, 1, 1, 1, 1, 3, 3, 3, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 4, 4, 4, 4, 1, 1, 1, 3
   };
   private static final byte[] EU_tmobile_SemiTrusted = new byte[]{
      3, 3, 3, 3, 3, 3, 3, 3, 4, 2, 2, 2, 2, 2, 2, 2, 2, 2, 3, 3, 3, 3, 3, 3, 3, 3, 3, 0, 3, 3, 4, 4, 4, 4, 0, 0, 0, 3
   };
   private static final byte[] EU_tmobile_Trusted = new byte[]{
      6, 6, 6, 6, 6, 6, 6, 6, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 0, 3, 3, 4, 4, 4, 4, 0, 0, 0, 3
   };
   private static final byte[] US_NextelTelus_UnTrusted = new byte[]{
      4, 4, 4, 4, 4, 4, 4, 4, 1, 1, 1, 1, 4, 4, 4, 1, 1, 1, 1, 1, 6, 6, 6, 6, 6, 6, 6, 0, 1, 1, 4, 4, 4, 4, 1, 1, 1, 3
   };
   private static final byte[] MIDP_IDENTIFIED_THIRD_PARTY_PROTECTION_DOMAIN = new byte[]{
      4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 0, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4
   };
   private static final long CERTSTATUSTIME = 1158007107581L;

   private static final byte[] nothingAllowed() {
      byte[] base = new byte[40];
      Arrays.fill(base, (byte)0);
      return base;
   }

   private static final byte[] everythingAllowed() {
      byte[] base = new byte[40];
      Arrays.fill(base, (byte)6);
      return base;
   }

   private static final byte[] newPolicy(byte[] prefix) {
      byte[] policy = nothingAllowed();
      int len = policy.length;
      if (prefix.length < len) {
         len = prefix.length;
      }

      System.arraycopy(prefix, 0, policy, 0, len);
      return policy;
   }

   private MIDPRootCertificates() {
   }

   public static final void main(String[] args) {
      int vendorId = -1;

      label76:
      try {
         vendorId = Branding.getVendorId();
         switch (vendorId) {
            case 103:
            case 126:
               injectUS_NextelTelusDefaultPolicy();
               break;
            case 114:
            case 123:
            case 134:
            case 169:
            case 170:
            case 171:
            case 201:
            case 202:
            case 250:
               injectEU_TMobileDefaultPolicy();
         }
      } finally {
         break label76;
      }

      if (!MIDletSecurity.prepareForRootDomainInstallation()) {
         System.out.println("MIDP Security install failed to prepare");
      } else {
         int id = PersistentInteger.getId(-32671864274826206L, 0);
         int downloadTime = PersistentInteger.get(id);
         int currentDownloadTime = (int)CodeModuleManager.getModuleDownloadTimestamp(ApplicationDescriptor.currentApplicationDescriptor().getModuleHandle());
         if (downloadTime != currentDownloadTime) {
            try {
               switch (vendorId) {
                  case 100:
                     injectUS_TMobileCertificates();
                     break;
                  case 102:
                     injectUS_CingularWirelessCertificates();
                     break;
                  case 107:
                     inject_RogersCertificates();
                     break;
                  case 109:
                     inject_BellCertificates();
                     break;
                  case 114:
                  case 123:
                  case 134:
                  case 169:
                  case 170:
                  case 171:
                  case 201:
                  case 202:
                  case 250:
                     injectEU_TMobileCertificates();
                     break;
                  case 118:
                  case 120:
                  case 124:
                  case 130:
                  case 132:
                  case 133:
                  case 137:
                  case 138:
                  case 139:
                  case 140:
                  case 143:
                  case 148:
                  case 152:
                  case 153:
                  case 161:
                  case 166:
                  case 167:
                  case 168:
                  case 176:
                  case 188:
                  case 192:
                  case 193:
                  case 194:
                  case 195:
                  case 196:
                  case 197:
                  case 198:
                  case 199:
                  case 200:
                  case 222:
                  case 231:
                  case 234:
                  case 235:
                  case 238:
                  case 247:
                     inject_VodafoneCertificates();
                     break;
                  case 129:
                  case 136:
                  case 215:
                  case 216:
                     inject_TelefonicaCertificates();
                     break;
                  case 236:
                     inject_RelianceCertificates();
               }

               PersistentInteger.set(id, currentDownloadTime);
            } finally {
               return;
            }
         }
      }
   }

   private static final void installUntrustedPolicy(byte[] policy) {
      policy = newPolicy(policy);
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      CodeSigningKey rri = CodeSigningKey.getBuiltInKey(51);
      ControlledAccess ca = new ControlledAccess(policy, rri);
      ar.put(-8029111670665436014L, ca);
   }

   private static final void installRootDomain(byte[] root, byte[] policy) {
      SHA1Digest digest = new SHA1Digest();
      digest.update(root);
      MIDletSecurity.installRootDomain(digest.getDigest(), policy);
   }

   private static final void injectUS_NextelTelusDefaultPolicy() {
      installUntrustedPolicy(US_NextelTelus_UnTrusted);
   }

   private static final void inject_VodafoneCertificates() {
      CertificateStatus status = new CertificateStatus(0, 1158007107581L, 1158007107581L, 0, 0, -1);
      String x509 = "X509";
      TrustedKeyStore trusted = (TrustedKeyStore)TrustedKeyStore.getInstance();
      DeviceKeyStore device = (DeviceKeyStore)DeviceKeyStore.getInstance();
      KeyStoreUtilitiesInternal.addRootCertificate(
         trusted, device, "Vodafone", MIDPRootCertificates$VODAFONE_ROOT_CERTIFICATES.VODAFONE_TOP_LEVEL_DOMAIN_CA, x509, status
      );
      KeyStoreUtilitiesInternal.addRootCertificate(
         trusted, device, "Vodafone Operator Domain", MIDPRootCertificates$VODAFONE_ROOT_CERTIFICATES.VODAFONE_OPERATOR_DOMAIN_CA, x509, status
      );
      byte[] policyInstall = everythingAllowed();
      installRootDomain(MIDPRootCertificates$VODAFONE_ROOT_CERTIFICATES.VODAFONE_TOP_LEVEL_DOMAIN_CA, policyInstall);
      installRootDomain(MIDPRootCertificates$VODAFONE_ROOT_CERTIFICATES.VODAFONE_OPERATOR_DOMAIN_CA, policyInstall);
   }

   private static final void inject_RelianceCertificates() {
      CertificateStatus status = new CertificateStatus(0, 1158007107581L, 1158007107581L, 0, 0, -1);
      String x509 = "X509";
      TrustedKeyStore trusted = (TrustedKeyStore)TrustedKeyStore.getInstance();
      DeviceKeyStore device = (DeviceKeyStore)DeviceKeyStore.getInstance();
      KeyStoreUtilitiesInternal.addRootCertificate(
         trusted, device, "Reliance Infocomm Ca", MIDPRootCertificates$RELIANCE_ROOT_CERTIFICATES.RELIANCE_INFOCOMM_CA, x509, status
      );
      byte[] policyInstall = everythingAllowed();
      installRootDomain(MIDPRootCertificates$RELIANCE_ROOT_CERTIFICATES.RELIANCE_INFOCOMM_CA, policyInstall);
   }

   private static final void inject_TelefonicaCertificates() {
      CertificateStatus status = new CertificateStatus(0, 1158007107581L, 1158007107581L, 0, 0, -1);
      String x509 = "X509";
      TrustedKeyStore trusted = (TrustedKeyStore)TrustedKeyStore.getInstance();
      DeviceKeyStore device = (DeviceKeyStore)DeviceKeyStore.getInstance();
      KeyStoreUtilitiesInternal.addRootCertificate(
         trusted, device, "Telefonica Moviles Espana SA", MIDPRootCertificates$TELEFONICA_ROOT_CERTIFICATES.TELEFONICA_MOVILES_ESPANA_SA, x509, status
      );
      byte[] policyInstall = everythingAllowed();
      installRootDomain(MIDPRootCertificates$TELEFONICA_ROOT_CERTIFICATES.TELEFONICA_MOVILES_ESPANA_SA, policyInstall);
   }

   private static final void injectUS_CingularWirelessCertificates() {
      CertificateStatus status = new CertificateStatus(0, 1158007107581L, 1158007107581L, 0, 0, -1);
      String x509 = "X509";
      TrustedKeyStore trusted = (TrustedKeyStore)TrustedKeyStore.getInstance();
      DeviceKeyStore device = (DeviceKeyStore)DeviceKeyStore.getInstance();
      KeyStoreUtilitiesInternal.addRootCertificate(
         trusted, device, "us.cingular.midp20.FullTrust", MIDPRootCertificates$CINGULAR_ROOT_CERTIFICATES.US_CINGULAR_FULL_TRUST, x509, status
      );
      KeyStoreUtilitiesInternal.addRootCertificate(
         trusted, device, "us.cingular.midp20.SemiTrust", MIDPRootCertificates$CINGULAR_ROOT_CERTIFICATES.US_CINGULAR_SEMI_TRUST, x509, status
      );
      KeyStoreUtilitiesInternal.addRootCertificate(
         trusted, device, "us.cingular.midp20.Trusted3rd", MIDPRootCertificates$CINGULAR_ROOT_CERTIFICATES.US_CINGULAR_TRUSTED_3RD, x509, status
      );
      byte[] policyInstall1 = everythingAllowed();
      installRootDomain(MIDPRootCertificates$CINGULAR_ROOT_CERTIFICATES.US_CINGULAR_FULL_TRUST, policyInstall1);
      byte[] policyInstall2 = newPolicy(US_cingular_SemiTrusted);
      installRootDomain(MIDPRootCertificates$CINGULAR_ROOT_CERTIFICATES.US_CINGULAR_SEMI_TRUST, policyInstall2);
      byte[] policyInstall3 = newPolicy(US_cingular_Trusted3rd);
      installRootDomain(MIDPRootCertificates$CINGULAR_ROOT_CERTIFICATES.US_CINGULAR_TRUSTED_3RD, policyInstall3);
   }

   private static final void injectUS_TMobileCertificates() {
      switch (InternalServices.getHardwareID()) {
         case -2080371453:
            injectUS_TMobileCertificatesPositron();
            return;
         case -2080371197:
         case -1929376253:
            injectUS_TMobileCertificatesGammarayAndDeltaray();
            return;
         case -2080370941:
            injectUS_TMobileCertificatesMamaBear();
            return;
         case -1929376509:
            injectUS_TMobileCertificatesComet();
         default:
            injectUS_TMobileCertificatesDefault();
      }
   }

   private static final void injectUS_TMobileCertificatesPositron() {
      CertificateStatus status = new CertificateStatus(0, 1158007107581L, 1158007107581L, 0, 0, -1);
      String x509 = "X509";
      TrustedKeyStore trusted = (TrustedKeyStore)TrustedKeyStore.getInstance();
      DeviceKeyStore device = (DeviceKeyStore)DeviceKeyStore.getInstance();
      KeyStoreUtilitiesInternal.addRootCertificate(
         trusted, device, "us.tmobile.midp20.FullTrust", MIDPRootCertificates$US_TMOBILE_ROOT_CERTIFICATES.US_TMOBILE_FULL_TRUST_POSITRON, x509, status
      );
      KeyStoreUtilitiesInternal.addRootCertificate(
         trusted, device, "us.tmobile.midp20.SemiTrust", MIDPRootCertificates$US_TMOBILE_ROOT_CERTIFICATES.US_TMOBILE_SEMI_TRUST_POSITRON, x509, status
      );
      byte[] policyInstall1 = everythingAllowed();
      installRootDomain(MIDPRootCertificates$US_TMOBILE_ROOT_CERTIFICATES.US_TMOBILE_FULL_TRUST_POSITRON, policyInstall1);
      byte[] policyInstall2 = newPolicy(US_tmobile_SemiTrusted);
      installRootDomain(MIDPRootCertificates$US_TMOBILE_ROOT_CERTIFICATES.US_TMOBILE_SEMI_TRUST_POSITRON, policyInstall2);
   }

   private static final void injectUS_TMobileCertificatesGammarayAndDeltaray() {
      CertificateStatus status = new CertificateStatus(0, 1158007107581L, 1158007107581L, 0, 0, -1);
      String x509 = "X509";
      TrustedKeyStore trusted = (TrustedKeyStore)TrustedKeyStore.getInstance();
      DeviceKeyStore device = (DeviceKeyStore)DeviceKeyStore.getInstance();
      KeyStoreUtilitiesInternal.addRootCertificate(
         trusted, device, "us.tmobile.midp20.FullTrust", MIDPRootCertificates$US_TMOBILE_ROOT_CERTIFICATES.US_TMOBILE_FULL_TRUST_GAMMARAY, x509, status
      );
      KeyStoreUtilitiesInternal.addRootCertificate(
         trusted, device, "us.tmobile.midp20.SemiTrust", MIDPRootCertificates$US_TMOBILE_ROOT_CERTIFICATES.US_TMOBILE_SEMI_TRUST_GAMMARAY, x509, status
      );
      byte[] policyInstall1 = everythingAllowed();
      installRootDomain(MIDPRootCertificates$US_TMOBILE_ROOT_CERTIFICATES.US_TMOBILE_FULL_TRUST_GAMMARAY, policyInstall1);
      byte[] policyInstall2 = newPolicy(US_tmobile_SemiTrusted);
      installRootDomain(MIDPRootCertificates$US_TMOBILE_ROOT_CERTIFICATES.US_TMOBILE_SEMI_TRUST_GAMMARAY, policyInstall2);
   }

   private static final void injectUS_TMobileCertificatesMamaBear() {
      CertificateStatus status = new CertificateStatus(0, 1158007107581L, 1158007107581L, 0, 0, -1);
      String x509 = "X509";
      TrustedKeyStore trusted = (TrustedKeyStore)TrustedKeyStore.getInstance();
      DeviceKeyStore device = (DeviceKeyStore)DeviceKeyStore.getInstance();
      KeyStoreUtilitiesInternal.addRootCertificate(
         trusted, device, "us.tmobile.midp20.FullTrust", MIDPRootCertificates$US_TMOBILE_ROOT_CERTIFICATES.US_TMOBILE_FULL_TRUST_MAMABEAR, x509, status
      );
      KeyStoreUtilitiesInternal.addRootCertificate(
         trusted, device, "us.tmobile.midp20.SemiTrust", MIDPRootCertificates$US_TMOBILE_ROOT_CERTIFICATES.US_TMOBILE_SEMI_TRUST_MAMABEAR, x509, status
      );
      byte[] policyInstall1 = everythingAllowed();
      installRootDomain(MIDPRootCertificates$US_TMOBILE_ROOT_CERTIFICATES.US_TMOBILE_FULL_TRUST_MAMABEAR, policyInstall1);
      byte[] policyInstall2 = newPolicy(US_tmobile_SemiTrusted);
      installRootDomain(MIDPRootCertificates$US_TMOBILE_ROOT_CERTIFICATES.US_TMOBILE_SEMI_TRUST_MAMABEAR, policyInstall2);
   }

   private static final void injectUS_TMobileCertificatesComet() {
      CertificateStatus status = new CertificateStatus(0, 1158007107581L, 1158007107581L, 0, 0, -1);
      String x509 = "X509";
      TrustedKeyStore trusted = (TrustedKeyStore)TrustedKeyStore.getInstance();
      DeviceKeyStore device = (DeviceKeyStore)DeviceKeyStore.getInstance();
      KeyStoreUtilitiesInternal.addRootCertificate(
         trusted, device, "T-Mobile USA Full Trust", MIDPRootCertificates$US_TMOBILE_ROOT_CERTIFICATES.US_TMOBILE_FULL_TRUST_COMET, x509, status
      );
      KeyStoreUtilitiesInternal.addRootCertificate(
         trusted, device, "T-Mobile USA Semi Trust", MIDPRootCertificates$US_TMOBILE_ROOT_CERTIFICATES.US_TMOBILE_SEMI_TRUST_COMET, x509, status
      );
      byte[] policyInstall1 = everythingAllowed();
      installRootDomain(MIDPRootCertificates$US_TMOBILE_ROOT_CERTIFICATES.US_TMOBILE_FULL_TRUST_COMET, policyInstall1);
      byte[] policyInstall2 = newPolicy(US_tmobile_SemiTrusted);
      installRootDomain(MIDPRootCertificates$US_TMOBILE_ROOT_CERTIFICATES.US_TMOBILE_SEMI_TRUST_COMET, policyInstall2);
   }

   private static final void injectUS_TMobileCertificatesDefault() {
      CertificateStatus status = new CertificateStatus(0, 1158007107581L, 1158007107581L, 0, 0, -1);
      String x509 = "X509";
      TrustedKeyStore trusted = (TrustedKeyStore)TrustedKeyStore.getInstance();
      DeviceKeyStore device = (DeviceKeyStore)DeviceKeyStore.getInstance();
      KeyStoreUtilitiesInternal.addRootCertificate(
         trusted, device, "us.tmobile.midp20.FullTrust", MIDPRootCertificates$US_TMOBILE_ROOT_CERTIFICATES.US_TMOBILE_FULL_TRUST_DEFAULT, x509, status
      );
      KeyStoreUtilitiesInternal.addRootCertificate(
         trusted, device, "us.tmobile.midp20.SemiTrust", MIDPRootCertificates$US_TMOBILE_ROOT_CERTIFICATES.US_TMOBILE_SEMI_TRUST_DEFAULT, x509, status
      );
      byte[] policyInstall1 = everythingAllowed();
      installRootDomain(MIDPRootCertificates$US_TMOBILE_ROOT_CERTIFICATES.US_TMOBILE_FULL_TRUST_DEFAULT, policyInstall1);
      byte[] policyInstall2 = newPolicy(US_tmobile_SemiTrusted);
      installRootDomain(MIDPRootCertificates$US_TMOBILE_ROOT_CERTIFICATES.US_TMOBILE_SEMI_TRUST_DEFAULT, policyInstall2);
   }

   private static final void injectEU_TMobileCertificates() {
      CertificateStatus status = new CertificateStatus(0, 1158007107581L, 1158007107581L, 0, 0, -1);
      String x509 = "X509";
      TrustedKeyStore trusted = (TrustedKeyStore)TrustedKeyStore.getInstance();
      DeviceKeyStore device = (DeviceKeyStore)DeviceKeyStore.getInstance();
      KeyStoreUtilitiesInternal.addRootCertificate(
         trusted, device, "eu.tmobile.midp20.CARoot", MIDPRootCertificates$EU_TMOBILE_ROOT_CERTIFICATES.EU_TMOBILE_CA_ROOT, x509, status
      );
      KeyStoreUtilitiesInternal.addRootCertificate(
         trusted, device, "eu.tmobile.midp20.SemiTrust", MIDPRootCertificates$EU_TMOBILE_ROOT_CERTIFICATES.EU_TMOBILE_SEMI_TRUST, x509, status
      );
      KeyStoreUtilitiesInternal.addRootCertificate(
         trusted, device, "us.tmobile.midp20.FullTrust", MIDPRootCertificates$EU_TMOBILE_ROOT_CERTIFICATES.EU_TMOBILE_FULL_TRUST, x509, status
      );
      byte[] policyInstall1 = newPolicy(EU_tmobile_Trusted);
      installRootDomain(MIDPRootCertificates$EU_TMOBILE_ROOT_CERTIFICATES.EU_TMOBILE_FULL_TRUST, policyInstall1);
      byte[] policyInstall2 = newPolicy(EU_tmobile_SemiTrusted);
      installRootDomain(MIDPRootCertificates$EU_TMOBILE_ROOT_CERTIFICATES.EU_TMOBILE_SEMI_TRUST, policyInstall2);
   }

   private static final void inject_RogersCertificates() {
      CertificateStatus status = new CertificateStatus(0, 1158007107581L, 1158007107581L, 0, 0, -1);
      String x509 = "X509";
      TrustedKeyStore trusted = (TrustedKeyStore)TrustedKeyStore.getInstance();
      DeviceKeyStore device = (DeviceKeyStore)DeviceKeyStore.getInstance();
      KeyStoreUtilitiesInternal.addRootCertificate(
         trusted, device, "Rogers Wireless Root CA", MIDPRootCertificates$ROGERS_ROOT_CERTIFICATES.ROGERS_WIRELESS_ROOT_CA, x509, status
      );
      KeyStoreUtilitiesInternal.addRootCertificate(
         trusted, device, "Rogers Wireless Test Root CA", MIDPRootCertificates$ROGERS_ROOT_CERTIFICATES.ROGERS_WIRELESS_TEST_ROOT_CA, x509, status
      );
      byte[] policyInstall1 = everythingAllowed();
      installRootDomain(MIDPRootCertificates$ROGERS_ROOT_CERTIFICATES.ROGERS_WIRELESS_ROOT_CA, policyInstall1);
      installRootDomain(MIDPRootCertificates$ROGERS_ROOT_CERTIFICATES.ROGERS_WIRELESS_TEST_ROOT_CA, policyInstall1);
   }

   private static final void inject_BellCertificates() {
      VerisignRootCertificates.injectCertificates();
      byte[] policyInstall = newPolicy(MIDP_IDENTIFIED_THIRD_PARTY_PROTECTION_DOMAIN);
      installRootDomain(VerisignRootCertificates.getClass3PublicPrimaryCertificateAuthorityCert(), policyInstall);
   }

   private static final void injectEU_TMobileDefaultPolicy() {
      installUntrustedPolicy(EU_tmobile_UnTrusted);
   }
}
