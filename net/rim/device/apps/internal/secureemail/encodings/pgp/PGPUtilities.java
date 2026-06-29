package net.rim.device.apps.internal.secureemail.encodings.pgp;

import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.crypto.certificate.CertificateChainProperties;
import net.rim.device.api.crypto.certificate.CertificateUtilities;
import net.rim.device.api.crypto.certificate.LDAPCertificateFetch;
import net.rim.device.api.crypto.certificate.pgp.PGPCertificate;
import net.rim.device.api.crypto.certificate.pgp.PGPKeyIDKeyStoreIndex;
import net.rim.device.api.crypto.certificate.pgp.PGPLDAPCertificateFetch;
import net.rim.device.api.crypto.certificate.pgp.PGPSubKeyProperties;
import net.rim.device.api.crypto.keystore.KeyStore;
import net.rim.device.api.crypto.keystore.KeyStoreIndex;
import net.rim.device.api.crypto.keystore.PGPKeyStore;
import net.rim.device.api.crypto.keystore.TrustedKeyStore;
import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.apps.internal.secureemail.SecureEmailUtilities;
import net.rim.device.internal.system.FIPSPolicy;

public final class PGPUtilities extends SecureEmailUtilities {
   private KeyStore _pgpKeyStore = PGPKeyStore.getInstance();
   private KeyStoreIndex _pgpKeyIDKeyStoreIndex = new PGPKeyIDKeyStoreIndex();

   PGPUtilities() {
   }

   @Override
   public final long getCertificateProperties(Certificate certificate) {
      Certificate[][] certificateThreads = CertificateUtilities.buildCertificateChains(certificate, this._pgpKeyStore);
      long[] certificateThreadProperties = CertificateChainProperties.getCertificateChainProperties(
         certificateThreads, this._pgpKeyStore, TrustedKeyStore.getInstance(), System.currentTimeMillis(), PGPFactory.getInstance().getCryptoSystemProperties()
      );
      return CertificateChainProperties.selectBestCertificateChainProperties(certificateThreadProperties);
   }

   @Override
   public final boolean isSignatureRequired() {
      return ITPolicy.getBoolean(26, 2, false);
   }

   @Override
   public final boolean isEncryptionRequired() {
      return ITPolicy.getBoolean(26, 3, false);
   }

   @Override
   public final int getITPolicyContentCiphers() {
      return FIPSPolicy.getInteger(26, 5, 63, 39) & 47;
   }

   @Override
   public final int getConstantForContentCipher(int contentCipher) {
      switch (contentCipher) {
         case 1:
            return 9;
         case 2:
            return 8;
         case 4:
            return 7;
         case 8:
            return 3;
         case 32:
            return 2;
         default:
            throw new IllegalArgumentException();
      }
   }

   @Override
   public final int getContentCipherForConstant(int constant) {
      switch (constant) {
         case 1:
         case 4:
         case 5:
         case 6:
            throw new IllegalArgumentException();
         case 2:
            return 32;
         case 3:
            return 8;
         case 7:
            return 4;
         case 8:
            return 2;
         case 9:
         default:
            return 1;
      }
   }

   @Override
   public final int getRecipientContentCiphers(String recipient) {
      return 47;
   }

   @Override
   public final int getCertificateContentCiphers(Certificate certificate) {
      int bitfield = 32;
      byte[] preferredContentCiphers = ((PGPCertificate)certificate).getPreferredSymmetricAlgorithms();
      int numPreferredContentCiphers = preferredContentCiphers == null ? 0 : preferredContentCiphers.length;

      for (int i = 0; i < numPreferredContentCiphers; i++) {
         switch (preferredContentCiphers[i]) {
            case 3:
               bitfield |= 8;
               break;
            case 7:
               bitfield |= 4;
               break;
            case 8:
               bitfield |= 2;
               break;
            case 9:
               bitfield |= 1;
         }
      }

      return bitfield;
   }

   @Override
   public final LDAPCertificateFetch getLDAPCertificateFetch() {
      return new PGPLDAPCertificateFetch();
   }

   @Override
   public final String getAlwaysBCCEmailAddress(String serviceUID) {
      if (serviceUID != null && !SecureEmailUtilities.checkITAdminBoundUID(serviceUID)) {
         return null;
      } else {
         return !ITPolicy.getBoolean(16, true) ? null : ITPolicy.getString(26, 4);
      }
   }

   public final String getUniversalServerAddress(String serviceUID) {
      return serviceUID != null && !SecureEmailUtilities.checkITAdminBoundUID(serviceUID) ? null : ITPolicy.getString(26, 8);
   }

   @Override
   protected final KeyStoreIndex getCertificateIDKeyStoreIndex() {
      return this._pgpKeyIDKeyStoreIndex;
   }

   public static final byte[] getSigningKeyID(PGPCertificate certificate) {
      long[] properties = PGPSubKeyProperties.getPGPSigningSubKeyProperties(
         certificate, System.currentTimeMillis(), PGPFactory.getInstance().getCryptoSystemProperties()
      );
      if (properties != null && properties.length != 0) {
         int bestSubKeyIndex = PGPSubKeyProperties.selectBestPGPSubKey(properties, certificate);
         if (bestSubKeyIndex < 0) {
            return certificate.getKeyID();
         }

         long bestProperties = properties[bestSubKeyIndex];
         return (bestProperties & 1024) == 0 && (bestProperties & 256) == 0 && (bestProperties & 2) == 0
            ? certificate.getSubKeyID(bestSubKeyIndex)
            : certificate.getKeyID();
      } else {
         return certificate.getKeyID();
      }
   }
}
