package net.rim.device.apps.internal.secureemail.encodings.pgp.cache;

import net.rim.device.api.crypto.pgp.PGPSignedInputStream;
import net.rim.device.api.ui.Manager;
import net.rim.device.apps.internal.secureemail.SecureEmailSignatureField;
import net.rim.device.apps.internal.secureemail.cache.CachedSignatureField;
import net.rim.device.apps.internal.secureemail.encodings.pgp.PGPBodyModel;
import net.rim.device.apps.internal.secureemail.encodings.pgp.PGPSignatureField;

public final class CachedPGPSignatureField extends CachedSignatureField {
   private PGPSignedInputStream _inputStream;
   private boolean _plainTextLeading;

   public CachedPGPSignatureField(
      PGPSignedInputStream inputStream,
      PGPBodyModel pgpBodyModel,
      int besVerificationState,
      byte[] besSignerCertificateHash,
      String besNoVerifyReason,
      boolean plainTextLeading
   ) {
      super(besVerificationState, besSignerCertificateHash, besNoVerifyReason);
      this._inputStream = inputStream;
      this._plainTextLeading = plainTextLeading;
   }

   @Override
   protected final SecureEmailSignatureField getSecureEmailSignatureField(Manager manager, Object context, SecureEmailSignatureField oldField) {
      return new PGPSignatureField(
         this._inputStream,
         super._serviceRecord,
         super._inbound,
         super._moreAvailable,
         super._creationDate,
         super._senderEmailAddress,
         super._isPINMessage,
         super._besVerificationState,
         super._besSignerCertificateHash,
         super._besNoVerifyReason,
         this._plainTextLeading,
         manager,
         context,
         oldField
      );
   }
}
