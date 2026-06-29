package net.rim.device.apps.internal.secureemail.encodings.smime.cache;

import net.rim.device.api.crypto.cms.CMSSignedDataInputStream;
import net.rim.device.api.ui.Manager;
import net.rim.device.apps.internal.secureemail.SecureEmailSignatureField;
import net.rim.device.apps.internal.secureemail.cache.CachedSignatureField;
import net.rim.device.apps.internal.secureemail.encodings.smime.SMIMESignatureField;

public class CachedSMIMESignatureField extends CachedSignatureField {
   private CMSSignedDataInputStream _cmsStream;

   public CachedSMIMESignatureField(CMSSignedDataInputStream cmsStream, int besVerificationState, byte[] besSignerCertificateHash, String besNoVerifyReason) {
      super(besVerificationState, besSignerCertificateHash, besNoVerifyReason);
      this._cmsStream = cmsStream;
   }

   @Override
   protected SecureEmailSignatureField getSecureEmailSignatureField(Manager manager, Object context, SecureEmailSignatureField oldField) {
      return new SMIMESignatureField(
         this._cmsStream,
         super._serviceRecord,
         super._inbound,
         super._moreAvailable,
         super._creationDate,
         super._senderEmailAddress,
         super._isPINMessage,
         super._besVerificationState,
         super._besSignerCertificateHash,
         super._besNoVerifyReason,
         manager,
         context,
         oldField
      );
   }
}
