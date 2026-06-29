package net.rim.device.apps.internal.secureemail.cache;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Manager;
import net.rim.device.apps.api.framework.model.ContextObject;

public class CachedSeparatorField extends CachedField {
   @Override
   public void fillManager(Manager manager, Object context) {
      if (!ContextObject.getFlag(context, 101)) {
         manager.add((Field)(new Object()));
      }
   }
}
