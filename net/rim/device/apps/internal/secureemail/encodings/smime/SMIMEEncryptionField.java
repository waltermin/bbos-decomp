package net.rim.device.apps.internal.secureemail.encodings.smime;

import net.rim.device.api.crypto.SymmetricKey;
import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.crypto.cms.CMSEnvelopedDataInputStream;
import net.rim.device.apps.internal.secureemail.SecureEmailEncryptionField;

public final class SMIMEEncryptionField extends SecureEmailEncryptionField {
   private CMSEnvelopedDataInputStream _cmsEnvelopedDataInputStream;

   public SMIMEEncryptionField(CMSEnvelopedDataInputStream cmsEnvelopedDataInputStream, int besEncryptionState, int besWeakRecipientState, Object context) {
      super(SMIMEFactory.getInstance(), besEncryptionState, besWeakRecipientState, context);
      this._cmsEnvelopedDataInputStream = cmsEnvelopedDataInputStream;
      this.initialize();
   }

   @Override
   protected final SymmetricKey getSessionKey() {
      return this._cmsEnvelopedDataInputStream.getSessionKey();
   }

   @Override
   protected final String getRecipientPublicKeyAlgorithm() {
      return this._cmsEnvelopedDataInputStream.getRecipientPublicKeyAlgorithm();
   }

   @Override
   protected final int getRecipientPublicKeyBitLength() {
      return this._cmsEnvelopedDataInputStream.getRecipientPublicKeyBitLength();
   }

   @Override
   protected final Certificate getRecipientCertificate() {
      return this._cmsEnvelopedDataInputStream.getCertificate(this._cmsEnvelopedDataInputStream.getRecipient());
   }

   @Override
   protected final int getContentCipher() {
      return SMIMEFactory.getInstance().getUtilities().getContentCipherForConstant(this._cmsEnvelopedDataInputStream.getContentCipherConstant());
   }
}
