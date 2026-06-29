package net.rim.device.apps.internal.secureemail.encodings.pgp.cache;

import net.rim.device.api.crypto.pgp.PGPEncryptedInputStream;
import net.rim.device.api.ui.Manager;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.internal.secureemail.cache.CachedEncryptionField;
import net.rim.device.apps.internal.secureemail.encodings.pgp.PGPEncryptionField;

public final class CachedPGPEncryptionField extends CachedEncryptionField {
   PGPEncryptedInputStream _inputStream;

   public CachedPGPEncryptionField(PGPEncryptedInputStream inputStream, int besEncryptionState, int besWeakRecipientState) {
      super(besEncryptionState, besWeakRecipientState);
      this._inputStream = inputStream;
   }

   @Override
   public final void fillManagerHeader(Manager manager, Object context) {
      if (!ContextObject.getFlag(context, 101)) {
         manager.add(new PGPEncryptionField(this._inputStream, super._besEncryptionState, super._besWeakRecipientState, context));
      }
   }
}
