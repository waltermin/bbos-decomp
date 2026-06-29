package net.rim.device.apps.internal.secureemail.encodings.smime.cache;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Manager;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.internal.secureemail.cache.CachedMissingPrivateKeyField;
import net.rim.device.apps.internal.secureemail.encodings.smime.SMIMEFactory;

public final class CachedSMIMEMissingPrivateKeyField extends CachedMissingPrivateKeyField {
   @Override
   public final void fillManagerHeader(Manager manager, Object context) {
      if (!ContextObject.getFlag(context, 101)) {
         manager.add((Field)(new Object(SMIMEFactory.getInstance())));
      }
   }
}
