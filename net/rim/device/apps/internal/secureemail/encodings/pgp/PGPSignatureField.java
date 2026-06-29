package net.rim.device.apps.internal.secureemail.encodings.pgp;

import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.crypto.pgp.PGPSignedInputStream;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.system.Application;
import net.rim.device.api.ui.Manager;
import net.rim.device.apps.api.framework.model.MatchProvider;
import net.rim.device.apps.internal.secureemail.SecureEmailSignatureField;
import net.rim.device.apps.internal.secureemail.SecureEmailSignatureField$SignatureStatusField;
import net.rim.device.apps.internal.secureemail.SecureEmailSignatureField$TrustStatusField;

public final class PGPSignatureField extends SecureEmailSignatureField {
   private PGPSignedInputStream _pgpSignedInputStream;
   private byte[] _signerKeyID;
   private boolean _plainTextLeading;
   private long _creationDate;

   public PGPSignatureField(
      PGPSignedInputStream pgpSignedInputStream,
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
         pgpSignedInputStream,
         serviceRecord,
         inbound,
         moreAvailable,
         creationDate,
         senderEmailAddress,
         isPINMessage,
         besVerificationState,
         besSignerCertificateHash,
         besNoVerifyReason,
         false,
         manager,
         context,
         null
      );
   }

   public PGPSignatureField(
      PGPSignedInputStream pgpSignedInputStream,
      ServiceRecord serviceRecord,
      boolean inbound,
      boolean moreAvailable,
      long creationDate,
      String senderEmailAddress,
      boolean isPINMessage,
      int besVerificationState,
      byte[] besSignerCertificateHash,
      String besNoVerifyReason,
      boolean plainTextLeading,
      Manager manager,
      Object context,
      SecureEmailSignatureField oldField
   ) {
      super(
         PGPFactory.getInstance(),
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
      this._pgpSignedInputStream = pgpSignedInputStream;
      this._plainTextLeading = plainTextLeading;
      this._creationDate = creationDate;
      this.initialize();
   }

   @Override
   protected final void getSignerCertificateWithoutVerifying() {
   }

   @Override
   protected final Certificate[] getIncludedCertificates() {
      return null;
   }

   @Override
   protected final boolean isSignatureVerificationPossible() {
      return true;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   protected final void checkSendingDate() {
      if (!this._plainTextLeading) {
         long signedDate = -1;
         boolean var5 = false /* VF: Semaphore variable */;

         try {
            var5 = true;
            signedDate = this._pgpSignedInputStream.getSignatureCreationTime(this._signerKeyID);
            var5 = false;
         } finally {
            if (var5) {
               return;
            }
         }

         if (signedDate != -1 && Math.abs(this._creationDate - signedDate) >= 1800000) {
            super._trustStatus.setStatus(14);
         }
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   protected final void verifySignature() {
      if (this._pgpSignedInputStream == null) {
         super._signatureStatus.setStatus(4);
      } else {
         byte[][] signerKeyIDs = this._pgpSignedInputStream.getSignerKeyIDs();
         if (signerKeyIDs != null && signerKeyIDs.length != 0) {
            Exception lastThrown = null;

            for (byte[] tempKeyID : signerKeyIDs) {
               try {
                  Certificate tempCertificate = this.locateSignerCertificate(tempKeyID);
                  if (tempCertificate != null) {
                     this._signerKeyID = tempKeyID;
                     super._signerCertificate = tempCertificate;
                     this._pgpSignedInputStream.verify(tempKeyID, tempCertificate);
                     super._signatureStatus.setStatus(1);
                     return;
                  }
               } catch (Throwable var8) {
                  lastThrown = e;
                  continue;
               }
            }

            if (lastThrown != null) {
               super._signatureStatus.setThrowable(lastThrown);
            } else {
               super._signatureStatus.setStatus(3);
            }
         } else if (super._moreAvailable) {
            super._signatureStatus.setStatus(3);
         } else {
            super._signatureStatus.setStatus(4);
         }
      }
   }

   @Override
   protected final String getSignatureDigestName() {
      return this._pgpSignedInputStream.getSignatureDigestName(this._signerKeyID);
   }

   @Override
   protected final void checkSendingAddress() {
      if (!this._plainTextLeading) {
         super.checkSendingAddress();
      }
   }

   private final Certificate locateSignerCertificate(byte[] keyID) {
      if (super._secureEmailCertificateServers.length == 0) {
         try {
            return this._pgpSignedInputStream.getSignerCertificate(keyID);
         } finally {
            ;
         }
      } else {
         int numSecureEmailCertificateServers = super._secureEmailCertificateServers.length;

         for (int i = 0; i < numSecureEmailCertificateServers; i++) {
            try {
               Certificate signerCertificate = super._secureEmailCertificateServers[i]
                  .getCertificateByCertificateID(keyID, super._senderEmailAddress, this.getServerOperationListener());
               if (signerCertificate != null) {
                  return signerCertificate;
               }
            } finally {
               continue;
            }
         }

         return null;
      }
   }

   @Override
   protected final SecureEmailSignatureField$SignatureStatusField createSignatureStatusField(
      Application displayApp, int initialSignatureStatus, String signerName, String signatureStatusDetails
   ) {
      return new PGPSignatureField$PGPSignatureStatusField(this, displayApp, initialSignatureStatus, signerName, signatureStatusDetails);
   }

   @Override
   protected final SecureEmailSignatureField$TrustStatusField createTrustStatusField(Application displayApp, int initialTrustStatus, String trustStatusDetails) {
      return new PGPSignatureField$PGPTrustStatusField(this, displayApp, initialTrustStatus, trustStatusDetails);
   }

   @Override
   protected final MatchProvider createSignerCertificateMatchProvider() {
      return new PGPCertificateMatchProvider(this._pgpSignedInputStream.getSignerKeyIDs());
   }
}
