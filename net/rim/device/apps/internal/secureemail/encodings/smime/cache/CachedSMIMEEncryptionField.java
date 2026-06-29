package net.rim.device.apps.internal.secureemail.encodings.smime.cache;

import net.rim.device.api.crypto.cms.CMSEnvelopedDataInputStream;
import net.rim.device.api.ui.Manager;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.internal.secureemail.cache.CachedEncryptionField;
import net.rim.device.apps.internal.secureemail.encodings.smime.SMIMEEncryptionField;

public class CachedSMIMEEncryptionField extends CachedEncryptionField {
   private CMSEnvelopedDataInputStream _cmsStream;

   public CachedSMIMEEncryptionField(CMSEnvelopedDataInputStream cmsStream, int besEncryptionState, int besWeakRecipientState) {
      super(besEncryptionState, besWeakRecipientState);
      this._cmsStream = cmsStream;
   }

   @Override
   public void fillManagerHeader(Manager manager, Object context) {
      if (!ContextObject.getFlag(context, 101)) {
         manager.add(new SMIMEEncryptionField(this._cmsStream, super._besEncryptionState, super._besWeakRecipientState, context));
      }
   }
}
