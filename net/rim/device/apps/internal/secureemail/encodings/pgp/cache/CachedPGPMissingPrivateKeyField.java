package net.rim.device.apps.internal.secureemail.encodings.pgp.cache;

import net.rim.device.api.ui.Manager;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.internal.secureemail.SecureEmailMissingPrivateKeyField;
import net.rim.device.apps.internal.secureemail.cache.CachedMissingPrivateKeyField;
import net.rim.device.apps.internal.secureemail.encodings.pgp.PGPFactory;

public final class CachedPGPMissingPrivateKeyField extends CachedMissingPrivateKeyField {
   @Override
   public final void fillManagerHeader(Manager manager, Object context) {
      if (!ContextObject.getFlag(context, 101)) {
         manager.add(new SecureEmailMissingPrivateKeyField(PGPFactory.getInstance()));
      }
   }
}
