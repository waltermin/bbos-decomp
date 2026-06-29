package net.rim.device.api.crypto.certificate.pgp;

import java.io.InputStream;
import java.util.Enumeration;
import java.util.Vector;
import net.rim.device.api.crypto.CryptoUnsupportedOperationException;
import net.rim.device.api.crypto.DecodeException;
import net.rim.device.api.crypto.HashCodeCalculator;
import net.rim.device.api.crypto.PublicKey;
import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.crypto.certificate.CertificateDisplayField;
import net.rim.device.api.crypto.certificate.CertificateExtension;
import net.rim.device.api.crypto.certificate.CertificateStatus;
import net.rim.device.api.crypto.certificate.CertificateVerificationException;
import net.rim.device.api.crypto.certificate.DistinguishedName;
import net.rim.device.api.crypto.keystore.BackwardStatusException;
import net.rim.device.api.crypto.keystore.CertificateStatusManager;
import net.rim.device.api.crypto.keystore.KeyStore;
import net.rim.device.api.crypto.keystore.KeyStoreData;
import net.rim.device.api.crypto.keystore.PGPKeyStore;
import net.rim.device.api.crypto.keystore.PublicKeyKeyStoreIndex;
import net.rim.device.api.crypto.oid.OID;
import net.rim.device.api.crypto.pgp.PGPEncodingException;
import net.rim.device.api.crypto.pgp.PGPVerificationException;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.io.SharedInputStream;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.internal.crypto.pgp.PGPPacket;
import net.rim.device.internal.crypto.pgp.PGPPacketParser;
import net.rim.device.internal.crypto.pgp.PGPPublicKeyPacket;
import net.rim.device.internal.crypto.pgp.PGPSignaturePacket;
import net.rim.device.internal.crypto.pgp.PGPSignatureSubPacketParser;
import net.rim.device.internal.crypto.pgp.PGPUserIDPacket;
import net.rim.device.internal.crypto.pgp.PGPUtilities;
import net.rim.vm.Array;
import net.rim.vm.Memory;

public final class PGPCertificate implements Certificate {
   private Content _content;
   public static final int TRUSTED = 0;
   public static final int PARTIALLY_TRUSTED = 1;
   public static final int UNTRUSTED = 2;
   public static final int UNKNOWN = 3;
   public static String PGP_KEY = "pgpkey";
   public static String PGP_USER_ID = "pgpuserid";
   public static String PGP_CERT_ID = "pgpcertid";
   public static String PGP_KEY_TYPE = "pgpkeytype";
   public static String PGP_KEY_SIZE = "pgpkeysize";
   public static String PGP_REVOKED = "pgprevoked";
   public static String PGP_DISABLED = "pgpdisabled";
   public static String PGP_CREATE_TIME = "pgpkeycreatetime";
   public static String PGP_EXPIRE_TIME = "pgpexpiretime";
   public static String PGP_KEY_ID = "pgpkeyid";

   public final boolean isGeneratedCertificate() {
      PGPSignaturePacket[] certificationSignatures = this.getCertificationSignatures();

      for (int i = 0; i < certificationSignatures.length; i++) {
         if (Arrays.equals(this._content._keyID, certificationSignatures[i].getSignerKeyID())) {
            return true;
         }
      }

      return false;
   }

   public final boolean isPreExistingCertificate() {
      return !this.isGeneratedCertificate();
   }

   public final Certificate[] getEmbeddedX509Certificates(byte[] keyID) {
      int numEmbeddedCerts = this._content._x509EmbeddedCertificates.length;
      Certificate[] x509Certs = new Certificate[0];
      if (numEmbeddedCerts > 0) {
         for (int i = 0; i < numEmbeddedCerts; i++) {
            if (Arrays.equals(this._content._x509EmbeddedCertificates[i].getParentID(), keyID)) {
               Arrays.add(x509Certs, this._content._x509EmbeddedCertificates[i].extractX509Certificate());
            }
         }
      }

      return x509Certs;
   }

   public final Certificate[] getEmbeddedX509Certificates() {
      int numEmbeddedCerts = this._content._x509EmbeddedCertificates.length;
      if (numEmbeddedCerts <= 0) {
         return new Certificate[0];
      }

      Certificate[] x509Certs = new Certificate[numEmbeddedCerts];

      for (int i = 0; i < numEmbeddedCerts; i++) {
         x509Certs[i] = this._content._x509EmbeddedCertificates[i].extractX509Certificate();
      }

      return x509Certs;
   }

   public final boolean containsEmbeddedX509Certificates() {
      return this._content._x509EmbeddedCertificates.length > 0;
   }

   public final byte[][] getADKFingerprints() {
      byte[][] fingerprints = new byte[0][];
      PGPSignaturePacket[] certificationSignatures = this.getCertificationSignatures();
      PGPSignaturePacket[] selfSignatures = this.getSelfSignatures(certificationSignatures);
      int numSelfSignatures = selfSignatures.length;

      for (int i = 0; i < numSelfSignatures; i++) {
         Vector subPackets = selfSignatures[i].getSignatureSubPackets();
         if (subPackets != null) {
            try {
               PGPSignatureSubPacketParser parser = new PGPSignatureSubPacketParser(subPackets);
               byte[][] currentFingerprints = parser.getADKFingerprints();
               int numCurrentFingerprints = currentFingerprints == null ? 0 : currentFingerprints.length;

               for (int j = 0; j < numCurrentFingerprints; j++) {
                  Arrays.add(fingerprints, currentFingerprints[j]);
               }
            } catch (PGPEncodingException var11) {
            }
         }
      }

      return fingerprints;
   }

   public final byte[][] getSignerKeyIDs(String emailAddress) {
      PGPSignaturePacket[] signatures = this.getCertificationSignatures(emailAddress);
      byte[][] signerKeyIDs = new byte[0][];
      int numSignatures = signatures.length;

      for (int i = 0; i < numSignatures; i++) {
         byte[] currentSignerKeyID = signatures[i].getSignerKeyID();
         if (currentSignerKeyID != null) {
            Arrays.add(signerKeyIDs, currentSignerKeyID);
         }
      }

      return signerKeyIDs;
   }

   public final byte[] getPreferredSymmetricAlgorithms() {
      return this._content._preferredSymmetricAlgorithms;
   }

   public final PublicKey getPublicKey(byte[] keyID) {
      if (Arrays.equals(this._content._keyID, keyID)) {
         return this.getPublicKey();
      }

      byte[][] subKeyIDs = this._content._subKeyIDs;
      if (subKeyIDs != null) {
         int numSubKeyIDs = subKeyIDs.length;

         for (int i = 0; i < numSubKeyIDs; i++) {
            if (Arrays.equals(subKeyIDs[i], keyID)) {
               return this.getSubKey(i);
            }
         }
      }

      return null;
   }

   public final Bitmap[] getUserImages() {
      Bitmap[] userImages = new Bitmap[0];
      int numUserAttributePackets = this._content._userAttributes.length;

      for (int i = 0; i < numUserAttributePackets; i++) {
         Bitmap[] currentUserImages = this._content._userAttributes[i].getImages();
         int numCurrentUserImages = currentUserImages == null ? 0 : currentUserImages.length;

         for (int j = 0; j < numCurrentUserImages; j++) {
            Arrays.add(userImages, currentUserImages[j]);
         }
      }

      return userImages;
   }

   public final long getNotAfter(byte[] keyID) {
      if (Arrays.equals(this._content._keyID, keyID)) {
         return this._content._notAfter;
      }

      int numSubKeys = this._content._subKeys.length;

      for (int i = 0; i < numSubKeys; i++) {
         if (Arrays.equals(this._content._subKeyIDs[i], keyID)) {
            return this._content._subKeyNotAfter[i];
         }
      }

      throw new IllegalArgumentException();
   }

   public final String[] getEmailAddresses() {
      int numEmailAddresses = 0;
      int length = this._content._userIDs.length;
      String[] emailAddresses = new String[length];

      for (int i = 0; i < length; i++) {
         String currentEmailAddress = this._content._userIDs[i].getEmailAddress();
         if (currentEmailAddress != null) {
            emailAddresses[numEmailAddresses++] = currentEmailAddress;
         }
      }

      Array.resize(emailAddresses, numEmailAddresses);
      return emailAddresses;
   }

   public final long getNotBefore(byte[] keyID) {
      if (Arrays.equals(this._content._keyID, keyID)) {
         return this._content._notBefore;
      }

      int numSubKeys = this._content._subKeys.length;

      for (int i = 0; i < numSubKeys; i++) {
         if (Arrays.equals(this._content._subKeyIDs[i], keyID)) {
            return this._content._subKeyNotBefore[i];
         }
      }

      throw new IllegalArgumentException();
   }

   public final String getPrimaryUserID() {
      return this._content._primaryUserID.getUserID();
   }

   public final String[] getUserIDs() {
      int length = this._content._userIDs.length;
      String[] userIDs = new String[length];

      for (int i = 0; i < length; i++) {
         userIDs[i] = this._content._userIDs[i].getUserID();
      }

      return userIDs;
   }

   public final byte[] getKeyID() {
      return Arrays.copy(this._content._keyID);
   }

   public final byte[] getFingerprint() {
      return Arrays.copy(this._content._fingerprint);
   }

   public final CertificateStatus getStatus(byte[] keyID) {
      CertificateStatus parentStatus = CertificateStatusManager.getInstance().getStatus(this);
      if (parentStatus != null && parentStatus.getStatus() == 1) {
         return parentStatus;
      }

      if (Arrays.equals(keyID, this._content._keyID)) {
         return this.getStatus(this._content._parentKeySignatures, this._content._publicKey);
      }

      int length = this._content._subKeyIDs.length;

      for (int i = 0; i < length; i++) {
         if (Arrays.equals(keyID, this._content._subKeyIDs[i])) {
            return this.getStatus(this._content._subKeySignatures[i], this._content._subKeys[i]);
         }
      }

      throw new IllegalArgumentException();
   }

   public final byte[] getSubKeyID(int index) {
      if (index >= 0 && index < this._content._subKeyIDs.length) {
         return this._content._subKeyIDs[index];
      } else {
         throw new IllegalArgumentException();
      }
   }

   public final byte[][] getSubKeyIDs() {
      return this._content._subKeyIDs;
   }

   public final PublicKey getSubKey(int index) {
      if (index >= 0 && index < this._content._subKeys.length) {
         try {
            return this._content._subKeys[index].getPublicKey();
         } catch (PGPEncodingException var5) {
            return null;
         } finally {
            ;
         }
      } else {
         throw new IllegalArgumentException();
      }
   }

   public final int getNumSubKeys() {
      return this._content._subKeys.length;
   }

   public final PublicKey[] getSubKeys() {
      int numSubKeys = this._content._subKeys.length;
      PublicKey[] subKeys = new PublicKey[numSubKeys];

      try {
         for (int i = 0; i < numSubKeys; i++) {
            subKeys[i] = this._content._subKeys[i].getPublicKey();
         }

         return subKeys;
      } catch (PGPEncodingException var6) {
         return null;
      } finally {
         ;
      }
   }

   public final String[] getSelfSignatureDigestAlgorithms() {
      String[] digestAlgorithms = new String[0];
      PGPSignaturePacket[] certificationSignatures = this.getCertificationSignatures();
      PGPSignaturePacket[] selfSignatures = this.getSelfSignatures(certificationSignatures);

      for (int i = 0; i < selfSignatures.length; i++) {
         PGPSignaturePacket currSignature = selfSignatures[i];

         try {
            String digestName = PGPUtilities.digestConstantToString(currSignature.getHashAlgorithm());
            if (!Arrays.contains(digestAlgorithms, digestName)) {
               Arrays.add(digestAlgorithms, digestName);
            }
         } finally {
            continue;
         }
      }

      return digestAlgorithms;
   }

   public final void verify(byte[] keyID) throws DecodeException, CertificateVerificationException {
      if (Arrays.equals(keyID, this._content._keyID)) {
         this.verify();
      } else {
         boolean verified = false;
         int numSubKeys = this._content._subKeys.length;

         for (int i = 0; i < numSubKeys; i++) {
            if (Arrays.equals(keyID, this._content._subKeyIDs[i])) {
               PGPSignaturePacket[] bindingSignatures = this.getBindingSignatures(this._content._subKeySignatures[i]);
               PGPSignaturePacket[] selfSignatures = this.getSelfSignatures(bindingSignatures);
               int numSelfSignatures = selfSignatures.length;

               for (int j = 0; j < numSelfSignatures; j++) {
                  try {
                     selfSignatures[j].verify(this._content._publicKey, this._content._subKeys[i]);
                     verified = true;
                  } catch (PGPVerificationException var10) {
                  } catch (PGPEncodingException e) {
                     throw new DecodeException(e.toString());
                  }
               }
               break;
            }
         }

         if (!verified) {
            throw new CertificateVerificationException();
         }
      }
   }

   public final boolean isValid(byte[] keyID) {
      return this.isValid(System.currentTimeMillis(), keyID);
   }

   public final void verifyTrustedIntroducer(PGPCertificate signer, int depth, long date) throws CertificateVerificationException {
      byte[] keyid = signer.getKeyID();
      PGPSignaturePacket[] signatures = this.getCertificationSignatures();
      int signatureLength = signatures.length;

      for (int i = 0; i < signatureLength; i++) {
         if (Arrays.equals(keyid, signatures[i].getSignerKeyID())) {
            Vector subPackets = signatures[i].getSignatureSubPackets();
            PGPSignatureSubPacketParser parser = new PGPSignatureSubPacketParser(subPackets);
            if (parser.getTrustLevel() < depth) {
               throw new CertificateVerificationException("CTTD");
            }

            long signatureCreationTime = parser.getSignatureCreationTime();
            if (signatureCreationTime < 0 || signatureCreationTime > date) {
               throw new CertificateVerificationException("CTIT");
            }

            long signatureExpirationTime = parser.getSignatureExpirationTime();
            if (signatureExpirationTime != 0 && signatureExpirationTime + signatureCreationTime < date) {
               throw new CertificateVerificationException("CTIT");
            }

            if (parser.isRevocable() && parser.getRevocationCode() != -1) {
               throw new CertificateVerificationException("CTKR");
            }
         }
      }
   }

   public final boolean isValid(long date, byte[] keyID) {
      if (Arrays.equals(this._content._keyID, keyID)) {
         return this.isValid(date);
      }

      int numSubKeys = this._content._subKeys.length;

      for (int i = 0; i < numSubKeys; i++) {
         if (Arrays.equals(this._content._subKeyIDs[i], keyID)) {
            if (date > this._content._subKeyNotBefore[i] && date < this._content._subKeyNotAfter[i]) {
               return true;
            }

            return false;
         }
      }

      throw new IllegalArgumentException();
   }

   public final void verify(PGPCertificate certificate) throws DecodeException, CertificateVerificationException {
      boolean verified = false;
      if (certificate == null) {
         throw new IllegalArgumentException();
      }

      int numUserIDs = this._content._userIDs.length;

      for (int i = 0; i < numUserIDs; i++) {
         for (PGPSignaturePacket currentSignature : this.getCertificationSignatures(this._content._userIDSignatures[i])) {
            try {
               byte[] signerKeyID = currentSignature.getSignerKeyID();
               if (Arrays.equals(signerKeyID, certificate._content._keyID)) {
                  currentSignature.verify(this._content._publicKey, certificate.getPublicKey(), this._content._userIDs[i]);
                  verified = true;
               } else {
                  int subKeyLength = certificate._content._subKeyIDs.length;

                  for (int k = 0; k < subKeyLength; k++) {
                     if (Arrays.equals(signerKeyID, certificate._content._subKeyIDs[k])) {
                        currentSignature.verify(this._content._publicKey, certificate.getSubKey(k), this._content._userIDs[i]);
                        verified = true;
                     }
                  }
               }
            } catch (PGPVerificationException var12) {
            } catch (PGPEncodingException e) {
               throw new DecodeException(e.toString());
            }
         }
      }

      if (!verified) {
         throw new CertificateVerificationException();
      }
   }

   public final int queryKeyUsage(String userID, long purpose) {
      int userIDIndex = this.getUserIDIndex(userID);
      if (userIDIndex < 0) {
         throw new IllegalArgumentException();
      } else {
         return this.queryKeyUsage(this._content._userIDSignatures[userIDIndex], purpose);
      }
   }

   public final int queryKeyUsage(byte[] keyID, long purpose) {
      if (Arrays.equals(keyID, this._content._keyID)) {
         boolean unspecified = false;
         String[] userIDs = this.getUserIDs();
         int numUserIDs = userIDs.length;

         for (int i = 0; i < numUserIDs; i++) {
            int keyUsageResult = this.queryKeyUsage(userIDs[i], purpose);
            if (keyUsageResult == 1) {
               return 1;
            }

            if (keyUsageResult == -1) {
               unspecified = true;
            }
         }

         return unspecified ? -1 : 0;
      } else {
         int length = this._content._subKeyIDs.length;

         for (int i = 0; i < length; i++) {
            if (Arrays.equals(keyID, this._content._subKeyIDs[i])) {
               return this.queryKeyUsage(this._content._subKeySignatures[i], purpose);
            }
         }

         throw new IllegalArgumentException();
      }
   }

   @Override
   public final void setStatus(CertificateStatus status) throws BackwardStatusException {
      throw new BackwardStatusException();
   }

   @Override
   public final void verify() throws DecodeException, CertificateVerificationException {
      int numUserIDs = this._content._userIDs.length;

      for (int i = 0; i < numUserIDs; i++) {
         PGPSignaturePacket[] certificationSignatures = this.getCertificationSignatures(this._content._userIDSignatures[i]);
         PGPSignaturePacket[] selfSignatures = this.getSelfSignatures(certificationSignatures);
         int numSelfSignatures = selfSignatures.length;

         try {
            for (int j = 0; j < numSelfSignatures; j++) {
               PGPSignaturePacket currentSignature = selfSignatures[j];
               currentSignature.verify(this._content._publicKey, this._content._userIDs[i]);
            }
         } catch (PGPEncodingException e) {
            throw new DecodeException(e.toString());
         } catch (PGPVerificationException e) {
            throw new CertificateVerificationException(e.toString());
         }
      }
   }

   @Override
   public final void verify(KeyStore keyStore) throws DecodeException, CertificateVerificationException {
      keyStore.addIndex(new PGPKeyIDKeyStoreIndex());
      boolean verified = false;
      int numUserIDs = this._content._userIDs.length;

      for (int i = 0; i < numUserIDs; i++) {
         for (PGPSignaturePacket currentSignature : this.getCertificationSignatures(this._content._userIDSignatures[i])) {
            try {
               byte[] signerKeyID = currentSignature.getSignerKeyID();
               if (Arrays.equals(signerKeyID, this._content._keyID)) {
                  currentSignature.verify(this._content._publicKey, this._content._userIDs[i]);
                  verified = true;
               } else {
                  for (Enumeration enumeration = keyStore.elements(-2737350786039236692L, signerKeyID); enumeration.hasMoreElements(); verified = true) {
                     KeyStoreData data = (KeyStoreData)enumeration.nextElement();
                     PGPCertificate certificate = (PGPCertificate)data.getCertificate();
                     currentSignature.verify(this._content._publicKey, certificate._content._publicKey.getPublicKey(), this._content._userIDs[i]);
                  }
               }
            } catch (PGPVerificationException var13) {
            } catch (PGPEncodingException e) {
               throw new DecodeException(e.toString());
            }
         }
      }

      if (!verified) {
         throw new CertificateVerificationException();
      }
   }

   @Override
   public final void verify(PublicKey issuerPublicKey) throws DecodeException, CertificateVerificationException {
      if (issuerPublicKey == null) {
         throw new IllegalArgumentException();
      }

      if (issuerPublicKey.equals(this.getPublicKey())) {
         this.verify();
      } else {
         KeyStore keyStore = PGPKeyStore.getInstance();
         keyStore.addIndex(new PublicKeyKeyStoreIndex());
         Enumeration enumeration = keyStore.elements(7540601854220086457L, issuerPublicKey);

         while (enumeration.hasMoreElements()) {
            KeyStoreData data = (KeyStoreData)enumeration.nextElement();
            Certificate certificate = data.getCertificate();
            if (certificate instanceof PGPCertificate) {
               this.verify((PGPCertificate)certificate);
               return;
            }
         }

         boolean verified = false;
         int numUserIDs = this._content._userIDs.length;

         for (int i = 0; i < numUserIDs; i++) {
            for (PGPSignaturePacket currentSignature : this.getCertificationSignatures(this._content._userIDSignatures[i])) {
               try {
                  currentSignature.verify(this._content._publicKey, issuerPublicKey, this._content._userIDs[i]);
                  verified = true;
               } catch (PGPVerificationException var11) {
               } catch (PGPEncodingException e) {
                  throw new DecodeException(e.toString());
               }
            }
         }

         if (!verified) {
            throw new CertificateVerificationException();
         }
      }
   }

   @Override
   public final int queryKeyUsage(long purpose) {
      boolean unspecified = false;
      int keyUsageResult = this.queryKeyUsage(this._content._keyID, purpose);
      if (keyUsageResult == 1) {
         return 1;
      }

      if (keyUsageResult == -1) {
         unspecified = true;
      }

      byte[][] subKeyIDs = this.getSubKeyIDs();
      int numSubKeyIDs = subKeyIDs.length;

      for (int i = 0; i < numSubKeyIDs; i++) {
         keyUsageResult = this.queryKeyUsage(subKeyIDs[i], purpose);
         if (keyUsageResult == 1) {
            return 1;
         }

         if (keyUsageResult == -1) {
            unspecified = true;
         }
      }

      return unspecified ? -1 : 0;
   }

   @Override
   public final boolean isValid(long date) {
      return date > this._content._notBefore && date < this._content._notAfter;
   }

   @Override
   public final boolean isValid() {
      return this.isValid(System.currentTimeMillis());
   }

   @Override
   public final String getSignatureAlgorithm() {
      try {
         return PGPUtilities.publicKeyAlgorithmConstantToString(this._content._signatureAlgorithm);
      } finally {
         ;
      }
   }

   @Override
   public final boolean isRoot() {
      return true;
   }

   @Override
   public final boolean isCA() {
      return true;
   }

   @Override
   public final int getVersion() {
      return this._content._version;
   }

   @Override
   public final String getType() {
      return "PGP";
   }

   @Override
   public final String getSubjectFriendlyName() {
      return this._content._friendlyName;
   }

   @Override
   public final DistinguishedName getSubject() {
      return new PGPDistinguishedName(this._content._primaryUserID.getEncoding());
   }

   @Override
   public final CertificateStatus getStatus() {
      CertificateStatus status = CertificateStatusManager.getInstance().getStatus(this);
      return status != null ? status : this.getStatus(this._content._parentKeySignatures, this._content._publicKey);
   }

   @Override
   public final byte[] getSerialNumber() {
      return this._content._fingerprint;
   }

   @Override
   public final String getPublicKeyAlgorithm() {
      try {
         return PGPUtilities.publicKeyAlgorithmConstantToString(this._content._publicKeyAlgorithm);
      } finally {
         ;
      }
   }

   @Override
   public final PublicKey getPublicKey() {
      try {
         return this._content._publicKey.getPublicKey();
      } catch (PGPEncodingException var4) {
         return null;
      } finally {
         ;
      }
   }

   @Override
   public final long getNotBefore() {
      return this._content._notBefore;
   }

   @Override
   public final long getNotAfter() {
      return this._content._notAfter;
   }

   @Override
   public final DistinguishedName getIssuer() {
      return new PGPDistinguishedName(this._content._primaryUserID.getEncoding());
   }

   @Override
   public final Object getInformation(long id, Object param, Object defaultValue) {
      if (id == -7850001002262082664L) {
         return this.getEmailAddresses();
      } else if (id == -334528756150594391L) {
         return Boolean.FALSE;
      } else if (id == -2021910959928808912L) {
         return Boolean.FALSE;
      } else if (id == -1188891808812199856L) {
         return Boolean.FALSE;
      } else if (id == -7341435958452683242L) {
         return Boolean.TRUE;
      } else if (id == -5753772986264564736L) {
         StringBuffer buffer = new StringBuffer();
         ResourceBundle rb = PGPUtilities.getResourceBundle();
         buffer.append(rb.getString(8031));
         buffer.append(this.getSubjectFriendlyName());
         return buffer.toString();
      } else {
         return defaultValue;
      }
   }

   @Override
   public final CertificateExtension[] getExtensions(boolean criticalBit) {
      return null;
   }

   @Override
   public final CertificateExtension[] getExtensions() {
      return null;
   }

   @Override
   public final CertificateExtension getExtension(OID oid) {
      return null;
   }

   @Override
   public final byte[] getEncoding(int field) {
      return null;
   }

   @Override
   public final byte[] getEncoding() {
      return Arrays.copy(this._content._encoding);
   }

   @Override
   public final CertificateDisplayField[] getCustomDisplayFields() {
      return null;
   }

   @Override
   public final void checkCertificateChain(int position, Certificate[] chain) {
   }

   @Override
   public final String toString() {
      return this.getSubjectFriendlyName();
   }

   private final int queryInternalKeyUsage(byte[] keyFlags, long purpose) {
      if (keyFlags == null || keyFlags.length == 0) {
         return -1;
      } else if (purpose == 32) {
         return (keyFlags[0] & 1) != 0 ? 1 : 0;
      } else if (purpose == 1) {
         return (keyFlags[0] & 2) != 0 ? 1 : 0;
      } else if (purpose != 8 && purpose != 4096 && purpose != 4) {
         return -1;
      } else {
         return (keyFlags[0] & 4) != 0 ? 1 : 0;
      }
   }

   private final int getUserIDIndex(String userID) {
      int numUserIDs = this._content._userIDs.length;

      for (int i = 0; i < numUserIDs; i++) {
         PGPUserIDPacket currentUserID = this._content._userIDs[i];
         if (StringUtilities.strEqualIgnoreCase(userID, currentUserID.getUserID(), 1701707776)) {
            return i;
         }
      }

      return -1;
   }

   private final CertificateStatus getRevokedStatus(int revocationCode, long signatureCreationTime) {
      int revocationReason;
      switch (revocationCode) {
         case 1:
            revocationReason = 4;
            break;
         case 2:
            revocationReason = 1;
            break;
         case 3:
            revocationReason = 8;
            break;
         case 32:
            revocationReason = 9;
            break;
         default:
            revocationReason = 0;
      }

      return new CertificateStatus(1, System.currentTimeMillis(), signatureCreationTime, -1, signatureCreationTime, revocationReason);
   }

   private final PGPSignaturePacket[] getCertificationSignatures() {
      PGPSignaturePacket[] certificationSignatures = new PGPSignaturePacket[0];
      int numUserIDs = this._content._userIDs.length;

      for (int i = 0; i < numUserIDs; i++) {
         PGPSignaturePacket[] currentUserIDSignatures = this.getCertificationSignatures(this._content._userIDSignatures[i]);
         int numExistingSignatures = certificationSignatures.length;
         int numNewSignatures = currentUserIDSignatures.length;
         Array.resize(certificationSignatures, numExistingSignatures + numNewSignatures);
         System.arraycopy(currentUserIDSignatures, 0, certificationSignatures, numExistingSignatures, numNewSignatures);
      }

      return certificationSignatures;
   }

   private final PGPSignaturePacket[] getCertificationSignatures(String emailAddress) {
      if (emailAddress == null) {
         return this.getCertificationSignatures();
      }

      int idIndex = this.getEmailAddressIndex(emailAddress);
      return idIndex == -1 ? new PGPSignaturePacket[0] : this.getCertificationSignatures(this._content._userIDSignatures[idIndex]);
   }

   private final PGPSignaturePacket[] getCertificationSignatures(PGPSignaturePacket[] signatures) {
      PGPSignaturePacket[] certificationSignatures = new PGPSignaturePacket[0];

      for (PGPSignaturePacket currentSignature : signatures) {
         switch (currentSignature.getSignatureType()) {
            case 16:
            case 17:
            case 18:
            case 19:
               Arrays.add(certificationSignatures, currentSignature);
               break;
            case 48:
               byte[] currentSignerKeyID = currentSignature.getSignerKeyID();
               long currentCreationTime = currentSignature.getCreationTime();
               int numCertificationSignatures = certificationSignatures.length;

               for (int k = 0; k < numCertificationSignatures; k++) {
                  if (Arrays.equals(currentSignerKeyID, certificationSignatures[k].getSignerKeyID())
                     && currentCreationTime > certificationSignatures[k].getCreationTime()) {
                     Arrays.removeAt(certificationSignatures, k);
                  }
               }
         }
      }

      return certificationSignatures;
   }

   private final PGPSignaturePacket[] getBindingSignatures(PGPSignaturePacket[] signatures) {
      PGPSignaturePacket[] bindingSignatures = new PGPSignaturePacket[0];

      for (PGPSignaturePacket currentSignature : signatures) {
         switch (currentSignature.getSignatureType()) {
            case 24:
               Arrays.add(bindingSignatures, currentSignature);
               break;
            case 40:
               byte[] currentSignerKeyID = currentSignature.getSignerKeyID();
               long currentCreationTime = currentSignature.getCreationTime();
               int numBindingSignatures = bindingSignatures.length;

               for (int k = 0; k < numBindingSignatures; k++) {
                  if (Arrays.equals(currentSignerKeyID, bindingSignatures[k].getSignerKeyID()) && currentCreationTime > bindingSignatures[k].getCreationTime()) {
                     Arrays.removeAt(bindingSignatures, k);
                  }
               }
         }
      }

      return bindingSignatures;
   }

   private final PGPSignaturePacket[] getSelfSignatures(PGPSignaturePacket[] signatures) {
      byte[] parentKeyID = this._content._keyID;
      int numSignatures = signatures.length;
      PGPSignaturePacket[] selfSignatures = new PGPSignaturePacket[numSignatures];
      int numSelfSignatures = 0;

      for (int i = 0; i < numSignatures; i++) {
         if (Arrays.equals(parentKeyID, signatures[i].getSignerKeyID())) {
            selfSignatures[numSelfSignatures++] = signatures[i];
         }
      }

      Array.resize(selfSignatures, numSelfSignatures);
      return selfSignatures;
   }

   private final int getEmailAddressIndex(String emailAddress) {
      int numUserIDs = this._content._userIDs.length;

      for (int i = 0; i < numUserIDs; i++) {
         PGPUserIDPacket currentUserID = this._content._userIDs[i];
         if (StringUtilities.strEqualIgnoreCase(emailAddress, currentUserID.getEmailAddress(), 1701707776)) {
            return i;
         }
      }

      return -1;
   }

   private final int queryKeyUsage(PGPSignaturePacket[] signaturePackets, long purpose) {
      try {
         PGPSignaturePacket[] selfSignatures = this.getSelfSignatures(signaturePackets);
         int numSelfSignatures = selfSignatures.length;
         PGPSignatureSubPacketParser[] selfSignatureParsers = new PGPSignatureSubPacketParser[numSelfSignatures];
         long mostRecentCreationTime = 0;
         int mostRecentIndex = -1;

         for (int i = 0; i < numSelfSignatures; i++) {
            selfSignatureParsers[i] = new PGPSignatureSubPacketParser(selfSignatures[i].getSignatureSubPackets());
            long currentCreationTime = 0;
            int currentSignatureVersion = selfSignatures[i].getVersion();
            switch (currentSignatureVersion) {
               case 2:
                  throw new CryptoUnsupportedOperationException("Ver:" + currentSignatureVersion);
               case 3:
               default:
                  currentCreationTime = selfSignatures[i].getCreationTime();
                  break;
               case 4:
                  currentCreationTime = selfSignatureParsers[i].getSignatureCreationTime();
            }

            if (currentCreationTime > mostRecentCreationTime && selfSignatureParsers[i].getKeyFlags() != null) {
               mostRecentCreationTime = currentCreationTime;
               mostRecentIndex = i;
            }
         }

         if (mostRecentIndex >= 0) {
            return this.queryInternalKeyUsage(selfSignatureParsers[mostRecentIndex].getKeyFlags(), purpose);
         }
      } catch (PGPEncodingException var16) {
         return -1;
      } finally {
         return -1;
      }

      return -1;
   }

   private final CertificateStatus getStatus(PGPSignaturePacket[] param1, PGPPublicKeyPacket param2) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: ldc2_w 9223372036854775807
      // 03: lstore 3
      // 04: aload 1
      // 05: arraylength
      // 06: istore 5
      // 08: bipush 0
      // 09: istore 6
      // 0b: iload 6
      // 0d: iload 5
      // 0f: if_icmplt 15
      // 12: goto d5
      // 15: aload 1
      // 16: iload 6
      // 18: aaload
      // 19: astore 7
      // 1b: aload 7
      // 1d: invokevirtual net/rim/device/internal/crypto/pgp/PGPSignaturePacket.getSignatureType ()I
      // 20: istore 8
      // 22: iload 8
      // 24: bipush 32
      // 26: if_icmpeq 33
      // 29: iload 8
      // 2b: bipush 40
      // 2d: if_icmpeq 33
      // 30: goto cf
      // 33: aload 7
      // 35: aload 0
      // 36: getfield net/rim/device/api/crypto/certificate/pgp/PGPCertificate._content Lnet/rim/device/api/crypto/certificate/pgp/Content;
      // 39: getfield net/rim/device/api/crypto/certificate/pgp/Content._publicKey Lnet/rim/device/internal/crypto/pgp/PGPPublicKeyPacket;
      // 3c: aload 2
      // 3d: invokevirtual net/rim/device/internal/crypto/pgp/PGPSignaturePacket.verify (Lnet/rim/device/internal/crypto/pgp/PGPPublicKeyPacket;Lnet/rim/device/internal/crypto/pgp/PGPPublicKeyPacket;)V
      // 40: bipush 0
      // 41: istore 9
      // 43: aload 7
      // 45: invokevirtual net/rim/device/internal/crypto/pgp/PGPSignaturePacket.getVersion ()I
      // 48: istore 10
      // 4a: iload 10
      // 4c: tableswitch 28 2 4 80 28 37
      // 68: aload 7
      // 6a: invokevirtual net/rim/device/internal/crypto/pgp/PGPSignaturePacket.getCreationTime ()J
      // 6d: lstore 3
      // 6e: goto b6
      // 71: new net/rim/device/internal/crypto/pgp/PGPSignatureSubPacketParser
      // 74: dup
      // 75: aload 7
      // 77: invokevirtual net/rim/device/internal/crypto/pgp/PGPSignaturePacket.getSignatureSubPackets ()Ljava/util/Vector;
      // 7a: invokespecial net/rim/device/internal/crypto/pgp/PGPSignatureSubPacketParser.<init> (Ljava/util/Vector;)V
      // 7d: astore 11
      // 7f: aload 11
      // 81: invokevirtual net/rim/device/internal/crypto/pgp/PGPSignatureSubPacketParser.getSignatureCreationTime ()J
      // 84: lstore 3
      // 85: aload 11
      // 87: invokevirtual net/rim/device/internal/crypto/pgp/PGPSignatureSubPacketParser.getRevocationCode ()B
      // 8a: istore 9
      // 8c: iload 9
      // 8e: ifge b6
      // 91: new net/rim/device/api/crypto/pgp/PGPEncodingException
      // 94: dup
      // 95: ldc_w "CNRC"
      // 98: invokespecial net/rim/device/api/crypto/pgp/PGPEncodingException.<init> (Ljava/lang/String;)V
      // 9b: athrow
      // 9c: new net/rim/device/api/crypto/CryptoUnsupportedOperationException
      // 9f: dup
      // a0: new java/lang/StringBuffer
      // a3: dup
      // a4: ldc_w "Ver:"
      // a7: invokespecial java/lang/StringBuffer.<init> (Ljava/lang/String;)V
      // aa: iload 10
      // ac: invokevirtual java/lang/StringBuffer.append (I)Ljava/lang/StringBuffer;
      // af: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // b2: invokespecial net/rim/device/api/crypto/CryptoUnsupportedOperationException.<init> (Ljava/lang/String;)V
      // b5: athrow
      // b6: aload 0
      // b7: iload 9
      // b9: lload 3
      // ba: invokespecial net/rim/device/api/crypto/certificate/pgp/PGPCertificate.getRevokedStatus (IJ)Lnet/rim/device/api/crypto/certificate/CertificateStatus;
      // bd: areturn
      // be: astore 9
      // c0: goto cf
      // c3: astore 9
      // c5: goto cf
      // c8: astore 9
      // ca: goto cf
      // cd: astore 9
      // cf: iinc 6 1
      // d2: goto 0b
      // d5: new net/rim/device/api/crypto/certificate/CertificateStatus
      // d8: dup
      // d9: bipush 0
      // da: invokestatic java/lang/System.currentTimeMillis ()J
      // dd: lload 3
      // de: bipush -1
      // e0: i2l
      // e1: bipush -1
      // e3: i2l
      // e4: invokespecial net/rim/device/api/crypto/certificate/CertificateStatus.<init> (IJJJJ)V
      // e7: areturn
      // try (25 -> 76): 77 net/rim/device/api/crypto/pgp/PGPEncodingException
      // try (25 -> 76): 79 net/rim/device/api/crypto/pgp/PGPVerificationException
      // try (25 -> 76): 81 null
      // try (25 -> 76): 83 null
   }

   public PGPCertificate(byte[] encoding, PGPPacket[] packets) {
      this._content = new Content(encoding, packets);
      Memory.createGroup(this._content);
   }

   public PGPCertificate(InputStream input) throws PGPEncodingException {
      PGPPacketParser parser = new PGPPacketParser(input);
      PGPPacket[][] packetArray = parser.getPackets();
      if (packetArray.length != 1) {
         throw new PGPEncodingException("CMKs");
      }

      this._content = new Content(parser.getEncodings()[0], packetArray[0]);
      Memory.createGroup(this._content);
   }

   public PGPCertificate(byte[] encoding) {
      this(SharedInputStream.getSharedInputStream(encoding));
   }

   @Override
   public final int hashCode() {
      return HashCodeCalculator.getCRC32(this._content._encoding);
   }

   @Override
   public final boolean equals(Object obj) {
      if (!(obj instanceof PGPCertificate)) {
         return false;
      }

      PGPCertificate certificate = (PGPCertificate)obj;
      return Arrays.equals(certificate._content._encoding, this._content._encoding);
   }
}
